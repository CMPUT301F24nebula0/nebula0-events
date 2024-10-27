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
import com.example.pickme_nebula0.SharedDialogue;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

// TODO: integrate changes with DB
// TODO: add profile image LATER
public class UserInfoActivity extends AppCompatActivity {
    private String deviceID;
    private FirebaseFirestore db;

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
        cancelButton = findViewById(R.id.buttonUserInfoCancel);
        facilityButton = findViewById(R.id.buttonUserInfoManageFacility);

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
                // TODO: verify data validity (require Name,Email; verify phone,email format)
                String name = nameField.getText().toString();
                String email = emailField.getText().toString();
                String phone = phoneField.getText().toString().replaceAll("[^0-9]", "");;


                String warning = validateUserInfo(name,email,phone);
                if (!warning.isBlank()){
                    // user had entered invalid data, warn them about it and don't update DB
                    SharedDialogue.showInvalidDataAlert(warning,UserInfoActivity.this);
                    return;
                }

                boolean notifEnabled = enableNotifBox.isEnabled();
                createUpdateUser(name,email,phone,notifEnabled);
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
        } else{ // returning users need to see their stored info, read it from DB
            headerTextView.setText(R.string.user_info_header_returning);
            populateFieldsFromDB();
        }

    }

    private void populateFieldsFromDB(){
        DocumentReference docRef = db.collection("User").document(deviceID);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        // Show user the data we have about them
                        try {
                            nameField.setText(document.getString("name"));
                            emailField.setText(document.getString("email"));
                            phoneField.setText(document.getString("phone"));
                            enableNotifBox.setActivated(document.getBoolean("notificationsEnabled"));
                        }
                        catch (Exception e){
                            System.out.printf("Failded to populate fields for user with deviceID-%s: %s",deviceID,e.getMessage());
                        }
                    } else {
                        // Document does not exist
                        System.out.printf("Document with deviceID-%s DNE in 'User' collection%n",deviceID);
                    }
                } else {
                    // Failed to get the document
                    System.out.printf("Failed to get document with deviceID-%s: %s",deviceID, task.getException().getMessage());
                }
            }
        });
    }

    public static String validateUserInfo(String name, String email, String phone){

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
        if (phone.length() != 0 && phone.length() != 10){
            String errString = "Phone number must be 10 digits";
            if (warning.isBlank()){
                warning = errString;
            } else{
                warning += "\n\n" + errString;
            }
        }

        return warning;
    }

    private void createUpdateUser(String name, String email, String phone, boolean notifEnabled) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Reference to the document
        DocumentReference docRef = db.collection("User").document(deviceID);

        // Check if the document exists
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) { // Create or overwrite user info
                DocumentSnapshot document = task.getResult();

                Map<String, Object> userData = new HashMap<>();
                userData.put("name", name);
                userData.put("email", email);
                userData.put("phone", phone);
                userData.put("notificationsEnabled",notifEnabled);

                docRef.set(userData)
                        .addOnSuccessListener(aVoid -> {
                            System.out.println("Document created/updated successfully.");
                        })
                        .addOnFailureListener(e -> {
                            System.out.println("Error creating document: " + e.getMessage());
                        });
            } else {
                // Failed to check document existence
                System.out.println("Failed to create/update document: " + task.getException().getMessage());
            }
        });
    }

}
