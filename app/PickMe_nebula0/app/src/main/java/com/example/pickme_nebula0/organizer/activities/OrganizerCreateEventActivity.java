package com.example.pickme_nebula0.organizer.activities;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;
import android.app.DatePickerDialog;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.pickme_nebula0.DeviceManager;
import com.example.pickme_nebula0.R;
import com.example.pickme_nebula0.SharedDialogue;
import com.example.pickme_nebula0.db.DBManager;
import com.example.pickme_nebula0.event.Event;
import com.example.pickme_nebula0.organizer.exceptions.OrganizerExceptions;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Activity for organizers to create events.
 *
 * This class allows organizers to input event details, configure settings like geolocation and waitlist capacity,
 * and optionally upload a poster image. Includes validation for user inputs and backend integration for storing events.
 *
 * @see Event
 * @see DBManager
 * @see SharedDialogue
 * @see OrganizerExceptions
 *
 * @author Taekwan Yoon
 */
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
    private Button selectPosterButton;
    private Button previewPosterButton;

    ActivityResultLauncher<PickVisualMediaRequest> pickMedia;

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
    Uri posterUri = null;

    // Device ID
    String deviceID;

    // Event object
    Event event;

    // Exceptions
    OrganizerExceptions organizerExceptions;

    // Firestore Instance
    FirebaseFirestore db;

    /**
     * Initializes the activity, sets up UI components, and defines logic for user interactions.
     *
     * Includes handlers for switches, date picker dialog, and event creation submission.
     * Validates user input before attempting to create and store an event.
     *
     * @param savedInstanceState the previously saved state of the activity, if any
     */
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
        selectPosterButton = findViewById(R.id.buttonSelectPoster);
        previewPosterButton = findViewById(R.id.buttonPreviewPoster);

        // Get device ID
        deviceID = DeviceManager.getDeviceId();

        // Initialize exceptions
        organizerExceptions = new OrganizerExceptions();

        db = FirebaseFirestore.getInstance();

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

        // set up poster selection
        pickMedia =
                registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                    // Callback is invoked after the user selects a media item or closes the
                    // photo picker.
                    if (uri != null) {
                        posterUri = uri;
                        Toast.makeText(this,"Poster selected",Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this,"Poster was not selected.",Toast.LENGTH_SHORT).show();
                    }
                });
        selectPosterButton.setOnClickListener(view -> {
            openImagePicker();
        });
        previewPosterButton.setOnClickListener(view->{
            SharedDialogue.displayPosterPopup(this,posterUri);
        });

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
            if(posterUri != null){
                dbManager.addEvent(this,event,posterUri);
            } else{
                dbManager.addEvent(this, event);
            }

            Toast.makeText(this, "Event Created", Toast.LENGTH_LONG).show();

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

    /**
     * Displays a date picker dialog for the user to select the event date.
     *
     * Prevents the selection of past dates and updates the event date field upon selection.
     */
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

    /**
     * Toggles the enabled state of an EditText field based on a switch's state.
     *
     * @param editText the EditText field to toggle
     * @param isChecked the state of the switch
     */
    private void SwitchToggleEditText(EditText editText, boolean isChecked)
    {
        if (isChecked) {
            editText.setEnabled(true);
        } else {
            editText.setEnabled(false);
            editText.setText("");
        }
    }

    /**
     * Check if any of the fields are filled
     *
     * @param fields EditText fields to check
     * @return boolean value of whether any of the fields are filled
     */
    private boolean isAnyFieldFilled(EditText... fields) {
        for (EditText field : fields) {
            if (!field.getText().toString().isEmpty()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Parse date string to Date object
     *
     * @param dateString date string to parse
     * @return Date object
     */
    private static Date parseDate(String dateString) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        try {
            return formatter.parse(dateString);
        } catch (ParseException e) {
            Log.d("OrganizerCreateEventActivity", "parseDate failed with error: " + e.getMessage());
            return null; // Handle exception as needed
        }
    }

    /**
     * Launches an image picker for the user to select a poster image for the event.
     */
    private void openImagePicker() {
        pickMedia.launch(new PickVisualMediaRequest.Builder()
                .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                .build());
    }
}