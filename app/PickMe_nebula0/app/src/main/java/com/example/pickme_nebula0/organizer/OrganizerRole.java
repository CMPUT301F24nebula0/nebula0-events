package com.example.pickme_nebula0.organizer;

import static com.example.pickme_nebula0.db.DBManagerStatic.notifyEntrantsOfStatus;

import android.util.Log;

import com.example.pickme_nebula0.db.DBManager;
import com.example.pickme_nebula0.entrant.EntrantRole;
import com.example.pickme_nebula0.event.Event;
import com.example.pickme_nebula0.user.User;
import com.google.common.collect.Sets;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

/**
 * OrganizerRole
 */
public class OrganizerRole extends User {
    private String organizerID;
    private ArrayList<Event> events = new ArrayList<Event>();
    private DBManager dbManager = new DBManager();

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

    // US 02.01.01 As a an organizer I want to create a new event and generate a unique promotional QR code that links to the event description and event poster in the app
    public boolean createEvent() {
//        boolean eventCreated = false;
//        // Create event
//        Event event = new Event();
//        events.add(event);
//        //
//        return false;
        throw new RuntimeException("NOT IMPLEMENTED");
    }

    // US 02.01.01 As a an organizer I want to create a new event and generate a unique promotional QR code that links to the event description and event poster in the app
    public boolean generateQRCode() {
        boolean QRCodeGenerated = false;
        // Generate QR code
        return false;
    }

    // US 02.01.04 As an organizer I want to store hash data of the generated QR code in my database
    public boolean storeHashDataQRCode() {
        boolean hashDataStored = false;
        // Store hash data
        return false;
    }

    // US 02.01.04 As an organizer I want to store hash data of the generated QR code in my database
    public boolean deleteHashDataQRCode() {
        boolean hashDataDeleted = false;
        // Delete hash data
        return false;
    }

    // US 02.02.01 As an organizer I want to view the list of entrants who joined my event waiting list
    public ArrayList<EntrantRole> viewWaitlist(Event event) {
        ArrayList<EntrantRole> entrantsInWaitlist = new ArrayList<EntrantRole>();

        // in future, get the list of entrants who joined the event waiting list
        return entrantsInWaitlist;
    }

    // US 02.02.02 As an organizer I want to see on a map where entrants joined my event waiting list from.
    public void viewMapOfWaitlist(Event event) {
        // in future, show the map of entrants who joined the event waiting list
    }

    // US 02.03.01 As an organizer I want to OPTIONALLY limit the number of entrants who can join my waiting list
    public int getWaitlistCapacity(Event event) {
        return event.getWaitlistCapacity();
    }

    // US 02.03.01 As an organizer I want to OPTIONALLY limit the number of entrants who can join my waiting list
    public void setWaitlistCapacity(Event event, int capacity) {
        event.setWaitlistCapacity(capacity);
    }

    // US 02.04.01 As an organizer I want to upload an event poster to provide visual information to entrants 
    public String getEventPoster(Event event) {
        return event.getEventPoster();
    }

    // US 02.04.01 As an organizer I want to upload an event poster to provide visual information to entrants 
    public void setEventPoster(Event event, String eventPoster) {
        event.setEventPoster(eventPoster);
    }


    // US 02.05.02
    // As an organizer I want to set the system to sample a specified number of attendees to register for the event

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

