package com.example.pickme_nebula0.event;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.pickme_nebula0.DeviceManager;
import com.example.pickme_nebula0.R;
import com.example.pickme_nebula0.db.DBManager;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

/**
 * This activity show the user event details and conditionally rendered elements based on their
 * registration status in this event.
 *
 * @author Stephine Yearley
 */
public class EventDetailUserActivity extends AppCompatActivity {
    private String eventID;
    private final String userID = DeviceManager.getDeviceId();
    private DBManager dbManager;

    // main UI components
    private Button unregisterButton, backButton, registerButton;
    private TextView eventDetailsTextView, userStatusTextView;

    // notification UI components
    private Button acceptButton, declineButton;
    private TextView notificationMessage;
    private LinearLayout notificationLayout;

    private FirebaseFirestore db;

    private boolean fromQR;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set the content view
        setContentView(R.layout.activity_event_detail_user);

        db = FirebaseFirestore.getInstance();
        dbManager = new DBManager();

        // Check if user is coming from QR code
        fromQR = getIntent().getBooleanExtra("fromQR",false);

        // Retrieve eventID from intent, go back if we fail to get valid eventID
        eventID = getIntent().getStringExtra("eventID");


        if (eventID == null || eventID.isEmpty()) {
            Toast.makeText(this, "Invalid Event ID.", Toast.LENGTH_SHORT).show();
            finish();
        }

        // link main components
        backButton = findViewById(R.id.button_edu_back);
        unregisterButton = findViewById(R.id.button_edu_unregister);
        eventDetailsTextView = findViewById(R.id.textView_edu_details);
        userStatusTextView = findViewById(R.id.textView_edu_status);
        registerButton = findViewById(R.id.button_edu_reg);

        // link notification components
        notificationLayout = findViewById(R.id.notification_layout);
        acceptButton = findViewById(R.id.entrant_event_notification_accept);
        declineButton = findViewById(R.id.entrant_event_notification_decline);
        notificationMessage = findViewById(R.id.entrant_event_notification_message);

        // Initially set main buttons invisible (may take a second to query DB and update visibility)
        unregisterButton.setVisibility(View.GONE);
        registerButton.setVisibility(View.GONE);

        // initially set notification invisible, but components to visible
        notificationLayout.setVisibility(View.GONE);
        acceptButton.setVisibility(View.VISIBLE);
        declineButton.setVisibility(View.VISIBLE);
        notificationMessage.setVisibility(View.VISIBLE);

        // when back button clicked, finish activity
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // when register button clicked, user joins waitlist
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                joinWaitlist(eventID, () -> { fromQR = false; renderAll(); });
            }
        });

        // when unregister button clicked, user status set to canceled
        unregisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO - we can change this later to fully remove them if we don't want a user of leaves of their own volition CANCELED
                dbManager.setRegistrantStatus(eventID,userID,DBManager.RegistrantStatus.CANCELED);
                renderUserStatus();
            }
        });

        // when accept button clicked, user status set to confirmed
        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbManager.setRegistrantStatus( eventID, userID, DBManager.RegistrantStatus.CONFIRMED);
                renderUserStatus(); // status has changed so re-render
            }
        });

        // when decline button clicked, user status set to canceled
        declineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO - we can change this later to fully remove them if we don't want a user of leaves of their own volition CANCELLED
                dbManager.setRegistrantStatus(eventID,userID,DBManager.RegistrantStatus.CANCELLED);
                renderUserStatus();
            }
        });

        unregisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO - we can change this later to fully remove them if we don't want a user of leaves of their own volition CANCELLED
                dbManager.setRegistrantStatus(eventID,userID,DBManager.RegistrantStatus.CANCELLED);
                renderUserStatus();
            }
        });

        // Populate screen based on event info and user's status as registrant
        renderAll();
    }

    @Override
    public void onResume(){
        super.onResume();
        renderAll();
    }

    /**
     * Renders information about the event, unaffected by user's status
     */
    private void renderEventInfo(){
        dbManager.getEvent(eventID, eventObj -> {
            if (eventObj instanceof Event) {
                Event event = (Event) eventObj;
                runOnUiThread(() -> {
                    StringBuilder details = generateEventDetails(event);
                    eventDetailsTextView.setText(details.toString());
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

    private StringBuilder generateEventDetails(Event event) {
        StringBuilder details = new StringBuilder();
        details.append("Event Name: ").append(event.getEventName()).append("\n\n");
        details.append("Description: ").append(event.getEventDescription()).append("\n\n");
        details.append("Date: ").append(event.getEventDate().toString()).append("\n\n");
        return details;
    }

    /**
     * Renders all information about event and user status
     * @see DBManager
     */
    private void renderAll(){
        renderEventInfo();
        if(fromQR){
            registerButton.setVisibility(View.VISIBLE);
            userStatusTextView.setText("Unregistered");
            // TODO - we should check if the user has already registered
        } else {
            dbManager.getUserStatusString(userID,eventID,(status)->{renderBasedOnUserStatus(status.toString());},()->{});
        }
    }

    /**
     * Renders information related to the users status
     * @see DBManager
     */
    private void renderUserStatus(){
        dbManager.getUserStatusString(userID,eventID, (status)->{renderBasedOnUserStatus(status.toString());},()->{});
    }

    /**
     * Queries database in order to render information based on the users registration status
     * @param status string representing users status, should perfectly match one of DBManger.RegistrantStatus enums
     * @see DBManager
     */
    private void renderBasedOnUserStatus(String status){
        // show status
        userStatusTextView.setText(status);

        // user cannot register (assumption: they have already registered)
        registerButton.setVisibility(View.GONE);

        // Toast to show user this is running?
        Toast.makeText(EventDetailUserActivity.this,status,Toast.LENGTH_SHORT);

        // user is waitlisted or confirmed:
        // - no notification sent to them
        // - can unregister from event
        if (status.equalsIgnoreCase("WAITLISTED") || status.equalsIgnoreCase("CONFIRMED")){
            notificationLayout.setVisibility(View.GONE);
            unregisterButton.setVisibility(View.VISIBLE);

        // user is selected:
        // - notification sent to them: accept or decline
        } else if (status.equalsIgnoreCase("SELECTED")) {
            showNotificationChoice();
            unregisterButton.setVisibility(View.GONE);

        // user is canceled:
        // - canceled their registration in the event
        } else if (status.equalsIgnoreCase("CANCELLED")){
            notificationLayout.setVisibility(View.GONE);
            unregisterButton.setVisibility(View.GONE);
        }
    }

    /**
     * Shows a notification allowing user to accept or decline
     */
    private void showNotificationChoice() {
        // set text to say user is accepted
        String message = "You have been accepted!";
        notificationMessage.setText(message);
        // show notification layout
        notificationLayout.setVisibility(View.VISIBLE);
    }

    /**
     * Handles joining the waitlist for an event.
     *
     * @param eventID The ID of the event.
     */
    private void joinWaitlist(String eventID, DBManager.Void2VoidCallback whenDone) {
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
            whenDone.run();
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Failed to join the waitlist: " + e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        });
    }
}
