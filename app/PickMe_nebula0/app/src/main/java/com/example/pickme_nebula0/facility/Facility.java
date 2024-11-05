package com.example.pickme_nebula0.facility;


import com.example.pickme_nebula0.DeviceManager;
import com.example.pickme_nebula0.db.DBManager;
import com.example.pickme_nebula0.event.Event;

import java.util.ArrayList;

/**
 * Facility
 */
public class Facility {
    private DBManager dbManager;
    private String facilityID;
    private String facilityName;
    private String facilityAddress;
    private String organizerID;

    /**
     * Constructor
     */
    public Facility(String facilityName, String facilityAddress) {
        dbManager = new DBManager();
        organizerID = DeviceManager.getDeviceId();

        this.facilityName = facilityName;
        this.facilityAddress = facilityAddress;
    }

    public Facility(String facilityID,String organizerID,String facilityName, String facilityAddress){
        dbManager = new DBManager();
        this.organizerID = organizerID;
        this.facilityID = facilityID;
        this.facilityName = facilityName;
        this.facilityAddress = facilityAddress;
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

    public String getFacilityName() {return this.facilityName;}

    public String getFacilityAddress() {return this.facilityAddress;}

    public void setFacilityName(String facilityName){this.facilityName = facilityName;}

    public void setFacilityAddress(String facilityAddress){this.facilityAddress = facilityAddress;}
}