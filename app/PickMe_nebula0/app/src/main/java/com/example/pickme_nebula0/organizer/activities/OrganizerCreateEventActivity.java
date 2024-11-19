package com.example.pickme_nebula0.organizer.activities;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;
import android.app.DatePickerDialog;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.pickme_nebula0.DeviceManager;
import com.example.pickme_nebula0.R;
import com.example.pickme_nebula0.db.DBManager;
import com.example.pickme_nebula0.event.Event;
import com.example.pickme_nebula0.organizer.exceptions.OrganizerExceptions;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class OrganizerCreateEventActivity extends AppCompatActivity {

    // Database Manager
    private DBManager dbManager;

    // Screen component variables
    private EditText eventNameField;
    private EditText eventDescriptionField;
    private EditText eventDateField;
    private Switch geolocationRequirementSwitch;
    private EditText geolocationRequirementField;
    private Switch waitlistCapacityRequiredSwitch;
    private EditText waitlistCapacityField;
    private EditText numberOfAttendeesField;
    private Button eventCreationSubmitButton;
    private Button eventCreationCancelButton;

    // User input variables
    String eventName;
    String eventDescription;
    String eventDate;
    boolean geolocationRequired;
    String geolocationRequirement;
    boolean waitlistCapacityRequired;
    String waitlistCapacity;
    String numberOfAttendees;

    // Variables needed for Backend handling
    Date eventDateObj;
    int geolocationMaxDistance;
    int waitlistMaxCapacity;
    int maxNumberOfAttendees;
    boolean eventCreated;

    // Device ID
    String deviceID;

    // Event object
    Event event;

    // Exceptions
    OrganizerExceptions organizerExceptions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize database manager
        dbManager = new DBManager();

        // attach to screen component xml
        setContentView(R.layout.activity_organizer_create_event);

        // Initialize screen components
        eventNameField = findViewById(R.id.event_name_field);
        eventDescriptionField = findViewById(R.id.event_description_field);
        eventDateField = findViewById(R.id.event_date_field);
        geolocationRequirementSwitch = findViewById(R.id.geolocation_requirement_switch);
        geolocationRequirementField = findViewById(R.id.geolocation_requirement_field);
        waitlistCapacityRequiredSwitch = findViewById(R.id.waitlist_capacity_required_switch);
        waitlistCapacityField = findViewById(R.id.waitlist_capacity_field);
        numberOfAttendeesField = findViewById(R.id.number_of_attendees_field);
        eventCreationSubmitButton = findViewById(R.id.event_creation_submit_button);
        eventCreationCancelButton = findViewById(R.id.event_creation_cancel_button);

        // Get device ID
        deviceID = DeviceManager.getDeviceId();

        // Initialize exceptions
        organizerExceptions = new OrganizerExceptions();

        /* TODO:
        @Author: Sina Shaban
        I just made a bit change in the way that we select the date there was a problem regarding dependency of Espresso picker
        and the firebase ; Therefore I made a changes for the half way summation that lets as write the date in the format
        (YYYY-MM-DD) instead of picking from a calender , I will also put a note so we should modify it for our final submission
         */

        // DatePicker logic
        eventDateField.setFocusable(false);
        eventDateField.setOnClickListener(v -> showDatePickerDialog());

        // Switch logic to enable/disable fields
        geolocationRequirementSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SwitchToggleEditText(geolocationRequirementField, isChecked);
        });
        waitlistCapacityRequiredSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SwitchToggleEditText(waitlistCapacityField, isChecked);
        });

        // Initially disable fields since both switches are off at the beginning
        waitlistCapacityField.setEnabled(false);
        geolocationRequirementField.setEnabled(false);

        // Submit button logic
        eventCreationSubmitButton.setOnClickListener(v -> {
            eventName = eventNameField.getText().toString();
            eventDescription = eventDescriptionField.getText().toString();
            eventDate = eventDateField.getText().toString();
            geolocationRequired = geolocationRequirementSwitch.isChecked();
            geolocationRequirement = geolocationRequirementField.getText().toString();
            waitlistCapacityRequired = waitlistCapacityRequiredSwitch.isChecked();
            waitlistCapacity = waitlistCapacityField.getText().toString();
            numberOfAttendees = numberOfAttendeesField.getText().toString();

            // Validate user input
            if (!organizerExceptions.validateEventCreationUserInput(this, deviceID, eventName, eventDescription, eventDate,
                    geolocationRequired, geolocationRequirement,
                    waitlistCapacityRequired, waitlistCapacity, numberOfAttendees)) {
                // If user input is invalid, don't add event
                return;
            }

            // switch & input logic
            geolocationMaxDistance = geolocationRequired ? Integer.parseInt(geolocationRequirement) : -1;
            waitlistMaxCapacity = waitlistCapacityRequired ? Integer.parseInt(waitlistCapacity) : -1;

            // variables event creation
            eventDateObj = parseDate(eventDate);
            maxNumberOfAttendees = Integer.parseInt(numberOfAttendees);

            try {
                // Try creating event object
                event = new Event(eventName, eventDescription, eventDateObj,
                        geolocationRequired, geolocationMaxDistance, waitlistCapacityRequired,
                        waitlistMaxCapacity, maxNumberOfAttendees);
                eventCreated = true;
            } catch (Exception e) {
                Log.d("OrganizerCreateEventActivity", "Event creation failed with error: " + e.getMessage());
                Toast.makeText(this, "Event creation failed. Please try again.", Toast.LENGTH_LONG).show();
                eventCreated = false;
                return;
            }

            // Add event to database
            dbManager.addEvent(event);
            Toast.makeText(this, "Event created successfully with QR Code!", Toast.LENGTH_LONG).show();

            finish();
        });

        // Check if any fields are filled before cancelling
        eventCreationCancelButton.setOnClickListener(v -> {
            if (isAnyFieldFilled(eventNameField, eventDescriptionField, eventDateField, geolocationRequirementField, waitlistCapacityField, numberOfAttendeesField)) {
                new AlertDialog.Builder(OrganizerCreateEventActivity.this)
                        .setTitle("Cancel Event Creation")
                        .setMessage("Are you sure you want to cancel? All input will be lost.")
                        .setPositiveButton("Yes", (dialog, which) -> finish())
                        .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                        .show();
            } else {
                finish(); // Close the activity directly if no fields are filled
            }
        });
    }

    // the Date Picker Dialog
    private void showDatePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year, month, dayOfMonth) -> {
                    calendar.set(year, month, dayOfMonth);
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    eventDateField.setText(dateFormat.format(calendar.getTime()));
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000); // Prevent past dates
        datePickerDialog.show();
    }

    // switch logic to enable/disable fields
    private void SwitchToggleEditText(EditText editText, boolean isChecked)
    {
        if (isChecked) {
            editText.setEnabled(true);
        } else {
            editText.setEnabled(false);
            editText.setText("");
        }
    }

    // check if any fields are filled (before cancelling)
    private boolean isAnyFieldFilled(EditText... fields) {
        for (EditText field : fields) {
            if (!field.getText().toString().isEmpty()) {
                return true;
            }
        }
        return false;
    }

    private static Date parseDate(String dateString) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        try {
            return formatter.parse(dateString);
        } catch (ParseException e) {
            Log.d("OrganizerCreateEventActivity", "parseDate failed with error: " + e.getMessage());
            return null; // Handle exception as needed
        }
    }
}