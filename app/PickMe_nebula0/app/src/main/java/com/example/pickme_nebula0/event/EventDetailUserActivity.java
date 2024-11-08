package com.example.pickme_nebula0.event;

import android.bluetooth.BluetoothClass;
import android.os.Bundle;
import android.util.Log;
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
    private String eventName;
    private final String userID = DeviceManager.getDeviceId();
    private DBManager dbManager;
    private Button unregBtn, backBtn, regBtn;
    private TextView eventDetailsTextView,userStatusTextView;
    private FirebaseFirestore db;

    private boolean fromQR;

    // for notification specifically
    private LinearLayout noteLayout;
    private TextView noteMessage;
    private Button noteAcceptBtn, noteDeclineBtn;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail_user);

        db = FirebaseFirestore.getInstance();
        dbManager = new DBManager();

        fromQR =getIntent().getBooleanExtra("fromQR",false);

        // Retrieve eventID from intent, go back if we fail to get valid eventID
        eventID = getIntent().getStringExtra("eventID");
        if (eventID == null || eventID.isEmpty()) {
            Toast.makeText(this, "Invalid Event ID.", Toast.LENGTH_SHORT).show();
            finish();
        }

        // Retrieve event Name from intent
        eventName = getIntent().getStringExtra("eventName");
        if (eventName == null || eventName.isEmpty()) {
            Toast.makeText(this, "Invalid Event Name.", Toast.LENGTH_SHORT).show();
            finish();
        }


        // Link components
        backBtn = findViewById(R.id.button_edu_back);
        unregBtn = findViewById(R.id.button_edu_unregister);
        regBtn = findViewById(R.id.button_edu_reg);
        eventDetailsTextView = findViewById(R.id.textView_event_details);
        userStatusTextView = findViewById(R.id.textView_user_status);

        // components from possible notification
        noteLayout = findViewById(R.id.notification_layout);
        noteMessage = findViewById(R.id.event_notification_message);
        noteAcceptBtn = findViewById(R.id.event_notification_accept);
        noteDeclineBtn = findViewById(R.id.event_notification_decline);

        // notification is visible, but layout may not be
        noteMessage.setVisibility(View.GONE);
        noteAcceptBtn.setVisibility(View.GONE);
        noteDeclineBtn.setVisibility(View.GONE);

        // Initially set main buttons invisible (may take a second to query DB and update visibility)
        unregBtn.setVisibility(View.GONE);
        regBtn.setVisibility(View.GONE);


        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        unregBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO - we can change this later to fully remove them if we don't want a user of leaves of their own volition CANCELED
                dbManager.setRegistrantStatus(eventID,userID,DBManager.RegistrantStatus.CANCELED);
                renderUserStatus();
            }
        });

        regBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                joinWaitlist(eventID,()->{fromQR = false;renderAll();});
            }
        });

        noteAcceptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbManager.setRegistrantStatus(eventID,userID,DBManager.RegistrantStatus.CONFIRMED);
                renderUserStatus(); // status has changed so re-render
            }
        });

        noteDeclineBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO - we can change this later to fully remove them if we don't want a user of leaves of their own volition CANCELED
                dbManager.setRegistrantStatus(eventID,userID,DBManager.RegistrantStatus.CANCELED);
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
                    StringBuilder details = new StringBuilder();
                    details.append("Event Name: ").append(event.getEventName()).append("\n\n");
                    details.append("Description: ").append(event.getEventDescription()).append("\n\n");
                    details.append("Date: ").append(event.getEventDate().toString()).append("\n\n");
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

    /**
     * Renders all information about event and user status
     * @see DBManager
     */
    private void renderAll(){
        renderEventInfo();
        if(fromQR){
            regBtn.setVisibility(View.VISIBLE);
            userStatusTextView.setText("Unregistered");
            // TODO - we should check if the user has already registered
        }else{
            dbManager.getUserStatusString(userID,eventID,(status)->{renderBasedOnUserStatus(status.toString());},()->{});
        }
    }

    /**
     * Renders information related to the users status
     * @see DBManager
     */
    private void renderUserStatus(){
        dbManager.getUserStatusString(userID,eventID,(status)->{renderBasedOnUserStatus(status.toString());},()->{});
    }

    /**
     * Queries database in order to render information based on the users registration status
     * @param status string representing users status, should perfectly match one of DBManger.RegistrantStatus enums
     * @see DBManager
     */
    private void renderBasedOnUserStatus(String status){
        userStatusTextView.setText(status);
        regBtn.setVisibility(View.GONE);

        Toast.makeText(EventDetailUserActivity.this,status,Toast.LENGTH_SHORT);

        if (status.equalsIgnoreCase("WAITLISTED") || status.equalsIgnoreCase("CONFIRMED")){
            noteAcceptBtn.setVisibility(View.GONE);
            noteDeclineBtn.setVisibility(View.GONE);
            unregBtn.setVisibility(View.VISIBLE);
        } else if (status.equalsIgnoreCase("SELECTED")) {
            noteAcceptBtn.setVisibility(View.VISIBLE);
            noteDeclineBtn.setVisibility(View.VISIBLE);
            unregBtn.setVisibility(View.GONE);
        } else if (status.equalsIgnoreCase("CANCELED")){
            noteAcceptBtn.setVisibility(View.GONE);
            noteDeclineBtn.setVisibility(View.GONE);
            unregBtn.setVisibility(View.GONE);
        }
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
