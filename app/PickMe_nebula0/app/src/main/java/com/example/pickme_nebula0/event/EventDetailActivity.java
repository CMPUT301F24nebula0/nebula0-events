package com.example.pickme_nebula0.event;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import java.util.ArrayList;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.pickme_nebula0.R;
import com.example.pickme_nebula0.db.DBManager;
import com.example.pickme_nebula0.db.FBStorageManager;
import com.example.pickme_nebula0.organizer.OrganizerRole;
import com.example.pickme_nebula0.user.User;
import com.example.pickme_nebula0.notification.NotificationCreationActivity;
import com.example.pickme_nebula0.organizer.activities.OrganizerEventParticipantsActivity;
import com.example.pickme_nebula0.DeviceManager;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

public class EventDetailActivity extends AppCompatActivity {

    // Initialize UI components
    private TextView eventDetailsTextView;
    private ImageView qrCodeImageView;
    private Button participantsButton;
    private Button msgEntrantsButton;
    private Button sampleEntrantsButton;
    private Button backButton;
    private Button viewPosterButton;
    private Button newPosterButton;
    ActivityResultLauncher<PickVisualMediaRequest> pickMedia;

    private String eventID;

    private DBManager dbManager;

    @Override
    protected void onResume(){
        super.onResume();
        renderPosterButtons();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set the content view
        setContentView(R.layout.activity_event_detail);

        // Initialize UI components
        backButton = findViewById(R.id.backButton);
        msgEntrantsButton = findViewById(R.id.button_ed_msgEntrants);
        sampleEntrantsButton = findViewById(R.id.button_sample_entrants);
        participantsButton = findViewById(R.id.participantsButton);
        eventDetailsTextView = findViewById(R.id.event_details_text_view);
        qrCodeImageView = findViewById(R.id.qr_code_image_view);
        viewPosterButton = findViewById(R.id.buttonViewPosterEventDetail);
        newPosterButton = findViewById(R.id.buttonNewPosterEventDetail);

        hideAllPosterButtons(); // generate these conditionally based on if there is a poster or not



        dbManager = new DBManager();

        // Retrieve eventID from intent
        eventID = getIntent().getStringExtra("eventID");

        if (eventID == null || eventID.trim().isEmpty()) {
            Toast.makeText(this, "Invalid Event ID.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Registers a photo picker activity launcher in single-select mode.
        pickMedia =
                registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                    // Callback is invoked after the user selects a media item or closes the
                    // photo picker.
                    if (uri != null) {
                        FBStorageManager.uploadPoster(uri,eventID,this);
                    } else {
                        Toast.makeText(this,"Could not upload new poster.",Toast.LENGTH_SHORT).show();
                    }
                });

        // Check if event has already sampled entrants
        OrganizerRole.sampledEntrantsExist(eventID, () -> {
            // rename text to resample entrants
            sampleEntrantsButton.setText("Resample Entrants");
        }, () -> {});

        // Set up button listeners
        setupButtons(eventID);

        // Fetch and display event details
        fetchEventDetails(eventID);
    }

