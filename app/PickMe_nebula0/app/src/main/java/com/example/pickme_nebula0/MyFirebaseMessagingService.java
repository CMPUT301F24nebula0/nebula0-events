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
 * MyFirebaseMessagingService
 *
 * This service handles Firebase Cloud Messaging (FCM) events for the app, such as:
 * - Handling new FCM tokens and saving them to Firestore.
 * - Receiving and displaying notifications when new messages are delivered.
 * - Registering the FCM token for a specific user in the Firestore database.
 *
 * Key Features:
 * - Automatically updates the FCM token when refreshed.
 * - Sends notifications to the user even if the app is not running.
 * - Creates and manages a notification channel for Android O+.
 *
 * Dependencies:
 * - Firebase Cloud Messaging (FCM) SDK.
 * - Firestore for saving and retrieving tokens.
 *
 * Author: Stephine Yearley
 */
public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String CHANNEL_ID = "pickme_cID";

    /**
     * Called whenever a new FCM token is generated for the device.
     *
     * - Logs the new token for debugging.
     * - Saves the token to Firestore, associated with the current user.
     *
     * @param token The refreshed FCM token.
     */
    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);
        Log.d("FCM", "Refreshed FCM token: " + token);

        saveTokenToFirestore(token,DeviceManager.getDeviceId());
    }

    /**
     * Saves the provided FCM token to the Firestore database for the specified user.
     *
     * - Updates the user's Firestore document with the new token.
     * - Logs success or failure of the update.
     *
     * @param fcmToken The FCM token to save.
     * @param userID The ID of the user to associate with the token.
     */
    public static void saveTokenToFirestore(String fcmToken, String userID) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("Users").document(userID)
                .update("fcmToken", fcmToken)
                .addOnSuccessListener(aVoid -> Log.d("Firestore", "FCM token saved successfully"))
                .addOnFailureListener(e -> Log.w("Firestore", "Error saving FCM token", e));
    }

    /**
     * Registers the FCM token for the specified user.
     *
     * - Retrieves the current FCM token.
     * - Calls `saveTokenToFirestore` to save the token in Firestore.
     * - Handles errors in retrieving the token.
     *
     * @param userID The ID of the user to register the token for.
     */
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

    /**
     * Called when a message is received from FCM.
     *
     * - Extracts the notification payload (title and body).
     * - Calls `sendNotification` to display the notification when the app is in the foreground.
     *
     * @param remoteMessage The message received from FCM.
     */
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

    /**
     * Sends a notification to the user.
     *
     * - Creates a notification channel for Android O+ devices if not already created.
     * - Displays the notification with the provided title and body.
     *
     * @param title The title of the notification.
     * @param body The body of the notification.
     */
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
