package com.example.pickme_nebula0.qr;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.Manifest;
import android.content.pm.PackageManager;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import com.example.pickme_nebula0.R;
import com.example.pickme_nebula0.event.EventDetailUserActivity;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

/**
 * Activity for scanning a QR code.
 *
 * This activity allows users to scan QR codes to retrieve event details and navigate
 * to the event details page. It handles permissions for the camera and processes QR
 * code scanning results.
 *
 * @see IntentIntegrator
 * @see EventDetailUserActivity
 */
public class QRCodeActivity extends AppCompatActivity {

    // QR code scanner
    private static final int PERMISSION_REQUEST_CAMERA = 1;

    FirebaseFirestore db;
    Button homeButton;

    // check if activity is active
    boolean active = false;

    /**
     * Initializes the QR code scanning activity.
     *
     * This method sets up the UI components, checks for camera permissions,
     * and initializes the QR code scanner if permissions are granted.
     *
     * @param savedInstanceState If the activity is being re-initialized after
     *                           previously being shut down, this Bundle contains
     *                           the most recently saved data.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        active = true;

        // layout
        setContentView(R.layout.activity_qr_code);

        // UI components
        homeButton = findViewById(R.id.homebtn);

        // Firebase Firestore
        db = FirebaseFirestore.getInstance();

        // check camera permission
        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST_CAMERA);
        } else {
            initQRCodeScanner();
        }
        Animation buttonClickAnimation = AnimationUtils.loadAnimation(this, R.anim.button_click_animation);

        homeButton.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               // Start animation
               v.startAnimation(buttonClickAnimation);

               // Perform action after animation
               v.postDelayed(() -> {
                   active = false;
                   finish();
               }, 200); // Delay matches animation duration
           }
        });
    }

    /**
     * Initializes and starts the QR code scanner.
     *
     * This method sets up the scanner with the desired QR code format,
     * locks the orientation, and starts scanning.
     */
    private void initQRCodeScanner() {
        // Initialize QR code scanner here
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
        integrator.setOrientationLocked(true);
        integrator.setPrompt("Scan a QR code");
        integrator.initiateScan();
    }

    /**
     * Callback for handling the result of a permission request.
     *
     * This method handles the response to the camera permission request. If permission
     * is granted, it initializes the QR code scanner. Otherwise, it shows a warning
     * and finishes the activity.
     *
     * @param requestCode The request code passed in requestPermissions.
     * @param permissions The requested permissions.
     * @param grantResults The grant results for the requested permissions.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult( requestCode, permissions,  grantResults);
        if (requestCode == PERMISSION_REQUEST_CAMERA) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initQRCodeScanner();
            } else {
                Toast.makeText(this, "Camera permission is required", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }

    /**
     * Callback for the result from QR code scanner
     * @param requestCode The integer request code originally supplied to startActivityForResult(), allowing you to identify who this result came from.
     * @param resultCode The integer result code returned by the child activity through its setResult().
     * @param data An Intent, which can return result data to the caller (various data can be attached to Intent "extras").
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // QR code scanner result
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, "Scan cancelled", Toast.LENGTH_LONG).show();
            } else {
                // Extract the event URI from the QR code
                String eventURI = result.getContents();
                // Extract the event ID from the URI
                String eventID = eventURI.substring(eventURI.lastIndexOf("/") + 1);

                // Navigate to the event details page
                navigateToEventDetails(eventID);
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    /**
     * Navigate to the event details page
     * @param eventID The ID of the event to navigate to
     */
    private void navigateToEventDetails(String eventID) {
        db.collection("Events")
                .document(eventID)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Check if event exists
                        DocumentSnapshot document = task.getResult();
                        // If event exists, navigate to the event details page
                        if (document.exists()) {
                            Intent intent = new Intent(QRCodeActivity.this, EventDetailUserActivity.class);
                            intent.putExtra("eventID", eventID);
                            intent.putExtra("fromQR", true);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(this, "Event not found", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(this, "Error fetching event", Toast.LENGTH_LONG).show();
                    }
                });
    }
}
