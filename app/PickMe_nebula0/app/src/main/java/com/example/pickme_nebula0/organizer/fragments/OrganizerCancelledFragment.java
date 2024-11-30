package com.example.pickme_nebula0.organizer.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pickme_nebula0.R;
import com.example.pickme_nebula0.db.DBManager;
import com.example.pickme_nebula0.organizer.adapters.CancelledAdapter;
import com.example.pickme_nebula0.user.User;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

/**
 * Fragment for viewing cancelled entrants
 */
public class OrganizerCancelledFragment extends Fragment {
    private final DBManager dbManager = new DBManager();
    ArrayList<User> cancelledUsers = new ArrayList<User>();
    private CancelledAdapter adapter;
    String eventID;

    public OrganizerCancelledFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_organizer_cancelled, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.cancelled_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new CancelledAdapter(getContext(), cancelledUsers);
        recyclerView.setAdapter(adapter);
        eventID = getActivity().getIntent().getStringExtra("eventID");
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadCancelledUsers();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void loadCancelledUsers() {
        cancelledUsers.clear();
        dbManager.loadUsersRegisteredInEvent(eventID, DBManager.RegistrantStatus.CANCELLED, "OrganizerCancelledFragment",
                (userObj) -> {
                    User user = (User) userObj;
                    cancelledUsers.add(user);
                    adapter.notifyDataSetChanged();
                });

    }
}