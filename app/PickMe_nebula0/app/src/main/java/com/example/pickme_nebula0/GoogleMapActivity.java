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
 * Class to display a map with a marker at a given latitude and longitude.
 *
 * @author Taekwan Yoon
 */
public class GoogleMapActivity extends FragmentActivity implements OnMapReadyCallback {
    double latitude;
    double longitude;
    Button backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_map); // attach map component to layout

        // initialize map
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_fragment);

        displayMap(mapFragment); // display map

        setLatLngFromIntent(); // set latitude and longitude from intent

        backButton(); // back button
    }

    /**
     * When map is ready, add marker to selected location and move camera to selected location.
     *
     * @param googleMap google map
     */
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        LatLng location = new LatLng(latitude, longitude);
        // add marker to selected location
        googleMap.addMarker(new MarkerOptions().position(location).title("Selected Location"));
        // move camera to selected location
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15));
    }

    /**
     * Display map.
     *
     * @param mapFragment map fragment
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
     * Get latitude and longitude from intent.
     */
    private void setLatLngFromIntent() {
        latitude = getIntent().getDoubleExtra("latitude", 50.7749);
        longitude = getIntent().getDoubleExtra("longitude", -122.4194);
    }

    /**
     * Attach back button and its logic.
     */
    private void backButton() {
        // back button
        backButton = findViewById(R.id.back_button);
        // back button logic
        backButton.setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());
    }
}

