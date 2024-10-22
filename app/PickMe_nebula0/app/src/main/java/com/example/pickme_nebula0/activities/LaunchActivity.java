package com.example.pickme_nebula0.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.pickme_nebula0.DeviceManager;
import com.example.pickme_nebula0.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class LaunchActivity extends AppCompatActivity {
    private FirebaseFirestore db;
    private boolean returning = false;

    String deviceID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_launch);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize Firestore and get device ID
        db = FirebaseFirestore.getInstance();
        deviceID = DeviceManager.getDeviceId(this);

        checkUserRegistration();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (returning) { // only go to home page when returning from registration, not on launch
            registeredCallback();
            returning = false;
        }
    }

    /**
     * Checks for user with this device ID in the database
     * If no such user exists, launches UserInfoActivity
     * If user already exists, launches HomePageActivity
     */
    private void checkUserRegistration(){

        DocumentReference docRef = db.collection("User").document(deviceID);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        String userID = document.getString("userID");
                        Log.d("Firestore", "UserID: " + userID);
                        registeredCallback();
                    } else {
                        Log.d("Firestore", "No such document");
                        unregisteredCallback();
                    }
                } else {
                    Log.d("Firestore", "get failed with ", task.getException());
                }
            }
        });
    }

    /**
     * Action to take if the user is not yet in the database
     */
    private void unregisteredCallback(){
        Intent intent = new Intent(LaunchActivity.this, UserInfoActivity.class);
        intent.putExtra("newUser", true);
        startActivity(intent);
        returning = true;
    }

    /**
     * Action to take if the user is already in the database
     */
    private void registeredCallback(){
        Intent intent = new Intent(LaunchActivity.this, HomePageActivity.class);
        startActivity(intent);
        returning = true;
    }

}