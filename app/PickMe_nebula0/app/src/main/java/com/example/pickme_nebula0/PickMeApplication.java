package com.example.pickme_nebula0;

import android.Manifest;
import android.app.Activity;
import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.FirebaseApp;

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

    public static PickMeApplication getInstance(){
        return instance;
    }

}
