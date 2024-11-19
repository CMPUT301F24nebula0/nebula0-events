package com.example.pickme_nebula0.entrant.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
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

    // tabs: Waitlist, Selected, Confirmed, Canceled
    private final String[] tabTitles = new String[]{"Waitlist", "Selected", "Confirmed","Canceled"};

    private ViewPager2 viewPager;
    private TabLayout tabLayout;
    private FragmentStateAdapter pagerAdapter;

    // buttons
    private Button scanQRButton, backButton;

    // fragments for each tab
    EntrantEventsFragment waitlistFrag, selectedFrag, confirmedFrag, canceledFrag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // set layout
        setContentView(R.layout.activity_entrant_home);

        // initialize fragments
        waitlistFrag = new EntrantEventsFragment("WAITLISTED");
        selectedFrag = new EntrantEventsFragment("SELECTED");
        confirmedFrag = new EntrantEventsFragment("CONFIRMED");
        canceledFrag = new EntrantEventsFragment("CANCELED");

        // Initialize ViewPager2 and TabLayout
        viewPager = findViewById(R.id.view_pager);
        tabLayout = findViewById(R.id.tab_layout);
        pagerAdapter = new ScreenSlidePagerAdapter(this);

        // Initialize buttons
        scanQRButton = findViewById(R.id.ScanQRButton);
        backButton = findViewById(R.id.backButton);

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