package com.example.pickme_nebula0.entrant.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import com.example.pickme_nebula0.R;
import com.example.pickme_nebula0.entrant.EntrantRole;
import com.example.pickme_nebula0.event.Event;
import com.example.pickme_nebula0.event.WaitlistEventAdapter;

import java.util.ArrayList;
import java.util.Date;

public class EntrantWaitlistContentFragment extends Fragment {

    private EntrantRole entrant;
    private ListView eventList;
    private ArrayList<Event> dataList;
    private WaitlistEventAdapter waitlistEventAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // fragment_entrant_waitlist.xml
        View rootView = inflater.inflate(R.layout.fragment_entrant_waitlist_content, container, false);

        // TESTING SETUP
        this.entrant = new EntrantRole(
                "somedeviceID", "entrantname", "e@mail.com",
                "1234567890", ""
        );

        // sample data
        // TODO: remove
        String[] eventTitles = {"Event 1", "Event 2", "Event 3"};
        String[] eventDescriptions = {
                "Description for Event 1", "Description for Event 2", "Description for Event 3"
        };
        int[] eventCapacity = {10, 20, 30};
        int[] waitingListCapacity = {11, 21, 31};

        // initialize data list
        for (int i = 0; i < eventTitles.length; i++) {
            // create new event
            Event eventToAdd = new Event(
                    eventTitles[i],
                    eventDescriptions[i],
                    null,
                    false,
                    0,
                    false,
                    eventCapacity[i],
                    waitingListCapacity[i]
            );
            // add event to entrant role
            entrant.joinWaitlist(eventToAdd);
        }

        // initialize listview and adapter
        eventList = rootView.findViewById(R.id.entrant_waitlist_content_list);
        waitlistEventAdapter = new WaitlistEventAdapter(getActivity(),  entrant.getEventsInWaitlist());
        eventList.setAdapter(waitlistEventAdapter);

        return rootView;
    }
}
