package com.example.pickme_nebula0.facility;

import com.example.pickme_nebula0.DeviceManager;
import com.example.pickme_nebula0.db.DBManager;

/**
 * Facility represents an organizer's facility
 */
public class Facility {
    private DBManager dbManager;
    private String facilityID;
    private String name;
    private String address;
    private String organizerID;

    /**
     * Constructor for creating a new facility with a specified name and address.
     *
     * @param facilityName    the name of the facility
     * @param facilityAddress the address of the facility
     */
    public Facility(String facilityName, String facilityAddress) {
        this.dbManager = new DBManager();
        this.organizerID = DeviceManager.getDeviceId();
        this.name = facilityName;
        this.address = facilityAddress;
    }

    /**
     * Default constructor for Firebase deserialization using `toObject`.
     */
    public Facility(){ }

    /**
     * Constructor for creating a facility with all attributes specified.
     *
     * @param facilityID      the unique identifier for the facility
     * @param organizerID     the ID of the organizer managing the facility
     * @param facilityName    the name of the facility
     * @param facilityAddress the address of the facility
     */
    public Facility(String facilityID, String organizerID, String facilityName, String facilityAddress){
        dbManager = new DBManager();
        this.organizerID = organizerID;
        this.facilityID = facilityID;
        this.name = facilityName;
        this.address = facilityAddress;
    }

    /**
     * Returns the facility ID.
     *
     * @return the unique identifier of the facility
     */
    public String getFacilityID() {
        return this.facilityID;
    }

    /**
     * Returns the organizer ID associated with this facility.
     *
     * @return the organizer ID
     */
    public String getOrganizerID(){return this.organizerID;}

    /**
     * Returns the name of the facility.
     *
     * @return the facility name
     */
    public String getName() {return this.name;}

    /**
     * Returns the address of the facility.
     *
     * @return the facility address
     */
    public String getAddress() {return this.address;}

    /**
     * Sets the name of the facility.
     *
     * @param name the new name of the facility
     */
    public void setName(String name){this.name = name;}

    /**
     * Sets the address of the facility.
     *
     * @param address the new address of the facility
     */
    public void setAddress(String address){this.address = address;}

    /**
     * Sets the facility ID.
     *
     * @param facilityID the unique identifier for the facility
     */
    public void setFacilityID(String facilityID){this.facilityID = facilityID;}

}