package com.example.pickme_nebula0.admin.activities;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.pickme_nebula0.DeviceManager;
import com.example.pickme_nebula0.R;
import com.example.pickme_nebula0.event.Event;
import com.example.pickme_nebula0.event.EventDetailUserActivity;
import com.example.pickme_nebula0.event.EventManager;
import com.example.pickme_nebula0.event.EventsArrayAdapter;
import com.example.pickme_nebula0.facility.Facility;
import com.example.pickme_nebula0.facility.FacilityArrayAdapter;
import com.example.pickme_nebula0.facility.FacilityDetailActivity;
import com.example.pickme_nebula0.notification.MessageViewActivity;
import com.example.pickme_nebula0.notification.Notification;
import com.example.pickme_nebula0.notification.NotificationArrayAdapter;
import com.example.pickme_nebula0.qr.ImageAdapter;
import com.example.pickme_nebula0.qr.QRcodeAdapter;
import com.example.pickme_nebula0.user.User;
import com.example.pickme_nebula0.user.UserArrayAdapter;
import com.example.pickme_nebula0.user.activities.UserDetailActivity;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.checkerframework.framework.qual.DefaultQualifier;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Allows admin to browse and delete items.
 *
 * @author Stephine Yearley
 * @author Sina Shaban
 */
public class AdminHomeActivity extends AppCompatActivity {
    private String deviceID;
    private FirebaseFirestore db;

    // Events
    private ArrayList<Event> events;
    private ListView eventsList;
    private EventsArrayAdapter eventAdapter;

    // Profiles
    private ArrayList<User> users;
    private ListView usersList;

    private ArrayList<Event> eventsWithImage;
    private ListView imagesList;
    private ImageAdapter imageAdapter;

    private ArrayList<Event> eventsWithQR;
    private ListView QRcodesList;
    private QRcodeAdapter QRAdapter;


    private UserArrayAdapter userAdapter;

    // Facilities
    private ArrayList<Facility> facilities;
    private ListView facilitiesList;

    private FacilityArrayAdapter facilityAdapter;

    // UI Elements
    ViewFlipper viewFlipper;
    Button btnManageEvents;
    Button btnManageUsers;
    Button btnManageImages;
    Button btnManageFacilities;
    Button btnManageQR;
    Button btnBack;

    @Override
    protected void onResume() {
        super.onResume();
        updateAll();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_manage);

        db = FirebaseFirestore.getInstance();

        // UI components
        viewFlipper = findViewById(R.id.admin_view_flipper);
        btnManageEvents = findViewById(R.id.manage_event);
        btnManageUsers = findViewById(R.id.manage_users);
        btnManageImages = findViewById(R.id.manage_image);
        btnManageFacilities = findViewById(R.id.manage_facilities);
        btnManageQR = findViewById(R.id.manage_qr_code);
        btnBack = findViewById(R.id.button_admin_back);

        // Set up list view for events
        events = new ArrayList<>();
        eventsList = findViewById(R.id.eventListView);

        eventAdapter = new EventsArrayAdapter(AdminHomeActivity.this,R.id.item_event, events);
        eventsList.setAdapter(eventAdapter);
        // for user profiles
        users = new ArrayList<>();
        usersList = findViewById(R.id.ProfileListView);
        userAdapter = new UserArrayAdapter(AdminHomeActivity.this,R.id.item_user, users);
        usersList.setAdapter(userAdapter);
        // for facility profiles
        facilities = new ArrayList<>();
        facilitiesList = findViewById(R.id.facilitiesListView);
        facilityAdapter = new FacilityArrayAdapter(AdminHomeActivity.this,R.id.item_facility, facilities);
        facilitiesList.setAdapter(facilityAdapter);

        // for images
        eventsWithImage = new ArrayList<>();
        imagesList=findViewById(R.id.ImageListView);
        imageAdapter=new ImageAdapter(AdminHomeActivity.this,R.id.item_image, eventsWithImage);
        imagesList.setAdapter(imageAdapter);

