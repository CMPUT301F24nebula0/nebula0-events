package com.example.pickme_nebula0.organizer;

import android.util.Log;

import androidx.annotation.Nullable;

import com.example.pickme_nebula0.db.DBManager;
import com.example.pickme_nebula0.event.Event;
import com.example.pickme_nebula0.event.EventManager;
import com.example.pickme_nebula0.user.User;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;


/**
 * Represents the role of an organizer in the application.
 *
 * This class extends the `User` class and provides functionalities specific to organizers,
 * such as event management, sampling and selecting users for events, and sending notifications.
 *
 * @see User
 * @see DBManager
 * @see EventManager
 */
public class OrganizerRole extends User {
    private String organizerID;

    private static String usersSelectedKey = "selected";
    private static String usersNotSelectedKey = "not selected";
    private static String organizer_tag = "OrganizerRole";  // for logging
    private static DBManager dbm = new DBManager();
    private static FirebaseFirestore db = dbm.db;

    /**
     * Default constructor for the `OrganizerRole` class.
     *
     * Initializes the organizer with a null organizer ID.
     */
    public OrganizerRole() {
        organizerID = null;
    }

    /**
     * Set organizerID
     * @param organizerID organizerID
     */
    public void setOrganizerID(String organizerID) {
        this.organizerID = organizerID;
    }

    //--------------- EVENT MANAGEMENT

    /**
     * Use to fetch past or ongoing events for organizer home activity.
     * @param organizerID ID of organizer
     * @param event_status status of interests for events we are retrieving
     * @param onSuccessCallback function to call on success
     * @param onFailureCallback function to call on failure
     */
    public static void get_event_by_status(String organizerID, EventManager.EventStatus event_status, DBManager.Obj2VoidCallback onSuccessCallback, DBManager.Void2VoidCallback onFailureCallback) {
        db.collection(dbm.eventsCollection)
            .whereEqualTo("organizerID", organizerID)
            .addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot querySnapshots, @Nullable FirebaseFirestoreException error) {
                    if (error != null) {
                        Log.w(organizer_tag, "Listen failed.", error);
                        onFailureCallback.run();
                    }

                    // pass all ongoing events to success callback
                    ArrayList<Event> events_matching_status = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : querySnapshots) {
                        if (doc == null) { Log.d(organizer_tag, "ongoing event doc was null"); continue; }
                        Event event = doc.toObject(Event.class);
                        Date event_date = event.getEventDate();

                        if (event_date != null ) {
                            if (event_status == EventManager.EventStatus.PAST) {
                                // event date should come before current date
                                if (!event_date.before(new Date())) { continue; }

                            } else if (event_status == EventManager.EventStatus.ONGOING) {
                                // event date should come after current date
                                if (event_date.before(new Date())) { continue; }
                            }

                            // event matches desired status
                            events_matching_status.add(event);

                        }
                    }

