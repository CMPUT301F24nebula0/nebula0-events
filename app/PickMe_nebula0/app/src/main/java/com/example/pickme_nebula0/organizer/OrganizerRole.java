package com.example.pickme_nebula0.organizer;

import android.util.Log;

import com.example.pickme_nebula0.db.DBManager;
import com.example.pickme_nebula0.entrant.EntrantRole;
import com.example.pickme_nebula0.event.Event;
import com.example.pickme_nebula0.user.User;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;


/**
 * OrganizerRole
 */
public class OrganizerRole extends User {
    private String organizerID;
    private ArrayList<Event> events = new ArrayList<Event>();

    private static String usersSelectedKey = "selected";
    private static String usersNotSelectedKey = "not selected";
    private static String organizer_tag = "OrganizerRole";  // for logging
    private static DBManager dbm = new DBManager();

    /**
     * Constructor
     */
    public OrganizerRole() {
        organizerID = null;
    }

    /**
     * Get organizerID
     * @return organizerID organizerID
     */
    public String getOrganizerID() {
        return this.organizerID;
    }

    /**
     * Set organizerID
     * @param organizerID organizerID
     */
    public void setOrganizerID(String organizerID) {
        this.organizerID = organizerID;
    }

    public boolean createEvent() {
//        boolean eventCreated = false;
//        // Create event
//        Event event = new Event();
//        events.add(event);
//        //
//        return false;
        throw new RuntimeException("NOT IMPLEMENTED");
    }

    public boolean generateQRCode() {
        boolean QRCodeGenerated = false;
        // Generate QR code
        return false;
    }

    public boolean storeHashDataQRCode() {
        boolean hashDataStored = false;
        // Store hash data
        return false;
    }

    public boolean deleteHashDataQRCode() {
        boolean hashDataDeleted = false;
        // Delete hash data
        return false;
    }

    public ArrayList<EntrantRole> viewWaitlist(Event event) {
        ArrayList<EntrantRole> entrantsInWaitlist = new ArrayList<EntrantRole>();

        // in future, get the list of entrants who joined the event waiting list
        return entrantsInWaitlist;
    }

    public void viewMapOfWaitlist(Event event) {
        // in future, show the map of entrants who joined the event waiting list
    }

    public int getWaitlistCapacity(Event event) {
        return event.getWaitlistCapacity();
    }

    public void setWaitlistCapacity(Event event, int capacity) {
        event.setWaitlistCapacity(capacity);
    }

    public String getEventPoster(Event event) {
        return event.getEventPoster();
    }

    public void setEventPoster(Event event, String eventPoster) {
        event.setEventPoster(eventPoster);
    }


    /**
     * US 02.05.02
     * As an organizer I want to set the system to sample a specified number of attendees to register for the event
     *
     * Samples all users in the waitlist, based on the event capacity of the given event,
     * or samples all if event capacity is none.
     * Sets their status to SELECTED and updates the corresponding EventRegistrants status.
     * Passes the list of selected users to onSuccessCallback (as an Object.)
     * @param eventID
     * @param onSuccessCallback
     */
    public static void sampleAndSelectUsers(String eventID, DBManager.Obj2VoidCallback onSuccessCallback) {
        DBManager dbManager = new DBManager();

        // get waitlist capacity first via event
        dbManager.getEvent(eventID, (eventObj) -> {
            Event event = (Event) eventObj;
            int eventCapacity = event.getEventCapacity();

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
     * US 02.05.03
     * As an organizer I want to be able to draw a replacement applicant from the pooling system
     * when a previously selected applicant cancels or rejects the invitation.
     *
     * Resample users whose status remains waitlisted by the number of free spots,
     * which is the difference between the event capacity and number of selected users (if any.)
     * Sets their status to SELECTED.
     * Passes the list of resampled users to onSuccessCallback (as an Object.)
     * @param eventID
     * @param onSuccessCallback
     */
    public static void resampleAndSelectUsers(String eventID, DBManager.Obj2VoidCallback onSuccessCallback) {
        // assumes that event capacity is defined, otherwise there would be no one to resample
        // get event capacity and (number of entrants who were selected + confirmed entrants)
        // the difference is the number of free spots to be filled by resampling
        DBManager dbManager = new DBManager();

        // get event capacity
        dbManager.getEvent(eventID, (eventObj) -> {
            Event event = (Event) eventObj;
            int eventCapacity = event.getEventCapacity();
//            assert(eventCapacity != -1);
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


    public ArrayList<EntrantRole> getInvitedEntrants(Event event) {
        return event.getEntrantsChosen();
    }

    // US 02.06.02 As an organizer I want to see a list of all the cancelled entrants
    public ArrayList<EntrantRole> getCancelledEntrants(Event event) {
        return event.getEntrantsCancelled();
    }

    // US 02.06.03 As an organizer I want to see a final list of entrants who enrolled for the event
    public ArrayList<EntrantRole> getEnrolledEntrants(Event event) {
        return event.getEntrantsEnrolled();
    }

    // US 02.06.05 As an organizer I want to cancel entrants that declined signing up for the event
    public void cancelEntrants(Event event, EntrantRole entrant) {
        // cancelEntrant performs error checking
        event.cancelEntrant(entrant);
    }

    //---------- NOTIFICATIONS
    // the following user stories are covered by notifyEntrantsByStatus in DBManager:
    // US 02.07.01 As an organizer I want to send notifications to all entrants on the waiting list
    // US 02.07.02 As an organizer I want to send notifications to all selected entrants
    // US 02.07.03 As an organizer I want to send a notification to all canceled entrants

    public boolean notifyEntrantsByStatus(String eventID, String title, String message, DBManager.RegistrantStatus status) {
        dbm.notifyEntrantsOfStatus(title, message, eventID, status);
        return true;
    }

    // US 02.05.01 As an organizer I want to send a notification to chosen entrants to sign up for events
    // called for each selected entrant (sampled or resampled)
    // defines title, message, and updates Notification documents
    // function is for individual entrants because functions that list
    // all users are callback functions and this makes the most sense
    public static boolean notifyEntrantChosen(Event event, User user) {
        if (!user.getNotificationsEnabled()) {return false;}
        String title = "Selected For Event";
        String message = "You have been selected to join the following event: " + event.getEventName();
        dbm.createNotification(title, message, user.getUserID(), event.getEventID());
        return true;
    }

    public static boolean notifyEntrantResampled(Event event, User user) {
        if (!user.getNotificationsEnabled()) {return false;}
        String title = "Selected For Event";
        String message = "You have been selected to join the following event, since some have declined their invite: " + event.getEventName();
        dbm.createNotification(title, message, user.getUserID(), event.getEventID());
        return true;
    }

    public static boolean notifyEntrantNotChosen(Event event, User user) {
        if (!user.getNotificationsEnabled()) {return false;}
        String title = "Not Selected For Event";
        String message = "You have not been sampled for the following event, but you will remain waitlisted in case a spot opens up: " + event.getEventName();
        dbm.createNotification(title, message, user.getUserID(), event.getEventID());
        return true;
    }

    public static boolean notifyEntrantCancelled(Event event, User user) {
        if (!user.getNotificationsEnabled()) {return false;}
        String title = "Cancelled Event Invite";
        String message = "Your invitation to join the following event has been cancelled: " + event.getEventName();
        dbm.createNotification(title, message, user.getUserID(), event.getEventID());
        return true;
    }

    // -------------- UTILITY FUNCTIONS
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

    // for resampling users
    private static void countConfirmedAndSelected(String eventID, DBManager.Obj2VoidCallback onSuccessCallback) {
//        DBManager dbm = new DBManager();
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
