package com.example.pickme_nebula0;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Random;
// TODO-sy switch over to FirebaseMessagingService when account is verified (google doesn't want my money rn)

public class NotificationService extends Service {
    private FirebaseFirestore db;
    private CollectionReference userNotifCollectionRef;

    private static final String PREFS_NAME = "MyPrefs";
    private static final String LAST_TIMESTAMP_KEY = "last_notification_timestamp";
    private long lastTimestamp;

    String CHANNEL_ID = "notif_channel";
    String CHANNEL_NAME = "notifications channel";
    String CHANNEL_DESCRIPTION = "channel for all PickMe notifications";

    @Override
    public void onCreate() {
        super.onCreate();

        db = FirebaseFirestore.getInstance();
        userNotifCollectionRef = db.collection("Notifications").document(DeviceManager.getDeviceId()).collection("userNotifs");

        // Create notification channel (only on Android 8.0 and above)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            channel.setDescription(CHANNEL_DESCRIPTION);

            // Get the NotificationManager system service
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        // Start listening for Firestore changes
        startFirestoreListener();
    }

    /**
     * Starts the listener which checks if new notification have been added for this users
     * Runs in background even when user is not actively using the app
     *
     * Based on code generated by OpenAI chatGPT 4 following prompt " generate notification when a
     * new document is added to a firestore collection"
     *
     */
    private void startFirestoreListener() {
        // Load the last timestamp from SharedPreferences
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        lastTimestamp = prefs.getLong(LAST_TIMESTAMP_KEY, 0);

        // Set up a listener for changes in the collection
        userNotifCollectionRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot value, FirebaseFirestoreException error) {
                if (error != null) {
                    Log.w("FirestoreListener", "Listen failed.", error);
                    return;
                }

                long newLastTimestamp = lastTimestamp;
                if (value != null && !value.isEmpty()) {
                    // Check for added documents
                    for (DocumentChange documentChange : value.getDocumentChanges()) {
                        if (documentChange.getType() == DocumentChange.Type.ADDED) {
                            Timestamp timestamp = documentChange.getDocument().getTimestamp("timestamp");
                            if(timestamp != null){
                                long timestampMillis = timestamp.toDate().getTime();

                                // Trigger notification only if the new timestamp is greater than the last one
                                if (timestampMillis > lastTimestamp) {
                                    db.collection("Users")
                                            .document(DeviceManager.getDeviceId())
                                            .get()
                                            .addOnCompleteListener(task -> {
                                                if (task.isSuccessful()) {
                                                    DocumentSnapshot document = task.getResult();
                                                    if (document.exists()) {
                                                        boolean notifEnabled = document.getBoolean("notificationsEnabled");
                                                        if(notifEnabled){
                                                            sendNotification(documentChange.getDocument());
                                                        }
                                                    }
                                                } else {
                                                    Log.d("Firestore", "get failed with ", task.getException());
                                                }
                                            });

                                    // Update the last timestamp to the most recent notification's timestamp
                                    if (timestampMillis > newLastTimestamp) {
                                        newLastTimestamp = timestampMillis;
                                    }
                                }
                            }
                        }
                    }
                }
                // Save the new last timestamp to SharedPreferences
                prefs.edit().putLong(LAST_TIMESTAMP_KEY, newLastTimestamp).apply();
                lastTimestamp = newLastTimestamp;
            }
        });
    }

    private void sendNotification(DocumentSnapshot documentSnapshot) {
        // Create and show the notification (you can customize the notification details)
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.notification_icon)
                .setContentTitle(documentSnapshot.getString("title"))
                .setContentText(documentSnapshot.getString("message"))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            Log.d("NOTIF", "Permission denied in sendNotification");
            return;
        }
        Log.d("NOTIF", "Permission granted in sendNotification");
        notificationManager.notify(new Random().nextInt(), builder.build());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Make sure the service runs in the background
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Clean up when the service is destroyed (e.g., stop listening to Firestore)
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}

