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

public class OrganizerHomeActivity extends AppCompatActivity {
    private final String[] tabTitles = new String[]{"Past", "Ongoing"};
    private Button createEventButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_organizer_home); // attach components to layout
        Animation buttonClickAnimation = AnimationUtils.loadAnimation(this, R.anim.button_click_animation);

        tabToggling(tabTitles);

        backButton();
        createEventButton();
    }

    /**
     * Function to toggle between tabs
     *
     * @param tabTitles array of tab titles
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
     * Function for back button
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


    /**
     * Function for create event button
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
     * Function to navigate to the create event activity
     */
    private void navigateTo() {
        Intent intent = new Intent(OrganizerHomeActivity.this, OrganizerCreateEventActivity.class);
        startActivity(intent);
    }

    private class ScreensSlidePagerAdapter extends FragmentStateAdapter {
        public ScreensSlidePagerAdapter(AppCompatActivity fa) {
            super(fa);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            // past tab selected
            if (position == 0) { return new OrganizerPastFragment(); }
            // ongoing tab selected
            else { return new OrganizerOngoingFragment(); }
        }

        @Override
        public int getItemCount() {
            return tabTitles.length;
        }
    }
}
