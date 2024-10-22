package com.example.pickme_nebula0.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.pickme_nebula0.DeviceManager;
import com.example.pickme_nebula0.R;

public class MainActivity extends AppCompatActivity {

    String deviceID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Check if the user has registered
        deviceID = DeviceManager.getDeviceId(this);
        // TODO: query DB to see if user has registered or if its their first time
        boolean registered = false;
        if (!registered){ // if not registered, send them to enter their user info
            Intent intent = new Intent(MainActivity.this, UserInfoActivity.class);
            intent.putExtra("newUser", true);
            startActivity(intent);
        }

        // User is registered, render homepage
        // TODO

    }
}