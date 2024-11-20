package com.example.pickme_nebula0.organizer.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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

    // tabs: "Past" and "Ongoing"
    private final String[] tabTitles = new String[]{"Past", "Ongoing"};

    // back button and create event button
    private Button backButton;
    private Button createEventButton;

    // display tabs: "past" and "ongoing"
    private ViewPager2 viewPager;
    // displays corresponding fragment when tab is selected
    private TabLayout tabLayout;
    // Adapter for ViewPager2 (to provide fragments)
    private FragmentStateAdapter pagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // attach components to layout
        setContentView(R.layout.activity_organizer_home);
        viewPager = findViewById(R.id.view_pager);
        tabLayout = findViewById(R.id.tab_layout);

        pagerAdapter = new ScreensSlidePagerAdapter(this);
        viewPager.setAdapter(pagerAdapter);

        backButton = findViewById(R.id.backButton);
        createEventButton = findViewById(R.id.createEventButton);


        // Sync tabLayout with viewPager
        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> tab.setText(tabTitles[position])
        ).attach();


        // back button and create event button
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getOnBackPressedDispatcher().onBackPressed();
            }
        });
        createEventButton.setOnClickListener(view -> navigateTo(OrganizerCreateEventActivity.class));


        // toggles the visibility of the createEventButton depending on selected tab
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                // if "Past" selected
                if (position == 0) { createEventButton.setVisibility(View.GONE); }
                // if "Ongoing" selected
                else { createEventButton.setVisibility(View.VISIBLE); }
            }
        });
    }

    // function for switching screens through intent
    private void navigateTo(Class<?> targetActivity) {
        Intent intent = new Intent(OrganizerHomeActivity.this, targetActivity);
        startActivity(intent);
    }

    // Supplies the fragments for the ViewPager2
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
