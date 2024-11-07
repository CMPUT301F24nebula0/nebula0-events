package com.example.pickme_nebula0.entrant.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
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

    // tabs
    private final String[] tabTitles = new String[]{"Waitlist", "Selected", "Joined Events"};
    EntrantWaitlistFragment ewf;
    EntrantJoinedFragment ejf;
    EntrantSelectedFragment esf;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ewf = new EntrantWaitlistFragment();
        ejf = new EntrantJoinedFragment();
        esf = new EntrantSelectedFragment();


        // attach screen layout xml file
        setContentView(R.layout.activity_entrant_home);

        // binding elements
        ViewPager2 viewPager = findViewById(R.id.view_pager);
        TabLayout tabLayout = findViewById(R.id.tab_layout);
        FragmentStateAdapter pagerAdapter = new ScreenSlidePagerAdapter(this);
        final Button scanQRButton = findViewById(R.id.ScanQRButton);
        final Button backButton = findViewById(R.id.backButton);

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
                    return ewf;
                case 1:
                    return esf;
                default:
                    return ejf;
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
        if (ewf.isAdded()) {
            ewf.loadEvents(); // Refresh waitlist fragment only if it's added
        }
    }



}