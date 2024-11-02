package com.example.pickme_nebula0.admin.activities;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import androidx.appcompat.app.AppCompatActivity;

import com.example.pickme_nebula0.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class AdminHomeActivity extends AppCompatActivity {
    private String deviceID;
    private FirebaseFirestore db;

    // UI Elements
    ViewFlipper viewFlipper;
    Button btnManageEvents;
    Button btnManageUsers;
    Button btnManageImages;
    Button btnManageFacilities;
    Button btnManageQR;
    private List<String> eventList;
    private ArrayAdapter<String> eventAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_manage);

        viewFlipper = findViewById(R.id.admin_view_flipper);
        btnManageEvents = findViewById(R.id.manage_event);
        btnManageUsers = findViewById(R.id.manage_users);
        btnManageImages = findViewById(R.id.manage_image);
        btnManageFacilities = findViewById(R.id.manage_facilities);
        btnManageQR = findViewById(R.id.manage_qr_code);

        // upon clicking the manage events button, show the manage events layout

        btnManageEvents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show Manage Events layout
                viewFlipper.setDisplayedChild(0);

                // Set up event list and search functionality within Manage Events
                ListView eventListView = findViewById(R.id.eventListView);
                EditText searchEvents = findViewById(R.id.searchEvents);

                // Sample event list data , In future this will be fetched from the database
                eventList = new ArrayList<>();
                eventList.add("Swimming 1 - 2025-01-01");
                eventList.add("Field Hockey 2 - 2025-09-21");


                // Setting up the adapter and attaching it to the ListView
                eventAdapter = new ArrayAdapter<>(AdminHomeActivity.this, android.R.layout.simple_list_item_1, eventList);
                eventListView.setAdapter(eventAdapter);

                // Filter events based on search input
                // just to Acknowledge that I use ChatGPT with the prompt: add functionality to filter events based on search input
                searchEvents.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        eventAdapter.getFilter().filter(s);
                    }

                    @Override
                    public void afterTextChanged(Editable s) { }
                });

                // Confirmation message for debugging and UI verification
                Toast.makeText(AdminHomeActivity.this, "Switched to Manage Events layout", Toast.LENGTH_SHORT).show();
            }
        });

        btnManageUsers.setOnClickListener(new View.OnClickListener() {
            @Override
            //TODO;
            public void onClick(View v) {
                viewFlipper.setDisplayedChild(1); // Show Manage Profile layout
                Toast.makeText(AdminHomeActivity.this, "Switched to Manage Users layout", Toast.LENGTH_SHORT).show();
            }
        });

        btnManageImages.setOnClickListener(new View.OnClickListener() {
            @Override
            //TODO;
            public void onClick(View v) {
                viewFlipper.setDisplayedChild(2); // Show Manage Image layout
                Toast.makeText(AdminHomeActivity.this, "Switched to Manage Images layout", Toast.LENGTH_SHORT).show();

            }
        });

        btnManageQR.setOnClickListener(new View.OnClickListener() {
            @Override
            //TODO;
            public void onClick(View v) {
                viewFlipper.setDisplayedChild(3); // Show Manage QR Code layout
                Toast.makeText(AdminHomeActivity.this, "Switched to Manage QR Code layout", Toast.LENGTH_SHORT).show();
            }
        });

        btnManageFacilities.setOnClickListener(new View.OnClickListener() {
            @Override
            //TODO;
            public void onClick(View v) {
                viewFlipper.setDisplayedChild(4); // Show Manage Facilities layout
                Toast.makeText(AdminHomeActivity.this, "Switched to Manage Facilities layout", Toast.LENGTH_SHORT).show();
            }
        });


    }
}
