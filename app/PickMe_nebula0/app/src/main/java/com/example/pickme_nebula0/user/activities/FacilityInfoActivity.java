package com.example.pickme_nebula0.user.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.example.pickme_nebula0.DeviceManager;
import com.example.pickme_nebula0.R;
import com.example.pickme_nebula0.SharedDialogue;
import com.example.pickme_nebula0.db.DBManager;
import com.example.pickme_nebula0.facility.Facility;

/**
 * FacilityInfoActivity
 *
 * This activity allows organizers to create or update their facility information.
 *
 * Features:
 * - Provides input fields for facility name and address.
 * - Validates user input to ensure non-blank fields.
 * - Integrates with the database to save or update facility information.
 * - Populates existing facility information if available.
 *
 * Author: Stephine Yearley
 *
 * @see Facility
 */
public class FacilityInfoActivity extends AppCompatActivity {
    private DBManager dbManager;
    EditText nameField, adrField;

    /**
     * Called when the activity is first created.
     *
     * - Initializes the database manager.
     * - Sets up the layout and UI components.
     * - Defines actions for cancel and confirm buttons.
     * - Retrieves existing facility data from the database to populate fields.
     *
     * @param savedInstanceState The saved instance state for restoring the activity.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        dbManager = new DBManager();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facility_info);

        nameField = findViewById(R.id.editText_fi_name);
        adrField = findViewById(R.id.editText_fi_address);

        Button cancelButton = findViewById(R.id.button_fi_cancel);
        Button confirmButton = findViewById(R.id.button_fi_confirm);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // do NOT update db with new info
                finish();
            }
        });

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String name = nameField.getText().toString();
                String address = adrField.getText().toString();

                String warning = validateFacilityInfo(name,address);
                if (!warning.isBlank()){
                    // user had entered invalid data, warn them about it and don't update DB
                    SharedDialogue.showInvalidDataAlert(warning,FacilityInfoActivity.this);
                    return;
                }

                Facility facility = new Facility(name,address);
                dbManager.addUpdateFacility(facility);
                finish();
            }
        });

        // populate fields from DB
        dbManager.getFacility(DeviceManager.getDeviceId(),this::populateFieldsFromDB,this::failedToPopulateFieldsFromDB);

    }

    /**
     * Validates the facility information entered by the user.
     *
     * - Ensures that the facility name and address are not blank.
     * - Provides a warning message if validation fails.
     *
     * @param name The name of the facility.
     * @param address The address of the facility.
     * @return A warning message if validation fails; otherwise, an empty string.
     */
    public static String validateFacilityInfo(String name, String address){
        String warning = "";

        if (name.isBlank()){
            warning += "Facility name cannot be blank";
        }
        if (address.isBlank()){
            String errString = "Facility address cannot be blank";
            if (warning.isBlank()){
                warning = errString;
            } else{
                warning += "\n\n" + errString;
            }
        }

        return warning;
    }

    /**
     * Callback to populate the screen's fields
     *
     * @param facility instance of object castable to facility, used to populate the fields on screen
     */
    private void populateFieldsFromDB(Object facility){
        Facility castedFacility = (Facility) facility;

        nameField.setText(castedFacility.getName());
        adrField.setText(castedFacility.getAddress());
    }

    /**
     *  Callback called if we fail to populate ths facility info for an existing facility
     */
    private void failedToPopulateFieldsFromDB(){
        SharedDialogue.showInvalidDataAlert("Fields could not be populated from DB, the data shown may not match what is in the DB",FacilityInfoActivity.this);
    }
}
