package com.example.pickme_nebula0.admin.activities;

import android.os.Bundle;
import android.view.View;

import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.pickme_nebula0.R;
import com.example.pickme_nebula0.db.DBManager;
import com.example.pickme_nebula0.event.Event;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;


/**
 * Activity for displaying details of an event to an admin and to allow them to delete the activity
 *
 * @author Stephine
 */
public class EventDetailAdminActivity extends AppCompatActivity {
    private TextView eventDetailsTextView;
    private TextView QRcodeHashTextVeiw;
    private DBManager dbManager;
    private ImageView imageView;
    private StorageReference storageRef;
    Button delImageBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_event_detail);

        String eventID = getIntent().getStringExtra("eventID");
        boolean isImage = getIntent().getBooleanExtra("isImage", false);
        if (eventID == null || eventID.isEmpty()) {
            Toast.makeText(this, "Invalid Event ID.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        dbManager = new DBManager();

        eventDetailsTextView = findViewById(R.id.textView_aed_eventInfo);
        imageView = findViewById(R.id.image_view_poster);

        final Button delEventBtn = findViewById(R.id.button_aed_delete_event);
        delEventBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbManager.deleteEvent(eventID);
                Toast.makeText(EventDetailAdminActivity.this, "Event Deleted", Toast.LENGTH_SHORT);
                finish();
            }
        });

        delImageBtn = findViewById(R.id.button_delete_image);
        delImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteImageFromFirebase(eventID);
                finish();
            }
        });

        final Button backBtn = findViewById(R.id.button_aed_back);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        fetchEventDetails(eventID);
        fetchQRcodehash(eventID);
        QRcodeHashTextVeiw = findViewById(R.id.eventqrcodehash);

        QRcodeHashTextVeiw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DeleteQRcode(eventID);
                QRcodeHashTextVeiw.setText("QRcodeHash: Null");
            }
        });

        if (isImage) {
            imageView.setVisibility(View.VISIBLE);
            loadImageFromFirebase(eventID);

        } else
        {
            imageView.setVisibility(View.GONE);
        }
    }

    public void DeleteQRcode(String eventID){
        dbManager.getEvent(eventID, eventObj -> {
            if (eventObj instanceof Event) {
                Event event = (Event) eventObj;
                runOnUiThread(() -> {
                    event.setQrCodeData("null");
                    dbManager.updateEvent(event);
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
    private void fetchQRcodehash(String eventID) {
        dbManager.getEvent(eventID, eventObj -> {
            if (eventObj instanceof Event) {
                Event event = (Event) eventObj;
               runOnUiThread(() -> {
                    StringBuilder details = new StringBuilder();
                    details.append("QRcodeHash: ");
                    if (event.getQrCodeData()==null){
                        details.append("null").append("\n\n");
                    }
                    else{
                        details.append(event.getQrCodeData()).append("\n\n");
                        //issue need to learn where the QR code data is held within events
                    }
                    QRcodeHashTextVeiw.setText(details.toString());
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


    private void fetchEventDetails(String eventID) {
        // TODO this code is duplicated we should refactor to avoid this later
        dbManager.getEvent(eventID, eventObj -> {
            if (eventObj instanceof Event) {
                Event event = (Event) eventObj;
                runOnUiThread(() -> {
                    StringBuilder details = new StringBuilder();
                    details.append("Event Name: ").append(event.getEventName()).append("\n\n");
                    details.append("Description: ").append(event.getEventDescription()).append("\n\n");
                    details.append("Date: ").append(event.getEventDate().toString()).append("\n\n");
                    details.append("Geolocation Required: ").append(event.getGeolocationRequired() ? "Yes" : "No").append("\n\n");
                    if (event.getGeolocationRequired()) {
                        details.append("Geolocation Max Distance: ").append(event.getGeolocationMaxDistance()).append(" km\n");
                    }
                    details.append("Waitlist Capacity Required: ").append(event.getWaitlistCapacityRequired() ? "Yes" : "No").append("\n");
                    if (event.getWaitlistCapacityRequired()) {
                        details.append("Waitlist Capacity: ").append(event.getWaitlistCapacity()).append("\n");
                    }

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

    private void loadImageFromFirebase(String eventID) {
        // Initialize FirebaseStorage with the correct bucket URL
        FirebaseStorage storage = FirebaseStorage.getInstance("gs://pickme-c2fb3.firebasestorage.app");
        storageRef = storage.getReference();
        // TODO: FIX THE PATH ACCORDINGLY
        StorageReference imageRef = storageRef.child("eventPosters/" + eventID + ".jpg");

        imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
            // Load the image using Picasso
            Picasso.get()
                    .load(uri)
                    .into(imageView);
        }).addOnFailureListener(exception -> {
            // Handle any errors
            Toast.makeText(EventDetailAdminActivity.this, "Failed to load image: " + exception.getMessage(), Toast.LENGTH_LONG).show();
            imageView.setVisibility(View.GONE);
        });
    }

    private void deleteImageFromFirebase(String eventID) {
        FirebaseStorage storage = FirebaseStorage.getInstance("gs://pickme-c2fb3.firebasestorage.app");
        StorageReference storageRef = storage.getReference();
        // TODO: FIX THE PATH ACCORDINGLY
        StorageReference imageRef = storageRef.child("eventPosters/" + eventID + ".jpg");

        imageRef.delete().addOnSuccessListener(aVoid -> {
            Toast.makeText(EventDetailAdminActivity.this, "Image Deleted", Toast.LENGTH_SHORT).show();
            imageView.setVisibility(View.GONE);
            delImageBtn.setVisibility(View.GONE);
        }).addOnFailureListener(exception -> {
            Toast.makeText(EventDetailAdminActivity.this, "Failed to delete image: " + exception.getMessage(), Toast.LENGTH_LONG).show();
        });
    }
}
