package com.example.pickme_nebula0.start.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.pickme_nebula0.MyFirebaseMessagingService;
import com.example.pickme_nebula0.db.DBManager;
import com.example.pickme_nebula0.DeviceManager;
import com.example.pickme_nebula0.R;
import com.example.pickme_nebula0.user.activities.UserInfoActivity;

/**
 * LaunchActivity
 *
 * This activity is displayed when the user first opens the app. It determines whether the user is
 * registered in the database.
 *
 * - If the user is registered, they are redirected to the HomePageActivity.
 * - If the user is not registered, they are redirected to the UserInfoActivity for registration.
 *
 * Features:
 * - Supports edge-to-edge UI.
 * - Handles user registration status dynamically.
 *
 * @author Stephine Yearley
 */
@SuppressLint("CustomSplashScreen")
public class LaunchActivity extends AppCompatActivity {
    private DBManager dbManager;
    private boolean returning = false;
    private String deviceID;

    /**
     * Called when the activity is first created.
     *
     * - Sets up edge-to-edge UI with padding for system bars.
     * - Retrieves the device ID.
     * - Checks if the user is registered in the database.
     *
     * @param savedInstanceState The saved instance state for restoring the activity.
     */
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

    /**
     * Called when the activity is visible to the user.
     *
     * If the user has returned from the registration screen, redirects them to the home screen.
     */
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
        MyFirebaseMessagingService.registerFCMToken(deviceID);
        Intent intent = new Intent(LaunchActivity.this, HomePageActivity.class);
        startActivity(intent);
        returning = true;
    }


}