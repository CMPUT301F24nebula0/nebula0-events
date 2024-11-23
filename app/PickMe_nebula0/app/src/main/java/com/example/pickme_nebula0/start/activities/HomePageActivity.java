package com.example.pickme_nebula0.start.activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import android.Manifest;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.example.pickme_nebula0.GeolocationManager;
import com.example.pickme_nebula0.R;
import com.example.pickme_nebula0.admin.activities.AdminHomeActivity;
import com.example.pickme_nebula0.entrant.activities.EntrantHomeActivity;
import com.example.pickme_nebula0.notification.MessageViewActivity;
import com.example.pickme_nebula0.organizer.activities.OrganizerHomeActivity;
import com.example.pickme_nebula0.user.activities.UserInfoActivity;


/**
 * Home page activity for the app
 * Allows user to navigate to different parts of the app
 * such as profile, admin, entrant, organizer, and messages
 * Also checks for notification permission
 *
 * @Author Taekwan Yoon
 */
public class HomePageActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_NOTIF_PERMISSION = 100;
    private static final int REQUEST_CODE_MEDIA_PERMISSION = 101;

    // UI components
    Button profileButton;
    Button adminButton;
    Button entrantButton;
    Button organizerButton;
    Button messagesButton;

    // START TY (example geolocation implementation)
//    Button buttonSaveGeolocation;
//    private GeolocationManager gm;
//    private ActivityResultLauncher<String[]> locationPermissionLauncher;
    // END TY

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // attach to layout xml file
        setContentView(R.layout.activity_home_page);

        // buttons on screen
        profileButton = findViewById(R.id.profile_button);
        adminButton = findViewById(R.id.adminButton);
        entrantButton = findViewById(R.id.entrantButton);
        organizerButton = findViewById(R.id.organizerButton);
        messagesButton = findViewById(R.id.button_messages);

        // START TY (example geolocation implementation)
//        buttonSaveGeolocation = findViewById(R.id.button_save_geolocation);
//        gm = new GeolocationManager(this);
//        locationPermissionLauncher = registerForActivityResult(
//                new ActivityResultContracts.RequestMultiplePermissions(),
//                result -> {
//                    boolean fineLocationGranted = result.getOrDefault(
//                            Manifest.permission.ACCESS_FINE_LOCATION, false);
//                    boolean coarseLocationGranted = result.getOrDefault(
//                            Manifest.permission.ACCESS_COARSE_LOCATION, false);
//
//                    if (fineLocationGranted || coarseLocationGranted) {
//                        Toast.makeText(this, "Location permission granted.", Toast.LENGTH_SHORT).show();
//                        // Now, proceed to save geolocation
//                        saveGeolocationData();
//                    } else {
//                        Toast.makeText(this, "Location permission denied.", Toast.LENGTH_SHORT).show();
//                    }
//                }
//        );
//        buttonSaveGeolocation.setOnClickListener(v -> {
//            if (gm.hasLocationPermission()) {
//                saveGeolocationData();
//            } else {
//                // Request location permissions
//                locationPermissionLauncher.launch(new String[]{
//                        Manifest.permission.ACCESS_FINE_LOCATION,
//                        Manifest.permission.ACCESS_COARSE_LOCATION
//                });
//            }
//        });

        // END TY

        // actions once buttons are pressed
        profileButton.setOnClickListener(view -> navigateTo(UserInfoActivity.class));
        adminButton.setOnClickListener(view -> navigateTo(AdminHomeActivity.class));
        entrantButton.setOnClickListener(view -> navigateTo(EntrantHomeActivity.class));
        organizerButton.setOnClickListener(view -> navigateTo(OrganizerHomeActivity.class));
        messagesButton.setOnClickListener(view -> navigateTo(MessageViewActivity.class));

        // Check if notification permission is granted
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                // Request permission, users can disable notifications in UserInfoActivity, but we want to have permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS}, REQUEST_CODE_NOTIF_PERMISSION);
            }
        }

        // Check if media access permission is granted
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_MEDIA_IMAGES}, REQUEST_CODE_MEDIA_PERMISSION);
        }
    }

    // START TY example geolocation implementation
//    private void saveGeolocationData() {
//        String userID = "c5e9e56f41572d06"; // Replace with actual userID
//        String eventID = "ouyj7XRzfSIqFRAXqnQ5"; // Replace with actual eventID
//
//        try {
//            gm.saveGeolocation(userID, eventID, success -> {
//                if (success) {
//                    Toast.makeText(HomePageActivity.this, "Geolocation saved successfully.", Toast.LENGTH_SHORT).show();
//                } else {
//                    Toast.makeText(HomePageActivity.this, "Failed to save geolocation.", Toast.LENGTH_SHORT).show();
//                }
//            });
//        } catch (SecurityException e) {
//            Toast.makeText(this, "Location permission not granted.", Toast.LENGTH_SHORT).show();
//        }
//    }
    // END TY

    /**
     * Navigate to the target activity
     * @param targetActivity
     */
    private void navigateTo(Class<?> targetActivity ) {
        Intent intent = new Intent(HomePageActivity.this, targetActivity);
        startActivity(intent);
    }

    /**
     * Generate a toast notification to let the user know the effect of their permission selection
     *
     * @param requestCode The request code passed in requestPermissions
     * @param permissions The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions
     *     which is either {@link android.content.pm.PackageManager#PERMISSION_GRANTED}
     *     or {@link android.content.pm.PackageManager#PERMISSION_DENIED}. Never null.
     *
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CODE_NOTIF_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, you can notify the user in the background
                Toast.makeText(this, "Notif Permission Granted", Toast.LENGTH_SHORT).show();
            } else {
                // Permission denied, handle accordingly
                Toast.makeText(this, "Notif Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }

        if (requestCode == REQUEST_CODE_MEDIA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, you can notify the user in the background
                Toast.makeText(this, "Media Permission Granted", Toast.LENGTH_SHORT).show();
            } else {
                // Permission denied, handle accordingly
                Toast.makeText(this, "Media Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}