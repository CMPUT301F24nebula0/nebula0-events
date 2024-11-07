package com.example.pickme_nebula0.organizer.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pickme_nebula0.R;
import com.example.pickme_nebula0.organizer.adapters.WaitlistedAdapter;
import com.example.pickme_nebula0.user.User;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class OrganizerWaitlistedFragment extends Fragment {
    private FirebaseFirestore db;
    ArrayList<User> waitlistedUsers = new ArrayList<User>();
    private WaitlistedAdapter adapter;

    public OrganizerWaitlistedFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_organizer_waitlisted, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.waitlisted_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new WaitlistedAdapter(getContext(), waitlistedUsers);
        recyclerView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadWaitlistedUsers();
    }

    private void loadWaitlistedUsers() {
        waitlistedUsers.clear();
        // TODO: TaekwanY
        // backend implementation
    }

}