       // for QR codes
        eventsWithQR = new ArrayList<>();
        QRcodesList=findViewById(R.id.QRcodeListView);
        QRAdapter=new QRcodeAdapter(AdminHomeActivity.this,R.id.item_qrcode, eventsWithQR);
        QRcodesList.setAdapter(QRAdapter);

        btnBack.setOnClickListener(v -> finish());


        // upon clicking the manage events button, show the manage events layout
        btnManageEvents.setOnClickListener(v -> {
            // Show Manage Events layout
            viewFlipper.setDisplayedChild(0);
            updateEvents();

            updateButtonAppearanceOnClick(btnManageEvents);

            // On click, show event details an allow admin to delete
            eventsList.setOnItemClickListener((adapterView, view, pos, id) -> {
                Event clickedEvent = (Event) adapterView.getItemAtPosition(pos);

                Intent intent = new Intent(AdminHomeActivity.this, EventDetailAdminActivity.class);
                intent.putExtra("eventID",clickedEvent.getEventID());
                intent.putExtra("isEvent", true);
                startActivity(intent);
            });
            // Confirmation message for debugging and UI verification
//            Toast.makeText(AdminHomeActivity.this, "Switched to Manage Events layout", Toast.LENGTH_SHORT).show();
        });

        // sets up interaction with first visible layout, ie. the manage events layout
        btnManageEvents.performClick();

        btnManageUsers.setOnClickListener(v -> {
            viewFlipper.setDisplayedChild(1); // Show Manage Profile layout
            updateProfiles();

            updateButtonAppearanceOnClick(btnManageUsers);

            // On click, show user details an allow admin to delete
            usersList.setOnItemClickListener((adapterView, view, pos, id) -> {
                User clickedUser = (User) adapterView.getItemAtPosition(pos);

                Toast.makeText(AdminHomeActivity.this, clickedUser.getUserID(), Toast.LENGTH_SHORT).show();


                Intent intent = new Intent(AdminHomeActivity.this, UserDetailActivity.class);
                intent.putExtra("userID",clickedUser.getUserID());
                intent.putExtra("admin",true);
                startActivity(intent);
            });

//            Toast.makeText(AdminHomeActivity.this, "Switched to Manage Users layout", Toast.LENGTH_SHORT).show();
        });

        //TODO;
        btnManageImages.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                viewFlipper.setDisplayedChild(2); // Show Manage QR Code layout
                updateImages();

                updateButtonAppearanceOnClick(btnManageImages);
//                Toast.makeText(AdminHomeActivity.this, "Switched to Manage Image layout", Toast.LENGTH_SHORT).show();
                // On click, show event details an allow admin to delete
                imagesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int pos, long id) {
                        Event clickedEvent = (Event) adapterView.getItemAtPosition(pos);

                        Intent intent = new Intent(AdminHomeActivity.this, EventDetailAdminActivity.class);
                        intent.putExtra("eventID",clickedEvent.getEventID());
                        intent.putExtra("isImage", true);
                        startActivity(intent);
                    }
                });
                // Confirmation message for debugging and UI verification
//                Toast.makeText(AdminHomeActivity.this, "Switched to Manage Events layout", Toast.LENGTH_SHORT).show();
            }
        });

        btnManageQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewFlipper.setDisplayedChild(3); // Show Manage QR Code layout
                updateQRCodes();

                updateButtonAppearanceOnClick(btnManageQR);

//                Toast.makeText(AdminHomeActivity.this, "Switched to Manage QR Code layout", Toast.LENGTH_SHORT).show();
                // On click, show event details an allow admin to delete
                QRcodesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int pos, long id) {
                        Event clickedEvent = (Event) adapterView.getItemAtPosition(pos);

                        Intent intent = new Intent(AdminHomeActivity.this, EventDetailAdminActivity.class);
                        intent.putExtra("eventID",clickedEvent.getEventID());
                        intent.putExtra("isQRCode", true);
                        startActivity(intent);
                    }
                });
                // Confirmation message for debugging and UI verification
