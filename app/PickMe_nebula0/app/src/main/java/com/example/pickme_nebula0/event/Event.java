package com.example.pickme_nebula0.event;

import com.example.pickme_nebula0.DeviceManager;
import com.example.pickme_nebula0.db.DBManager;
import com.example.pickme_nebula0.entrant.EntrantRole;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

/**
 * Class to represent an event.
 *
 * @author Taekwan Yoon
 */
public class Event {
    private String qrCodeData;
    private String hashedQRCode;
    private DBManager dbManager;

    private String eventID;
    private String organizerID; // we get facility info through organizer

    private String eventName;
    private String eventDescription;
    private Date eventDate;

    private boolean geolocationRequired;
    private int geolocationMaxDistance = -1; // -1 means no limit
    private boolean waitlistCapacityRequired;
    protected int waitlistCapacity = -1;
    protected int eventCapacity = -1;
    private Date createdDateTime;
    private int numberOfAttendees;
    private int geolocationRequirement;

    protected String poster;

    private int unfilledSpots = 0;  // # of entrants to be resampled

    protected ArrayList<EntrantRole> entrantsInWaitlist = new ArrayList<EntrantRole>();
    protected ArrayList<EntrantRole> entrantsChosen = new ArrayList<EntrantRole>();
    protected ArrayList<EntrantRole> entrantsCancelled = new ArrayList<EntrantRole>();
    protected ArrayList<EntrantRole> entrantsEnrolled = new ArrayList<EntrantRole>();
    protected ArrayList<EntrantRole> entrantsToResample = new ArrayList<EntrantRole>();

    public Event() {

    }
    /**
     * Constructor
     */
    public Event(String eventName, String eventDescription, Date eventDate, boolean geolocationRequired, int geolocationMaxDistance,
                 boolean waitlistCapacityRequired, int waitlistCapacity,
                 int eventCapacity) {
        dbManager = new DBManager();
        eventID = dbManager.createIDForDocumentIn(dbManager.eventsCollection);

        organizerID = DeviceManager.getDeviceId();
        this.eventName = eventName;
        this.eventDescription = eventDescription;
        this.eventDate = eventDate;
        this.geolocationRequired = geolocationRequired;
        this.geolocationMaxDistance = geolocationMaxDistance;
        this.waitlistCapacityRequired = waitlistCapacityRequired;
        this.waitlistCapacity = waitlistCapacity;
        this.eventCapacity = eventCapacity;
    }

    public Event(String eventID,String organizerID, String eventName, String eventDescription, Date eventDate, boolean geolocationRequired, int geolocationMaxDistance,
                 boolean waitlistCapacityRequired, int waitlistCapacity,
                 int eventCapacity) {
        dbManager = new DBManager();

        this.eventID = eventID;
        this.organizerID = organizerID;
        this.eventName = eventName;
        this.eventDescription = eventDescription;
        this.eventDate = eventDate;
        this.geolocationRequired = geolocationRequired;
        this.geolocationMaxDistance = geolocationMaxDistance;
        this.waitlistCapacityRequired = waitlistCapacityRequired;
        this.waitlistCapacity = waitlistCapacity;
        this.eventCapacity = eventCapacity;
    }

    /**
     * Returns the event ID.
     *
     * @return event ID
     */
    public String getEventID() {
        return this.eventID;
    }

    public String getHashedQRCode(){
        return this.hashedQRCode;
    }
    public void setHashedQRCode(String hashcode){
        this.hashedQRCode =hashcode;
    }
    /**
     * Returns the event name.
     *
     * @return event name
     */
    public String getEventName() { return this.eventName; }

    /**
     * Returns the event description.
     *
     * @return event description
     */
    public String getEventDescription() { return this.eventDescription; }

    /**
     * Returns the event date.
     *
     * @return event date
     */
    public Date getEventDate() { return this.eventDate; }

    /**
     * Returns whether geolocation is required.
     *
     * @return whether geolocation is required
     */
    public boolean getGeolocationRequired() { return this.geolocationRequired; }

    /**
     * Returns the maximum distance for geolocation.
     *
     * @return maximum distance for geolocation
     */
    public int getGeolocationMaxDistance() { return this.geolocationMaxDistance; }
    public boolean getWaitlistCapacityRequired() { return this.waitlistCapacityRequired; }

    /**
     * Returns the waitlist capacity.
     *
     * @return waitlist capacity
     */
    public int getWaitlistCapacity() { return this.waitlistCapacity; }

    /**
     * Returns the event capacity.
     *
     * @return event capacity
     */
    public int getEventCapacity() { return this.eventCapacity; }

    /**
     * Returns the organizer ID.
     *
     * @return organizer ID
     */
    public String getOrganizerID() {return organizerID;}

    /**
     * Returns the event poster.
     *
     * @return event poster
     */
    public String getPoster() {
        return this.poster;
    }

    /**
     * Returns the created date and time.
     *
     * @return created date and time
     */
    public Date getCreatedDateTime() { return this.createdDateTime; }

    /**
     * Returns the number of attendees.
     *
     * @return number of attendees
     */
    public int getNumberOfAttendees() { return this.numberOfAttendees; }

