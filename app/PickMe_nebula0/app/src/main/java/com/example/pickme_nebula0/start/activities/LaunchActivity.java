package com.example.pickme_nebula0.start.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.pickme_nebula0.db.DBManager;
import com.example.pickme_nebula0.DeviceManager;
import com.example.pickme_nebula0.R;
import com.example.pickme_nebula0.user.activities.UserInfoActivity;

/**
 * Activity when user first opens app.
 * Checks if they are registered.
 * If yes, sends them to home screen.
 * If no, sends to user info screen.
 *
 * @author Stephine Yearley
 */
@SuppressLint("CustomSplashScreen")
public class LaunchActivity extends AppCompatActivity {
    private DBManager dbManager;
    private boolean returning = false;
    private String deviceID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);

        // set layout
        setContentView(R.layout.activity_launch);

        // set padding for system bars
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // get device ID
        deviceID = DeviceManager.getDeviceId();

        // check if user is registered
        dbManager = new DBManager();
        dbManager.checkUserRegistration(deviceID,this::registeredCallback,this::unregisteredCallback);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (returning) { // only go to home page when returning from registration, not on launch
            registeredCallback();
            returning = false;
        }
    }

    /**
     * Action to take if the user is not in the database
     * Sends user to user info screen
     */
    private void unregisteredCallback(){
        Intent intent = new Intent(LaunchActivity.this, UserInfoActivity.class);
        intent.putExtra("newUser", true);
        startActivity(intent);
        returning = true;
    }

    /**
     * Action to take if the user is in the database
     * Sends user to home screen
     */
    private void registeredCallback(){
        Intent intent = new Intent(LaunchActivity.this, HomePageActivity.class);
        startActivity(intent);
        returning = true;
    }

}