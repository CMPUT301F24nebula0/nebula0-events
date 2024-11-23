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
 * Class for the Organizer Event Participants Activity
 *
 * @Author: Taekwan Yoon
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
     * Class for the Screens Slide Pager Adapter
     *
     * @author Taekwan Yoon
     */
    private class ScreensSlidePagerAdapter extends FragmentStateAdapter {
        public ScreensSlidePagerAdapter(AppCompatActivity fa) {
            super(fa);
        }

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

        @Override
        public int getItemCount() {
            return tabTitles.length;
        }
    }
}
