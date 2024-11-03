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

    protected ArrayList<EntrantRole> entrantsInWaitingList = new ArrayList<EntrantRole>();
    protected ArrayList<EntrantRole> entrantsChosen = new ArrayList<EntrantRole>();
    protected ArrayList<EntrantRole> entrantsDeclined = new ArrayList<EntrantRole>();
    protected ArrayList<EntrantRole> entrantsCancelled = new ArrayList<EntrantRole>();
    protected ArrayList<EntrantRole> entrantsEnrolled = new ArrayList<EntrantRole>();

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
        return this.entrantsCancelled;
    }

    public ArrayList<EntrantRole> getEntrantsEnrolled() {
        return this.entrantsEnrolled;
    }

    public ArrayList<EntrantRole> getEntrantsDeclined() {
        return this.entrantsDeclined;
    }


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
            throw new IllegalStateException("Waiting list capacity exceeded somehow. Should not happen, check implementation for adding entrants.")
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

    public void removeEntrantFromWaitingList(EntrantRole entrant) {
        this.entrantsInWaitingList.remove(entrant);
    }

    public void addEntrantToChosen(EntrantRole entrant) {
        this.entrantsChosen.add(entrant);
    }

    public void removeEntrantFromChosen(EntrantRole entrant) {
        this.entrantsChosen.remove(entrant);
    }

    // renamed function to be shorter
    // may be less descriptive but other function can be named resampleEntrants
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
        }
    }

    public void addToEntrantCancelled(EntrantRole entrant) {
        // maybe add error checking
        this.entrantsCancelled.add(entrant);
    }

    public void removeFromEntrantCancelled(EntrantRole entrant) {
        // maybe add error checking
        this.entrantsCancelled.remove(entrant);
    }

    public void addToEntrantEnrolled(EntrantRole entrant) {
        // maybe add error checking
        this.entrantsEnrolled.add(entrant);
    }
}
