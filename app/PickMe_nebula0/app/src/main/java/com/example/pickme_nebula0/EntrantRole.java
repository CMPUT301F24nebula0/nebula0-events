package com.example.pickme_nebula0;

import java.util.ArrayList;

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
    public EntrantRole(String userID, String firstName, String lastName, String email, String phoneNumber, String profilePicture) {
        // Call the superclass constructor (User) with parameters
        super(userID, firstName, lastName, email, phoneNumber, profilePicture);
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

   // US 01.01.01 As an entrant, I want to join the waiting list for a specific event
    public boolean joinWaitingList(Event event) {
        event.getEntrantsInWaitingList().add(this);
        boolean isJoined = true;
        // in future, add the user to the event
        return isJoined;
    }

    // US 01.01.02 As an entrant, I want to unjoin a waiting list for a specific event 
    public boolean unjoinWaitingList(Event event) {
        boolean isUnjoined = true;
        event.removeEntrantFromWaitingList(this);
        // in future, remove the user from the event
        return isUnjoined;
    }
    
    // US 01.03.03 As an entrant I want my profile picture to be deterministically generated from my profile name if I haven't uploaded a profile image yet.
    public String generateProfilePicture() {
        // in future, generate a profile picture for the user based on profile name
        String initial = this.firstName.charAt(0) + "" + this.lastName.charAt(0);
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

    // US 01.05.02 As an entrant I want to be able to accept the invitation to register/sign up when chosen to participate in an event
    public boolean acceptInvitation(Event event) {
        boolean isAccepted = false;
        // in future, accept the invitation to the event

        return isAccepted;
    }

    // US 01.05.03 As an entrant I want to be able to decline an invitation when chosen to participate in an event
    public boolean declineInvitation(Event event) {
        boolean isDeclined = false;
        // in future, decline the invitation to the event

        return isDeclined;
    }

    // US 01.06.01 As an entrant I want to view event details within the app by scanning the promotional QR code
    // US 01.06.02 As an entrant I want to be able to be sign up for an event by scanning the QR code
    public void scanQRCode() {
        // in future, scan a QR code
    }


}