//                Toast.makeText(AdminHomeActivity.this, "Switched to Manage Events layout", Toast.LENGTH_SHORT).show();
            }
        });

        btnManageFacilities.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewFlipper.setDisplayedChild(4); // Show Manage Facilities layout
                updateFacilities();

                updateButtonAppearanceOnClick(btnManageFacilities);

                // On click, show user details an allow admin to delete
                facilitiesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int pos, long id) {
                        Facility clickedFacility = (Facility) adapterView.getItemAtPosition(pos);

                        Intent intent = new Intent(AdminHomeActivity.this, FacilityDetailActivity.class);
                        intent.putExtra("facilityID",clickedFacility.getFacilityID());
                        startActivity(intent);
                    }
                });

//                Toast.makeText(AdminHomeActivity.this, "Switched to Manage Facilities layout", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateAll(){
        updateEvents();
        updateProfiles();
        updateImages();
        updateQRCodes();
        updateFacilities();
    }

    private void updateEvents(){
        EventManager.get_all_events((eventsObj) -> {
            ArrayList<Event> fetched_events = (ArrayList<Event>) eventsObj;

            runOnUiThread(() -> {
                events.clear();
                events.addAll(fetched_events);
                eventAdapter.notifyDataSetChanged();
            });
        }, () -> {});
    }

    private void updateProfiles(){
        users.clear();
        userAdapter.notifyDataSetChanged();
        db.collection("Users")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            User u = document.toObject(User.class);
                            users.add(u);
                        }
                        userAdapter.notifyDataSetChanged();
                    }
                });
    }

    private void updateImages() {
        eventsWithImage.clear();
        imageAdapter.notifyDataSetChanged();

        db.collection("Events")
            .addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot querySnapshots, @Nullable FirebaseFirestoreException error) {
                    if (error != null || querySnapshots == null) {
                        Log.w("AdminHomeActivity", "Listen failed.", error);
                        return;
                    }

                    eventsWithImage.clear();
                    // check if each fetched event has a non null image
                    for (QueryDocumentSnapshot doc : querySnapshots) {
                        if (doc == null) { continue; }
                        if (doc.contains("poster") && doc.get("poster") != null) {
                            Event event = doc.toObject(Event.class);
                            eventsWithImage.add(event);
                        }
                    }

                    imageAdapter.notifyDataSetChanged();
                }
            });
    }
/*
there is no reason to separate the QR codes from their respective event
as a QR code doesn't exist on its own  aslo a change made to the QR code
 */
    private void updateQRCodes(){

        EventManager.get_all_events((eventsObj) -> {
            ArrayList<Event> fetched_events = (ArrayList<Event>) eventsObj;
            eventsWithQR.clear();

            // only add qr code to list if its data exists
            for (Event event: fetched_events) {
                String qr_code_data = event.getQrCodeData();
                if (qr_code_data == null || qr_code_data.equals("null")) { continue; }
                eventsWithQR.add(event);
            }
            QRAdapter.notifyDataSetChanged();
        }, () -> Log.d(this.getClass().getSimpleName(), "Failed to update QR code list"));
    }

    private void updateFacilities(){
        facilities.clear();
        facilityAdapter.notifyDataSetChanged();
        db.collection("Facilities")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Facility f = document.toObject(Facility.class);
                            facilities.add(f);
                        }
                       facilityAdapter.notifyDataSetChanged();
                    }
                });
    }

    private void updateButtonAppearanceOnClick(Button clickedButton) {
        ArrayList<Button> buttonArrayList = new ArrayList<>(
                Arrays.asList(btnManageEvents, btnManageUsers, btnManageImages, btnManageFacilities, btnManageQR)
        );

        for (Button button: buttonArrayList) {
            if (button == clickedButton) {
                button.setTypeface(null, Typeface.BOLD_ITALIC);
                button.setTextColor(Color.BLACK);
            } else {
                button.setTypeface(null, Typeface.NORMAL);
                button.setTextColor(Color.GRAY);
            }
        }
    }
}
