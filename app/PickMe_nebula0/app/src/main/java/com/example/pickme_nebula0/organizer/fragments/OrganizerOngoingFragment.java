package com.example.pickme_nebula0.organizer.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pickme_nebula0.DeviceManager;
import com.example.pickme_nebula0.R;
import com.example.pickme_nebula0.event.Event;
import com.example.pickme_nebula0.organizer.adapters.OngoingEventsAdapter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Date;

public class OrganizerOngoingFragment extends Fragment {
    private FirebaseFirestore db;
    ArrayList<Event> ongoingEvents = new ArrayList<Event>();
    private OngoingEventsAdapter adapter;

    public OrganizerOngoingFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_organizer_ongoing, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.ongoing_events_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new OngoingEventsAdapter(getContext(), ongoingEvents);
        recyclerView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();

//        db.collection("Events")
//                .whereEqualTo("organizerID", DeviceManager.getDeviceId())
//                .get()
//                .addOnCompleteListener(task -> {
//                    if (task.isSuccessful()) {
//                        for (QueryDocumentSnapshot document : task.getResult()) {
//                            Event event = document.toObject(Event.class);
//                            if (event.getEventDate() != null && !event.getEventDate().before(new Date())) {
//                                ongoingEvents.add(event);
//                            }
//                        }
//                        adapter.notifyDataSetChanged();
//                    }
//                });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadOngoingEvents();
    }

    private void loadOngoingEvents() {
        ongoingEvents.clear();
        db.collection("Events")
                .whereEqualTo("organizerID", DeviceManager.getDeviceId())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Event event = document.toObject(Event.class);
                            if (event.getEventDate() != null && !event.getEventDate().before(new Date())) {
                                ongoingEvents.add(event);
                            }
                        }
                        adapter.notifyDataSetChanged();
                    }
                });
    }
}
