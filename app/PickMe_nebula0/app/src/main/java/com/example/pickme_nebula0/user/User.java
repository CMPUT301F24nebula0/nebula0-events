package com.example.pickme_nebula0.user;

import androidx.annotation.NonNull;

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
    protected String status;
    boolean admin;
    boolean notificationsEnabled;
    protected String phone;
    protected String profilePic;
    // todo - start as entrant only then add organizer, admin role conditionally

    public User(){

    }

    public User(String userID, String status) {
        this.userID = userID;
        this.status = status;
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

    public User(Boolean admin, String email, String nam, boolean notificationsEnabled, String phone, String profilePic)
    {
        this.admin = admin;
        this.email = email;
        this.name = nam;
        this.notificationsEnabled = notificationsEnabled;
        this.phone = phone;
        this.profilePic = profilePic;
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

    public void setName(String name) {
        this.name = name;
    }

    public boolean getAdmin() {
        return this.admin;
    }
    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    public boolean getNotificationsEnabled() {
        return this.notificationsEnabled;
    }
    public void setNotificationsEnabled(boolean notificationsEnabled) {
        this.notificationsEnabled = notificationsEnabled;
    }
    public String getPhone() {
        return this.phone;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * correct version
     * @return
     */
    public String getProfilePic() {
        return this.profilePic;
    }
    public void genProfilePic(){
        char Firstletter=this.getName().charAt(0);
        

    }
    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
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

    public String getStatus() { return this.status; }
    public void setStatus(String status) { this.status = status;}

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

    // TODO get rid of these
    /**
     * Returns the profile picture of the user
     * outdated to be finished
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

    @NonNull
    @Override
    public String toString() {
        return "User{" +
                "userID='" + userID + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", role='" + role + '\'' +
                ", profilePicture='" + profilePicture + '\'' +
                ", notifEnabled=" + notifEnabled +
                ", status='" + status + '\'' +
                '}';
    }

}
