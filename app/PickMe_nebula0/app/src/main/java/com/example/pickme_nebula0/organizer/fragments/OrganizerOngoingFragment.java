package com.example.pickme_nebula0.organizer.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.pickme_nebula0.R;

public class OrganizerOngoingFragment extends Fragment {

    public OrganizerOngoingFragment() {
    }

    public static OrganizerOngoingFragment newInstance() {
        return new OrganizerOngoingFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_organizer_ongoing, container, false);
    }
}
