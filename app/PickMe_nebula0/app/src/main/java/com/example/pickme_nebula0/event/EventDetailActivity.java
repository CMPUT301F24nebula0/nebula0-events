package com.example.pickme_nebula0.event;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.pickme_nebula0.R;
import com.example.pickme_nebula0.db.DBManager;
import com.example.pickme_nebula0.notification.NotificationCreationActivity;
import com.example.pickme_nebula0.organizer.activities.OrganizerEventParticipantsActivity;
import com.example.pickme_nebula0.DeviceManager;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class EventDetailActivity extends AppCompatActivity {
    private FirebaseFirestore db;
    private TextView eventDetailsTextView;
    private ImageView qrCodeImageView;
    private DBManager dbManager;
    private Button participantsButton;
    private Button msgEntrantsButton;
    private Button backButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);

        // Initialize UI components
        backButton = findViewById(R.id.backButton);
        msgEntrantsButton = findViewById(R.id.button_ed_msgEntrants);
        participantsButton = findViewById(R.id.participantsButton);
        eventDetailsTextView = findViewById(R.id.event_details_text_view);
        qrCodeImageView = findViewById(R.id.qr_code_image_view);

        db = FirebaseFirestore.getInstance();
        dbManager = new DBManager();

        // Retrieve eventID from intent
        String eventID = getIntent().getStringExtra("eventID");

        if (eventID == null || eventID.trim().isEmpty()) {
            Toast.makeText(this, "Invalid Event ID.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

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
        backButton.setOnClickListener(v -> onBackPressed());

        // Message Entrants Button
        msgEntrantsButton.setOnClickListener(v -> {
            Intent intent = new Intent(EventDetailActivity.this, NotificationCreationActivity.class);
            intent.putExtra("eventID", eventID);
            startActivity(intent);
        });

        // Participants Button
        participantsButton.setOnClickListener(view -> navigateTo(OrganizerEventParticipantsActivity.class));

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
                    eventDetailsTextView.setText(details.toString());

                    displayQRCode(event.getQrCodeData());

                    // Update waitlist button visibility based on user's current status
                    String userID = DeviceManager.getDeviceId();
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
}