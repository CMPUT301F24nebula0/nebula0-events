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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.type.DateTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class OrganizerCreateEventActivity extends AppCompatActivity {

    private DBManager dbManager;
    private EditText eventDateField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        dbManager = new DBManager();
        super.onCreate(savedInstanceState);

        // attach to screen component xml
        setContentView(R.layout.activity_organizer_create_event);

        // components on screen
        EditText eventNameField = findViewById(R.id.event_name_field);
        EditText eventDescriptionField = findViewById(R.id.event_description_field);
        EditText eventDateField = findViewById(R.id.event_date_field);
        Switch geolocationRequirementSwitch = findViewById(R.id.geolocation_requirement_switch);
        EditText geolocationRequirementField = findViewById(R.id.geolocation_requirement_field);
        Switch waitlistCapacityRequiredSwitch = findViewById(R.id.waitlist_capacity_required_switch);
        EditText waitlistCapacityField = findViewById(R.id.waitlist_capacity_field);
        EditText numberOfAttendeesField = findViewById(R.id.number_of_attendees_field);
        Button eventCreationSubmitButton = findViewById(R.id.event_creation_submit_button);
        Button eventCreationCancelButton = findViewById(R.id.event_creation_cancel_button);

        // get deviceID as the foreignKey
        String deviceID = DeviceManager.getDeviceId();


/*
@Author: Sina Shaban
I just made a bit change in the way that we select the date there was a problem regarding dependency of Espresso picker
and the firebase ; Therefore I made a changes for the half way summation that lets as write the date in the format
(YYYY-MM-DD) instead of picking from a calender , I will also put a note so we should modify it for our final submission

 */

//        // DatePicker logic
//        eventDateField.setFocusable(false);
//        eventDateField.setOnClickListener(v -> showDatePickerDialog());

        // Geolocation Requirement Switch Logic
        geolocationRequirementSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // the geolocation field can be edited
                geolocationRequirementField.setEnabled(true);
            } else {
                geolocationRequirementField.setEnabled(false);
                geolocationRequirementField.setText("");
            }
        });

        // Waitlist Capacity Switch Logic
        waitlistCapacityRequiredSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // the waitlist capacity field can be edited
                waitlistCapacityField.setEnabled(true);
            } else {
                waitlistCapacityField.setEnabled(false);
                waitlistCapacityField.setText("");
            }
        });

        // Initially disable the geolocation requirement and waitlist capacity fields
        // since both switches are off initially
        waitlistCapacityField.setEnabled(false);
        geolocationRequirementField.setEnabled(false);


        // Submit button logic
        eventCreationSubmitButton.setOnClickListener(v -> {
            String eventName = eventNameField.getText().toString();
            String eventDescription = eventDescriptionField.getText().toString();
            String eventDate = eventDateField.getText().toString();
            boolean geolocationRequired = geolocationRequirementSwitch.isChecked();
            String geolocationRequirement = geolocationRequirementField.getText().toString();
            boolean waitlistCapacityRequired = waitlistCapacityRequiredSwitch.isChecked();
            String waitlistCapacity = waitlistCapacityField.getText().toString();
            String numberOfAttendees = numberOfAttendeesField.getText().toString();

            // Validate user input
            if (!validateEventCreationUserInput(deviceID, eventName, eventDescription, eventDate,
                    geolocationRequired, geolocationRequirement,
                    waitlistCapacityRequired, waitlistCapacity, numberOfAttendees)) {
                // If user input is invalid, don't add event
                return;
            }

            Date eventDateObj = parseDate(eventDate);

            // if geolocation requirement switch on
            int geolocationMaxDistance = -1;
            if (geolocationRequired) {
                // save geolocation requirement
                geolocationMaxDistance = Integer.parseInt(geolocationRequirement);
            }

            // if waitlist capacity requirement switch on
            int waitlistMaxCapacity = -1;
            if (waitlistCapacityRequired) {
                waitlistMaxCapacity = Integer.parseInt(waitlistCapacity);
            }

            int maxNumberOfAttendees = Integer.parseInt(numberOfAttendees);


            Event event = new Event(eventName, eventDescription, eventDateObj,
                    geolocationRequired, geolocationMaxDistance, waitlistCapacityRequired,
                    waitlistMaxCapacity, maxNumberOfAttendees);

            dbManager.addEvent(event);
            Toast.makeText(this, "Event created successfully with QR Code!", Toast.LENGTH_LONG).show();

            finish();
        });

        // Cancel button logic
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

    // the Date Picker
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

    // validating input
    // TODO: check and test if there are more conditions needed to check
    private boolean validateEventCreationUserInput(
            String deviceID,
            String eventName,
            String eventDescription,
            String eventDate,
            boolean geolocationRequired,
            String geolocationRequirement,
            boolean waitlistCapacityRequired,
            String waitlistCapacity,
            String numberOfAttendees
    ) {
        StringBuilder warningMessage = new StringBuilder("The event cannot be created for the following reason(s):");
        boolean valid = true;

        if (deviceID == null || deviceID.isEmpty()) {
            warningMessage.append("\n- The deviceID could not be retrieved.");
            valid = false;
        }
        if (eventName.isEmpty()) {
            warningMessage.append("\n- The event name cannot be blank.");
            valid = false;
        }
        if (eventDescription.isEmpty()) {
            warningMessage.append("\n- The event description cannot be blank.");
            valid = false;
        }
        if (eventDate.isEmpty()) {
            warningMessage.append("\n- The event date cannot be blank.");
            valid = false;
        } else {
            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                Date selectedDate = dateFormat.parse(eventDate);

                // Remove time components for comparison
                Calendar selectedCalendar = Calendar.getInstance();
                selectedCalendar.setTime(selectedDate);
                selectedCalendar.set(Calendar.HOUR_OF_DAY, 0);
                selectedCalendar.set(Calendar.MINUTE, 0);
                selectedCalendar.set(Calendar.SECOND, 0);
                selectedCalendar.set(Calendar.MILLISECOND, 0);

                Calendar currentCalendar = Calendar.getInstance();
                currentCalendar.set(Calendar.HOUR_OF_DAY, 0);
                currentCalendar.set(Calendar.MINUTE, 0);
                currentCalendar.set(Calendar.SECOND, 0);
                currentCalendar.set(Calendar.MILLISECOND, 0);

                if (selectedCalendar.before(currentCalendar)) {
                    warningMessage.append("\n- The event date must be today or in the future.");
                    valid = false;
                }
            } catch (ParseException e) {
                warningMessage.append("\n- The event date format is incorrect.");
                valid = false;
            }
        }

        if (geolocationRequired) {
            if (geolocationRequirement.isEmpty()) {
                warningMessage.append("\n- The geolocation requirement cannot be blank.");
                valid = false;
            } else {
                try {
                    int radius = Integer.parseInt(geolocationRequirement);
                    if (radius <= 0) {
                        warningMessage.append("\n- The geolocation radius must be a positive number.");
                        valid = false;
                    }
                } catch (NumberFormatException e) {
                    warningMessage.append("\n- The geolocation requirement must be a number (km radius).");
                    valid = false;
                }
            }
        }

        if (numberOfAttendees.isEmpty()) {
            warningMessage.append("\n- The number of attendees cannot be blank.");
            valid = false;
        } else {
            try {
                int attendees = Integer.parseInt(numberOfAttendees);
                if (attendees <= 0) {
                    warningMessage.append("\n- The number of attendees must be a positive number.");
                    valid = false;
                }
            } catch (NumberFormatException e) {
                warningMessage.append("\n- The number of attendees must be a number.");
                valid = false;
            }
        }

        if (waitlistCapacityRequired) {
            if (waitlistCapacity.isEmpty()) {
                warningMessage.append("\n- The waitlist capacity cannot be blank.");
                valid = false;
            } else {
                try {
                    int waitlistCap = Integer.parseInt(waitlistCapacity);
                    int attendees = Integer.parseInt(numberOfAttendees);

                    if (waitlistCap < attendees) {
                        warningMessage.append("\n- The waitlist capacity must be equal to or greater than the number of attendees.");
                        valid = false;
                    }

                    if (waitlistCap <= 0) {
                        warningMessage.append("\n- The waitlist capacity must be a positive number.");
                        valid = false;
                    }

                } catch (NumberFormatException e) {
                    warningMessage.append("\n- The waitlist capacity must be a number.");
                    valid = false;
                }
            }
        }

        if (!valid) {
            Toast.makeText(this, warningMessage.toString(), Toast.LENGTH_LONG).show();
        }

        return valid;
    }

    // check if any fields are field (before cancelling)
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