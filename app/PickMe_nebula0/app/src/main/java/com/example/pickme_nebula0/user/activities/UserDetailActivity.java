package com.example.pickme_nebula0.user.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.pickme_nebula0.GoogleMapActivity;
import com.example.pickme_nebula0.R;
import com.example.pickme_nebula0.db.DBManager;

import com.example.pickme_nebula0.db.FBStorageManager;
import com.example.pickme_nebula0.event.Event;
import com.example.pickme_nebula0.facility.Facility;
import com.example.pickme_nebula0.organizer.fragments.OrganizerSelectedFragment;

import com.example.pickme_nebula0.user.User;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

/**
 * UserDetailActivity
 *
 * This activity allows admins or organizers to view detailed information about a user.
 *
 * Features:
 * - Displays user details such as ID, name, email, and profile picture.
 * - Provides actions for deleting the user, canceling their event registration,
 *   and displaying their geolocation on a map.
 * - Allows deletion and replacement of the user's profile image.
 *
 * Author: Taekwan Yoon
 *
 * @see User
 * @see Facility
 * @see DBManager
 * @see FBStorageManager
 */
public class UserDetailActivity extends AppCompatActivity {
    private FirebaseFirestore db;
    private TextView userDetailsTextView;
    private DBManager dbManager;
    private ImageView profileImage;
    private StorageReference storageRef;
    private String userName;
    private String userID;

    /**
     * Initializes the activity.
     *
     * - Sets up UI components and event listeners for buttons.
     * - Fetches user details from Firestore.
     * - Configures conditional visibility and actions based on user role (admin/organizer).
     *
     * @param savedInstanceState The saved instance state for restoring the activity.
     */
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
        final Button delImgButton = findViewById(R.id.button_ud_delete_image);
        profileImage = findViewById(R.id.profile_image);
        userID = getIntent().getStringExtra("userID");

        if(!getIntent().getBooleanExtra("admin",false)){
            delButton.setVisibility(View.GONE);
        }
        boolean isAdmin = getIntent().getBooleanExtra("admin", false);
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

                finish();
            });
        }

        // check if userID and eventID got passed from the intent
//        Log.d("UserDetailActivity", "userID: " + userID + ", eventID: " + eventID);

        if(!getIntent().getBooleanExtra("organizer", true)){
            mapButton.setVisibility(View.GONE);
        } else {
            // hide mapButton if geolocation requirement is off
            // means geolocation data was not stored for entrants and
            // geolocation will fail assertion check
            dbManager.getEvent(eventID, (eventObj) -> {
                Event event = (Event) eventObj;
                if (!event.getGeolocationRequired()) {
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


            }, () -> {
                // could not retrieve event
                Log.d("UserDetailActivity", "Could not retrieve event to set mapButton");
                mapButton.setVisibility(View.GONE);
                Toast.makeText(UserDetailActivity.this,"Could not retrieve this user's registered event",Toast.LENGTH_SHORT).show();
             });
        }

        delImgButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(userName != null){
                    deleteAndReplaceProfileImage(userName); }
                }
        });

        backButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) { getOnBackPressedDispatcher().onBackPressed(); }
        });

        delButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbManager.deleteUser(userID,()->{
                    Toast.makeText(UserDetailActivity.this,"User Deleted",Toast.LENGTH_SHORT).show();
                    finish();});
            }
        });

        userDetailsTextView = findViewById(R.id.user_details_text_view);

        fetchUserDetails(userID);

        if (isAdmin) {
            mapButton.setVisibility(View.GONE);
            profileImage.setVisibility(View.VISIBLE);
            loadProfileImageFromFirebase(userID);
            delImgButton.setVisibility(View.VISIBLE);
        } else {
            mapButton.setVisibility(View.VISIBLE);
            profileImage.setVisibility(View.GONE);
            delImgButton.setVisibility(View.GONE);
        }

    }

    /**
     * Fetches the details of a user from Firestore and updates the UI.
     *
     * - Retrieves user data and displays their details in the UI.
     * - Handles cases where the user is not found or retrieval fails.
     *
     * @param userID The ID of the user whose details are to be fetched.
     */
    private void fetchUserDetails(String userID) {
        dbManager.getUser(userID, userObj -> {
            if (userObj instanceof User) {
                User user = (User) userObj;
                runOnUiThread(() -> {
                    StringBuilder details = new StringBuilder();
                    details.append("UserID: ").append(user.getUserID()).append("\n\n");
                    details.append("User Name: ").append(user.getName()).append("\n\n");
                    userName = user.getName();
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

    /**
     * Loads a user's profile image from Firebase Storage and displays it in the UI.
     *
     * - Uses Picasso for image loading.
     * - Handles cases where the image does not exist or fails to load.
     *
     * @param userID The ID of the user whose profile image is to be loaded.
     */
    private void loadProfileImageFromFirebase(String userID) {
        FirebaseStorage storage = FirebaseStorage.getInstance("gs://pickme-c2fb3.firebasestorage.app");
        storageRef = storage.getReference();

        StorageReference imageRef = storageRef.child("profilePics/" + userID);
        imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
            Picasso.get()
                    .load(uri)
                    .fit()
                    .placeholder(R.drawable.ic_profile_placeholder)
                    .error(R.drawable.ic_profile_placeholder)
                    .into(profileImage);
        }).addOnFailureListener(exception -> {
            profileImage.setVisibility(View.GONE);
        });
    }

    /**
     * Deletes and replaces a user's profile image.
     *
     * - Generates a placeholder profile image.
     * - Updates the profile image in the UI and Firebase Storage.
     *
     * @param name The name of the user for generating a placeholder image.
     */
    private void deleteAndReplaceProfileImage(String name){
        Uri newPicUri = UserInfoActivity.genProfilePic(this,name);
        Picasso.get()
                .load(newPicUri)
                .placeholder(R.drawable.ic_profile_placeholder)
                .error(R.drawable.ic_profile_placeholder)
                .into(profileImage);
        FBStorageManager.uploadProfilePic(newPicUri,userID,UserDetailActivity.this);
    }
}