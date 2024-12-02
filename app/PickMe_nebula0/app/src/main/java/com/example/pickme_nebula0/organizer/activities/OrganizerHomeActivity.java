package com.example.pickme_nebula0.organizer.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.example.pickme_nebula0.R;
import com.example.pickme_nebula0.organizer.fragments.OrganizerOngoingFragment;
import com.example.pickme_nebula0.organizer.fragments.OrganizerPastFragment;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

/**
 * Activity for the organizer's home screen.
 *
 * Displays a tabbed interface with two categories: "Past" and "Ongoing" events. Provides functionality
 * for navigating to the "Create Event" activity and toggling between event categories.
 *
 * @see OrganizerPastFragment
 * @see OrganizerOngoingFragment
 * @see OrganizerCreateEventActivity
 */
public class OrganizerHomeActivity extends AppCompatActivity {
    private final String[] tabTitles = new String[]{"Past", "Ongoing"};
    private Button createEventButton;
    private Button refreshButton;

    /**
     * Initializes the activity, sets up the tabbed interface, and configures button functionality.
     *
     * Attaches the UI layout, configures tabs for "Past" and "Ongoing" events, and sets up the
     * "Create Event" and "Back" buttons.
     *
     * @param savedInstanceState the previously saved state of the activity, if any
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_organizer_home); // attach components to layout

        tabToggling(tabTitles);

        backButton();
        createEventButton();
        refreshButton();
    }

    /**
     * Configures the tabbed interface for toggling between "Past" and "Ongoing" events.
     *
     * Sets up the `ViewPager2` with fragments for each tab and synchronizes it with the `TabLayout`.
     * Ensures the "Create Event" button is visible for all tabs.
     *
     * @param tabTitles an array of tab titles ("Past" and "Ongoing")
     */
    private void tabToggling(String[] tabTitles) {
        ViewPager2 viewPager = findViewById(R.id.view_pager); // display tabs: "past" and "ongoing"

        // displays corresponding fragment when tab is selected
        TabLayout tabLayout = findViewById(R.id.tab_layout);

        // Adapter for ViewPager2 (to provide fragments)
        FragmentStateAdapter pagerAdapter = new ScreensSlidePagerAdapter(this);
        viewPager.setAdapter(pagerAdapter);

        // Sync tabLayout with viewPager
        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> tab.setText(tabTitles[position])
        ).attach();

        // toggles the visibility of the createEventButton depending on selected tab
        // FIXED: visibility of createEventButton is set to VISIBLE for both tabs
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                // if "Past" selected
                if (position == 0) { createEventButton.setVisibility(View.VISIBLE); }
                // if "Ongoing" selected
                else { createEventButton.setVisibility(View.VISIBLE); }
            }
        });
    }

    /**
     * Configures the back button to navigate to the previous activity.
     *
     * Attaches a click listener to the back button that triggers the system's back press dispatcher.
     */
    private void backButton() {
        Button backButton = findViewById(R.id.backButton);
        Animation buttonClickAnimation = AnimationUtils.loadAnimation(this, R.anim.button_click_animation);

        // Back Button Logic with Animation
        backButton.setOnClickListener(v -> {
            // Start animation
            v.startAnimation(buttonClickAnimation);

            // Perform action after animation
            v.postDelayed(() -> getOnBackPressedDispatcher().onBackPressed(), 200); // Delay matches animation duration
        });
    }

    private void refreshButton(){
        Animation buttonClickAnimation = AnimationUtils.loadAnimation(this, R.anim.button_click_animation);

        refreshButton = findViewById(R.id.buttonRefreshOrgEvents);

        refreshButton.setOnClickListener(view-> onResume());
    }

    /**
     * Configures the "Create Event" button to navigate to the event creation activity.
     *
     * Attaches a click listener to the button that calls the `navigateTo` method.
     */
    private void createEventButton() {
        createEventButton = findViewById(R.id.createEventButton);
        Animation buttonClickAnimation = AnimationUtils.loadAnimation(this, R.anim.button_click_animation);

        createEventButton.setOnClickListener(view -> {
            // Start animation
            view.startAnimation(buttonClickAnimation);

            // Perform action after animation
            view.postDelayed(this::navigateTo, 200); // Delay matches animation duration
        });    }

    /**
     * Navigates to the "Create Event" activity.
     *
     * Starts the `OrganizerCreateEventActivity` when the "Create Event" button is clicked.
     */
    private void navigateTo() {
        Intent intent = new Intent(OrganizerHomeActivity.this, OrganizerCreateEventActivity.class);
        startActivity(intent);
    }

    /**
     * Adapter for managing fragments in the tabbed interface.
     *
     * Provides fragments for the "Past" and "Ongoing" event categories.
     */
    private class ScreensSlidePagerAdapter extends FragmentStateAdapter {
        /**
         * Constructs the adapter for managing fragments in `ViewPager2`.
         *
         * @param fa the parent activity where this adapter is used
         */
        public ScreensSlidePagerAdapter(AppCompatActivity fa) {
            super(fa);
        }

        /**
         * Creates a fragment for the specified tab position.
         *
         * @param position the position of the tab
         * @return the fragment corresponding to the tab
         */
        @NonNull
        @Override
        public Fragment createFragment(int position) {
            // past tab selected
            if (position == 0) { return new OrganizerPastFragment(); }
            // ongoing tab selected
            else { return new OrganizerOngoingFragment(); }
        }

        /**
         * Returns the number of tabs in the interface.
         *
         * @return the total number of tabs
         */
        @Override
        public int getItemCount() {
            return tabTitles.length;
        }
    }
}