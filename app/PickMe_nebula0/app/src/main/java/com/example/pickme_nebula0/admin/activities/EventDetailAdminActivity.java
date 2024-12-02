package com.example.pickme_nebula0.admin.activities;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;

import android.view.View;

import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.pickme_nebula0.R;
import com.example.pickme_nebula0.db.DBManager;
import com.example.pickme_nebula0.event.Event;

import com.example.pickme_nebula0.event.EventManager;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;


/**
 * Activity for displaying details of an event to an admin and to allow them to delete the activity
 *
 * @author Stephine
 * @author Evan
 */
public class EventDetailAdminActivity extends AppCompatActivity {
    private TextView eventDetailsTextView;
    private TextView QRCodeHashTextView;
    private DBManager dbManager;
    private ImageView imageView;
    private StorageReference storageRef;
    private ImageView qrCode;
    Button delImageBtn;
    Button delQRCodeBtn;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    /**
     * Interface for QRCode Callback functions
     */
    public interface QRCodeCallback {
        void onQRCodeFetched(String qrCodeData);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_event_detail);

        String eventID = getIntent().getStringExtra("eventID");
        boolean isImage = getIntent().getBooleanExtra("isImage", false);
        boolean isQRcode = getIntent().getBooleanExtra("isQRCode", false);
        boolean isEvent = getIntent().getBooleanExtra("isEvent", false);
        if (eventID == null || eventID.isEmpty()) {
            Toast.makeText(this, "Invalid Event ID.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        dbManager = new DBManager();

        eventDetailsTextView = findViewById(R.id.textView_aed_eventInfo);
        imageView = findViewById(R.id.image_view_poster);
        qrCode = findViewById(R.id.qrCode);

        final Button delEventBtn = findViewById(R.id.button_aed_delete_event);
        delEventBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbManager.deleteEvent(eventID);
                Toast.makeText(EventDetailAdminActivity.this, "Event Deleted", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        delImageBtn = findViewById(R.id.button_delete_image);
        delImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventManager.removePoster(eventID, () -> {
                    // poster removed from storage and event doc
                    Toast.makeText(EventDetailAdminActivity.this, "Image Deleted", Toast.LENGTH_SHORT).show();
                    imageView.setVisibility(View.GONE);
                    delImageBtn.setVisibility(View.GONE);
                }, () -> {
                    // poster could not be removed
                    Toast.makeText(EventDetailAdminActivity.this, "Failed to delete image", Toast.LENGTH_LONG).show();
                    imageView.setVisibility(View.GONE);
                    delImageBtn.setVisibility(View.GONE);
                });

                finish();
            }
        });

        delQRCodeBtn = findViewById(R.id.button_delete_qr);
        delQRCodeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteQRCode(eventID);
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
        fetchQRCodeHash(eventID);
        QRCodeHashTextView = findViewById(R.id.eventqrcodehash);

        QRCodeHashTextView.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                deleteQRCode(eventID);
                QRCodeHashTextView.setText("QRCodeHash: Null");
            }
        });

        if (isImage) {
            imageView.setVisibility(View.VISIBLE);
            delImageBtn.setVisibility(View.VISIBLE);
            loadImageFromFirebase(eventID);
        } else
        {
            imageView.setVisibility(View.GONE);
            delImageBtn.setVisibility(View.GONE);
        }

        if (isQRcode) {
            delQRCodeBtn.setVisibility(View.VISIBLE);
            qrCode.setVisibility(View.VISIBLE);

            getQRCodeData(eventID, qrBase64 -> {
                if (qrBase64 != null) {
                    displayQRCode(qrBase64);
                } else {
                    qrCode.setVisibility(View.GONE);
                    Toast.makeText(this, "QR Code data not available.", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            delQRCodeBtn.setVisibility(View.GONE);
            qrCode.setVisibility(View.GONE);
        }

        if (isEvent) {
            delEventBtn.setVisibility(View.VISIBLE);
        } else {
            delEventBtn.setVisibility(View.GONE);
        }


    }

    /**
     * Deletes QR code for given event from Database
     * @param eventID ID of event we want to delete the QR code for
     */
    private void deleteQRCode(String eventID){
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

    /**
     * Gets QR code string for display
     * @param eventID eventID for the event we want to display QR code for
     */
    private void fetchQRCodeHash(String eventID) {
        dbManager.getEvent(eventID, eventObj -> {
            if (eventObj instanceof Event) {
                Event event = (Event) eventObj;
               runOnUiThread(() -> {
                    StringBuilder details = new StringBuilder();
                    details.append("QRCodeHash: ");
                    if (event.getQrCodeData()==null){
                        details.append("null").append("\n\n");
                    }
                    else{
                        details.append(event.getQrCodeData()).append("\n\n");
                        //issue need to learn where the QR code data is held within events
                    }
                    QRCodeHashTextView.setText(details.toString());
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


    /**
     * Gets event details for given event
     * @param eventID ID of event we want to retrieve information for
     */
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
//                    if (event.getGeolocationRequired()) {
//                        details.append("Geolocation Max Distance: ").append(event.getGeolocationMaxDistance()).append(" km\n");
//                    }
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

    /**
     * Loads an image of event poster into view
     * @param eventID ID of event we are retrieving image for
     */
    private void loadImageFromFirebase(String eventID) {
        // Initialize FirebaseStorage with the correct bucket URL
        FirebaseStorage storage = FirebaseStorage.getInstance("gs://pickme-c2fb3.firebasestorage.app");
        storageRef = storage.getReference();

        StorageReference imageRef = storageRef.child("eventPosters/" + eventID);
        imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
            // Load the image using Picasso
            Picasso.get()
                    .load(uri)
                    .placeholder(R.drawable.no_poster_placeholder)
                    .error(R.drawable.error_image)
                    .into(imageView);
        }).addOnFailureListener(exception -> {
            // Handle any errors
            Toast.makeText(EventDetailAdminActivity.this, "Failed to load image: " + exception.getMessage(), Toast.LENGTH_LONG).show();
            imageView.setVisibility(View.GONE);
        });
    }

    /**
     * Display bitmap image of QR code
     * @param qrBase64 QR code
     */
    private void displayQRCode(String qrBase64) {
        if (qrBase64 == null || qrBase64.trim().isEmpty()) {

            qrCode.setVisibility(View.GONE);
            return;
        }

        try {
            byte[] decodedBytes = Base64.decode(qrBase64, Base64.DEFAULT);
            Bitmap qrBitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);

            if (qrBitmap != null) {
                qrCode.setImageBitmap(qrBitmap);
                qrCode.setVisibility(View.VISIBLE);
            } else {
                qrCode.setVisibility(View.GONE);
            }
        } catch (IllegalArgumentException e) {
            qrCode.setVisibility(View.GONE);
        }
    }

    /**
     * Fetches QR code data from database, performs callback on data if successful or on null if unsuccessful
     * @param eventID ID of event we want to retrieve QR code data for
     * @param callback function taking a QR code data/null to be called
     */
    public void getQRCodeData(String eventID, QRCodeCallback callback) {
        DocumentReference docRef = db.collection("Events").document(eventID);

        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    String qrCodeData = document.getString("qrCodeData");
                    callback.onQRCodeFetched(qrCodeData); // Pass data to the callback
                } else {
                    callback.onQRCodeFetched(null); // Handle case where document doesn't exist
                }
            } else {
                callback.onQRCodeFetched(null); // Handle failure
            }
        });
    }


}
