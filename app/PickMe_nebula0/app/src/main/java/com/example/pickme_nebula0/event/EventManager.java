package com.example.pickme_nebula0.event;

import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.pickme_nebula0.db.DBManager;
import com.example.pickme_nebula0.db.FBStorageManager;
import com.example.pickme_nebula0.user.User;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

// static class for Event-related DB functionality
public class EventManager {
    private static DBManager dbm = new DBManager();
    private static FirebaseFirestore db = dbm.db;
    private static String event_manager_tag = "EventManager";

    public enum EventStatus {
        PAST, ONGOING
    }

    public interface Event2VoidCallback {
        void run(Event event);
    }

    public interface String2VoidCallback {
        void run(String event);
    }


    /**
     * Checks if the waitlist of a given event is full.
     * Runs a success callback if not full, and a failure callback if full.
     * Use this when a user attempts to register for an event.
     * @param eventID
     * @param waitlistNotFullCallback
     * @param waitlistFullCallback
     */

    public static void waitlist_full(String eventID,
                                     DBManager.Void2VoidCallback waitlistNotFullCallback,
                                     DBManager.Void2VoidCallback waitlistFullCallback) {
        waitlist_full(eventID, waitlistNotFullCallback, waitlistFullCallback, (error_message) -> {});
    }