    /**
     * Returns the geolocation requirement.
     *
     * @return geolocation requirement
     */
    public int getGeolocationRequirement() { return this.geolocationRequirement; }

    /**
     * Returns the QR code data.
     *
     * @return QR code data
     */
    public String getQrCodeData() {
        return qrCodeData;
    }

    /**
     * Returns the entrants in the waiting list.
     *
     * @return entrants in the waiting list
     */
    public ArrayList<EntrantRole> getEntrantsInWaitlist() {
        return this.entrantsInWaitlist;
    }

    /**
     * Returns the entrants chosen.
     *
     * @return entrants chosen
     */
    public ArrayList<EntrantRole> getEntrantsChosen() {
        return this.entrantsChosen;
    }

    /**
     * Returns the entrants cancelled.
     *
     * @return entrants cancelled
     */
    public ArrayList<EntrantRole> getEntrantsCancelled() {
        // FETCH FROM DB INSTEAD
        return this.entrantsCancelled;
    }

    /**
     * Returns the entrants enrolled.
     *
     * @return entrants enrolled
     */
    public ArrayList<EntrantRole> getEntrantsEnrolled() {
        return this.entrantsEnrolled;
    }

    // setters
    public void setEventID(String eventID) { this.eventID = eventID; }
    public void setEventName(String eventName) { this.eventName = eventName; }
    public void setEventDescription(String eventDescription) { this.eventDescription = eventDescription; }
    public void setEventDate(Date eventDate) { this.eventDate = eventDate; }
    public void setGeolocationRequired(boolean geolocationRequired) { this.geolocationRequired = geolocationRequired; }
    public void setGeolocationMaxDistance(int geolocationMaxDistance) { this.geolocationMaxDistance = geolocationMaxDistance; }
    public void setWaitlistCapacityRequired(boolean waitlistCapacityRequired) { this.waitlistCapacityRequired = waitlistCapacityRequired; }
    public void setWaitlistCapacity(int waitlistCapacity) { this.waitlistCapacity = waitlistCapacity; }
    public void setEventCapacity(int eventCapacity) { this.eventCapacity = eventCapacity; }
    public void setOrganizerID(String organizerID) { this.organizerID = organizerID; }
    public void setPoster(String poster) { this.poster = poster; }
    public void setCreatedDateTime(Date createdDateTime) { this.createdDateTime = createdDateTime; }
    public void setNumberOfAttendees (int numberOfAttendees) { this.numberOfAttendees = numberOfAttendees; }
    public void setGeolocationRequirement (int geolocationRequirement) { this.geolocationRequirement = geolocationRequirement; }
    public void setQrCodeData(String qrCodeData) { this.qrCodeData = qrCodeData; }
    //------------ WAITLIST LOGIC

    /**
     * Simplifies checking if waiting list is full.
     * Can be used when adding entrant or used by EntrantRole to check
     * if list is full, and notify entrant if they attempt to
     * add themself to a full list.
     *
     * Removes need for object to first check if capacity is defined,
     * since it returns false anyways if no capacity is defined.
     *
     * @return boolean Whether list is full or not
     */
    public boolean waitlistFull() {
        // waiting list is never full for a list with unlimited capacity
        if (this.waitlistCapacity == -1) { return false; }

        if (this.entrantsInWaitlist.size() > this.waitlistCapacity) {
            throw new IllegalStateException("Waitlist capacity exceeded somehow. Should not happen, check implementation for adding entrants.");
        }
        return (this.entrantsInWaitlist.size() == this.waitlistCapacity);
    }

    /**
     * Add entrant to waiting list if not full.
     * Calling object should check whether adding was successful or not
     * and handle each case appropriately, eg. notifying entrant
     * if waiting list is full.
     *
     * @param entrant Entrant to add
     * @return boolean Whether addition was successful or rejected
     */
    public boolean addEntrantToWaitlist(EntrantRole entrant) {
        // TODO: return specific error message
        if (waitlistFull() || entrantInWaitlist(entrant)) { return false;}

        this.entrantsInWaitlist.add(entrant);
        return true;
    }

    // can be done by organizer or entrant who removes themselves
    public boolean removeEntrantFromWaitlist(EntrantRole entrant) {
        boolean entrantRemoved = this.entrantsInWaitlist.remove(entrant);
        // check if entrant exists in DB
        //TODO

        if (entrantRemoved) {
            // UPDATE DB
        }

        return entrantRemoved;
    }

    //---------- SAMPLE ENTRANTS
    /*
    Entrants who opted to be resampled if not chosen are added to a list
    when sampling occurs. This makes it easier to resample later on,
    and is done during sampling because this info does not need to be
    known to Event before sampling occurs (would be slow and unnecessary
    to track prior to sampling.)
     */

