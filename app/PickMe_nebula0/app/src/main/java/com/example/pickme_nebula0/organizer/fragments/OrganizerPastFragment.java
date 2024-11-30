package com.example.pickme_nebula0.organizer.fragments;

import android.annotation.SuppressLint;
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
import com.example.pickme_nebula0.event.EventManager;
import com.example.pickme_nebula0.organizer.OrganizerRole;
import com.example.pickme_nebula0.organizer.adapters.PastEventsAdapter;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

/**
 * Fragments for viewing past events
 */
public class OrganizerPastFragment extends Fragment {
    private FirebaseFirestore db;
    ArrayList<Event> pastEvents = new ArrayList<Event>();
    private PastEventsAdapter adapter;

    public OrganizerPastFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_organizer_past, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.past_events_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new PastEventsAdapter(getContext(), pastEvents);
        recyclerView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadPastEvents();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void loadPastEvents() {
        OrganizerRole.get_event_by_status(DeviceManager.getDeviceId(), EventManager.EventStatus.PAST, (pastEventsObj) -> {
            ArrayList<Event> events = (ArrayList<Event>) pastEventsObj;

            getActivity().runOnUiThread(() -> {
                pastEvents.clear();
                pastEvents.addAll(events);
                adapter.notifyDataSetChanged();
            });
        }, () -> Log.d(this.getClass().getSimpleName(), "Could not load past events"));


//        pastEvents.clear();
//        db.collection("Events")
//                .whereEqualTo("organizerID", DeviceManager.getDeviceId())
//                .get()
//                .addOnCompleteListener(task -> {
//                    if (task.isSuccessful()) {
//                        for (QueryDocumentSnapshot document : task.getResult()) {
//                            Event event = document.toObject(Event.class);
//                            if (event.getEventDate() != null && event.getEventDate().before(new Date())) {
//                                pastEvents.add(event);
//                            }
//                        }
//                        adapter.notifyDataSetChanged();
//                    }
//                });
    }
}
