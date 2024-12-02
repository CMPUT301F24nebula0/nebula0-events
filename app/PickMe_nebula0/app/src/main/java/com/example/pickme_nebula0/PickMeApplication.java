package com.example.pickme_nebula0;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

import com.google.firebase.FirebaseApp;

/**
 * PickMeApplication
 *
 * This class serves as the base `Application` class for the PickMe app.
 * It performs the following key functions:
 *
 * - Sets up notification channels required for Android O+ devices.
 * - Initializes the FirebaseApp instance for Firebase functionalities.
 * - Provides a static method to retrieve the application instance for global access.
 *
 * Features:
 * - Configures a notification channel (`pickme_cID`) for app-wide notifications.
 * - Ensures proper initialization of Firebase when the app is launched.
 *
 * Dependencies:
 * - Firebase SDK.
 */
public class PickMeApplication extends Application {
    private static PickMeApplication instance;

    /**
     * Called when the application is first created.
     *
     * - Initializes the FirebaseApp for Firebase-related operations.
     * - Sets up the notification channel (`pickme_cID`) for notifications.
     * - Stores a reference to the application instance for global access.
     *
     * If the device is running Android O or later, it creates a notification channel
     * with high importance for app notifications.
     */
    @Override
    public void onCreate(){
        super.onCreate();
        instance = this;

        FirebaseApp.initializeApp(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "pickme_cID", // Channel ID
                    "PickMeAll", // Channel Name
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("All notifications for PickMe App");


            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }

    }

    /**
     * Retrieves the application instance.
     *
     * Provides global access to the application context and other application-wide resources.
     *
     * @return The singleton instance of `PickMeApplication`.
     */
    public static PickMeApplication getInstance(){
        return instance;
    }

}
