package com.example.pickme_nebula0.facility;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.pickme_nebula0.R;
import com.example.pickme_nebula0.db.DBManager;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * Activity for viewing and managing facility details for admin users.
 *
 * This activity allows the admin to view the details of a facility and delete it if necessary.
 * The facility ID is retrieved from the intent, and facility information is fetched from Firestore.
 * Includes options to go back to the previous screen or delete the facility.
 */
public class FacilityDetailActivity extends AppCompatActivity {

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final DBManager dbManager = new DBManager();

    private TextView facilityDetails;

    /**
     * Initializes the activity, retrieves the facility ID from the intent, and sets up UI components.
     *
     * Validates the facility ID, sets up button click listeners for navigation and deletion,
     * and triggers fetching facility details from the database.
     *
     * @param savedInstanceState the previously saved state of the activity, if any
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facility_detail);

        // Retrieve eventID from intent
        String facilityID = getIntent().getStringExtra("facilityID");

        if (facilityID == null || facilityID.trim().isEmpty()) {
            Toast.makeText(this, "Invalid Facility ID.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        facilityDetails = findViewById(R.id.textView_fda_info);
        final Button backBtn = findViewById(R.id.button_fda_back);
        final Button delBtn = findViewById(R.id.button_fda_delete);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               finish();
            }
        });

        delBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbManager.deleteFacility(facilityID,()->{finish();});
            }
        });

        fetchFacilityDetails(facilityID);

    }

    /**
     * Fetches the details of a facility from Firestore and displays them on the screen.
     *
     * @param facilityID the ID of the facility whose details are to be fetched
     */
    private void fetchFacilityDetails(String facilityID) {
        db.collection("Facilities").document(facilityID)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String name = task.getResult().getString("name");
                        String adr = task.getResult().getString("address");
                        facilityDetails.setText(String.format("Name: %s\nAddress: %s",name,adr));
                    }
                });
    }

}
