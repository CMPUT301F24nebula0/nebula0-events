package com.example.pickme_nebula0.entrant.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.pickme_nebula0.DeviceManager;
import com.example.pickme_nebula0.R;
import com.example.pickme_nebula0.db.DBManager;
import com.example.pickme_nebula0.event.Event;
import com.example.pickme_nebula0.event.EventDetailUserActivity;
import com.example.pickme_nebula0.event.EventsArrayAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

/**
 * Fragment for displaying a list of events of given status
 */
public class EntrantEventsFragment extends Fragment {
    private FirebaseFirestore db;
    private ArrayList<Event> events;
    private ListView eventList;
    private EventsArrayAdapter eventAdapter;
    private String status;

    /**
     * Constructor takes status of event types to display
     * @param status only events where user has this registration status will be shown
     */
    public EntrantEventsFragment(String status){
        this.status = status;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_entrant_eventlist, container, false);

        db = FirebaseFirestore.getInstance();

        events = new ArrayList<>();
        eventList = rootView.findViewById(R.id.entrant_waitlisted_events_listview);
        eventAdapter = new EventsArrayAdapter(getActivity(), R.id.item_event, events);
        eventList.setAdapter(eventAdapter);

        eventList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long id) {
                // On-click, go the EventDetailUserActivity
                Event clickedEvent = (Event) adapterView.getItemAtPosition(pos);

                Intent intent = new Intent(getActivity(), EventDetailUserActivity.class);
                intent.putExtra("eventID",clickedEvent.getEventID());
                startActivity(intent);
            }
        });

        loadEvents();
        return rootView;
    }


    /**
     * Updates the list of messages with all messages stored for this user in the data base
     */
    public void loadEvents(){
        events.clear();
        eventAdapter.notifyDataSetChanged();
        CollectionReference registeredEventsCol = db.collection("Users").document(DeviceManager.getDeviceId()).collection("RegisteredEvents");

        registeredEventsCol.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        if (document.getString("status").equalsIgnoreCase(status)){
                            DocumentReference eventDocRef = db.collection("Events").document(document.getId());
                            eventDocRef.get()
                                    .addOnSuccessListener(eventDoc -> {
                                        if (eventDoc.exists()) {
                                            Event event = eventDoc.toObject(Event.class);
                                            events.add(event);
                                            eventAdapter.notifyDataSetChanged();
                                        }
                                    });
                        }
                    }
                }
            }
        });
    }

}
