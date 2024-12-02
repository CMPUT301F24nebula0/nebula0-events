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
 * Fragment for displaying a list of past events for an organizer.
 *
 * This fragment initializes a RecyclerView with a `PastEventsAdapter` to display past events
 * and dynamically loads data from the database based on the organizer's role and event status.
 *
 * @see PastEventsAdapter
 * @see Event
 * @see Fragment
 */
public class OrganizerPastFragment extends Fragment {
    private FirebaseFirestore db;
    ArrayList<Event> pastEvents = new ArrayList<Event>();
    private PastEventsAdapter adapter;

    /**
     * Default constructor for `OrganizerPastFragment`.
     *
     * Required for fragment instantiation.
     */
    public OrganizerPastFragment() { }

    /**
     * Creates and initializes the view for the fragment.
     *
     * Sets up the RecyclerView with a `PastEventsAdapter` to display past events.
     *
     * @param inflater           the LayoutInflater object used to inflate views
     * @param container          the parent view that this fragment's UI is attached to
     * @param savedInstanceState the previously saved state of the fragment, if any
     * @return the initialized view for the fragment
     */
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

    /**
     * Invoked when the fragment becomes visible to the user.
     *
     * Calls `loadPastEvents` to populate the RecyclerView with updated data.
     */
    @Override
    public void onResume() {
        super.onResume();
        loadPastEvents();
    }

    /**
     * Loads the list of past events from the database and updates the RecyclerView.
     *
     * Retrieves past events based on the organizer's device ID and event status.
     * Clears the current list of events, updates it with new data, and notifies the adapter.
     *
     * Logs an error message if the data cannot be loaded.
     */
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
    }
}
