package com.example.pickme_nebula0.event;

import android.Manifest;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
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
import com.example.pickme_nebula0.facility.Facility;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

/**
 * EventDetailUserActivity
 *
 * This activity allows a user to view event details and interact with the event based on their registration status.
 *
 * Key Features:
 * - Displays event details including facilities and registration status.
 * - Allows registration and unregistration for events.
 * - Handles geolocation-based registration if required by the event.
 * - Integrates with Firestore for event and user data.
 * - Supports QR code-based navigation to events.
 * - Provides a notification interface for selected users.
 *
 * Dependencies:
 * - `DBManager` for Firestore operations.
 * - `GeolocationManager` for handling location permissions and geolocation data.
 * - `SharedDialogue` for reusable UI elements like poster popups.
 *
 * Author: Stephine Yearley
 */
public class EventDetailUserActivity extends AppCompatActivity {
    private String eventID;
    private final String userID = DeviceManager.getDeviceId();
    private DBManager dbManager;

    // main UI components
    private Button unregisterButton, backButton, registerButton, posterViewButton;
    private TextView eventDetailsTextView, userStatusTextView, facilityDetailsTextView;


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

    /**
     * Called when the activity is created.
     *
     * - Initializes UI components and Firebase references.
     * - Retrieves event information based on the provided event ID.
     * - Sets up buttons and handlers for user actions.
     * - Configures geolocation permissions if required by the event.
     *
     * @param savedInstanceState Saved state for restoring the activity.
     */
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
        facilityDetailsTextView = findViewById(R.id.textFacilityDetails);
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

        // this may take a split second to query db, so set invisible at first
        facilityDetailsTextView.setVisibility(View.INVISIBLE); // show details or default (warning)
        Animation buttonClickAnimation = AnimationUtils.loadAnimation(this, R.anim.button_click_animation);


        posterViewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(buttonClickAnimation);

                v.postDelayed(() -> SharedDialogue.displayPosterPopup(EventDetailUserActivity.this, eventID), 200);
            }
        });

        // when back button clicked, finish activity
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(buttonClickAnimation);

                // Perform action after animation
                v.postDelayed(() -> finish(), 200);            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(buttonClickAnimation);
                EventManager.checkWaitlistFull(eventID, (eventDocSnapshotObj) -> {
                    DocumentSnapshot eventDocSnapshot = (DocumentSnapshot) eventDocSnapshotObj;

                // waitlist not full
                // TODO: refactor this

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
                                                saveGeolocationData(userID, eventID);

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

                // waitlist capacity full
                }, () -> {
                    Toast.makeText(EventDetailUserActivity.this, "Waitlist is full.", Toast.LENGTH_SHORT).show();

                });

            }
        });

        // when unregister button clicked, user status set to canceled
        unregisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO - we can change this later to fully remove them if we don't want a user of leaves of their own volition CANCELLED
                v.startAnimation(buttonClickAnimation);

                // Perform action after animation
                v.postDelayed(() -> {
                    dbManager.setRegistrantStatus(eventID, userID, DBManager.RegistrantStatus.CANCELLED);
                    renderUserStatus();
                }, 200);
            }
        });

        // when accept button clicked, user status set to confirmed
        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start animation
                v.startAnimation(buttonClickAnimation);

                // Perform action after animation
                v.postDelayed(() -> {
                    dbManager.setRegistrantStatus(eventID, userID, DBManager.RegistrantStatus.CONFIRMED);
                    renderUserStatus();
                }, 200); // status has changed so re-render
            }
        });

        // when decline button clicked, user status set to canceled
        declineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO - we can change this later to fully remove them if we don't want a user of leaves of their own volition CANCELLED
                // Start animation
                v.startAnimation(buttonClickAnimation);

                // Perform action after animation
                v.postDelayed(() -> {
                    dbManager.setRegistrantStatus(eventID, userID, DBManager.RegistrantStatus.CANCELLED);
                    renderUserStatus();
                }, 200);
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

    /**
     * Called when the activity resumes.
     *
     * Re-renders the event and user details to ensure the UI is updated.
     */
    @Override
    public void onResume(){
        super.onResume();
        renderAll();
    }

    /**
     * Save's user's geolocation data
     * @param userID ID of user of interest
     * @param eventID ID of event of interest
     */
    private void saveGeolocationData(String userID, String eventID) {
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

    /**
     * Builds string detailing event info
     * @param event event we are retrieving info for
     * @return a string containing event details
     */
    private StringBuilder generateEventDetails(Event event) {
        StringBuilder details = new StringBuilder();
        String dateString = event.getEventDate().toString();
        int firstColonLoc = dateString.indexOf(":");
        String niceDate = dateString.substring(0,firstColonLoc-2);
        details.append("Name: ").append(event.getEventName()).append("\n");
        details.append("Description: ").append(event.getEventDescription()).append("\n");
        details.append("Date: ").append(niceDate).append("\n");
        renderFacilityInfo(event);
        return details;
    }

    /**
     * Retrieves, formats, and displays facility info
     * @param event event we are retrieving facility info for
     */
    private void renderFacilityInfo(Event event){
        dbManager.getFacility(event.getOrganizerID(),(obj)->{
            Facility f = (Facility) obj;
            String name = f.getName();
            String adr = f.getAddress();
            if (name == null || adr == null){
                return;
            }
            String facilityInfoString = String.format("Location:\n\t\t%s\n\t\t%s",name,adr);
            facilityDetailsTextView.setText(facilityInfoString);}
                ,()->{Toast.makeText(this,"Failed to get facility info",Toast.LENGTH_SHORT).show();});

        facilityDetailsTextView.setVisibility(View.VISIBLE); // show details or default (warning)
    }

    /**
     * Renders all information about event and user status
     * @see DBManager
     */
    private void renderAll(){
        renderEventInfo();
        if(fromQR){
            dbManager.getUserStatusString(userID,eventID,
                    (status)->{renderBasedOnUserStatus(status.toString());},
                    ()->{registerButton.setVisibility(View.VISIBLE);
                userStatusTextView.setText("Unregistered");});
        } else {
            renderUserStatus();
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