    /**
     * Sets up the button listeners.
     *
     * @param eventID The ID of the event.
     */
    private void setupButtons(String eventID) {
        // Back Button
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getOnBackPressedDispatcher().onBackPressed();
            }
        });

        // Message Entrants Button
        msgEntrantsButton.setOnClickListener(v -> {
            Intent intent = new Intent(EventDetailActivity.this, NotificationCreationActivity.class);
            intent.putExtra("eventID", eventID);
            startActivity(intent);
        });

        // Sample Entrants Button
        sampleEntrantsButton.setOnClickListener(v -> {
            OrganizerRole.sampledEntrantsExist(eventID, () -> {
                Log.d("EventDetailActivity", "RESAMPLING USERS");
                // sampled entrants exist
                // do resampling instead
                OrganizerRole.resampleAndSelectUsers(eventID, (resampledUsersObj) -> {
                    for (User user : ((ArrayList<User>) resampledUsersObj)) {
                        Log.d("EventDetailActivity", "resampled user "+user.getUserID());
                    }});
            }, () -> {
                Log.d("EventDetailActivity", "SAMPLING USERS");
                // do first sampling
                OrganizerRole.sampleAndSelectUsers(eventID, (selectedUsersObj) -> {
                    for (User user : ((ArrayList<User>) selectedUsersObj)) {
                        Log.d("EventDetailActivity", "sampled user "+user.getUserID());
                    }
                });
            });
        });

        // Participants Button
        participantsButton.setOnClickListener(view -> {
            Intent intent = new Intent(EventDetailActivity.this, OrganizerEventParticipantsActivity.class);
            intent.putExtra("eventID", eventID);
            startActivity(intent);
        });

        newPosterButton.setOnClickListener(view -> {
            openImagePicker();
        });

        viewPosterButton.setOnClickListener(view -> {
            showImagePopup();
        });

        renderPosterButtons();

    }

    private void openImagePicker() {
        pickMedia.launch(new PickVisualMediaRequest.Builder()
                .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                .build());
    }

    /**
     * Navigates to the specified activity with the eventID.
     *
     * @param targetActivity The activity to navigate to.
     */
    private void navigateTo(Class<?> targetActivity) {
        Intent intent = new Intent(EventDetailActivity.this, targetActivity);
        intent.putExtra("eventID", getIntent().getStringExtra("eventID"));
        startActivity(intent);
    }

    /**
     * Fetches event details from the database and updates the UI.
     *
     * @param eventID The ID of the event.
     */
    private void fetchEventDetails(String eventID) {
        dbManager.getEvent(eventID, eventObj -> {
            if (eventObj instanceof Event) {
                Event event = (Event) eventObj;
                runOnUiThread(() -> {
                    StringBuilder details = generateEventDetails(event);
                    eventDetailsTextView.setText(details.toString());
                    displayQRCode(event.getQrCodeData());
                });
            } else {
                runOnUiThread(() -> {
                    Toast.makeText(this, "Event not found.", Toast.LENGTH_SHORT).show();
                    finish();
                });
            }
        }, () -> runOnUiThread(() -> {
            Toast.makeText(this, "Failed to retrieve event data.", Toast.LENGTH_SHORT).show();
            finish();
        }));
    }

    /**
     * Generates the event details to display.
     * @param event The event object.
     * @return The event details.
     */
    private StringBuilder generateEventDetails(Event event) {
        StringBuilder details = new StringBuilder();
        details.append("Event Name: ").append(event.getEventName()).append("\n\n");
        details.append("Description: ").append(event.getEventDescription()).append("\n\n");
        details.append("Date: ").append(event.getEventDate().toString()).append("\n\n");
        details.append("Geolocation Required: ").append(event.getGeolocationRequired() ? "Yes" : "No").append("\n\n");

        if (event.getGeolocationRequired()) {
            details.append("Geolocation Max Distance: ").append(event.getGeolocationMaxDistance()).append(" km\n");
        }

        details.append("Waitlist Capacity Required: ").append(event.getWaitlistCapacityRequired() ? "Yes" : "No").append("\n");

        if (event.getWaitlistCapacityRequired()) {
            details.append("Waitlist Capacity: ").append(event.getWaitlistCapacity()).append("\n");
        }

        details.append("Number of Attendees: ").append(event.getNumberOfAttendees()).append("\n");

        return details;
    }

    /**
     * Displays the QR code from a Base64 string.
     *
     * @param qrBase64 The Base64-encoded QR code.
     */
    private void displayQRCode(String qrBase64) {
        if (qrBase64 == null || qrBase64.trim().isEmpty()) {
            Toast.makeText(this, "QR Code data is unavailable.", Toast.LENGTH_SHORT).show();
            qrCodeImageView.setVisibility(View.GONE);
            return;
        }

        try {
            byte[] decodedBytes = Base64.decode(qrBase64, Base64.DEFAULT);
            Bitmap qrBitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
            if (qrBitmap != null) {
                qrCodeImageView.setImageBitmap(qrBitmap);
                qrCodeImageView.setVisibility(View.VISIBLE);
            } else {
                Toast.makeText(this, "Failed to decode QR Code.", Toast.LENGTH_SHORT).show();
                qrCodeImageView.setVisibility(View.GONE);
            }
        } catch (IllegalArgumentException e) {
            Toast.makeText(this, "Invalid QR Code data.", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
            qrCodeImageView.setVisibility(View.GONE);
        }
    }

    private void showImagePopup() {
        // Create a Dialog
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_poster_viewer);

        // Find the ImageView and Button
        ImageView imageView = dialog.findViewById(R.id.imageViewPoster);
        Button closeButton = dialog.findViewById(R.id.buttonPosterViewClose);

        // Set the image resource
        FBStorageManager.retrievePosterUri(eventID,(uri)->{Picasso.get()
                .load(uri)
                .fit()
                .centerInside()
                .placeholder(R.drawable.ic_profile_placeholder)
                .error(R.drawable.ic_profile_placeholder)
                .into(imageView);
            dialog.show();
            },()->{Toast.makeText(this,"Could not retrieve poster",Toast.LENGTH_SHORT).show();});

        // Set close button action
        closeButton.setOnClickListener(v -> dialog.dismiss());

        // Show the dialog

        Toast.makeText(this,"showImagePopup",Toast.LENGTH_SHORT).show();
    }

    private void renderPosterButtons(){
        FBStorageManager.retrievePosterUri(eventID,this::showButtonsIfPoster,this::showButtonsIfNoPoster);
    }

    private void hideAllPosterButtons(){
        viewPosterButton.setVisibility(View.GONE);
        newPosterButton.setVisibility(View.GONE);

    }

    private void showButtonsIfNoPoster(){
        viewPosterButton.setVisibility(View.GONE);
        newPosterButton.setVisibility(View.VISIBLE);
    }

    private void showButtonsIfPoster(Uri uri){
        viewPosterButton.setVisibility(View.VISIBLE);
        newPosterButton.setVisibility(View.VISIBLE);
    }

}