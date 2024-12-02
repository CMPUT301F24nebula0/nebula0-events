package com.example.pickme_nebula0.organizer.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.example.pickme_nebula0.R;
import com.example.pickme_nebula0.organizer.fragments.OrganizerCancelledFragment;
import com.example.pickme_nebula0.organizer.fragments.OrganizerConfirmedFragment;
import com.example.pickme_nebula0.organizer.fragments.OrganizerSelectedFragment;
import com.example.pickme_nebula0.organizer.fragments.OrganizerWaitlistedFragment;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

/**
 * Activity for organizers to view participants of an event by category.
 *
 * This class displays a tabbed interface with four categories: Waitlisted, Selected, Confirmed, and Cancelled.
 * Each tab shows participants in the respective category using fragments.
 *
 * @see OrganizerWaitlistedFragment
 * @see OrganizerSelectedFragment
 * @see OrganizerConfirmedFragment
 * @see OrganizerCancelledFragment
 *
 * @author Taekwan Yoon
 */
public class OrganizerEventParticipantsActivity extends AppCompatActivity {

    // tabs: Waitlisted, Selected, Confirmed, Cancelled
    private final String[] tabTitles = new String[] {"Waitlisted", "Selected", "Confirmed", "Cancelled"};

    // UI components
    ViewPager2 viewPager;
    TabLayout tabLayout;
    FragmentStateAdapter pagerAdapter;
    Button backButton;
    String eventID;

    /**
     * Initializes the activity, sets up the ViewPager2 with tabbed categories, and handles user interactions.
     *
     * Retrieves the event ID from the intent, configures tabs for participant categories, and implements
     * back button functionality.
     *
     * @param savedInstanceState the previously saved state of the activity, if any
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        eventID = getIntent().getStringExtra("eventID");
        // set the layout
        setContentView(R.layout.activity_organizer_participants);

        // initialize UI components
        viewPager = findViewById(R.id.view_pager);
        tabLayout = findViewById(R.id.tab_layout);
        pagerAdapter = new ScreensSlidePagerAdapter(this);
        backButton = findViewById(R.id.backButton);

        // Set the adapter for ViewPager2
        viewPager.setAdapter(pagerAdapter);

        // Sync TabLayout with ViewPager2
        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> tab.setText(tabTitles[position])
        ).attach();

        // Set the current item to the selected tab
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
            }
        });

        // back button
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { getOnBackPressedDispatcher().onBackPressed(); }
        });
    }

    /**
     * Adapter for managing fragments displayed in the tabbed interface.
     *
     * Dynamically loads the appropriate fragment for each tab in the participant categories.
     */
    private class ScreensSlidePagerAdapter extends FragmentStateAdapter {
        public ScreensSlidePagerAdapter(AppCompatActivity fa) {
            super(fa);
        }

        /**
         * Creates a fragment for the specified position.
         *
         * @param position the position of the tab
         * @return the fragment corresponding to the tab
         */
        @NonNull
        @Override
        public Fragment createFragment(int position) {
            switch (position) {
                // Waitlisted, Selected, Confirmed, Cancelled
                case 0: return new OrganizerWaitlistedFragment(eventID);
                case 1: return new OrganizerSelectedFragment();
                case 2: return new OrganizerConfirmedFragment();
                case 3: return new OrganizerCancelledFragment();
            }
            // default to Waitlisted
            return new OrganizerWaitlistedFragment(eventID);
        }

        /**
         * Returns the number of tabs/categories.
         *
         * @return the total number of tabs
         */
        @Override
        public int getItemCount() {
            return tabTitles.length;
        }
    }
}
