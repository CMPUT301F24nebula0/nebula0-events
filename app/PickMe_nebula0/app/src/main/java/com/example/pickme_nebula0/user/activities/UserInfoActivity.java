package com.example.pickme_nebula0.user.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.pickme_nebula0.db.DBManager;
import com.example.pickme_nebula0.DeviceManager;
import com.example.pickme_nebula0.R;
import com.example.pickme_nebula0.SharedDialogue;
import com.example.pickme_nebula0.db.FBStorageManager;
import com.example.pickme_nebula0.user.User;
import com.squareup.picasso.Picasso;

// TODO: add profile image LATER

/**
 * Activity allowing user to register or update their profile information or navigate to FacilityInfoActivity
 *
 * @author Stephine Yearley
 * @see User
 */
public class UserInfoActivity extends AppCompatActivity {
    private String deviceID;
    private DBManager dbManager;

    // UI Elements
    TextView headerTextView;
    EditText nameField;
    EditText emailField;
    EditText phoneField;
    CheckBox enableNotifBox;
    Button confirmButton;
    Button cancelButton;
    Button facilityButton;
    ImageView profilePicImageView;
    Button changeProfilePicButton;
    Button removeProfilePicButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        boolean newUser = getIntent().getBooleanExtra("newUser",false);
        deviceID = DeviceManager.getDeviceId();
        dbManager = new DBManager();

        headerTextView = findViewById(R.id.textViewUserInfoHeader);
        nameField = findViewById(R.id.editTextUserInfoName);
        emailField = findViewById(R.id.editTextUserInfoEmail);
        phoneField = findViewById(R.id.editTextUserInfoPhone);
        enableNotifBox = findViewById(R.id.checkBoxUserInfoNotifEnabled);
        confirmButton= findViewById(R.id.buttonUserInfoConfirm);
        cancelButton = findViewById(R.id.buttonUserInfoCancel);
        facilityButton = findViewById(R.id.buttonUserInfoManageFacility);
        profilePicImageView = findViewById(R.id.imageViewProfilePic);
        changeProfilePicButton = findViewById(R.id.buttonChangePicture);
        removeProfilePicButton = findViewById(R.id.buttonRemovePicture);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // do NOT update db with new info
                finish();
            }
        });

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String name = nameField.getText().toString();
                String email = emailField.getText().toString();
                String phone = phoneField.getText().toString().replaceAll("[^0-9]", "");
                boolean notifEnabled = enableNotifBox.isChecked();

                String warning = validateUserInfo(name,email,phone);
                if (!warning.isBlank()){
                    // user had entered invalid data, warn them about it and don't update DB
                    SharedDialogue.showInvalidDataAlert(warning,UserInfoActivity.this);
                    return;
                }

                User u = new User(deviceID,name,email,phone,notifEnabled);
                dbManager.addUpdateUserProfile(u);
                finish();
            }
        });

        facilityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserInfoActivity.this, FacilityInfoActivity.class);
                startActivity(intent);
            }
        });

        changeProfilePicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO
            }
        });

        removeProfilePicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO
            }
        });

        if (newUser){ // new user's require a slightly different screen layout
            headerTextView.setText(R.string.user_info_header_first_time);
            cancelButton.setVisibility(View.GONE);
            facilityButton.setVisibility(View.GONE);
            enableNotifBox.setChecked(true);
            profilePicImageView.setVisibility(View.GONE);
            changeProfilePicButton.setVisibility(View.GONE);
            removeProfilePicButton.setVisibility(View.GONE);
        } else{ // returning users need to see their stored info, read it from DB
            headerTextView.setText(R.string.user_info_header_returning);
            dbManager.getUser(deviceID,this::populateFieldsFromDB,this::failedToPopulateFieldsFromDB);
        }

    }

    /**
     * Callback to populate the screen's fields
     *
     * @param user instance of user containing data used to populate the fields on screen
     */
    private void populateFieldsFromDB(Object user){
        User castedUser = (User) user;
        nameField.setText(castedUser.getName());
        emailField.setText(castedUser.getEmail());
        if(castedUser.getPhone() != null){
            phoneField.setText(castedUser.getPhone());
        }
        enableNotifBox.setChecked(castedUser.getNotificationsEnabled());
//        FBStorageManager.retrieveProfilePicUri(deviceID,this::renderProfilePicture,this::generateAndSetProfilePicture);
    }

    /**
     *  Callback called if we fail to populate ths user info screen for an existing user
     */
    private void failedToPopulateFieldsFromDB(){
        SharedDialogue.showInvalidDataAlert("Fields could not be populated from DB, the data shown may not match what is in the DB",UserInfoActivity.this);
    }

    /**
     * Checks whether given fields values are valid.
     *
     * @param name name to validate
     * @param email email to validate
     * @param phone phone number to validate
     * @return a blank string if all is valid, else returns a warning string containing information of invalidities
     */
    public static String validateUserInfo(String name, String email, String phone){
        // TODO: data valildation should probably be done within the User Class, but this will work for now
        String warning = "";
        // verify name is non-numeric
        if (name.matches(".*\\d.*")){
            warning = "Name cannot contain numbers";
        }
        // verify email address has proper format
        if (!email.matches(".+@.+\\..+")){
            String errString = "Email must be in format like name@mail.com";
            if (warning.isBlank()){
                warning = errString;
            } else{
                warning += "\n\n" + errString;
            }
        }
        if(name.isBlank() || email.isBlank()){
            String errString = "Must provide both Name and Email";
            if (warning.isBlank()){
                warning = errString;
            } else{
                warning += "\n\n" + errString;
            }
        }
        // verify that, if provided, is 10 numbers (e.g. xxx-xxx-xxxx)
        if (!phone.isEmpty() && phone.length() != 10){
            String errString = "Phone number must be 10 digits";
            if (warning.isBlank()){
                warning = errString;
            } else{
                warning += "\n\n" + errString;
            }
        }

        return warning;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 100) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, you can notify the user in the background
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();

            } else {
                // Permission denied, handle accordingly
                Toast.makeText(this, "Permission denied, can't show notifications", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void renderProfilePicture(Uri uri){
        try {
            Picasso.get().load(uri).into(profilePicImageView);
        } catch(Exception e){
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();

        }
    }

    public void generateAndSetProfilePicture(){
        // TODO
    }


}
