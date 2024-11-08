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

public class OrganizerEventParticipantsActivity extends AppCompatActivity {

    //tabs
    private final String[] tabTitles = new String[] {"Waitlisted", "Selected", "Confirmed", "Cancelled"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_organizer_participants);

        ViewPager2 viewPager = findViewById(R.id.view_pager);
        TabLayout tabLayout = findViewById(R.id.tab_layout);
        FragmentStateAdapter pagerAdapter = new ScreensSlidePagerAdapter(this);
        final Button backButton = findViewById(R.id.backButton);

        viewPager.setAdapter(pagerAdapter);

        // Initialize TabLayout and link it with ViewPager2
        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> tab.setText(tabTitles[position])
        ).attach();

        // back button
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { getOnBackPressedDispatcher().onBackPressed(); }
        });

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
            }
        });


    }

    private class ScreensSlidePagerAdapter extends FragmentStateAdapter {
        public ScreensSlidePagerAdapter(AppCompatActivity fa) {
            super(fa);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            if (position == 0) { return new OrganizerWaitlistedFragment(); }
            else if (position == 1) { return new OrganizerSelectedFragment(); }
            else if (position == 2) { return new OrganizerConfirmedFragment(); }
            else { return new OrganizerCancelledFragment(); }
        }

        @Override
        public int getItemCount() {
            return tabTitles.length;
        }
    }
}
