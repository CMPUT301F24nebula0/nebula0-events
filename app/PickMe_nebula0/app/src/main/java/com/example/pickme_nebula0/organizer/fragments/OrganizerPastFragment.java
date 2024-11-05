package com.example.pickme_nebula0.organizer.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.pickme_nebula0.DeviceManager;
import com.example.pickme_nebula0.R;
import com.example.pickme_nebula0.event.Event;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Date;

public class OrganizerPastFragment extends Fragment {
    private FirebaseFirestore db;
    ArrayList<Event> pastEvents = new ArrayList<Event>();

    public OrganizerPastFragment() {
    }

    public static OrganizerOngoingFragment newInstance() {
        return new OrganizerOngoingFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_organizer_past, container, false);

        db = FirebaseFirestore.getInstance();

        db.collection("Events")
                .whereEqualTo("organizerID", DeviceManager.getDeviceId())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Event event = document.toObject(Event.class);
                            if (event.getEventDate() != null && event.getEventDate().before(new Date())) {
                                pastEvents.add(event);
                            }
                            Log.d("test", event.getEventName() + " | Event ID: " + document.getId());
                        }
                    } else {
                        Log.d("Firestore", "Error getting past events: ", task.getException());
                    }
                });

        return view;
    }
}