    /**
     * Randomly sample n entrants from the waiting list,
     * where n = eventCapacity.
     *
     */
    public void sampleEntrants() {
        // eventCapacity == -1 means no limit
        if (this.eventCapacity == -1 || this.entrantsInWaitlist.size() <= this.eventCapacity) {
            this.entrantsChosen = new ArrayList<>(this.entrantsInWaitlist);
        } else {
            // waiting entrants exceeds event capacity
            // randomly sample
            // create new arraylist to avoid altering original
            ArrayList<EntrantRole> shuffledEntrants = new ArrayList<>(this.entrantsInWaitlist);
            Collections.shuffle(shuffledEntrants);
            ArrayList<EntrantRole> selectedEntrants = new ArrayList<>(shuffledEntrants.subList(0, this.eventCapacity));
            // modify selectedEntrants as needed prior to assigning to this.entrantsChosen

            this.entrantsChosen = selectedEntrants;

            // have this in the DB later
            entrantsToResample = new ArrayList<>(shuffledEntrants.subList(this.eventCapacity, shuffledEntrants.size()));
        }
    }

    //------------- AFTER REGISTRATION CLOSES
    /*
    If an entrant is removed, manually resample entrants.
    unfilledSpots tracks the number of spots to be resampled.
    According to discussion forum, it's ok for resampling to be done
    manually instead of after each removed entrant.

     */

    public ArrayList<EntrantRole> getEntrantsToResample() {
        // FETCH FROM DB LATER

        // creates new arraylist because return value will be modified by resampleEntrants
        return new ArrayList<>(this.entrantsToResample);
    }

    /**
     * Replaces cancelled/declined entrants with
     * unselected entrants who opted in to being resampled.
     */
    public void resampleEntrants() {
        if (this.unfilledSpots == 0) { return; }

        ArrayList<EntrantRole> entrantsToResample = getEntrantsToResample();
        Collections.shuffle(entrantsToResample);

        for (int i=0; i<this.unfilledSpots; i++) {
            int ind = 0;    // make sure this is consistent
            addEntrantToChosen(entrantsToResample.get(ind));
            entrantsToResample.remove(ind);
            // notify resampled entrants they were chosen
        }

        this.unfilledSpots = 0;
        this.entrantsToResample = entrantsToResample;
    }

    // should only be done when sampling waiting entrants or resampling entrants
    public boolean addEntrantToChosen(EntrantRole entrant) {
        if (entrantChosen(entrant)) { return false; }
        this.entrantsChosen.add(entrant);
        // check if entrant exists in DB instead of relying on private attribute

        // UPDATE DB

        return true;
    }

    /**
     * Removes an entrant from the chosen list.
     * CALL THIS FROM:
     *  EntrantRole - if declining invite.
     * Updates DB.
     */
    public boolean removeChosenEntrant(EntrantRole entrant) {
        boolean entrantRemoved = this.entrantsChosen.remove(entrant);

        // check if entrant exists in DB instead of relying on private attribute
        if (entrantRemoved) {
            unfilledSpots += 1;
            // UPDATE DB
        }

        return entrantRemoved;
    }

    /**
     * Removes an entrant from the chosen list.
     * CALL THIS FROM:
     *  OrganizerRole - if cancelling chosen entrant.
     */
    public boolean cancelEntrant(EntrantRole entrant) {

        boolean entrantRemoved = removeChosenEntrant(entrant);
        if (entrantRemoved) {
            // unfilledSpots already updated from removeChosenEntrant

            // UPDATE DB
            this.entrantsCancelled.add(entrant);
            // this.entrantsChosen is already updated
        }

        return entrantRemoved;
    }

    /**
     * Enrolls all entrants currently in chosen list.
     * Assumes that organizer doesn't want to enroll each entrant
     * one by one.
     */
    public void enrollEntrants() {
        // fetch chosen entrants from DB and update DB
        this.entrantsEnrolled = new ArrayList<>(this.entrantsChosen);
    }

    /**
     * Enrolls an entrant.
     * Called by entrant when they accept the invite and sign up for the event.
     * Note: no way to remove entrants, as this functionality is not required
     * for any of the user requirements.
     */
    public boolean enrollEntrant(EntrantRole entrant) {
        if (entrantEnrolled(entrant)) { return false; }
        this.entrantsEnrolled.add(entrant);

        // update DB with following:
        // entrant status
        // event list of chosen entrants

        return true;
    }

    //-------------- UTILITY FUNCTIONS
    public boolean entrantInWaitlist(EntrantRole entrant) {
        return entrantInList(entrant, this.entrantsInWaitlist);
    }

    public boolean entrantChosen(EntrantRole entrant) {
        return entrantInList(entrant, this.entrantsChosen);
    }

    public boolean entrantCanBeResampled(EntrantRole entrant) {
        return entrantInList(entrant, this.entrantsToResample);
    }

    public boolean entrantCancelled(EntrantRole entrant) {
        return entrantInList(entrant, this.entrantsCancelled);
    }

    public boolean entrantEnrolled(EntrantRole entrant) {
        return entrantInList(entrant, this.entrantsEnrolled);
    }

    private boolean entrantInList(EntrantRole entrant, ArrayList<EntrantRole> entrantList) {
        // FETCH FROM DB
        boolean found_entrant = false;

        for (int i=0; i<entrantList.size(); i++) {
            EntrantRole current_entrant = entrantList.get(i);
            if (current_entrant.equals(entrant)) { break; }
        }

        return found_entrant;
    }


}