                    Log.d(organizer_tag, String.format("Fetched %d %s events", events_matching_status.size(), event_status.toString()));
                    onSuccessCallback.run(events_matching_status);
                }
            });
    }


    /**
     * Samples all users in the waitlist, based on the event capacity of the given event,
     * or samples all if event capacity is none.
     * Sets their status to SELECTED and updates the corresponding EventRegistrants status.
     * Passes the list of selected users to onSuccessCallback (as an Object.)
     * @param eventID ID of event of interest
     * @param onSuccessCallback function run if successful
     */
    public static void sampleAndSelectUsers(String eventID, DBManager.Obj2VoidCallback onSuccessCallback) {
        DBManager dbManager = new DBManager();

        // get waitlist capacity first via event
        dbManager.getEvent(eventID, (eventObj) -> {
            Event event = (Event) eventObj;
            int eventCapacity = event.getNumberOfAttendees();

            // get random sample of users
            sampleUsers(eventID, eventCapacity, (sampleResultsObj) -> {
                HashMap<String, ArrayList<User>> sampleResults = (HashMap<String, ArrayList<User>>) sampleResultsObj;

                ArrayList<User> usersSelected = sampleResults.get(usersSelectedKey);
                ArrayList<User> usersNotSelected = sampleResults.get(usersNotSelectedKey);

                Log.d(organizer_tag, java.lang.String.format("Sampled %s users from event capacity %s", usersSelected.size(), eventCapacity));

                // update status of selected entrants and notify
                for (User user : usersSelected) {
                    Log.d(organizer_tag, java.lang.String.format("Sampled user %s for initial event selection", user.getName()));
                    dbManager.setRegistrantStatus(eventID, user.getUserID(), DBManager.RegistrantStatus.SELECTED);
                    notifyEntrantChosen(event, user);
                }

                // notify entrants not sampled
                for (User user : usersNotSelected) {
//                    Log.d(organizer_tag, java.lang.String.format("Did not sample user %s for initial event selection", user.getName()));
                    notifyEntrantNotChosen(event, user);
                }
                onSuccessCallback.run(usersSelected);
            });

        }, () -> {Log.d(organizer_tag, "Could not fetch event to check event capacity.");});
    }

    /**
     * Resample users whose status remains waitlisted by the number of free spots,
     * which is the difference between the event capacity and number of selected users (if any.)
     * Sets their status to SELECTED.
     * Passes the list of resampled users to onSuccessCallback (as an Object.)
     * @param eventID ID of event of interest
     * @param onSuccessCallback function run on success
     */
    public static void resampleAndSelectUsers(String eventID, DBManager.Obj2VoidCallback onSuccessCallback) {
        // assumes that event capacity is defined, otherwise there would be no one to resample
        // get event capacity and (number of entrants who were selected + confirmed entrants)
        // the difference is the number of free spots to be filled by resampling
        DBManager dbManager = new DBManager();

        // get event capacity
        dbManager.getEvent(eventID, (eventObj) -> {
            Event event = (Event) eventObj;
            int eventCapacity = event.getNumberOfAttendees();
            if (eventCapacity == -1) {return;}

            // get number of entrants who are selected and confirmed
            countConfirmedAndSelected(eventID, (countObj) -> {
                int count = (int) countObj;
                int free_spots = eventCapacity - count;
                Log.d(organizer_tag, String.format("Free spots %d = %d-%d", free_spots, eventCapacity, count));
                if (free_spots > 0) {
                    // resample by number of free spots
                    sampleUsers(eventID, free_spots, (sampleResultsObj) -> {
                        HashMap<String, ArrayList<User>> sampleResults = (HashMap<String, ArrayList<User>>) sampleResultsObj;

                        ArrayList<User> usersResampled = sampleResults.get(usersSelectedKey);
                        for (User user : usersResampled) {
                            // set new status and notify
                            dbManager.setRegistrantStatus(eventID, user.getUserID(), DBManager.RegistrantStatus.SELECTED);
                            notifyEntrantResampled(event, user);
                        }
                        onSuccessCallback.run(usersResampled);
                    });
                } else { Log.d(organizer_tag, "No entrants to resample"); }
            });
            }, () -> {Log.d(organizer_tag, "failed to fetch event for resampling"); return;});
    }

    // cancel all users who are selected and did not accept their invite
    public static void cancelUsers(String eventID, DBManager.Obj2VoidCallback onSuccessCallback) {
        DBManager dbManager = new DBManager();

        dbManager.getEvent(eventID, (eventObj) -> {
            // load users registered in event
            dbManager.loadUsersRegisteredInEvent(eventID, DBManager.RegistrantStatus.SELECTED, (selectedUsersObj) -> {
                ArrayList<User> users = (ArrayList<User>) selectedUsersObj;
                for (User user:users) {
                    dbManager.setRegistrantStatus(eventID, user.getUserID(), DBManager.RegistrantStatus.CANCELLED);
                    notifyEntrantCancelled((Event) eventObj, user);
                }
            });

        }, () -> {Log.d(organizer_tag, "Could not fetch event for cancelling users");});
    }


    /**
     * Utility function that randomly samples users registered for an event
     * and passes sampled and not sampled users to the onSuccessCallback
     * as a Hashmap typecasted to as an Object.
     * The hashmap has the following keys:
     *  usersSelectedKey
     *  usersNotSelectedKey
     * @param eventID
     * @param sampleNum
     * @param onSuccessCallback
     */
    public static void sampleUsers(String eventID, int sampleNum, DBManager.Obj2VoidCallback onSuccessCallback) {
        DBManager dbManager = new DBManager();

        // load users registered in event
        // then shuffle and sample
        dbManager.loadAllUsersRegisteredInEvent(eventID, DBManager.RegistrantStatus.WAITLISTED,
                (userListObj) -> {
                    ArrayList<User> users = new ArrayList<>((List<User>) userListObj);
                    ArrayList<User> usersSelected = new ArrayList<>();
                    ArrayList<User> usersNotSelected = new ArrayList<>();

                    if (sampleNum == -1 || users.size() <= sampleNum) {
                        usersSelected.addAll(users);
                    } else {
                        ArrayList<User> randomUsers = new ArrayList<>(users);
                        Collections.shuffle(randomUsers);
                        usersSelected = new ArrayList<>(randomUsers.subList(0, sampleNum));
                        usersNotSelected = new ArrayList<>(randomUsers.subList(sampleNum, randomUsers.size()));
                    }

                    HashMap<String, ArrayList<User>> samplingResult = new HashMap<>();
                    samplingResult.put(usersSelectedKey, usersSelected);
                    samplingResult.put(usersNotSelectedKey, usersNotSelected);

                    onSuccessCallback.run(samplingResult);

                });
    }


    /**
     * Notifies users they have been selected for an event
     * @param event event of interest
     * @param user user to notify
     * @return true if that user had notifications enabled, else false
     */
    public static boolean notifyEntrantChosen(Event event, User user) {
        if (!user.getNotificationsEnabled()) {return false;}
        String title = "Selected For Event";
        String message = "You have been selected to join the following event: " + event.getEventName();
        dbm.createNotification(title, message, user.getUserID(), event.getEventID());
        return true;
    }

    /**
     * Notifies entrant they have been selected for an event due to resampling
     * @param event event of interest
     * @param user user to notify
     * @return true if that user had notifications enabled, else false
     */
    public static boolean notifyEntrantResampled(Event event, User user) {
        if (!user.getNotificationsEnabled()) {return false;}
        String title = "Selected For Event";
        String message = "You have been selected to join the following event, since some have declined their invite: " + event.getEventName();
        dbm.createNotification(title, message, user.getUserID(), event.getEventID());
        return true;
    }

    /**
     * Notifies entrant that they have not been chosen for an event
     * @param event event of interest
     * @param user user to notify
     * @return true if that user had notifications enabled, else false
     */
    public static boolean notifyEntrantNotChosen(Event event, User user) {
        if (!user.getNotificationsEnabled()) {return false;}
        String title = "Not Selected For Event";
        String message = "You have not been sampled for the following event, but you will remain waitlisted in case a spot opens up: " + event.getEventName();
        dbm.createNotification(title, message, user.getUserID(), event.getEventID());
        return true;
    }

    /**
     * Notifies entrant that they're invitation had been canceled
     * @param event event of interest
     * @param user user to notify
     * @return true if that user had notifications enabled, else false
     */
    public static boolean notifyEntrantCancelled(Event event, User user) {
        if (!user.getNotificationsEnabled()) {return false;}
        String title = "Cancelled Event Invite";
        String message = "Your invitation to join the following event has been cancelled: " + event.getEventName();
        dbm.createNotification(title, message, user.getUserID(), event.getEventID());
        return true;
    }

    // -------------- UTILITY FUNCTIONS

    /**
     * Checks if the event has entrants who have accepted but have yet to accept/decline
     * @param eventID ID of event of interest
     * @param entrantsExistCallback function to run if SELECTED entrants exist for this event
     * @param entrantsNotExistCallback function to run if no SELECTED entrants exist for this event
     */
    public static void sampledEntrantsExist(String eventID, DBManager.Void2VoidCallback entrantsExistCallback, DBManager.Void2VoidCallback entrantsNotExistCallback) {
        CollectionReference eventRegistrantsRef = dbm.db.collection(dbm.eventsCollection)
                .document(eventID)
                .collection(dbm.eventRegistrantsCollection);
        Task<QuerySnapshot> selectedQuery = eventRegistrantsRef.whereEqualTo(dbm.eventStatusKey, DBManager.RegistrantStatus.SELECTED).get();
        selectedQuery.addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot querySnapshot = task.getResult();
                int numSelected = querySnapshot.size();

                if (numSelected > 0) {
                    entrantsExistCallback.run();
                } else {
                    entrantsNotExistCallback.run();
                }
            } else {
                Log.d(organizer_tag, "Error getting selected entrants: " + task.getException());
            }
        });
    }

    /**
     * Counts the number of users with `CONFIRMED` or `SELECTED` status for an event.
     *
     * @param eventID             the ID of the event
     * @param onSuccessCallback   function to call with the count upon success
     */
    private static void countConfirmedAndSelected(String eventID, DBManager.Obj2VoidCallback onSuccessCallback) {

        // reference to EventRegistrants subcollection
        CollectionReference eventRegistrantsRef = dbm.db.collection(dbm.eventsCollection)
                .document(eventID)
                .collection(dbm.eventRegistrantsCollection);

        Task<QuerySnapshot> selectedQuery = eventRegistrantsRef.whereEqualTo(dbm.eventStatusKey, DBManager.RegistrantStatus.SELECTED).get();
        Task<QuerySnapshot> confirmedQuery = eventRegistrantsRef.whereEqualTo(dbm.eventStatusKey, DBManager.RegistrantStatus.CONFIRMED).get();

        // combine results
        Tasks.whenAllComplete(selectedQuery, confirmedQuery).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                int selectedCount = selectedQuery.getResult().size();
                int confirmedCount = confirmedQuery.getResult().size();
                onSuccessCallback.run(selectedCount + confirmedCount);
            } else {
                Log.e("Firestore", "Error counting selected and confirmed: ", task.getException());
            }
        });
    }
}
