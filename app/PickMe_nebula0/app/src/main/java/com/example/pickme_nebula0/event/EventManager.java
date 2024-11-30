package com.example.pickme_nebula0.event;

import static com.example.pickme_nebula0.db.FBStorageManager.eventPosterFieldName;

import android.util.Log;

import androidx.annotation.Nullable;

import com.example.pickme_nebula0.db.DBManager;
import com.example.pickme_nebula0.db.FBStorageManager;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

/**
 * static class for Event-related DB functionality
 */
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
     * Passes event as DocumentSnapshot to success callback.
     *
     * @param eventID ID of event we are checking the waitlist for
     * @param waitlistNotFullCallback function to run if waitlist is not full
     * @param waitlistFullCallback function to run if waitlist is full
     */

    public static void checkWaitlistFull(String eventID,
                                         DBManager.Obj2VoidCallback waitlistNotFullCallback,
                                         DBManager.Void2VoidCallback waitlistFullCallback) {
        checkWaitlistFull(eventID, waitlistNotFullCallback, waitlistFullCallback, (error_message) -> {});
    }

    public static void checkWaitlistFull(String eventID,
                                         DBManager.Obj2VoidCallback waitlistNotFullCallback,
                                         DBManager.Void2VoidCallback waitlistFullCallback,
                                         String2VoidCallback onFailureCallback) {

        getEventDoc(eventID, (eventDocSnapshotObj) -> {
            DocumentSnapshot eventDocSnapshot = (DocumentSnapshot) eventDocSnapshotObj;

            getEventFromDocSnapshot(eventDocSnapshot, (event) -> {
                int waitlist_capacity = event.getWaitlistCapacity();
                if (waitlist_capacity == -1) {
                    // unlimited capacity
                    waitlistNotFullCallback.run(eventDocSnapshotObj);
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
                                    waitlistNotFullCallback.run(eventDocSnapshot);
                                } else {
                                    // probably due to testing
                                    Log.d(event_manager_tag, String.format("WARNING: Number of waitlisted entrants (%d) is greater than waitlist capacity (%d) at time of checking", registeredUsers, waitlist_capacity));
                                    waitlistFullCallback.run();
                                }

                            }
                        }
                    });
                }
            }, (eventLoadErrorMessage) -> {
                Log.d(event_manager_tag, "Could not load event to check waitlist capacity: "+eventLoadErrorMessage);
            });


        }, (eventDocError) -> {
            Log.d(event_manager_tag, "Failed to fetch event to check waitlist capacity");
            onFailureCallback.run(eventDocError);
        });
    }


    /**
     * For admin to fetch a list of all existing events
     * Passes list of events (arraylist typecasted as Object)
     * to callback if successful,
     * otherwise runs failure callback.
     *
     * @param onSuccessCallback function called if we succeeded retrieving events
     * @param onFailureCallback function called if we failed retrieving events
     */
    public static void getAllEvents(DBManager.Obj2VoidCallback onSuccessCallback, DBManager.Void2VoidCallback onFailureCallback) {
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
     * using a document snapshot, load an event obj, has error handling
     * @param eventDocSnapshot Document Snapshot of event document
     * @param onSuccessCallback function called if successful
     * @param onFailureCallback function called if we fail
     */
    public static void getEventFromDocSnapshot(DocumentSnapshot eventDocSnapshot, Event2VoidCallback onSuccessCallback,
                                               String2VoidCallback onFailureCallback){

        Event event = null;
        try { event = eventDocSnapshot.toObject(Event.class); }
        catch (Exception e) {
            onFailureCallback.run("Error converting eventDocSnapshot to Event object: "+e.toString());
        }

        if (event != null) {
            onSuccessCallback.run(event);
        } else {
            onFailureCallback.run("Loaded event is null");
        }

    }


    /**
     * Utility function. Some calling functions need the document snapshot itself
     * rather than the event object.
     * @param eventID ID of event of interest
     * @param onSuccessCallback function called if we succeed in getting event doc
     * @param onFailureCallback function called if we fail to get event doc
     */
    public static void getEventDoc(String eventID, DBManager.Obj2VoidCallback onSuccessCallback, String2VoidCallback onFailureCallback) {
        // copied from DBManager
        DocumentReference eventDocRef = db.collection(dbm.eventsCollection).document(eventID);
        eventDocRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    onSuccessCallback.run(document);

                } else { onFailureCallback.run("Event does not exist at path: "+eventDocRef.getPath()); }
            }
            else {
                onFailureCallback.run("Could not fetch event from database");
            }
        });
    }

    //----------------ADMIN FUNCTIONALITY
    // event-related functionality used by the admin

    /**
     * Removes poster from backend and display
     *
     * @param eventID ID of event we want to remove poster for
     * @param onSuccessCallback function to call if successful
     * @param onFailureCallback function to call on failure
     */
    public static void removePoster(String eventID, DBManager.Void2VoidCallback onSuccessCallback, DBManager.Void2VoidCallback onFailureCallback) {
        FBStorageManager.deleteEventPosterFromStorage(eventID, () -> {
            // removed poster from storage
            // update event docs
            DocumentReference eventDocRef = db.collection(dbm.eventsCollection).document(eventID);
            dbm.updateField(eventDocRef, eventPosterFieldName, null);
            onSuccessCallback.run();

        }, onFailureCallback);
    }

}

