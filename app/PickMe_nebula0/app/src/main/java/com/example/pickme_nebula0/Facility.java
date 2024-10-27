package com.example.pickme_nebula0;


import java.util.ArrayList;

/**
 * Facility
 */
public class Facility {
    private String facilityID;
    private ArrayList<Event> events = new ArrayList<Event>();

    /**
     * Constructor
     */
    public Facility()
    {
        String facilityID = null;

    }

    /**
     * Get facilityID
     * @return facilityID facilityID
     */
    public String getFacilityID() {
        return this.facilityID;
    }

    /**
     * Set facilityID
     * @param facilityID facilityID
     */
    public void setFacilityID(String facilityID) {
        this.facilityID = facilityID;
    }

    public ArrayList<Event> getEvents() {
        return this.events;
    }

    public void addEvent(Event event) {
        this.events.add(event);
    }

    public void removeEvent(Event event) {
        this.events.remove(event);
    }
}
