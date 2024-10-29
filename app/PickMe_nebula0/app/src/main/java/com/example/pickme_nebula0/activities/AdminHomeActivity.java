package com.example.pickme_nebula0.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ViewFlipper;

import androidx.appcompat.app.AppCompatActivity;

import com.example.pickme_nebula0.R;
import com.google.firebase.firestore.FirebaseFirestore;

public class AdminHomeActivity extends AppCompatActivity {
    private String deviceID;
    private FirebaseFirestore db;

    // UI Elements
    ViewFlipper viewFlipper;
    Button btnManageEvents;
    Button btnManageUsers;
    Button btnManageImages;
    Button btnManageFacilities;
    Button btnManageQR;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_manage);

        viewFlipper = findViewById(R.id.admin_view_flipper);
        btnManageEvents = findViewById(R.id.manage_event);
        btnManageUsers = findViewById(R.id.manage_users);
        btnManageImages = findViewById(R.id.manage_image);
        btnManageFacilities = findViewById(R.id.manage_facilities);
        btnManageQR = findViewById(R.id.manage_qr_code);


        btnManageEvents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO;
                viewFlipper.setDisplayedChild(0); // Show Manage Events layout
            }
        });

        btnManageUsers.setOnClickListener(new View.OnClickListener() {
            @Override
            //TODO;
            public void onClick(View v) {
                viewFlipper.setDisplayedChild(1); // Show Manage Profile layout
            }
        });

        btnManageImages.setOnClickListener(new View.OnClickListener() {
            @Override
            //TODO;
            public void onClick(View v) {
                viewFlipper.setDisplayedChild(2); // Show Manage Image layout
            }
        });

        btnManageQR.setOnClickListener(new View.OnClickListener() {
            @Override
            //TODO;
            public void onClick(View v) {
                viewFlipper.setDisplayedChild(3); // Show Manage QR Code layout
            }
        });

        btnManageFacilities.setOnClickListener(new View.OnClickListener() {
            @Override
            //TODO;
            public void onClick(View v) {
                viewFlipper.setDisplayedChild(4); // Show Manage Facilities layout
            }
        });


    }
}
