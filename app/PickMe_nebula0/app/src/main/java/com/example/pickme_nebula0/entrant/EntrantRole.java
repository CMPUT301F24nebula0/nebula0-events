package com.example.pickme_nebula0.entrant;

import com.example.pickme_nebula0.event.Event;
import com.example.pickme_nebula0.user.User;

import java.util.ArrayList;
import java.util.Objects;

/**
 * EntrantRole
 */
public class EntrantRole extends User {
    private String entrantID;
    private boolean isOptedOutFromNotification = false;
    private ArrayList<Event> joinedWaitingList = new ArrayList<Event>();
    private ArrayList<Event> joinedEvents = new ArrayList<Event>();

    /**
     * Constructor
     */
    public EntrantRole(String userID, String name, String email, String phoneNumber, String profilePicture) {
        // Call the superclass constructor (User) with parameters
        super(userID, name, email, phoneNumber, profilePicture);
        this.entrantID = null; // Initialize entrantID as null or another value
    }

    /**
     * Get entrantID
     * @return entrantID entrantID
     */
    public String getEntrantID() {
        return this.entrantID;
    }

    /**
     * Set entrantID
     * @param entrantID entrantID
     */
    public void setEntrantID(String entrantID) {
        this.entrantID = entrantID;
    }

    //------------ GET STATUS OF EVENTS

    // these can be used for returning custom error messages
    // if entrant attempts to join event they've already joined
    // or frontend can prevent user from joining
    public boolean inWaitlist(Event event) {
        // checking directly from event is safer
        return event.entrantInWaitlist(this);
    }

    public boolean isChosenForEvent(Event event) {
        return event.entrantChosen(this);
    }

    public boolean isEnrolledForEvent(Event event) {
        return event.entrantEnrolled(this);
    }


    //------------ MANAGE EVENTS

   // US 01.01.01 As an entrant, I want to join the waiting list for a specific event
    // updates both list in Entrant and Event
    public boolean joinWaitingList(Event event) {
        // isJoined is false if waitList is already full
        // or joining fails for some other reason
        // see addEntrantToWaitingList (Event)
        boolean isJoined = event.addEntrantToWaitingList(this);
        if (isJoined) {
            if (!eventInEvents(event, this.joinedWaitingList)) { this.joinedWaitingList.add(event); }
        }

        return isJoined;
    }

    // US 01.01.02 As an entrant, I want to unjoin a waiting list for a specific event 
    public boolean unjoinWaitingList(Event event) {
        boolean isUnjoined = event.removeEntrantFromWaitingList(this);
        if (isUnjoined) {
            this.joinedWaitingList.remove(event);
        }

        return isUnjoined;
    }
    
    // US 01.03.03 As an entrant I want my profile picture to be deterministically generated from my profile name if I haven't uploaded a profile image yet.
    public String generateProfilePicture() {
        // in future, generate a profile picture for the user based on profile name
        String initial = this.name.charAt(0) + "";
        initial = initial.toUpperCase();

        // in future, return the generated profile picture
        return "";
    }

    // US 01.04.01 As an entrant I want to receive notification when chosen from the waiting list (when I "win" the lottery)
    // US 01.04.02 As an entrant I want to receive notification of not chosen on the app (when I "lose" the lottery)
    public void receiveNotificationForWaitingList() {
        if (!this.isOptedOutFromNotification) {
            // in future, receive notification for waiting list
            // when chosen or not chosen for the waiting list
        } else {
            // since the user has opted out from notification, do not send any notification
        }
    }

    // US 01.04.03 As an entrant I want to opt out of receiving notifications from organizers and admin
    public void optOutFromNotification() {
        if (!this.isOptedOutFromNotification) {
            this.isOptedOutFromNotification = true;
        } else {
            // in future, show a message that the user is already opted out
        }
    }

    public boolean canRecieveNotifs() { return !this.optOutFromNotification; }

    // US 01.05.02 As an entrant I want to be able to accept the invitation to register/sign up when chosen to participate in an event
    public boolean acceptInvitation(Event event) {
        boolean isAccepted = false;

        // returns false with error
        // currently, rejects entrant if they are already enrolled
        isAccepted = event.enrollEntrant(this);
        if (isAccepted) {
            this.joinedWaitingList.remove(event);
            if (!eventInEvents(event, this.joinedEvents)) { this.joinedEvents.add(event); }
        }

        return isAccepted;
    }

    // US 01.05.03 As an entrant I want to be able to decline an invitation when chosen to participate in an event
    public boolean declineInvitation(Event event) {
        boolean isDeclined = false;
        isDeclined = event.removeChosenEntrant(this);
        if (isDeclined) {
            this.joinedWaitingList.remove(event);
            this.joinedEvents.remove(event);
        }

        return isDeclined;
    }


    // US 01.06.01 As an entrant I want to view event details within the app by scanning the promotional QR code
    // US 01.06.02 As an entrant I want to be able to be sign up for an event by scanning the QR code

    // should return an eventID for something on the frontend to load
    public String scanQRCode() {
        // in future, scan a QR code

        return "";
    }

    // FOR TESTING PURPOSES
    public String scanQRCode(String eventID) {
        // in future, scan a QR code

        return eventID;
    }

    //--------- UTILITY FUNCTIONS

    // for preventing duplicate joined Events
    public boolean eventInEvents(Event event, ArrayList<Event> events) {
        boolean event_equals = false;
        String event_id = event.getEventID();
        for (int i=0; i<events.size(); i++) {
            Event current_event = events.get(i);
            if (Objects.equals(current_event.getEventID(), event_id)) {
                event_equals = true;
                break;
            }
        }

        return event_equals;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) { return true; }
        if (!(o instanceof EntrantRole)) { return false; }
        EntrantRole entrant = (EntrantRole) o;
        return Objects.equals(this.getEntrantID(), entrant.getEntrantID());
    }


}
