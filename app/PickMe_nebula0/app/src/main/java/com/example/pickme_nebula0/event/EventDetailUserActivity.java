package com.example.pickme_nebula0.event;

import android.Manifest;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.pickme_nebula0.DeviceManager;
import com.example.pickme_nebula0.GeolocationManager;
import com.example.pickme_nebula0.R;
import com.example.pickme_nebula0.SharedDialogue;
import com.example.pickme_nebula0.db.DBManager;
import com.example.pickme_nebula0.start.activities.HomePageActivity;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
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
    private Button unregisterButton, backButton, registerButton, posterViewButton;
    private TextView eventDetailsTextView, userStatusTextView;


    // START TY
    private GeolocationManager gm;
    private ActivityResultLauncher<String[]> locationPermissionLauncher;
    // END TY

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
        posterViewButton = findViewById(R.id.buttonPosterView);

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

        posterViewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedDialogue.displayPosterPopup(EventDetailUserActivity.this,eventID);
            }
        });

        // when back button clicked, finish activity
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check if the event requires geolocation
                db.collection("Events")
                        .document(eventID)
                        .get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                // Ensure document exists and fetch the "geolocationRequired" field
                                DocumentSnapshot document = task.getResult();
                                if (document != null && document.exists()) {
                                    Boolean requiresGeolocation = document.getBoolean("geolocationRequired");

                                    // Handle geolocation requirement
                                    if (requiresGeolocation != null && requiresGeolocation) {
                                        // Geolocation required: Show dialog to notify the user
                                        showGeolocationRequiredDialog(() -> {
                                            // User agrees: Join waitlist
                                            joinWaitlist(eventID, () -> {
                                                fromQR = false;
                                                renderAll();
                                            });

                                            // Check location permissions and save geolocation if needed
                                            if (gm.hasLocationPermission()) {
                                                saveGeolocationData(userID, eventID);
                                            } else {
                                                // Request location permissions
                                                locationPermissionLauncher.launch(new String[]{
                                                        Manifest.permission.ACCESS_FINE_LOCATION,
                                                        Manifest.permission.ACCESS_COARSE_LOCATION
                                                });
                                            }
                                        });
                                    } else {
                                        // Geolocation not required: Proceed with joining the waitlist
                                        joinWaitlist(eventID, () -> {
                                            fromQR = false;
                                            renderAll();
                                        });
                                    }
                                } else {
                                    // Document does not exist
                                    Toast.makeText(EventDetailUserActivity.this, "Event not found.", Toast.LENGTH_LONG).show();
                                }
                            } else {
                                // Firestore query failed
                                Exception e = task.getException();
                                Toast.makeText(EventDetailUserActivity.this, "Failed to fetch event details: " + (e != null ? e.getMessage() : "Unknown error"), Toast.LENGTH_LONG).show();
                            }
                        });
            }
        });

        // when unregister button clicked, user status set to canceled
        unregisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO - we can change this later to fully remove them if we don't want a user of leaves of their own volition CANCELLED
                dbManager.setRegistrantStatus(eventID,userID,DBManager.RegistrantStatus.CANCELLED);
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
        // START TY
        gm = new GeolocationManager(this);
        locationPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestMultiplePermissions(),
                result -> {
                    boolean fineLocationGranted = result.getOrDefault(
                            Manifest.permission.ACCESS_FINE_LOCATION, false);
                    boolean coarseLocationGranted = result.getOrDefault(
                            Manifest.permission.ACCESS_COARSE_LOCATION, false);

                    if (fineLocationGranted || coarseLocationGranted) {
                        Toast.makeText(this, "Location permission granted.", Toast.LENGTH_SHORT).show();
                        // Now, proceed to save geolocation
                        saveGeolocationData(userID, eventID);
                    } else {
                        Toast.makeText(this, "Location permission denied.", Toast.LENGTH_SHORT).show();
                    }
                }
        );
        // END TY

        // Populate screen based on event info and user's status as registrant
        renderAll();
    }

    @Override
    public void onResume(){
        super.onResume();
        renderAll();
    }

    // START TY
    private void saveGeolocationData(String userID, String eventID) {
//        String userID = "c5e9e56f41572d06"; // Replace with actual userID
//        String eventID = "ouyj7XRzfSIqFRAXqnQ5"; // Replace with actual eventID

        try {
            gm.saveGeolocation(userID, eventID, success -> {
                if (success) {
                } else {
                }
            });
        } catch (SecurityException e) {
            Toast.makeText(this, "Location permission not granted.", Toast.LENGTH_SHORT).show();
        }
    }
    // END TY
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

        // NOTE: could replace this with addRegistrantToWaitlist from DBManager (including callback functions)
        // however, DBManager does not perform atomic writes so we can add that in later and then consider
        // calling that function

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
    /**
     * Shows a dialog to notify the user that the event requires geolocation.
     *
     * @param onConfirm Callback to execute if the user agrees to continue.
     */
    private void showGeolocationRequiredDialog(Runnable onConfirm) {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Geolocation Required")
                .setMessage("This event requires geolocation information to join the waitlist. Do you want to proceed?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    // User agrees to proceed
                    onConfirm.run();
                })
                .setNegativeButton("No", (dialog, which) -> {
                    // User cancels
                    dialog.dismiss();
                })
                .setCancelable(true)
                .show();
    }

}