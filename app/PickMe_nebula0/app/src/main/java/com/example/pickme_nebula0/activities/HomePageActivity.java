package com.example.pickme_nebula0.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.pickme_nebula0.R;

public class HomePageActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        final Button  QRbtn=findViewById(R.id.QRbtn);
        QRbtn.setOnClickListener(new View.OnClickListener() {
                public void onClick (View view) {

                Intent i = new Intent(HomePageActivity.this, QRcodeActivity.class);
                startActivity(i);
            }
        });
    }
}
