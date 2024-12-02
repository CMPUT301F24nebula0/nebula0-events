package com.example.pickme_nebula0.user;

import androidx.annotation.NonNull;

/**
 * User Class
 *
 * This class represents a user profile, encompassing information for entrants, organizers, and admins.
 *
 * Key Features:
 * - Encapsulates user attributes such as `userID`, `name`, `email`, `phone`, `role`, and `profilePic`.
 * - Supports constructors for different use cases, such as creating new users, admin users, and fetching user profiles from Firestore.
 * - Provides getter and setter methods for Firebase Firestore to interact with this class seamlessly.
 *
 * Use Cases:
 * - Creating and managing user profiles for various roles (entrant, organizer, admin).
 * - Interfacing with Firebase Firestore using object mapping.
 *
 * Note:
 * - Some methods and fields might appear unused but are essential for Firebase Firestore's `toObject` and `fromObject` methods.
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

    /**
     * Default Constructor
     *
     * Required for Firebase Firestore's `toObject` and `fromObject` methods.
     */    public User(){ }

    /**
     * Constructs a `User` with a `userID` and `status`.
     *
     * Useful for initializing users with minimal data, such as their ID and registration status.
     *
     * @param userID The unique identifier for the user.
     * @param status The current status of the user.
     */
    public User(String userID, String status) {
        this.userID = userID;
        this.status = status;
    }

    /**
     * Minimal Constructor
     *
     * Creates a `User` instance with only the essential attributes: `userID`, `name`, and `email`.
     *
     * @param deviceID The unique identifier for the user's device.
     * @param name The user's name.
     * @param email The user's email address.
     */
    public User(String deviceID, String name, String email) {
        this.userID = deviceID;
        this.name = name;
        this.email = email;
    }

    /**
     * Constructor for Admin User Initialization
     *
     * Initializes an admin user with attributes such as `name`, `email`, `notificationsEnabled`, `phone`, and `profilePic`.
     *
     * @param admin Whether the user is an admin.
     * @param email The admin's email address.
     * @param nam The admin's name.
     * @param notificationsEnabled Whether notifications are enabled for the admin.
     * @param phone The admin's phone number.
     * @param profilePic The admin's profile picture URL.
     */
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
     * Fully Parameterized Constructor
     *
     * Creates a `User` instance with additional attributes like `phone` and `notificationsEnabled`.
     *
     * @param deviceID The unique identifier for the user's device.
     * @param name The user's name.
     * @param email The user's email address.
     * @param phoneNumber The user's phone number.
     * @param notifEnabled Whether notifications are enabled for the user.
     */
    public User(String deviceID, String name, String email, String phoneNumber ,Boolean notifEnabled) {
        this.userID = deviceID;
        this.name = name;
        this.email = email;
        this.phone = phoneNumber;
        this.notificationsEnabled = notifEnabled;
    }

    /**
     * Fully Parameterized Constructor
     *
     * Creates a `User` instance with additional attributes like `phone`.
     *
     * @param deviceID The unique identifier for the user's device.
     * @param name The user's name.
     * @param email The user's email address.
     * @param phoneNumber The user's phone number.
     */
    public User(String deviceID, String name, String email, String phoneNumber ,String profilePicture) {
        this.userID = deviceID;
        this.name = name;
        this.email = email;
        this.phone = phoneNumber;
    }

    // Note that even though some getters and setters seem unused, they are required for Firebase to/from Object method

    /**
     * @return The user's unique identifier.
     */
    public String getUserID() {
        return this.userID;
    }

    /**
     * @return The user's name.
     */
    public String getName() {
        return this.name;
    }

    /**
     * @return Whether the user is an admin.
     */
    public boolean getAdmin() {
        return this.admin;
    }

    /**
     * @return Whether notifications are enabled for the user.
     */
    public boolean getNotificationsEnabled() {
        return this.notificationsEnabled;
    }

    /**
     * @return The user's phone number.
     */
    public String getPhone() {
        return this.phone;
    }

    /**
     * @return The user's profile picture URL.
     */
    public String getProfilePic() {
        return this.profilePic;
    }

    /**
     * @return The user's email address.
     */
    public String getEmail() {
        return this.email;
    }

    /**
     * @return The user's status (e.g., active, waitlisted, canceled).
     */
    public String getStatus() { return this.status; }

    /**
     * Sets the user's unique identifier.
     *
     * @param userID The unique identifier to set.
     */
    public void setUserID(String userID) {
        this.userID = userID;
    }

    /**
     * Sets the user's name.
     *
     * @param name The name to set.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Sets whether the user is an admin.
     *
     * @param admin The admin status to set.
     */
    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    /**
     * Sets whether notifications are enabled for the user.
     *
     * @param notificationsEnabled The notifications status to set.
     */
    public void setNotificationsEnabled(boolean notificationsEnabled) {
        this.notificationsEnabled = notificationsEnabled;
    }

    /**
     * Sets the user's phone number.
     *
     * @param phone The phone number to set.
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * Sets the user's profile picture URL.
     *
     * @param profilePic The profile picture URL to set.
     */
    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    /**
     * Sets the user's email address.
     *
     * @param email The email address to set.
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Sets the user's status (e.g., active, waitlisted, canceled).
     *
     * @param status The status to set.
     */
    public void setStatus(String status) { this.status = status;}

    /**
     * Converts the user object to a string representation.
     *
     * @return A string containing the user's attributes.
     */
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
