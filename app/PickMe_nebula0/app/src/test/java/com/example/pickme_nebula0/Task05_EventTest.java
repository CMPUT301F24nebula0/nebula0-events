package com.example.pickme_nebula0;

import org.junit.*;
import static org.junit.Assert.*;
import android.util.Log;

import com.example.pickme_nebula0.event.Event;
import com.example.pickme_nebula0.entrant.EntrantRole;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Date;

/*
DOESNT WORK UNLESS DEVICE ID AND FIREBASE FUNCTIONALITY ARE COMMENTED OUT
related: https://developer.android.com/training/testing/local-tests#mocking-dependencies
 */

public class Task05_EventTest {
    private Event event;
    private ArrayList<EntrantRole> entrants;

    @Before
    public void setup() throws Exception {
        int entrant_num = 5;
        event = new Event("event1", "event1 description", new Date(), 50, 70, -1);
//        entrants = setup_entrants(entrant_num);
    }

    public ArrayList<EntrantRole> setup_entrants(int entrant_num) {
        ArrayList<EntrantRole> entrants = new ArrayList<>();

        for (int i=0; i<entrant_num; i++) {
            String entrantID = String.valueOf(i);
            EntrantRole entrant = new EntrantRole(entrantID, "Entrant"+entrantID, String.format("entrant%d@gmail.com", i), String.format("780-%d00-0000", i), null);
            entrants.add(entrant);
        }

        return entrants;
    }

    @Test
    public void some_test() {
        assertEquals(1, 1);
    }


    // US 02.03.01 As an organizer I want to OPTIONALLY limit the number of entrants who can join my waiting list
    @Test
    public void waiting_list_test() {
        assertEquals(0, event.getEntrantsInWaitingList().size());

        int waitlist_capacity = 5;
        int entrant_num = waitlist_capacity+3;
        event.setWaitingListCapacity(waitlist_capacity);
        entrants = setup_entrants(entrant_num);

        for (int i=0; i<waitlist_capacity; i++) { event.addEntrantToWaitingList(entrants.get(i)); }

        assertTrue(event.waitingListFull());

        boolean entrantAdded = event.addEntrantToWaitingList(entrants.get(entrant_num-1));
        assertTrue(!entrantAdded);
        assertEquals(event.getEntrantsInWaitingList().size(), waitlist_capacity);
    }

    public Event setup_event(ArrayList<EntrantRole> entrants, int event_capacity) {
        Event event = new Event(String.format("EventWith%dEntrants", event_capacity), "description", new Date(), event_capacity, -1, -1);
        for (int i=0; i<entrants.size(); i++) {
            event.addEntrantToWaitingList(entrants.get(i));
        }

        return event;
    }


    // US 02.05.02 As an organizer I want to set the system to sample a specified number of attendees to register for the event
    @Test
    public void sample_entrants() {
        int entrant_num = 10;
        int sample_num = 5;

        ArrayList<EntrantRole> entrants = setup_entrants(entrant_num);
        Event event = setup_event(entrants, sample_num);

        event.sampleEntrants();

        ArrayList<EntrantRole> chosen = event.getEntrantsChosen();
        assertEquals(event.getEntrantsChosen().size(), sample_num);
    }


    // US 02.05.03 As an organizer I want to be able to draw a replacement applicant from the pooling system
    // when a previously selected applicant cancels or rejects the invitation
    // AND
    // US 02.06.04 As an organizer I want to cancel entrants that did not sign up for the event
    @Test
    public void resample_entrants() {
        int entrant_num = 10;
        int sample_num = 5;

        ArrayList<EntrantRole> entrants = setup_entrants(entrant_num);
        Event event = setup_event(entrants, sample_num);

        event.sampleEntrants();

        // resampling does nothing if no empty spots
        int chosen_entrants = event.getEntrantsChosen().size();
        event.resampleEntrants();
        assertEquals(chosen_entrants, event.getEntrantsChosen().size());

        // cancel entrants and resample
        int size_before_cancel = event.getEntrantsChosen().size();
        int entrants_to_cancel = 2;

        for (int i=0; i<entrants_to_cancel; i++) {
            EntrantRole cancelled_entrant = event.getEntrantsChosen().get(i);
            System.out.println("cancelled entrant id: "+cancelled_entrant.getUserID());
            boolean entrant_cancelled = event.cancelEntrant(cancelled_entrant);
            assertTrue(entrant_cancelled);
            assertEquals(event.getEntrantsChosen().size(), size_before_cancel-(i+1));
        }

        event.resampleEntrants();
        assertEquals(event.getEntrantsCancelled().size(), entrants_to_cancel);
        assertEquals(chosen_entrants, event.getEntrantsChosen().size());
    }

}
