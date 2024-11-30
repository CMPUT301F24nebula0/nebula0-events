package com.example.pickme_nebula0.user;

import androidx.annotation.NonNull;

/**
 * User class. Represents profile informations for entrants, organizers, and admin
 */
public class User {
    protected String userID;
    protected String name;
    protected String email;

    protected String phone;
    boolean notificationsEnabled = true;
    protected String profilePic;

    protected String role;
    protected String status;
    boolean admin;

    // un-parameterized constructor required for document toObject firestore function
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
        this.phone = phoneNumber;
        this.notificationsEnabled = notifEnabled;
    }

    public User(String deviceID, String name, String email, String phoneNumber ,String profilePicture) {
        this.userID = deviceID;
        this.name = name;
        this.email = email;
        this.phone = phoneNumber;
    }

    // Note that even though some getters and setters seem unused, they are required for Firebase to/from Object method

    // getters
    public String getUserID() {
        return this.userID;
    }
    public String getName() {
        return this.name;
    }
    public boolean getAdmin() {
        return this.admin;
    }
    public boolean getNotificationsEnabled() {
        return this.notificationsEnabled;
    }
    public String getPhone() {
        return this.phone;
    }
    public String getProfilePic() {
        return this.profilePic;
    }
    public String getEmail() {
        return this.email;
    }
    public String getStatus() { return this.status; }

    // setters
    public void setUserID(String userID) {
        this.userID = userID;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setAdmin(boolean admin) {
        this.admin = admin;
    }
    public void setNotificationsEnabled(boolean notificationsEnabled) {
        this.notificationsEnabled = notificationsEnabled;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }
    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public void setStatus(String status) { this.status = status;}


    @NonNull
    @Override
    public String toString() {
        return "User{" +
                "userID='" + userID + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", phoneNumber='" + phone + '\'' +
                ", role='" + role + '\'' +
                ", profilePicture='" + profilePic+ '\'' +
                ", notificationsEnabled=" + notificationsEnabled +
                ", status='" + status + '\'' +
                '}';
    }

}
