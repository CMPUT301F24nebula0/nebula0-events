package com.example.pickme_nebula0.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.pickme_nebula0.DeviceManager;
import com.example.pickme_nebula0.R;

// TODO: integrate changes with DB
// TODO: add profile image LATER
public class UserInfoActivity extends AppCompatActivity {
    String deviceID;

    // UI Elements
    TextView headerTextView;
    EditText nameField;
    EditText emailField;
    EditText phoneField;
    CheckBox enableNotifBox;
    Button confirmButton;
    Button cancelButton;
    Button facilityButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        boolean newUser = getIntent().getBooleanExtra("newUser",false);
        deviceID = DeviceManager.getDeviceId(this);

        headerTextView = findViewById(R.id.textViewUserInfoHeader);
        nameField = findViewById(R.id.editTextUserInfoName);
        emailField = findViewById(R.id.editTextUserInfoEmail);
        phoneField = findViewById(R.id.editTextUserInfoPhone);
        enableNotifBox = findViewById(R.id.checkBoxUserInfoNotifEnabled);
        confirmButton= findViewById(R.id.buttonUserInfoConfirm);
        cancelButton =findViewById(R.id.buttonUserInfoCancel);
        facilityButton =findViewById(R.id.buttonUserInfoManageFacility);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: save user info to DB
                finish();
            }
        });

        facilityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: launch facility management activity
                finish();
            }
        });


        if (newUser){ // new user's require a slightly different screen layout
            headerTextView.setText(R.string.user_info_header_first_time);
            cancelButton.setVisibility(View.GONE);
            facilityButton.setVisibility(View.GONE);
        } else{ // returning users need to see their stored info
            headerTextView.setText(R.string.user_info_header_returning);
            // TODO: populate these with user info from database
            // nameField.setText();
            // emailField.setText();
            // phoneField.setText();
            // enableNotifBox.setActivated();

        }

    }

}
