package com.example.pickme_nebula0;

import java.util.ArrayList;

/**
 * Event
 */
public class Event {
    private String eventID;
    // -1 means no waiting list capacity limit
    protected int waitingListCapacity = -1;
    protected int eventCapacity = -1;
    protected String eventPoster;
    protected ArrayList<EntrantRole> entrantsInWaitingList = new ArrayList<EntrantRole>();
    protected ArrayList<EntrantRole> entrantsChosen = new ArrayList<EntrantRole>();
    protected ArrayList<EntrantRole> entrantsDeclined = new ArrayList<EntrantRole>();
    protected ArrayList<EntrantRole> entrantsCancelled = new ArrayList<EntrantRole>();
    protected ArrayList<EntrantRole> entrantsEnrolled = new ArrayList<EntrantRole>();
    /**
     * Constructor
     */
    public Event() {
        eventID = null;
    }

    /**
     * Get eventID
     * @return eventID eventID
     */
    public String getEventID() {
        return this.eventID;
    }

    /**
     * Set eventID
     * @param eventID eventID
     */
    public void setEventID(String eventID) {
        this.eventID = eventID;
    }

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

    public void addEntrantToWaitingList(EntrantRole entrant) {
        this.entrantsInWaitingList.add(entrant);
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

    public void sampleFromWaitingList() {
        if (this.eventCapacity == -1) {
            this.entrantsChosen.addAll(this.entrantsInWaitingList);
        } else
        {
            int remainingCapacity = this.eventCapacity - this.entrantsChosen.size();
            if (remainingCapacity > 0) {
                // randomly add entrants in waiting list to chosen list (remaining capacity amount)
            }
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
