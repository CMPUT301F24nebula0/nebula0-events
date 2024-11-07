package com.example.pickme_nebula0.notification;

import com.google.firebase.Timestamp;

import java.sql.Time;

/**
 * Notification class
 */
public class Notification {
    private String notificationID;
    private String title;
    private  String message;
    private Timestamp timestamp;
    private String eventID;
    private String userID;

    /**
     * Constructor
     */
    public Notification(String title, String message, String userID, String eventID, Timestamp timestamp, String notificationID){
        this.title = title;
        this.message = message;
        this.timestamp = timestamp;
        this.eventID = eventID;
        this.userID = userID;
        this.notificationID = notificationID;
    }

    // Empty Constructor required for Firestore
    public Notification(){};

    // getters
    public String getNotificationID() {
        return this.notificationID;
    }
    public String getTitle() {
        return this.title;
    }
    public String getMessage() {
        return this.message;
    }
    public Timestamp getTimestamp() {
        return this.timestamp;
    }
    public String getEventID() {
        return this.eventID;
    }
    public String getUserID(){ return this.userID;}

    // Setters
    public void setNotificationID(String notificationID) {
        this.notificationID = notificationID;
    }
    public void setTitle(String title){this.title = title;}
    public void setMessage(String message){this.message = message;}
    public void setTimestamp(Timestamp timestamp){this.timestamp = timestamp;}
    public void setEventID(String eventID){this.eventID = eventID;}
    public void setUserID(String userID){this.userID = userID;}


}
