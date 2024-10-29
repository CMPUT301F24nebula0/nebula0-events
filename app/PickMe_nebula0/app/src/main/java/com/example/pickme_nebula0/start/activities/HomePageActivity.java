package com.example.pickme_nebula0.start.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.pickme_nebula0.R;
import com.example.pickme_nebula0.admin.activities.AdminHomeActivity;
import com.example.pickme_nebula0.entrant.activities.EntrantHomeActivity;
import com.example.pickme_nebula0.organizer.activities.OrganizerHomeActivity;
import com.example.pickme_nebula0.user.activities.UserInfoActivity;

public class HomePageActivity extends AppCompatActivity {
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
    }

    // function for switching screens through intent
    private void navigateTo(Class<?> targetActivity ) {
        Intent intent = new Intent(HomePageActivity.this, targetActivity);
        startActivity(intent);
    }
}
