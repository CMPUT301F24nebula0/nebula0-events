package com.example.pickme_nebula0.entrant.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.example.pickme_nebula0.R;
import com.example.pickme_nebula0.entrant.fragments.EntrantEventsFragment;
import com.example.pickme_nebula0.qr.QRCodeActivity;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

/**
 * Activity for displaying events the user has signed up for
 */
public class EntrantHomeActivity extends AppCompatActivity {

    // tabs
    private final String[] tabTitles = new String[]{"Waitlist", "Selected", "Confirmed","Canceled"};
    EntrantEventsFragment waitlistFrag, selectedFrag, confirmedFrag, canceledFrag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        waitlistFrag = new EntrantEventsFragment("WAITLISTED");
        selectedFrag = new EntrantEventsFragment("SELECTED");
        confirmedFrag = new EntrantEventsFragment("CONFIRMED");
        canceledFrag = new EntrantEventsFragment("CANCELED");

        setContentView(R.layout.activity_entrant_home);

        ViewPager2 viewPager = findViewById(R.id.view_pager);
        TabLayout tabLayout = findViewById(R.id.tab_layout);
        FragmentStateAdapter pagerAdapter = new ScreenSlidePagerAdapter(this);
        final Button scanQRButton = findViewById(R.id.ScanQRButton);
        final Button backButton = findViewById(R.id.backButton);

        viewPager.setAdapter(pagerAdapter);

        // Initialize TabLayout and link it with ViewPager2
        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> tab.setText(tabTitles[position])
        ).attach();

        // Scan QR Button
        scanQRButton.setOnClickListener(view -> {
            Intent i = new Intent(EntrantHomeActivity.this, QRCodeActivity.class);
            startActivity(i);
        });

        // back Button
        backButton.setOnClickListener(view -> getOnBackPressedDispatcher().onBackPressed());
    }

    // Adapter for ViewPager2
    private class ScreenSlidePagerAdapter extends FragmentStateAdapter {
        public ScreenSlidePagerAdapter(AppCompatActivity fa) {
            super(fa);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            switch (position) {
                case 0:
                    return waitlistFrag;
                case 1:
                    return selectedFrag;
                case 2:
                    return confirmedFrag;
                default:
                    return canceledFrag;
            }
        }

        @Override
        public int getItemCount() {
            return tabTitles.length;
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        // TODO - tidy this by making a list of fragments and iterate
        if (waitlistFrag.isAdded()) {
            waitlistFrag.loadEvents();
        }
        if (confirmedFrag.isAdded()) {
            confirmedFrag.loadEvents();
        }
        if (selectedFrag.isAdded()) {
            selectedFrag.loadEvents();
        }
        if (canceledFrag.isAdded()) {
            canceledFrag.loadEvents();
        }
    }



}