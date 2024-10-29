package com.example.pickme_nebula0.entrant.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.example.pickme_nebula0.R;
import com.example.pickme_nebula0.qr.QRCodeActivity;
import com.example.pickme_nebula0.entrant.fragments.EntrantSelectedFragment;
import com.example.pickme_nebula0.entrant.fragments.EntrantJoinedFragment;
import com.example.pickme_nebula0.entrant.fragments.EntrantWaitlistFragment;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class EntrantHomeActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private FragmentStateAdapter pagerAdapter;
    private TabLayout tabLayout;
    private String[] tabTitles = new String[]{"Waitlist", "Selected", "Joined Events"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entrant_home);

        // Initialize ViewPager2 and set the adapter
        viewPager = findViewById(R.id.view_pager);
        pagerAdapter = new ScreenSlidePagerAdapter(this);
        viewPager.setAdapter(pagerAdapter);

        // Initialize TabLayout and link it with ViewPager2
        tabLayout = findViewById(R.id.tab_layout);
        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> tab.setText(tabTitles[position])
        ).attach();

        // Scan QR Button
        final Button scanQRButton = findViewById(R.id.ScanQRButton);
        scanQRButton.setOnClickListener(view -> {
            Intent i = new Intent(EntrantHomeActivity.this, QRCodeActivity.class);
            startActivity(i);
        });

        // Back Button
        final Button backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());
    }

    // Adapter for ViewPager2
    private class ScreenSlidePagerAdapter extends FragmentStateAdapter {
        public ScreenSlidePagerAdapter(AppCompatActivity fa) {
            super(fa);
        }

        @Override
        public Fragment createFragment(int position) {
            switch (position) {
                case 0:
                    return new EntrantWaitlistFragment();
                case 1:
                    return new EntrantSelectedFragment();
                case 2:
                    return new EntrantJoinedFragment();
                default:
                    return new EntrantWaitlistFragment();
            }
        }

        @Override
        public int getItemCount() {
            return tabTitles.length;
        }
    }
}