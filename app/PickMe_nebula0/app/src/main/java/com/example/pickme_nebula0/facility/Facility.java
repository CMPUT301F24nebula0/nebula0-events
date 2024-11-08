package com.example.pickme_nebula0.facility;


import com.example.pickme_nebula0.DeviceManager;
import com.example.pickme_nebula0.db.DBManager;

/**
 * Facility
 */
public class Facility {
    private DBManager dbManager;
    private String facilityID;
    private String name;
    private String address;
    private String organizerID;

    /**
     * Constructor
     */
    public Facility(String facilityName, String facilityAddress) {
        dbManager = new DBManager();
        organizerID = DeviceManager.getDeviceId();

        this.name = facilityName;
        this.address = facilityAddress;
    }

    /**
     * Unparameterized constructor for firebase toObject
     */
    public Facility(){

    }

    public Facility(String facilityID,String organizerID,String facilityName, String facilityAddress){
        dbManager = new DBManager();
        this.organizerID = organizerID;
        this.facilityID = facilityID;
        this.name = facilityName;
        this.address = facilityAddress;
    }

    /**
     * Get facilityID
     *
     * @return facilityID facilityID
     */
    public String getFacilityID() {
        return this.facilityID;
    }

    public String getOrganizerID(){return this.organizerID;}

    public String getName() {return this.name;}

    public String getAddress() {return this.address;}

    public void setName(String name){this.name = name;}

    public void setAddress(String address){this.address = address;}

    public void setFacilityID(String facilityID){this.facilityID = facilityID;}

}