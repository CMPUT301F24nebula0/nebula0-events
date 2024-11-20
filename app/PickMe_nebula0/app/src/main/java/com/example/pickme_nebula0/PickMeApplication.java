package com.example.pickme_nebula0;

import static androidx.activity.result.ActivityResultCallerKt.registerForActivityResult;

import android.app.Application;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.Manifest;
import android.os.Build;
import android.util.Log;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class PickMeApplication extends Application {
    private static PickMeApplication instance;

    @Override
    public void onCreate(){
        super.onCreate();
        instance = this;
        Intent serviceIntent = new Intent(this, NotificationService.class);
        startService(serviceIntent);
    }

    public static PickMeApplication getInstance(){
        return instance;
    }

//    private void setUpNotifs(){
//        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
//        ) {
//            Log.d("NOTIF","permission granted");
//            // Permission is granted
//            // Proceed forward to create a notification
//        } else {
//            Log.d("NOTIF","permission denied");
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//                if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
//                        != PackageManager.PERMISSION_GRANTED) {
//                    ActivityCompat.requestPermissions(this.getApplicationContext(), new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1);
//                }
//            }
//        }
//    }
}
