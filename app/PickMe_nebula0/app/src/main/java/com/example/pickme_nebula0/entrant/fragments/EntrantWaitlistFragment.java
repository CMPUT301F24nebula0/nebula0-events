package com.example.pickme_nebula0.entrant.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.pickme_nebula0.R;

public class EntrantWaitlistFragment extends Fragment {

    public EntrantWaitlistFragment() {
    }

    public static EntrantWaitlistFragment newInstance() {
        return new EntrantWaitlistFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // fragment_entrant_waitlist.xml
        View rootView = inflater.inflate(R.layout.fragment_entrant_waitlist, container, false);

        // update entrant_waitlist_content_activity FragmentContainerView
        // in fragment_entrant_waitlist.xml
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        EntrantWaitlistContentFragment fragment = new EntrantWaitlistContentFragment();
        transaction.replace(R.id.entrant_waitlist_content_activity, fragment);

        // Commit the transaction
        transaction.commit();

        return rootView;
    }
}