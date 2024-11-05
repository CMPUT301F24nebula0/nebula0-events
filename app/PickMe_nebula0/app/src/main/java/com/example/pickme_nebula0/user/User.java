package com.example.pickme_nebula0.user;

import java.util.ArrayList;
import java.util.Arrays;


/**
 * User class
 */
public class User {
    protected String userID;
    protected String name;
    protected String email;
    protected String phoneNumber;
    protected String role;
    protected String profilePicture;
    protected Boolean notifEnabled;
    private final ArrayList<String> roles = new ArrayList<String>(Arrays.asList("admin", "entrant", "organizer"));
    // todo - start as entrant only then add organizer, admin role conditionally

    public User(){

    }

    /**
     * Minimum constructor for this class
     *
     * @param deviceID
     * @param name
     * @param email
     */
    public User(String deviceID, String name, String email) {
        this.userID = deviceID;
        this.name = name;
        this.email = email;
        this.notifEnabled = true;
    }

    /**
     * Additional parameterized constructor
     *
     * @param deviceID
     * @param name
     * @param email
     * @param phoneNumber
     * @param notifEnabled
     */
    public User(String deviceID, String name, String email, String phoneNumber ,Boolean notifEnabled) {
        this.userID = deviceID;
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.notifEnabled = notifEnabled;
    }

    public User(String deviceID, String name, String email, String phoneNumber ,String profilePicture) {
        this.userID = deviceID;
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.profilePicture = profilePicture;
    }


    /**
     * Returns the user ID
     *
     * @return  userID  the userID of the user
     */
    public String getUserID() {
        return this.userID;
    }

    public Boolean notifEnabled(){
        return notifEnabled;
    }

    /**
     * Sets the user ID
     *
     * @param  userID  the userID of the user
     */
    public void setUserID(String userID) {
        this.userID = userID;
    }

    /**
     * Returns the first name of the user
     *
     * @return  firstName  the first name of the user
     */
    public String getName() {
        return this.name;
    }

    /**
     * Sets the first name of the user
     *
     * @param  firstName  the first name of the user
     */
    public void setName(String firstName) {
        this.name = name;
    }

    /**
     * Returns the email of the user
     *
     * @return  email  the email of the user
     */
    public String getEmail() {
        return this.email;
    }

    /**
     * Sets the email of the user
     *
     * @param  email  the email of the user
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Returns the phone number of the user
     *
     * @return  phoneNumber  the phone number of the user
     */
    public String getPhoneNumber() {
        return this.phoneNumber;
    }

    /**
     * Sets the phone number of the user
     *
     * @param  phoneNumber  the phone number of the user
     */
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    /**
     * Returns the role of the user
     *
     * @return  role  the role of the user
     */
    public String getRole() {
        // check if role is valid
        if (this.roles.contains(this.role)) {
            return this.role;
        } else {
            // in future, throw an exception or return an error message in UI
            return "Invalid role";
        }
    }

    /**
     * Sets the role of the user
     *
     * @param  role  the role of the user
     */
    public void setRole(String role) {
        // check if role is valid
        if (this.roles.contains(role)) {
            this.role = role;
        } else {
            // in future, throw an exception or return an error message in UI
            System.out.println("Invalid role");
        }
    }

    /**
     * Returns the profile picture of the user
     *
     * @return  profilePicture  the profile picture of the user
     */
    public String getProfilePicture() {
        return this.profilePicture;
    }

    /**
     * Sets the profile picture of the user
     *
     * @param  profilePicture  the profile picture of the user
     */
    // US 01.03.01 As an entrant I want to upload a profile picture for a more personalized experience
    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    /**
     * Removes the profile picture of the user
     */
    // US 01.03.02 As an entrant I want remove profile picture if need be
    public void removeProfilePicture() {
        this.profilePicture = null;
    }



}