    public static void waitlist_full(String eventID,
                                     DBManager.Void2VoidCallback waitlistNotFullCallback,
                                     DBManager.Void2VoidCallback waitlistFullCallback,
                                     String2VoidCallback onFailureCallback) {

        get_event(eventID, (eventObj) -> {
            Event event = (Event) eventObj;
            int waitlist_capacity = event.getWaitlistCapacity();
            if (waitlist_capacity == -1) {
                // unlimited capacity
                waitlistNotFullCallback.run();
            } else {
                // check if there is still space
                // get number of users in waitlist
                CollectionReference registeredUsers = db.collection(dbm.eventsCollection)
                        .document(eventID).collection(dbm.eventRegistrantsCollection);

                registeredUsers.addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot querySnapshots, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            Log.e(event_manager_tag, error.toString());
                            onFailureCallback.run("Error fetching registered users: "+error.toString());
                        }

                        if (querySnapshots != null) {
                            // get number of waitlisted users
                            int registeredUsers = 0;
                            for (QueryDocumentSnapshot doc : querySnapshots) {
                                if (!doc.exists()) { continue; }
                                registeredUsers += 1;
                            }

                            // compare with waitlist capacity and call appropriate callback
                            int waitlist_available_spots = waitlist_capacity - registeredUsers;
                            if (waitlist_available_spots == 0) {
                                waitlistFullCallback.run();
                            } else if (waitlist_available_spots > 0) {
                                waitlistNotFullCallback.run();
                            } else {
                                // probably due to testing
                                Log.d(event_manager_tag, String.format("WARNING: Number of waitlisted entrants (%d) is greater than waitlist capacity (%d) at time of checking", registeredUsers, waitlist_capacity));
                                waitlistFullCallback.run();
                            }

                        }
                    }
                });
            }

        }, (error_message) -> {
            Log.d(event_manager_tag, "Failed to fetch event to check waitlist capacity");
            onFailureCallback.run(error_message);
        });
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


    /**
     * Gets event associated with a given eventID as an Event object.
     * If successful in retrieving event, performs onSuccessCallback on event object.
     * If unsuccessful, perform onFailureCallback, which occurs under the following conditions:
     *      fetching event is unsuccessful
     *      event does not exist in database
     *      event loaded as Object is null
     * and passes the error message as a String.
     *
     * To be used within EventManager, but this can still be used for new code if necessary.
     *
     * @param eventID eventID of event to retrieve and perform onSuccessCallback on
     * @param onSuccessCallback operation to perform on retrieved Event object
     * @param onFailureCallback operation to perform if event cannot be retrieved
     */
    public static void get_event(String eventID, Event2VoidCallback onSuccessCallback,
                         String2VoidCallback onFailureCallback){

        // copied from DBManager
        DocumentReference eventDocRef = db.collection(dbm.eventsCollection).document(eventID);
        eventDocRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    Event event = document.toObject(Event.class);
                    if (event != null) {
                        onSuccessCallback.run(event);
                    } else {
                        onFailureCallback.run("Loaded event is null");
                    }

                } else { onFailureCallback.run("Event does not exist at path: "+eventDocRef.getPath()); }
            }
            else {
                onFailureCallback.run("Could not fetch event from database");
            }
        });
    }

    //----------------ADMIN FUNCTIONALITY
    // event-related functionality used by the admin

    public static void fetch_qr_code_hash(String eventID, String2VoidCallback onSuccessCallback, String2VoidCallback onFailureCallback) {
        get_event(eventID, (event) -> {
            String qr_code_data = event.getQrCodeData();
            if (qr_code_data == null || qr_code_data.equals("null")) {
                onFailureCallback.run("QR code data is null");
            } else {
                onSuccessCallback.run(qr_code_data);
            }
        }, (error_message) -> {

        });
    }


    /**
     * Fetches event details that are to be displayed to the admin view,
     * and formats them as strings.
     *
     * @param eventID
     * @param onSuccessCallback
     * @param onFailureCallback
     */
    public static void delete_qr_code(String eventID, Event2VoidCallback onSuccessCallback, String2VoidCallback onFailureCallback) {
        get_event(eventID, (event) -> {
            event.setQrCodeData("null");
            dbm.updateEvent(event);
            onSuccessCallback.run(event);

        }, (error_message) -> {onFailureCallback.run(error_message);});
    }


    /**
     * Fetches event details that are to be displayed to the admin view,
     * and formats them as strings.
     *
     * On success, passes a HashMap with the following keys:
     *      "Event Name"
     *      "Description"
     *      "Date"
     *      "Geolocation Required"
     *      "Geolocation Max Distance"
     *      "Waitlist Capacity"
     *
     *
     * @param eventID
     * @param onSuccessCallback
     * @param onFailureCallback
     */
    public static void admin_view_event_details(String eventID, DBManager.Obj2VoidCallback onSuccessCallback, String2VoidCallback onFailureCallback) {
        HashMap<String, String> event_details = new HashMap<>();

        get_event(eventID, (event) -> {
            try {
                event_details.put("Event Name", event.getEventName());
                event_details.put("Event Description", event.getEventDescription());
                event_details.put("Date", event.getEventDate().toString());
                event_details.put("Waitlist Capacity", String.valueOf(event.getWaitlistCapacity()));
                event_details.put("Geolocation Max Distance", String.valueOf(event.getGeolocationMaxDistance()));

                // these may not be necessary
                event_details.put("Waitlist Capacity Required", event.getWaitlistCapacityRequired() ? "Yes" : "No");
                event_details.put("Geolocation Required", event.getGeolocationRequired() ? "Yes" : "No");

                onSuccessCallback.run(event_details);
            } catch (Exception e) {
                onFailureCallback.run("Failed to format event details:" + e.getMessage());
            }

        }, (error_message) -> onFailureCallback.run(error_message));

    }


    /**
     * Passes the poster URI to callback if it exists for a given event.
     * Otherwise, runs posterNotFound callback.
     * Use for admin poster viewing.
     *
     * Feels safer to fetch directly from database instead of using getter
     * from event object.
     *
     * @param eventID
     * @param posterFoundCallback
     * @param posterNotFoundCallback
     */
    public static void event_has_poster(String eventID, String2VoidCallback posterFoundCallback, DBManager.Void2VoidCallback posterNotFoundCallback) {
        DocumentReference eventDoc = db.collection(dbm.eventsCollection).document(eventID);
        dbm.performIfFieldPopulated(eventDoc, FBStorageManager.event_poster_field, (posterURIObj) -> {
            posterFoundCallback.run((String) posterURIObj);
            Log.d(event_manager_tag, "poster found: " + (String) posterURIObj);
        }, () -> {
            posterNotFoundCallback.run();
            Log.d(event_manager_tag, "no poster found");
        });
    }
}

