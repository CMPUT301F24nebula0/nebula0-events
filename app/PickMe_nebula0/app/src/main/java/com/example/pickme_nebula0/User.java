package com.example.pickme_nebula0;

import java.util.ArrayList;
import java.util.Arrays;

// comment to test git remove functionality

/**
 * User class
 */
public class User {
    protected String userID;
    protected String firstName;
    protected String lastName;
    protected String email;
    protected String phoneNumber;
    protected String role;
    protected String profilePicture;
    private final ArrayList<String> roles = new ArrayList<String>(Arrays.asList("admin", "entrant", "organizer"));

    /**
     * Constructor for User class
     */
    public User() {
        this.userID = null;
        this.firstName = null;
        this.lastName = null;
        this.email = null;
        this.phoneNumber = null;
        this.profilePicture = null;
    }

    /**
     * Constructor for User class
     * @param   firstName   the first name of the user
     * @param   lastName    the last name of the user
     */
    public User(String firstName, String lastName) {
        this.userID = null;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = null;
        this.phoneNumber = null;
        this.profilePicture = null;
    }

    /**
     * Constructor for User class
     * @param   userID          the userID of the user
     * @param   firstName       the first name of the user
     * @param   lastName        the last name of the user
     * @param   email           the email of the user
     * @param   phoneNumber     the phone number of the user
     * @param   profilePicture  the profile picture of the user
     */
    public User(String userID, String firstName, String lastName, String email, String phoneNumber, String profilePicture) {
        this.userID = userID;
        this.firstName = firstName;
        this.lastName = lastName;
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
    public String getFirstName() {
        return this.firstName;
    }

    /**
     * Sets the first name of the user
     *
     * @param  firstName  the first name of the user
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Returns the last name of the user
     *
     * @return  lastName  the last name of the user
     */
    public String getLastName() {
        return this.lastName;
    }

    /**
     * Sets the last name of the user
     *
     * @param  lastName  the last name of the user
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
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
