package com.example.pickme_nebula0.entrant.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.pickme_nebula0.R;

public class EntrantWaitlistFragment extends Fragment {

    public EntrantWaitlistFragment() {
    }

    public static EntrantWaitlistFragment newInstance() {
        return new EntrantWaitlistFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_entrant_waitlist, container, false);
    }
}