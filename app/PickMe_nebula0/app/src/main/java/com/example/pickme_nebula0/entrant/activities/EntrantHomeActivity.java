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
 * EntrantHomeActivity
 *
 * This activity serves as the main screen for entrants, allowing them to view events they have signed up for.
 *
 * Key Features:
 * - Tab-based navigation using `ViewPager2` and `TabLayout` for different event statuses:
 *   - Waitlist
 *   - Selected
 *   - Confirmed
 *   - Cancelled
 * - QR Code scanning functionality for checking event details.
 * - Back navigation to exit the activity.
 * - Dynamic event loading for each tab when the activity resumes.
 *
 * Dependencies:
 * - `EntrantEventsFragment` for displaying event lists based on their status.
 * - `TabLayoutMediator` for linking `TabLayout` and `ViewPager2`.
 * - `QRCodeActivity` for scanning QR codes.
 */
public class EntrantHomeActivity extends AppCompatActivity {

    // tabs: Waitlist, Selected, Confirmed, Cancelled
    private final String[] tabTitles = new String[]{"Waitlist", "Selected", "Confirmed","Cancelled"};

    private ViewPager2 viewPager;
    private TabLayout tabLayout;
    private FragmentStateAdapter pagerAdapter;

    // buttons
    private Button scanQRButton, backButton;

    // fragments for each tab
    EntrantEventsFragment waitlistFrag, selectedFrag, confirmedFrag, cancelledFrag;

    /**
     * Initializes the activity and sets up UI components.
     *
     * - Sets up the layout and initializes fragments for different event statuses.
     * - Configures `ViewPager2` with a custom `ScreenSlidePagerAdapter`.
     * - Links `TabLayout` with `ViewPager2` using `TabLayoutMediator`.
     * - Adds click listeners for the "Scan QR" and "Back" buttons.
     *
     * @param savedInstanceState The saved instance state for restoring the activity.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // set layout
        setContentView(R.layout.activity_entrant_home);

        // initialize fragments
        waitlistFrag = new EntrantEventsFragment("WAITLISTED");
        selectedFrag = new EntrantEventsFragment("SELECTED");
        confirmedFrag = new EntrantEventsFragment("CONFIRMED");
        cancelledFrag = new EntrantEventsFragment("CANCELLED");

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

    /**
     * Adapter for managing fragments in ViewPager2.
     *
     * - Maps each tab position to the corresponding fragment.
     */
    private class ScreenSlidePagerAdapter extends FragmentStateAdapter {
        /**
         * Constructor for the adapter.
         *
         * @param fa The parent activity hosting the ViewPager2.
         */
        public ScreenSlidePagerAdapter(AppCompatActivity fa) {
            super(fa);
        }

        /**
         * Creates the fragment for a given tab position.
         *
         * @param position The position of the tab (0 = Waitlist, 1 = Selected, etc.).
         * @return The fragment corresponding to the given position.
         */
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
                    return cancelledFrag;
            }
        }

        /**
         * Returns the number of tabs (fragments) in the adapter.
         *
         * @return The total number of tabs.
         */
        @Override
        public int getItemCount() {
            return tabTitles.length;
        }
    }

    /**
     * Called when the activity is resumed.
     *
     * - Reloads events for each fragment if it has been added to the activity.
     */
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
        if (cancelledFrag.isAdded()) {
            cancelledFrag.loadEvents();
        }
    }
}