package com.example.pickme_nebula0;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresPermission;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.android.gms.location.Priority;

import java.util.HashMap;
import java.util.Map;

/**
 * Manages geolocation functionalities such as retrieving and saving location data.
 */
public class GeolocationManager {

    private final FusedLocationProviderClient fusedLocationClient;
    private final Context context;
    private final FirebaseFirestore firestore;

    /**
     * Constructor initializes the FusedLocationProviderClient and Firestore instance.
     *
     * @param context The application context.
     */
    public GeolocationManager(Context context) {
        this.context = context;
        this.fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
        this.firestore = FirebaseFirestore.getInstance();
    }

    /**
     * Checks if the app has either fine or coarse location permission.
     *
     * @return True if at least one location permission is granted, false otherwise.
     */
    public boolean hasLocationPermission() {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * Saves the user's geolocation to Firestore under the specified userID and eventID.
     *
     * @param userID   The ID of the user.
     * @param eventID  The ID of the event.
     * @param callback The callback to handle success or failure.
     */
    @RequiresPermission(anyOf = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION})
    public void saveGeolocation(String userID, String eventID, GeolocationCallback callback) {
        if (!hasLocationPermission()) {
            Toast.makeText(context, "Location permission not granted", Toast.LENGTH_SHORT).show();
            callback.onGeolocationSaved(false);
            return;
        }

        try {
            fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && task.getResult() != null) {
                            Location location = task.getResult();
                            double latitude = location.getLatitude();
                            double longitude = location.getLongitude();

                            GeoPoint geoPoint = new GeoPoint(latitude, longitude);
                            Map<String, Object> data = new HashMap<>();
                            data.put("geolocation", geoPoint);

                            DocumentReference docRefUsers = firestore.collection("Users")
                                    .document(userID)
                                    .collection("RegisteredEvents")
                                    .document(eventID);

                            DocumentReference docRefEvents = firestore.collection("Events")
                                    .document(eventID)
                                    .collection("EventRegistrants")
                                    .document(userID);

                            docRefUsers.set(data, SetOptions.merge())
                                    .addOnSuccessListener(aVoid -> {
                                        callback.onGeolocationSaved(true);
                                    })
                                    .addOnFailureListener(e -> {
                                        callback.onGeolocationSaved(false);
                                    });

                            docRefEvents.set(data, SetOptions.merge())
                                    .addOnSuccessListener(aVoid -> {
                                        callback.onGeolocationSaved(true);
                                    })
                                    .addOnFailureListener(e -> {
                                        callback.onGeolocationSaved(false);
                                    });
                        } else {
                            Toast.makeText(context, "Failed to retrieve location", Toast.LENGTH_SHORT).show();
                            callback.onGeolocationSaved(false);
                        }
                    });
        } catch (SecurityException e) {
            Toast.makeText(context, "Location permission not granted.", Toast.LENGTH_SHORT).show();
            callback.onGeolocationSaved(false);
        }
    }

    /**
     * Callback interface for geolocation save operations.
     */
    public interface GeolocationCallback {
        void onGeolocationSaved(boolean success);
    }
}