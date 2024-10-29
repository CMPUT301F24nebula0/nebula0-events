package com.example.pickme_nebula0.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.pickme_nebula0.R;

public class HomePageActivity extends AppCompatActivity {
    Button adminButton;
    Button entrantButton;
    Button organizerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        final Button QRbtn=findViewById(R.id.QRbtn);
        QRbtn.setOnClickListener(new View.OnClickListener() {
                public void onClick (View view) {

                Intent i = new Intent(HomePageActivity.this, QRcodeActivity.class);
                startActivity(i);
            }
        });

        adminButton = findViewById(R.id.adminButton);
        entrantButton = findViewById(R.id.entrantButton);
        organizerButton = findViewById(R.id.organizerButton);

        adminButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToAdminHomePage();
            }
        });

        entrantButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToEntrantHomePage();
            }
        });

        organizerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToOrganizerHomePage();
            }
        });
    }

    private void navigateToAdminHomePage() {
        Intent intent = new Intent(HomePageActivity.this, AdminHomeActivity.class);
        startActivity(intent);
    }

    private void navigateToEntrantHomePage() {
        Intent intent = new Intent(HomePageActivity.this, EntrantHomeActivity.class);
        startActivity(intent);
    }

    private void navigateToOrganizerHomePage() {
        Intent intent = new Intent(HomePageActivity.this, OrganizerHomeActivity.class);
        startActivity(intent);
    }
}
