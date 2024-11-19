package com.example.pickme_nebula0.qr;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
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

public class QRCodeActivity extends AppCompatActivity {
    
    // QR code scanner
    private static final int PERMISSION_REQUEST_CAMERA = 1;

    //
    FirebaseFirestore db;
    Button homeButton;

    //
    boolean active = false;

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

        homeButton.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               active = false;
               finish();
           }
        });
    }

    /**
     * Initialize QR code scanner
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
     * Callback for the result from requesting permissions. This method
     * @param requestCode The request code passed in requestPermissions
     * @param permissions The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions
     *     which is either {@link android.content.pm.PackageManager#PERMISSION_GRANTED}
     *     or {@link android.content.pm.PackageManager#PERMISSION_DENIED}. Never null.
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
                Toast.makeText(this, "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();

                String eventURI = result.getContents();
                String eventID = eventURI.substring(eventURI.lastIndexOf("/") + 1);

                db.collection("Events")
                        .document(eventID)
                        .get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();

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
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
