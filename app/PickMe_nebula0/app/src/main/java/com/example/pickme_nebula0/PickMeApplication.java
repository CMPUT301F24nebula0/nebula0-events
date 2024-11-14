package com.example.pickme_nebula0;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

public class PickMeApplication extends Application {
    private static PickMeApplication instance;

    @Override
    public void onCreate(){
        super.onCreate();
        instance = this;

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

    public static PickMeApplication getInstance(){
        return instance;
    }

}
