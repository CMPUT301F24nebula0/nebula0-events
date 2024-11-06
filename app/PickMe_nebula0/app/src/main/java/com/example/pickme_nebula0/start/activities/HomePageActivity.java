package com.example.pickme_nebula0.start.activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import android.Manifest;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.example.pickme_nebula0.R;
import com.example.pickme_nebula0.admin.activities.AdminHomeActivity;
import com.example.pickme_nebula0.entrant.activities.EntrantHomeActivity;
import com.example.pickme_nebula0.organizer.activities.OrganizerHomeActivity;
import com.example.pickme_nebula0.user.activities.UserInfoActivity;

public class HomePageActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_PERMISSION = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // attach to layout xml file
        setContentView(R.layout.activity_home_page);

        // buttons on screen
        final Button profileButton = findViewById(R.id.profile_button);
        final Button adminButton = findViewById(R.id.adminButton);
        final Button entrantButton = findViewById(R.id.entrantButton);
        final Button organizerButton = findViewById(R.id.organizerButton);

        // actions once buttons are pressed
        profileButton.setOnClickListener(view -> navigateTo(UserInfoActivity.class));
        adminButton.setOnClickListener(view -> navigateTo(AdminHomeActivity.class));
        entrantButton.setOnClickListener(view -> navigateTo(EntrantHomeActivity.class));
        organizerButton.setOnClickListener(view -> navigateTo(OrganizerHomeActivity.class));

        // Check if notification permission is granted
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                // Request permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS}, REQUEST_CODE_PERMISSION);
            }
        }
    }

    // function for switching screens through intent
    private void navigateTo(Class<?> targetActivity ) {
        Intent intent = new Intent(HomePageActivity.this, targetActivity);
        startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CODE_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, you can notify the user in the background
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();

            } else {
                // Permission denied, handle accordingly
                Toast.makeText(this, "Permission denied, can't show notifications", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
