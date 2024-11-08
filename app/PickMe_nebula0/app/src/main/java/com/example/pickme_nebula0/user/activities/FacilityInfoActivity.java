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
 * Activity for organizer to create/update their facility information.
 *
 * @author Stephine Yearley
 * @see Facility
 */
public class FacilityInfoActivity extends AppCompatActivity {
    private DBManager dbManager;
    EditText nameField, adrField;

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
