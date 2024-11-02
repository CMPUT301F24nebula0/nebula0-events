package com.example.pickme_nebula0.organizer.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;
import android.app.DatePickerDialog;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.pickme_nebula0.DeviceManager;
import com.example.pickme_nebula0.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.type.DateTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class OrganizerCreateEventActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private EditText eventDateField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // initialize firebase
        db = FirebaseFirestore.getInstance();

        // attach to screen component xml
        setContentView(R.layout.activity_organizer_create_event);

        // components on screen
        EditText eventNameField = findViewById(R.id.event_name_field);
        EditText eventDescriptionField = findViewById(R.id.event_description_field);
        eventDateField = findViewById(R.id.event_date_field);
        EditText facilityNameField = findViewById(R.id.facility_name_field);
        EditText facilityAddressField = findViewById(R.id.facility_address_field);
        Switch geolocationRequirementSwitch = findViewById(R.id.geolocation_requirement_switch);
        EditText geolocationRequirementField = findViewById(R.id.geolocation_requirement_field);
        Switch waitlistCapacityRequiredSwitch = findViewById(R.id.waitlist_capacity_required_switch);
        EditText waitlistCapacityField = findViewById(R.id.waitlist_capacity_field);
        EditText numberOfAttendeesField = findViewById(R.id.number_of_attendees_field);
        Button eventCreationSubmitButton = findViewById(R.id.event_creation_submit_button);
        Button eventCreationCancelButton = findViewById(R.id.event_creation_cancel_button);

        // get deviceID as the foreignKey
        String deviceID = DeviceManager.getDeviceId(this);

        // DatePicker logic
        eventDateField.setFocusable(false);
        eventDateField.setOnClickListener(v -> showDatePickerDialog());

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
            String facilityName = facilityNameField.getText().toString();
            String facilityAddress = facilityAddressField.getText().toString();
            boolean geolocationRequired = geolocationRequirementSwitch.isChecked();
            String geolocationRequirement = geolocationRequirementField.getText().toString();
            boolean waitlistCapacityRequired = waitlistCapacityRequiredSwitch.isChecked();
            String waitlistCapacity = waitlistCapacityField.getText().toString();
            String numberOfAttendees = numberOfAttendeesField.getText().toString();

            // Validate user input
            if (validateEventCreationUserInput(deviceID, eventName, eventDescription, eventDate,
                    facilityName, facilityAddress, geolocationRequired, geolocationRequirement,
                    waitlistCapacityRequired, waitlistCapacity, numberOfAttendees)) {
                // if validated, store event data in Firestore
                Map<String, Object> eventData = new HashMap<>();
                eventData.put("deviceID", deviceID);
                eventData.put("eventName", eventName);
                eventData.put("eventDescription", eventDescription);
                eventData.put("eventDate", eventDate);
                eventData.put("facilityName", facilityName);
                eventData.put("facilityAddress", facilityAddress);
                eventData.put("geolocationRequired", geolocationRequired);
                eventData.put("geolocationRequirement", geolocationRequirement);
                eventData.put("waitlistCapacityRequired", waitlistCapacityRequired);
                eventData.put("waitlistCapacity", waitlistCapacity);
                eventData.put("createdDateTime", new Date());

                // if geolocation requirement switch on
                if (geolocationRequired) {
                    // save geolocation requirement
                    eventData.put("geolocationRequirement", Integer.parseInt(geolocationRequirement));
                }

                // if waitlist capacity requirement switch on
                if (waitlistCapacityRequired) {
                    eventData.put("waitlistCapacity", Integer.parseInt(waitlistCapacity));
                }

                eventData.put("numberOfAttendees", Integer.parseInt(numberOfAttendees));

                db.collection("Events").add(eventData)
                        .addOnSuccessListener(documentReference ->
                                // for debugging
                                // TODO: put custom message box instead
                                Toast.makeText(OrganizerCreateEventActivity.this, "Event data saved successfully", Toast.LENGTH_SHORT).show())
                        .addOnFailureListener(e ->
                                // for debugging
                                // TODO: put custom message box instead
                                Toast.makeText(OrganizerCreateEventActivity.this, "Error saving event data", Toast.LENGTH_SHORT).show());
            }
        });

        // Cancel button logic
        eventCreationCancelButton.setOnClickListener(v -> {
            if (isAnyFieldFilled(eventNameField, eventDescriptionField, eventDateField, facilityNameField,
                    facilityAddressField, geolocationRequirementField, waitlistCapacityField, numberOfAttendeesField)) {

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
            String facilityName,
            String facilityAddress,
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
        if (facilityName.isEmpty()) {
            warningMessage.append("\n- The facility name cannot be blank.");
            valid = false;
        }
        if (facilityAddress.isEmpty()) {
            warningMessage.append("\n- The facility address cannot be blank.");
            valid = false;
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
}