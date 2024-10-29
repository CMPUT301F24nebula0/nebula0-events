package com.example.pickme_nebula0;

import android.widget.ArrayAdapter;

import java.util.ArrayList;

/**
 * AdminRole
 * @Authors: SinaShaban
 *
 */
public class AdminRole extends User {
    private String adminID;
    private ArrayList<User> usersList;
    private ArrayAdapter<User> userAdapter;
    private ArrayList<String> imagesList;
    private ArrayAdapter<String> imageAdapter;
    private ArrayList<Event> eventsList;
    private ArrayAdapter<Event> eventAdapter;
    private ArrayList<Facility> facilitiesList;
    private ArrayAdapter<Facility> facilityAdapter;


    /**
     * Constructor
     */
    public AdminRole(String adminID, ArrayList<User> usersList, ArrayAdapter<User> userAdapter,
                     ArrayList<String> imagesList, ArrayAdapter<String> imageAdapter,
                     ArrayList<Event> eventsList, ArrayAdapter<Event> eventAdapter,
                     ArrayList<Facility> facilitiesList, ArrayAdapter<Facility> facilityAdapter) {
        this.adminID = adminID;
        this.usersList = usersList;
        this.userAdapter = userAdapter;
        this.imagesList = imagesList;
        this.imageAdapter = imageAdapter;
        this.eventsList = eventsList;
        this.eventAdapter = eventAdapter;
        this.facilitiesList = facilitiesList;
        this.facilityAdapter = facilityAdapter;
    }

    /**
     * Get adminID
     * @return adminID adminID
     */
    public String getAdminID() {
        return this.adminID;
    }

    /**
     * Set adminID
     * @param adminID adminID
     */
    public void setAdminID(String adminID) {
        this.adminID = adminID;
    }

    // all functions below will require db access to view/remove events
    // access DB manager object

    // US 03.01.01 As an administrator, I want to be able to remove events.
    public void removeEvent(Event event) {
        if(event != null && eventsList.contains(event)){
            eventsList.remove(event);
            eventAdapter.notifyDataSetChanged(); // Update UI if using an adapter

        }else {
            // handle the case where the event does not exist


        }


    }

    //US 03.02.01 As an administrator, I want to be able to remove profiles.
    public void removeUser(User user) {
        if(user != null && usersList.contains(user)){

            usersList.remove(user);
            userAdapter.notifyDataSetChanged(); // Update UI if using an adapter

        }else {
            // handle the case where the user does not exist

        }


    }


    //US 03.03.01 As an administrator, I want to be able to remove images.
    public void removeImage(String image) {
        // in future, remove image
        if(image != null && eventsList.contains(image)){
            eventsList.remove(image);
            eventAdapter.notifyDataSetChanged(); // Update UI if using an adapter
            }else {
            // handle the case where the image does not exist
        }
    }

    //US 03.03.02 As an administrator, I want to be able to remove hashed QR code data
    public void removeHashedQRCodeData(String hashData) {
        // in future, remove hash data
        if(hashData != null && eventsList.contains(hashData)){
            eventsList.remove(hashData);
            eventAdapter.notifyDataSetChanged(); // Update UI if using an adapter
        }else {
            // handle the case where the hash data does not exist
        }
    }
    // US 03.04.01 As an administrator, I want to be able to browse events.

    // US 03.05.01 As an administrator, I want to be able to browse profiles.

    // US 03.06.01 As an administrator, I want to be able to browse images.

    // US 03.07.01 As an administrator I want to remove facilities that violate app policy
    public void removeFacility(Facility facility) {
        // update firebase to remove facility from db

        // remove facility from organizer who owns it
        if(facility != null && facilitiesList.contains(facility)){
            facilitiesList.remove(facility);
            facilityAdapter.notifyDataSetChanged(); // Update UI if using an adapter
        }else {
            // handle the case where the facility does not exist
            // should be possible for an organizer to have no facility
        }



    }


    //US 03.04.01 As an administrator, I want to be able to browse events.
    public void browseEvents() {
        // in future, browse events
        if(!eventsList.isEmpty()){
            for(Event event : eventsList){
                // display event details
            }
            eventAdapter.notifyDataSetChanged(); // Refresh UI if needed
        }
        else {
            // handle the case where there are no events
        }
    }

    //US 03.05.01 As an administrator, I want to be able to browse profiles.
    public void browseUsers() {
        // in future, browse users
        if(!usersList.isEmpty()){
            for(User user : usersList){
                // display user details
            }
            userAdapter.notifyDataSetChanged(); // Refresh UI if needed
            }
        else {
            // handle the case where there are no users
        }

    }


    //US 03.06.01 As an administrator, I want to be able to browse images.
    public void browseImages() {
        // in future, browse images
        if(!imagesList.isEmpty()){
            for(String image : imagesList){
                // display image details
            }
            imageAdapter.notifyDataSetChanged(); // Refresh UI if needed
            }
        else {
            // handle the case where there are no images
        }

    }


    //US 03.07.01 As an administrator I want to remove facilities that violate app policy
    public void DisplayFacility() {
        // in future, remove facility
        if(!facilitiesList.isEmpty()){
            for(Facility facility : facilitiesList){
                // display facility details
            }
            facilityAdapter.notifyDataSetChanged(); // Refresh UI if needed

            }
        else {
            // handle the case where there are no facilities
        }


    }


}
