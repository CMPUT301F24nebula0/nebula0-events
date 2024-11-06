package com.example.pickme_nebula0.event;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.pickme_nebula0.R;
import com.example.pickme_nebula0.db.DBManager;
import com.google.firebase.firestore.FirebaseFirestore;

public class EventDetailActivity extends AppCompatActivity {
    private FirebaseFirestore db;
    private TextView eventDetailsTextView;
    private ImageView qrCodeImageView;
    private DBManager dbManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);
        final Button backButton = findViewById(R.id.backButton);

        dbManager = new DBManager();

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { getOnBackPressedDispatcher().onBackPressed(); }
        });

        eventDetailsTextView = findViewById(R.id.event_details_text_view);
        qrCodeImageView = findViewById(R.id.qr_code_image_view);

        String eventID = getIntent().getStringExtra("eventID");
        if (eventID == null || eventID.isEmpty()) {
            Toast.makeText(this, "Invalid Event ID.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        //
        fetchEventDetails(eventID);
    }

    private void fetchEventDetails(String eventID) {
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
                    details.append("Number of Attendees: ").append(event.getNumberOfAttendees()).append("\n");
                    eventDetailsTextView.setText(details.toString());

                    displayQRCode(event.getQrCodeData());
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

    private void displayQRCode(String qrBase64) {
        if (qrBase64 == null || qrBase64.isEmpty()) {
            Toast.makeText(this, "QR Code data is unavailable.", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            byte[] decodedBytes = Base64.decode(qrBase64, Base64.DEFAULT);
            Bitmap qrBitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
            if (qrBitmap != null) {
                qrCodeImageView.setImageBitmap(qrBitmap);
                qrCodeImageView.setVisibility(View.VISIBLE);
            } else {
                Toast.makeText(this, "Failed to decode QR Code.", Toast.LENGTH_SHORT).show();
            }
        } catch (IllegalArgumentException e) {
            Toast.makeText(this, "Invalid QR Code data.", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }
}
