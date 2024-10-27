package com.example.pickme_nebula0.activities;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.pickme_nebula0.R;

public class QRcodeActivity extends AppCompatActivity {

    Button homebtn;
    boolean active = false;
    String City;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        active = true;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qrcodeactivity);

        homebtn = findViewById(R.id.homebtn);
        homebtn.setOnClickListener(new View.OnClickListener() {
                                       @Override
                                       public void onClick(View v) {
                                           active = false;
                                           finish();

                                       }
                                   });
    }
}
