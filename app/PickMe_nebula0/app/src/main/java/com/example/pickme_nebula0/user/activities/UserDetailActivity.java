package com.example.pickme_nebula0.user.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.pickme_nebula0.GoogleMapActivity;
import com.example.pickme_nebula0.R;
import com.example.pickme_nebula0.db.DBManager;
import com.example.pickme_nebula0.organizer.fragments.OrganizerSelectedFragment;
import com.example.pickme_nebula0.user.User;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

/**
 * Activity for admin or organizer to view details about a user
 */
public class UserDetailActivity extends AppCompatActivity {
    private FirebaseFirestore db;
    private TextView userDetailsTextView;
    private DBManager dbManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_detail);
        dbManager = new DBManager();

        db = FirebaseFirestore.getInstance();

        final Button backButton = findViewById(R.id.backButton);
        final Button delButton = findViewById(R.id.button_ud_delete);
        final Button cancelEntrantButton = findViewById(R.id.button_cancel_selected_entrant);
        final Button mapButton = findViewById(R.id.button_map);

        if(!getIntent().getBooleanExtra("admin",false)){
            delButton.setVisibility(View.GONE);
        }
        String userID = getIntent().getStringExtra("userID");
        if (userID == null || userID.isEmpty()) {
            Toast.makeText(this, "Invalid User ID.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        String eventID = getIntent().getStringExtra("eventID");
        if (eventID == null || eventID.isEmpty()) {
            cancelEntrantButton.setVisibility(View.GONE);
        } else {
            cancelEntrantButton.setVisibility(View.VISIBLE);
            cancelEntrantButton.setOnClickListener(v -> {
                // cancelling an entrant from organizer's end
                db.collection("Events").document(eventID)
                        .collection("EventRegistrants")
                        .document(userID).update("status", "CANCELLED");

                db.collection("Users").document(userID)
                        .collection("RegisteredEvents")
                        .document(eventID).update("status", "CANCELLED");
                //TODO: Add a notification logic here for the cancelled entrant

                finish();
            });
        }

        // check if userID and eventID got passed from the intent
        Log.d("UserDetailActivity", "userID: " + userID + ", eventID: " + eventID);

        if(!getIntent().getBooleanExtra("organizer", true)){
            mapButton.setVisibility(View.GONE);
        } else {
            mapButton.setVisibility(View.VISIBLE);
            mapButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    db.collection("Events")
                                    .document(eventID)
                                            .collection("EventRegistrants")
                                                    .document(userID)
                                                            .get()
                                                                    .addOnCompleteListener(task -> {
                                                                        if (task.isSuccessful()) {
                                                                            DocumentSnapshot document = task.getResult();
                                                                            if (document.exists()) {
                                                                                GeoPoint geolocation = document.getGeoPoint("geolocation");
                                                                                assert geolocation != null;

                                                                                double latitude = geolocation.getLatitude();
                                                                                double longitude = geolocation.getLongitude();

                                                                                Log.d("UserDetailActivity", "Latitude: " + latitude + ", Longitude: " + longitude);
                                                                                Intent intent = new Intent(UserDetailActivity.this, GoogleMapActivity.class);
                                                                                intent.putExtra("latitude", latitude);
                                                                                intent.putExtra("longitude", longitude);
                                                                                startActivity(intent);
                                                                            }
                                                                        }
                                                                    });
                }
            });
        }



        backButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) { getOnBackPressedDispatcher().onBackPressed(); }
        });

        delButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbManager.deleteUser(userID,()->{finish();});
            }
        });

        userDetailsTextView = findViewById(R.id.user_details_text_view);

        fetchUserDetails(userID);
    }

    private void fetchUserDetails(String userID) {
        dbManager.getUser(userID, userObj -> {
            if (userObj instanceof User) {
                User user = (User) userObj;
                runOnUiThread(() -> {
                    StringBuilder details = new StringBuilder();
                    details.append("UserID: ").append(user.getUserID()).append("\n\n");
                    details.append("User Name: ").append(user.getName()).append("\n\n");
                    details.append("User Email: ").append(user.getEmail()).append("\n\n");
                    // Append other user details as needed
                    userDetailsTextView.setText(details.toString());
                });
            } else {
                runOnUiThread(() -> {
                    Toast.makeText(this, "User not found.", Toast.LENGTH_SHORT).show();
                    finish();
                });
            }
        }, () -> runOnUiThread(() -> {
            Toast.makeText(this, "Failed to retrieve user data.", Toast.LENGTH_SHORT).show();
            finish();
        }));
    }
}