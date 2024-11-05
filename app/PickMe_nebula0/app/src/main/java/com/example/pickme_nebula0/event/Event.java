package com.example.pickme_nebula0.event;

import com.example.pickme_nebula0.DeviceManager;
import com.example.pickme_nebula0.db.DBManager;
import com.example.pickme_nebula0.entrant.EntrantRole;
import com.example.pickme_nebula0.user.activities.UserInfoActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

/**
 * Event
 */
public class Event {
    private DBManager dbManager;

    private String eventID;
    private String organizerID; // we get facility info through organizer

    private String eventName;
    private String eventDescription;
    private Date eventDate;
    protected String eventPoster;

    // -1 means no limit
    protected int waitingListCapacity = -1;
    protected int eventCapacity = -1;
    private int geolocationMaxDistance = -1;
    private int unfilledSpots = 0;  // # of entrants to be resampled

    protected ArrayList<EntrantRole> entrantsInWaitingList = new ArrayList<EntrantRole>();
    protected ArrayList<EntrantRole> entrantsChosen = new ArrayList<EntrantRole>();
    protected ArrayList<EntrantRole> entrantsCancelled = new ArrayList<EntrantRole>();
    protected ArrayList<EntrantRole> entrantsEnrolled = new ArrayList<EntrantRole>();
    protected ArrayList<EntrantRole> entrantsToResample = new ArrayList<EntrantRole>();

    /**
     * Constructor
     */
    public Event(String eventName, String eventDescription, Date eventDate, int eventCapacity, int waitingListCapacity, int geolocationMaxDistance) {
        dbManager = new DBManager();
        eventID = dbManager.createIDForDocumentIn(dbManager.eventsCollection);
        organizerID = DeviceManager.getDeviceId();

        this.eventName = eventName;
        this.eventDescription = eventDescription;
        this.eventDate = eventDate;
        this.eventCapacity = eventCapacity;
        this.waitingListCapacity = waitingListCapacity;
        this.geolocationMaxDistance = geolocationMaxDistance;
    }

    /**
     * Get eventID
     * @return eventID eventID
     */
    public String getEventID() {
        return this.eventID;
    }

    public String getEventName() {return this.eventName;}

    public String getEventDescription() {return this.eventDescription;}

    public Date getEventDate(){return  this.eventDate;}

    public int getGeolocationMaxDistance(){return  this.geolocationMaxDistance;}

    /**
     * Get Waiting List Capacity
     * @return waitingListCapacity waiting list capacity
     */
    public int getWaitingListCapacity() {
        return this.waitingListCapacity;
    }

    /**
     * Set Waiting List Capacity
     * @param waitingListCapacity waiting list capacity
     */
    public void setWaitingListCapacity(int waitingListCapacity) {
        this.waitingListCapacity = waitingListCapacity;
    }

    public String getOrganizerID() {return organizerID;}

    public String getEventPoster() {
        return this.eventPoster;
    }

    public void setEventPoster(String eventPoster) {
        this.eventPoster = eventPoster;
    }

    public int getEventCapacity() {
        return this.eventCapacity;
    }

    public void setEventCapacity(int eventCapacity) {
        this.eventCapacity = eventCapacity;
    }

    public ArrayList<EntrantRole> getEntrantsInWaitingList() {
        return this.entrantsInWaitingList;
    }

    public ArrayList<EntrantRole> getEntrantsChosen() {
        return this.entrantsChosen;
    }

    public ArrayList<EntrantRole> getEntrantsCancelled() {
        // FETCH FROM DB INSTEAD
        return this.entrantsCancelled;
    }

    public ArrayList<EntrantRole> getEntrantsEnrolled() {
        return this.entrantsEnrolled;
    }


    //------------ WAITING LIST LOGIC

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
    public boolean waitingListFull() {
        // waiting list is never full for a list with unlimited capacity
        if (this.waitingListCapacity == -1) { return false; }

        if (this.entrantsInWaitingList.size() > this.waitingListCapacity) {
            throw new IllegalStateException("Waiting list capacity exceeded somehow. Should not happen, check implementation for adding entrants.");
        }
        return (this.entrantsInWaitingList.size() == this.waitingListCapacity);
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
    public boolean addEntrantToWaitingList(EntrantRole entrant) {
        if (waitingListFull()) { return false;}

        this.entrantsInWaitingList.add(entrant);
        return true;
    }

    // can be done by organizer or entrant who removes themselves
    public void removeEntrantFromWaitingList(EntrantRole entrant) {
        boolean entrantRemoved = this.entrantsInWaitingList.remove(entrant);
        // check if entrant exists in DB instead of relying on private attribute

        if (entrantRemoved) {
            // UPDATE DB
        }
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
        if (this.eventCapacity == -1 || this.entrantsInWaitingList.size() <= this.eventCapacity) {
            this.entrantsChosen = new ArrayList<>(this.entrantsInWaitingList);
        } else {
            // waiting entrants exceeds event capacity
            // randomly sample
            // create new arraylist to avoid altering original
            ArrayList<EntrantRole> shuffledEntrants = new ArrayList<>(this.entrantsInWaitingList);
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
        }

        this.unfilledSpots = 0;
        this.entrantsToResample = entrantsToResample;
    }

    // should only be done when sampling waiting entrants or resampling entrants
    public void addEntrantToChosen(EntrantRole entrant) {
        this.entrantsChosen.add(entrant);
        // check if entrant exists in DB instead of relying on private attribute

        // UPDATE DB
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

    //-------------- UTILITY FUNCTIONS
    public boolean entrantInWaitlist(EntrantRole entrant) {
        return entrantInList(entrant, this.entrantsInWaitingList);
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
