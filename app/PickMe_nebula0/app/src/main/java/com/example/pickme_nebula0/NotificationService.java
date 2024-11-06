package com.example.pickme_nebula0;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Random;

public class NotificationService extends Service {
    private FirebaseFirestore db;
    private CollectionReference userNotifCollectionRef;

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

    private void startFirestoreListener() {
        // Set up a listener for changes in the collection
        userNotifCollectionRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot value, FirebaseFirestoreException error) {
                if (error != null) {
                    Log.w("FirestoreListener", "Listen failed.", error);
                    return;
                }

                // TODO add timestamp
                if (value != null && !value.isEmpty()) {
                    // Check for added documents
                    for (DocumentChange documentChange : value.getDocumentChanges()) {
                        if (documentChange.getType() == DocumentChange.Type.MODIFIED) {
                            // New document added, trigger notification
                            sendNotification(documentChange.getDocument());
                        }
                    }
                }
            }
        });
    }

    private void sendNotification(DocumentSnapshot documentSnapshot) {
        // Create and show the notification (you can customize the notification details)
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.notification_icon)
                .setContentTitle("New Document Added")
                .setContentText("A new document has been added to the collection.")
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

