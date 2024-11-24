package com.example.pickme_nebula0.event;

import android.util.Log;

import com.example.pickme_nebula0.db.DBManager;
import com.example.pickme_nebula0.user.User;

import java.util.ArrayList;
import java.util.List;

// static class for Event-related DB functionality
public class EventManager {
    private static DBManager dbm = new DBManager();
    private static String event_manager_tag = "EventManager";

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
}
