package com.example.pickme_nebula0.start.activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import android.Manifest;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.example.pickme_nebula0.DeviceManager;
import com.example.pickme_nebula0.R;
import com.example.pickme_nebula0.admin.activities.AdminHomeActivity;
import com.example.pickme_nebula0.db.DBManager;
import com.example.pickme_nebula0.entrant.activities.EntrantHomeActivity;
import com.example.pickme_nebula0.notification.MessageViewActivity;
import com.example.pickme_nebula0.organizer.activities.OrganizerHomeActivity;
import com.example.pickme_nebula0.user.activities.UserInfoActivity;


/**
 * Activity for the home page of the app.
 *
 * This activity serves as the main navigation hub, allowing users to:
 * - Access their profile
 * - Navigate to admin, entrant, or organizer functionalities
 * - View messages
 * - Refresh the visibility of buttons based on user roles
 *
 * Additionally, it ensures that necessary permissions for notifications and media access are requested.
 *
 * @Author Taekwan Yoon
 */
public class HomePageActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_NOTIF_PERMISSION = 100;
    private static final int REQUEST_CODE_MEDIA_PERMISSION = 101;

    DBManager dbManager = new DBManager();

    // UI components
    Button profileButton;
    Button adminButton;
    Button entrantButton;
    Button organizerButton;
    Button messagesButton;
    Button refreshButton;

    /**
     * Called when the activity is visible to the user.
     * Updates button visibility based on the user's role.
     */
    @Override
    protected void onResume(){
        super.onResume();
        updateButtonVisibility();
    }

    /**
     * Initializes the activity, setting up UI components and requesting permissions.
     *
     * - Attaches UI components to their respective views.
     * - Sets up button click listeners for navigation.
     * - Requests notification and media permissions if not already granted.
     *
     * @param savedInstanceState The saved instance state for restoring the activity.
     */
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
        refreshButton = findViewById(R.id.buttonRefreshHome);

        // admin and organizer button rendered conditionally if user is admin/organizer
        adminButton.setVisibility(View.GONE);
        organizerButton.setVisibility(View.GONE);

        Animation buttonClickAnimation = AnimationUtils.loadAnimation(this, R.anim.button_click_animation);

        // actions once buttons are pressed
        profileButton.setOnClickListener(view -> {
            view.startAnimation(buttonClickAnimation);
            navigateTo(UserInfoActivity.class);
        });

        adminButton.setOnClickListener(view -> {
            view.startAnimation(buttonClickAnimation);
            navigateTo(AdminHomeActivity.class);
        });

        entrantButton.setOnClickListener(view -> {
            view.startAnimation(buttonClickAnimation);
            navigateTo(EntrantHomeActivity.class);
        });

        organizerButton.setOnClickListener(view -> {
            view.startAnimation(buttonClickAnimation);
            navigateTo(OrganizerHomeActivity.class);
        });

        messagesButton.setOnClickListener(view -> {
            view.startAnimation(buttonClickAnimation);
            navigateTo(MessageViewActivity.class);
        });

        refreshButton.setOnClickListener(view -> {
            view.startAnimation(buttonClickAnimation);
            updateButtonVisibility();
        });

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

    /**
     * Navigates to the specified target activity.
     *
     * @param targetActivity The class of the activity to navigate to.
     */
    private void navigateTo(Class<?> targetActivity ) {
        Intent intent = new Intent(HomePageActivity.this, targetActivity);
        startActivity(intent);
    }

    /**
     * Updates the visibility of admin and organizer buttons based on the user's role.
     *
     * Uses `DBManager` to check if the current user is an admin or organizer
     * and updates the visibility of the corresponding buttons.
     */
    private void updateButtonVisibility(){
        String userID = DeviceManager.getDeviceId();
        dbManager.doIfAdmin(userID,()->{adminButton.setVisibility(View.VISIBLE);},()->{adminButton.setVisibility(View.GONE);});
        dbManager.doIfOrganizer(userID,()->{organizerButton.setVisibility(View.VISIBLE);},()->{organizerButton.setVisibility(View.GONE);});
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