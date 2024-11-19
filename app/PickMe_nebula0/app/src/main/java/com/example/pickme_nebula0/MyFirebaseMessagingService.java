package com.example.pickme_nebula0;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;


/**
 * @author Stephine Yearley
 * This class handles Firestore Cloud Messaging registration and token refresh
 * It is based of code generated by Open AI's ChatGPT in response to prompt:
 * I have an android app written in java in android studio. I am using firestore as my database. I want to generate an android notification for a specific user everytime a new document is created in a specfic collection of the database. I want the user to receive these notifications even if the application on their phone isn't running (e.g. has been fully killed)
 */
public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String CHANNEL_ID = "pickme_cID";

    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);
        Log.d("FCM", "Refreshed FCM token: " + token);

        saveTokenToFirestore(token,DeviceManager.getDeviceId());
    }

    public static void saveTokenToFirestore(String fcmToken, String userID) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("Users").document(userID)
                .update("fcmToken", fcmToken)
                .addOnSuccessListener(aVoid -> Log.d("Firestore", "FCM token saved successfully"))
                .addOnFailureListener(e -> Log.w("Firestore", "Error saving FCM token", e));
    }

    public static void registerFCMToken(String userID) {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.w("FCM", "Fetching FCM token failed", task.getException());
                        return;
                    }

                    String fcmToken = task.getResult();
                    Log.d("FCM", "FCM Token: " + fcmToken);

                    saveTokenToFirestore(fcmToken,userID);
                });
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // Check if message contains a notification payload
        if (remoteMessage.getNotification() != null) {
            String title = remoteMessage.getNotification().getTitle();
            String body = remoteMessage.getNotification().getBody();

            // Show notification when app is in the foreground
            sendNotification(title, body);
        }
    }

    // Handle notifications when app is in foreground
    private void sendNotification(String title, String body) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Create notification channel, if the channel already exists, this does nothing
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "PickMeAll",
                    NotificationManager.IMPORTANCE_HIGH
            );
            notificationManager.createNotificationChannel(channel);
        }

        // Create the notification
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(body)
                .setSmallIcon(R.drawable.notification_icon)  // Use your app's icon here
                .setAutoCancel(true);

        // Show the notification
        notificationManager.notify(0, notificationBuilder.build());
    }
}