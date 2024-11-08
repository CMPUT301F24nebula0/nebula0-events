package com.example.pickme_nebula0.event;

import android.bluetooth.BluetoothClass;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.pickme_nebula0.DeviceManager;
import com.example.pickme_nebula0.R;
import com.example.pickme_nebula0.db.DBManager;

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
    private Button acceptBtn, declineBtn, unregBtn,backBtn;
    private TextView eventDetailsTextView,userStatusTextView;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail_user);

        dbManager = new DBManager();

        // Retrieve eventID from intent, go back if we fail to get valid eventID
        eventID = getIntent().getStringExtra("eventID");
        if (eventID == null || eventID.isEmpty()) {
            Toast.makeText(this, "Invalid Event ID.", Toast.LENGTH_SHORT).show();
            finish();
        }

        // Link components
        final Button backBtn = findViewById(R.id.button_edu_back);
        acceptBtn = findViewById(R.id.button_edu_accept);
        declineBtn = findViewById(R.id.button_edu_decline);
        unregBtn = findViewById(R.id.button_edu_unregister);
        eventDetailsTextView = findViewById(R.id.textView_edu_details);
        userStatusTextView = findViewById(R.id.textView_edu_status);

        // Initially set all buttons invisible (may take a second to query DB and update visibility)
        acceptBtn.setVisibility(View.GONE);
        declineBtn.setVisibility(View.GONE);
        unregBtn.setVisibility(View.GONE);

        // Register button behavior
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        acceptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbManager.setRegistrantStatus(eventID,userID,DBManager.RegistrantStatus.CONFIRMED);
                renderUserStatus(); // status has changed so re-render
            }
        });

        declineBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO - we can change this later to fully remove them if we don't want a user of leaves of their own volition CANCELED
                dbManager.setRegistrantStatus(eventID,userID,DBManager.RegistrantStatus.CANCELED);
                renderUserStatus();
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
        dbManager.getUserStatusString(userID,eventID,(status)->{renderBasedOnUserStatus(status.toString());},()->{});
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

        Toast.makeText(EventDetailUserActivity.this,status,Toast.LENGTH_SHORT);

        if (status.equalsIgnoreCase("WAITLISTED") || status.equalsIgnoreCase("CONFIRMED")){
            acceptBtn.setVisibility(View.GONE);
            declineBtn.setVisibility(View.GONE);
            unregBtn.setVisibility(View.VISIBLE);
        } else if (status.equalsIgnoreCase("SELECTED")) {
            acceptBtn.setVisibility(View.VISIBLE);
            declineBtn.setVisibility(View.VISIBLE);
            unregBtn.setVisibility(View.GONE);
        } else if (status.equalsIgnoreCase("CANCELED")){
            acceptBtn.setVisibility(View.GONE);
            declineBtn.setVisibility(View.GONE);
            unregBtn.setVisibility(View.GONE);
        }
    }
}
