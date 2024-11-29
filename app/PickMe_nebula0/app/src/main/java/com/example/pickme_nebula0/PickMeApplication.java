package com.example.pickme_nebula0;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

import com.google.firebase.FirebaseApp;

/**
 * Application class, sets up notification channels, and allows retrieval of application instance
 */
public class PickMeApplication extends Application {
    private static PickMeApplication instance;

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
     * Gets application instance
     * @return instance of PickMeApplication
     */
    public static PickMeApplication getInstance(){
        return instance;
    }

}
