package com.example.pickme_nebula0;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * GoogleMapActivity
 *
 * A `FragmentActivity` that integrates Google Maps to display a specific location marked by latitude and longitude.
 *
 * Key Features:
 * - Displays a Google Map with zoom controls.
 * - Places a marker on the map at the provided latitude and longitude.
 * - Moves the camera to focus on the specified location.
 * - Allows the user to navigate back using a back button.
 *
 * Dependencies:
 * - Google Maps API for Android.
 * - `SupportMapFragment` for map embedding.
 *
 * Use Cases:
 * - Viewing specific locations on a map.
 * - Enhancing user experience in location-based applications.
 *
 * Author: Taekwan Yoon
 */
public class GoogleMapActivity extends FragmentActivity implements OnMapReadyCallback {
    double latitude;
    double longitude;

    Button backButton;

    /**
     * Initializes the activity.
     *
     * - Sets up the layout with the map component and back button.
     * - Retrieves the latitude and longitude from the intent.
     * - Displays the map with the specified location.
     *
     * @param savedInstanceState The saved instance state for restoring the activity.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_map); // attach map component to layout

        // initialize map
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_fragment);

        setLatLngFromIntent(); // set latitude and longitude from intent

        displayMap(mapFragment); // display map

        backButton(); // back button
    }

    /**
     * When map is ready, add marker to selected location and move camera to selected location.
     *
     * @param googleMap google map
     */
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        Log.d("MapActivity", "onMapReady called");

        LatLng location = new LatLng(latitude, longitude);

        googleMap.getUiSettings().setZoomControlsEnabled(true);

        String label = "Lat: " + latitude + ", Lng: " + longitude;

        placeMarker(googleMap, location, label);

        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 10));

    }

    /**
     * Places a marker on the map at the specified location with the given label.
     *
     * @param googleMap The Google Map object.
     * @param location The `LatLng` object specifying the marker's position.
     * @param label The label to display on the marker.
     */
    public void placeMarker(GoogleMap googleMap, LatLng location, String label) {
        googleMap.addMarker(new MarkerOptions().position(location).title(label));
    }

    /**
     * Displays the map by asynchronously initializing it.
     *
     * - Attaches the `OnMapReadyCallback` to the `SupportMapFragment`.
     * - Handles cases where the map fragment is null by displaying an error message and logging the issue.
     *
     * @param mapFragment The `SupportMapFragment` containing the map.
     */
    private void displayMap(SupportMapFragment mapFragment) {
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        } else {
            Toast.makeText(this, "Error initializing map.", Toast.LENGTH_SHORT).show();
            Log.e("MapActivity", "SupportMapFragment is null");
            finish();
        }
    }

    /**
     * Extracts latitude and longitude from the intent that started the activity.
     *
     * - Defaults to `50.7749` (latitude) and `-122.4194` (longitude) if no data is provided.
     */
    private void setLatLngFromIntent() {
        latitude = getIntent().getDoubleExtra("latitude", 50.7749);
        longitude = getIntent().getDoubleExtra("longitude", -122.4194);
    }

    /**
     * Sets up the back button to navigate back to the previous screen.
     *
     * - Attaches an `OnClickListener` to trigger the `onBackPressed` method when clicked.
     */
    private void backButton() {
        // back button
        backButton = findViewById(R.id.back_button);
        // back button logic
        backButton.setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());
    }
}

