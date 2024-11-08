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

    // tabs
    private final String[] tabTitles = new String[]{"Past", "Ongoing"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // attach screen layout xml file
        setContentView(R.layout.activity_organizer_home);

        // binding elements
        ViewPager2 viewPager = findViewById(R.id.view_pager);
        TabLayout tabLayout = findViewById(R.id.tab_layout);
        FragmentStateAdapter pagerAdapter = new ScreensSlidePagerAdapter(this);
        final Button backButton = findViewById(R.id.backButton);
        final Button createEventButton = findViewById(R.id.createEventButton);

        viewPager.setAdapter(pagerAdapter);

        // Initialize TabLayout and link it with ViewPager2
        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> tab.setText(tabTitles[position])
        ).attach();

        // back button
        createEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getOnBackPressedDispatcher().onBackPressed();
            }
        });

        // once create event button pressed
        backButton.setOnClickListener(view ->
                navigateTo(OrganizerCreateEventActivity.class));

        // to keep track of which tab is selected
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

    // Adapter for ViewPager2
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
