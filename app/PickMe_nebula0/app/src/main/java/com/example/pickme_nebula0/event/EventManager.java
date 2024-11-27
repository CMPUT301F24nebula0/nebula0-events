package com.example.pickme_nebula0.event;

import android.util.Log;

import androidx.annotation.Nullable;

import com.example.pickme_nebula0.db.DBManager;
import com.example.pickme_nebula0.user.User;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

// static class for Event-related DB functionality
public class EventManager {
    private static DBManager dbm = new DBManager();
    private static FirebaseFirestore db = dbm.db;
    private static String event_manager_tag = "EventManager";

    public enum EventStatus {
        PAST, ONGOING
    }

    /**
     * Checks if the waitlist of a given event is full.
     * Runs a success callback if not full, and a failure callback if full.
     * Use this when a user attempts to register for an event.
     * @param eventID
     * @param waitlistNotFullCallback
     * @param waitlistFullCallback
     */
    public static void waitlist_full(String eventID, DBManager.Void2VoidCallback waitlistNotFullCallback, DBManager.Void2VoidCallback waitlistFullCallback) {
        dbm.getEvent(eventID, (eventObj) -> {
            Event event = (Event) eventObj;
            int waitlist_capacity = event.getWaitlistCapacity();
            if (waitlist_capacity == -1) {
                // unlimited capacity
                waitlistNotFullCallback.run();
            } else {
                // check if there is still space
                // get number of users in waitlist
                dbm.loadAllUsersRegisteredInEvent(eventID, DBManager.RegistrantStatus.WAITLISTED, (usersObj) -> {
                    List<User> users = (List<User>) usersObj;
                    int waitlist_available_spots = waitlist_capacity - users.size();
                    if (waitlist_available_spots == 0) {
                        waitlistFullCallback.run();
                    } else if (waitlist_available_spots > 0) {
                        waitlistNotFullCallback.run();
                    } else {
                        // probably due to testing
                        Log.d(event_manager_tag, String.format("WARNING: Number of waitlisted entrants (%d) is greater than waitlist capacity (%d) at time of checking", users.size(), waitlist_capacity));
                        waitlistFullCallback.run();
                    }
                });
            }

        }, () -> { Log.d(event_manager_tag, "Failed to fetch event to check waitlist capacity"); });
    }


    /**
     * For admin to fetch a list of all existing events
     * Passes list of events (arraylist typecasted as Object)
     * to callback if successful,
     * otherwise runs failure callback.
     *
     * @param onSuccessCallback
     * @param onFailureCallback
     */
    public static void get_all_events(DBManager.Obj2VoidCallback onSuccessCallback, DBManager.Void2VoidCallback onFailureCallback) {
        db.collection(dbm.eventsCollection)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot querySnapshots, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            Log.e(event_manager_tag, error.toString());
                            onFailureCallback.run();
                        }

                        if (querySnapshots != null) {
                            // fetch all hashed qr codes and pass to on success callback
                            ArrayList<Event> events = new ArrayList<>();
                            for (QueryDocumentSnapshot doc : querySnapshots) {
                                Event e = doc.toObject(Event.class);
                                events.add(e);
                            }
                            Log.d(event_manager_tag, String.format("Fetched %d events", events.size()));
                            onSuccessCallback.run(events);
                        }
                    }
                });
    }


}
