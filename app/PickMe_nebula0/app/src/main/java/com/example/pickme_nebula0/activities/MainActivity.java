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

public class MainActivity extends AppCompatActivity {
    private FirebaseFirestore db;

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

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Reference the document in the 'user' collection
        DocumentReference docRef = db.collection("User").document("7DkUTlD2uTzXP1eBeUfb");

        // Get the document and print userID
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        // Retrieve and log the userID field
                        String userID = document.getString("userID");
                        Log.d("Firestore", "UserID: " + userID);
                    } else {
                        Log.d("Firestore", "No such document");
                    }
                } else {
                    Log.d("Firestore", "get failed with ", task.getException());
                }
            }
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