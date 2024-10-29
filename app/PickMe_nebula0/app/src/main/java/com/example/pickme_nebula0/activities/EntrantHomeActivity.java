package com.example.pickme_nebula0.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.pickme_nebula0.R;

public class EntrantHomeActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entrant_home);

        final Button ScanQRButton=findViewById(R.id.ScanQRButton);
        ScanQRButton.setOnClickListener(new View.OnClickListener() {
            public void onClick (View view) {
                Intent i = new Intent(EntrantHomeActivity.this, QRcodeActivity.class);
                startActivity(i);
            }
        });
    }
}
