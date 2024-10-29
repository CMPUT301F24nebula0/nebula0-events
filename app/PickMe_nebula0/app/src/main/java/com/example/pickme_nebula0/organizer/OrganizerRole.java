package com.example.pickme_nebula0.organizer;

import com.example.pickme_nebula0.entrant.EntrantRole;
import com.example.pickme_nebula0.event.Event;
import com.example.pickme_nebula0.user.User;

import java.util.ArrayList;
/**
 * OrganizerRole
 */
public class OrganizerRole extends User {
    private String organizerID;
    private ArrayList<Event> events = new ArrayList<Event>();

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
        boolean eventCreated = false;
        // Create event
        Event event = new Event();
        events.add(event);
        //
        return false;
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
    public ArrayList<EntrantRole> viewWaitingList(Event event) {
        ArrayList<EntrantRole> entrantsInWaitingList = new ArrayList<EntrantRole>();

        // in future, get the list of entrants who joined the event waiting list
        return entrantsInWaitingList;
    }

    // US 02.02.02 As an organizer I want to see on a map where entrants joined my event waiting list from.
    public void viewMapOfWaitingList(Event event) {
        // in future, show the map of entrants who joined the event waiting list
    }

    // US 02.03.01 As an organizer I want to OPTIONALLY limit the number of entrants who can join my waiting list
    public int getWaitingListCapacity(Event event) {
        return event.getWaitingListCapacity();
    }

    // US 02.03.01 As an organizer I want to OPTIONALLY limit the number of entrants who can join my waiting list
    public void setWaitingListCapacity(Event event, int capacity) {
        event.setWaitingListCapacity(capacity);
    }

    // US 02.04.01 As an organizer I want to upload an event poster to provide visual information to entrants 
    public String getEventPoster(Event event) {
        return event.getEventPoster();
    }

    // US 02.04.01 As an organizer I want to upload an event poster to provide visual information to entrants 
    public void setEventPoster(Event event, String eventPoster) {
        event.setEventPoster(eventPoster);
    }

    public boolean sendNotificationToEntrantsInWaitingList(Event event) {
        boolean notificationSent = false;

        ArrayList<EntrantRole> entrantsInWaitingList = event.getEntrantsInWaitingList();
        // Send notification
        return notificationSent;
    }

    // US 02.05.01 As an organizer I want to send a notification to chosen entrants to sign up for events
    // update entrant info in firebase?
    public boolean sendNotificationToEntrantsChosen(Event event) {
        boolean notificationSent = false;

        ArrayList<EntrantRole> entrantsChosen = event.getEntrantsChosen();
        // Send notification
        return notificationSent;
    }

    // US 02.05.02
    // As an organizer I want to set the system to sample a specified number of attendees to register for the event
    public ArrayList<EntrantRole> sampleEntrants(Event event, int entrantNum) {
        // get all entrants from an event
        // randomly select entrant_num entrants from the Event's entrants
        // then update Event's chosen entrants

        ArrayList<EntrantRole> entrantsChosen = event.getEntrantsChosen();
        return entrantsChosen;
    }

    // US 02.05.03
    // As an organizer I want to be able to draw a replacement applicant from the pooling system
    // when a previously selected applicant cancels or rejects the invitation
    public EntrantRole resampleEntrant(Event event) {
        // remove cancelled applicant from invitation list (if found)

        // fetch list of Entrants who elected to be resampled
        // randomly select one of these entrants
        // add entrant to list of invited entrants


        EntrantRole resampled_entrant = null;
        return resampled_entrant;
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
        // add error checking
        event.getEntrantsChosen().remove(entrant);
        // in future, cancel entrants that declined signing up for the event
    }

    // US 02.07.01 As an organizer I want to send notifications to all entrants on the waiting list
    public boolean notifyWaitingEntrants(Event event) {
        boolean notificationSent = false;

        ArrayList<EntrantRole> entrantsWaiting = event.getEntrantsInWaitingList();
        // Send notification
        return notificationSent;
    }

    // US 02.07.02 As an organizer I want to send notifications to all selected entrants
    public boolean notifySelectedEntrants(Event event) {
        boolean notificationSent = false;

        ArrayList<EntrantRole> entrantsChosen = event.getEntrantsChosen();
        // Send notification
        return notificationSent;
    }

    // US 02.07.03 As an organizer I want to send a notification to all canceled entrants
    public boolean notifyCancelledEntrants(Event event) {
        boolean notificationSent = false;

        ArrayList<EntrantRole> entrantsCanceled = event.getEntrantsCancelled();
        // Send notification
        return notificationSent;
    }


}
