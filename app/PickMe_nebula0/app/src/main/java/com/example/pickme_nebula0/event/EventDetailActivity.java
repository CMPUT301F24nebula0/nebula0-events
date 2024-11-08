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
    private Button joinWaitlistButton;
    private Button cancelWaitlistButton;
    private Button msgEntrantsButton;
    private Button backButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);

        // Initialize UI components
        backButton = findViewById(R.id.backButton);
        msgEntrantsButton = findViewById(R.id.button_ed_msgEntrants);
        joinWaitlistButton = findViewById(R.id.button_JoinWaitlist);
        cancelWaitlistButton = findViewById(R.id.button_CancelWaitlist);
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

        // Handle action extra
        String action = getIntent().getStringExtra("action");
        if ("scan".equals(action)) {
            msgEntrantsButton.setVisibility(View.GONE);
            joinWaitlistButton.setVisibility(View.VISIBLE);
            cancelWaitlistButton.setVisibility(View.VISIBLE);
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

        // Join Waitlist Button
        joinWaitlistButton.setOnClickListener(v -> joinWaitlist(eventID));

        // Cancel Waitlist Button
        // Modified to simply navigate back without performing any action
        cancelWaitlistButton.setOnClickListener(v -> onBackPressed());
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
                    checkUserWaitlistStatus(eventID, userID);
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

    /**
     * Handles joining the waitlist for an event.
     *
     * @param eventID The ID of the event.
     */
    private void joinWaitlist(String eventID) {
        String userID = DeviceManager.getDeviceId();
        if (userID == null || userID.trim().isEmpty()) {
            Toast.makeText(this, "User not authenticated.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Prepare data for Events -> eventID -> EventRegistrants -> userID
        DocumentReference eventRegistrantRef = db.collection("Events")
                .document(eventID)
                .collection("EventRegistrants")
                .document(userID);

        Map<String, Object> eventRegistrantData = new HashMap<>();
        eventRegistrantData.put("status", "WAITLISTED");

        // Prepare data for Users -> userID -> RegisteredEvents -> eventID
        DocumentReference userRegisteredEventRef = db.collection("Users")
                .document(userID)
                .collection("RegisteredEvents")
                .document(eventID);

        Map<String, Object> userRegisteredEventData = new HashMap<>();
        userRegisteredEventData.put("status", "WAITLISTED");

        // Perform both writes atomically
        db.runTransaction(transaction -> {
            transaction.set(eventRegistrantRef, eventRegistrantData, SetOptions.merge());
            transaction.set(userRegisteredEventRef, userRegisteredEventData, SetOptions.merge());
            return null;
        }).addOnSuccessListener(aVoid -> {
            Toast.makeText(this, "Successfully joined the waitlist.", Toast.LENGTH_SHORT).show();
            joinWaitlistButton.setVisibility(View.GONE);
            cancelWaitlistButton.setVisibility(View.VISIBLE);
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Failed to join the waitlist: " + e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        });
    }

    /**
     * Checks the user's current waitlist status and updates button visibility accordingly.
     *
     * @param eventID The ID of the event.
     * @param userID  The ID of the user.
     */
    private void checkUserWaitlistStatus(String eventID, String userID) {
        if (userID == null || userID.trim().isEmpty()) {
            // User not authenticated; hide waitlist buttons
            joinWaitlistButton.setVisibility(View.GONE);
            cancelWaitlistButton.setVisibility(View.GONE);
            return;
        }

        // Reference to Events -> eventID -> EventRegistrants -> userID
        DocumentReference eventRegistrantRef = db.collection("Events")
                .document(eventID)
                .collection("EventRegistrants")
                .document(userID);

        eventRegistrantRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String status = documentSnapshot.getString("status");
                if ("WAITLISTED".equals(status)) {
                    joinWaitlistButton.setVisibility(View.GONE);
                    cancelWaitlistButton.setVisibility(View.VISIBLE);
                } else {
                    joinWaitlistButton.setVisibility(View.VISIBLE);
                    cancelWaitlistButton.setVisibility(View.GONE);
                }
            } else {
                joinWaitlistButton.setVisibility(View.VISIBLE);
                cancelWaitlistButton.setVisibility(View.GONE);
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Failed to check waitlist status: " + e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        });
    }
}