            // get random sample of users and add them to selected list
            sampleUsers(eventID, eventCapacity, (userListObj) -> {
                ArrayList<User> usersSelected = (ArrayList<User>) userListObj;
                Log.d("TEST", String.format("Sampled %s users from event capacity %s", eventCapacity, usersSelected.size()));

                for (User user : usersSelected) {
                    Log.d("TEST", String.format("Sampled user %s for initial event selection", user.getName()));
                    dbManager.setRegistrantStatus(eventID, user.getUserID(), DBManager.RegistrantStatus.SELECTED);

                    // possible race condition
                    // consider calling from onSuccessCallback instead
                    dbManager.notifyEntrantsOfStatus("Not Selected For Event",
                            "You will remain waitlisted in case a spot opens up",
                            eventID, DBManager.RegistrantStatus.WAITLISTED);
                }
                onSuccessCallback.run(usersSelected);
            });

        }, () -> {Log.d("Firestore", "Could not fetch event to check event capacity.");});
    }

    /**
     * Utility function that randomly samples users registered for an event
     * and passes this list to the onSuccessCallback (as an Object.)
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
                ArrayList<User> users = (ArrayList<User>) userListObj;
                ArrayList<User> usersToSelect = new ArrayList<>();

                if (sampleNum == -1 || users.size() <= sampleNum) {
                    usersToSelect.addAll(users);
                } else {
                    ArrayList<User> randomUsers = new ArrayList<>(users);
                    Collections.shuffle(randomUsers);
                    usersToSelect = new ArrayList<>(randomUsers.subList(0, sampleNum));
                }

                onSuccessCallback.run(usersToSelect);

            });
    }

    /**
     * US 02.05.03
     * As an organizer I want to be able to draw a replacement applicant from the pooling system
     * when a previously selected applicant cancels or rejects the invitation.
     *
     * Resample users whose status remains waitlisted.
     * Sets their status to SELECTED and updates the corresponding EventRegistrants status.
     * Passes the list of resampled users to onSuccessCallback (as an Object.)
     * @param eventID
     * @param resampleNum
     * @param onSuccessCallback
     */
    public static void resampleAndSelectUsers(String eventID, int resampleNum, DBManager.Obj2VoidCallback onSuccessCallback) {
        // assumes all entrants in waitlist elected to be resampled
        DBManager dbManager = new DBManager();
        sampleUsers(eventID, resampleNum, (userListObj) -> {
            ArrayList<User> usersResampled = (ArrayList<User>) userListObj;
            for (User user : usersResampled) {
                Log.d("TEST", String.format("Resampled user %s", user.getName()));
                dbManager.setRegistrantStatus(eventID, user.getUserID(), DBManager.RegistrantStatus.SELECTED);
            }

            onSuccessCallback.run(usersResampled);
        });
    }


    // US 02.06.01 As an organizer I want to view a list of all chosen entrants who are invited to apply
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
    // US 02.07.01 As an organizer I want to send notifications to all entrants on the waiting list
    public boolean notifyEntrantsInWaitlist(Event event, String message) {
        boolean notificationSent = notifyEntrants(event, event.getEntrantsInWaitlist(), message);
        return notificationSent;
    }

    // US 02.07.02 As an organizer I want to send notifications to all selected entrants
    public boolean notifySelectedEntrants(Event event, String message) {
        boolean notificationSent = notifyEntrants(event, event.getEntrantsChosen(), message);
        return notificationSent;
    }

    // US 02.07.03 As an organizer I want to send a notification to all canceled entrants
    public boolean notifyCancelledEntrants(Event event, String message) {
        boolean notificationSent = notifyEntrants(event, event.getEntrantsCancelled(), message);
        return notificationSent;
    }

    // US 02.05.01 As an organizer I want to send a notification to chosen entrants to sign up for events
    public boolean notifyEntrantsChosen(Event event) {
        String message = "You have been selected for "+event.getEventName()+". Sign up now.";
        boolean notificationSent = notifyEntrants(event, event.getEntrantsChosen(), message);
        return notificationSent;
    }

    public boolean notifyEntrants(Event event, ArrayList<EntrantRole> entrants, String message) {
        boolean notificationSent = false;

        // notification attributes
        Date timestamp = new Date();
        String eventID = event.getEventID();

        for (int i=0; i<entrants.size(); i++) {
            EntrantRole current_entrant = entrants.get(i);

            if (current_entrant.canRecieveNotifs()) {
                String entrantID = current_entrant.getUserID(); // replace with device ID

                // NOTIFICATION GOES HERE
                // how to send notification?

            }
        }

        return notificationSent;
    }
}
