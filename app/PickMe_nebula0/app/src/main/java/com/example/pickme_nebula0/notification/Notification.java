package com.example.pickme_nebula0.notification;

import com.google.firebase.Timestamp;

import java.sql.Time;

/**
 * Notification class, represents notification messages.
 * Notifications are used to create android notifications and messages viewable in app
 *
 * @see NotificationCreationActivity
 * @see MessageViewActivity
 */
public class Notification {
    // In order to use Firestore's toObject function, these attribute names must match the fields of
    // Notifications.<userID>.userNotifs.<notifID>, and have an appropriately named getter and setter
    private String notificationID;
    private String title;
    private  String message;
    private Timestamp timestamp;
    private String eventID;
    private String userID;

    /**
     * Parameterized constructor
     *
     * @param title title/subject line of notification
     * @param message message body of notification
     * @param userID deviceID of user receiving this notification
     * @param eventID eventID of event this notification is associated with
     * @param timestamp timestamp of when this notification was created
     * @param notificationID unique identifier for this notification withing the Notifications.<userID>.userNotifs collection
     */
    public Notification(String title, String message, String userID, String eventID, Timestamp timestamp, String notificationID){
        this.title = title;
        this.message = message;
        this.timestamp = timestamp;
        this.eventID = eventID;
        this.userID = userID;
        this.notificationID = notificationID;
    }

    /**
     * Default constructor required for Firestore's `toObject` function.
     */
    public Notification(){};

    /**
     * Returns the unique notification ID.
     *
     * @return the notification ID
     */
    public String getNotificationID() {
        return this.notificationID;
    }

    /**
     * Returns the title or subject of the notification.
     *
     * @return the notification title
     */
    public String getTitle() {
        return this.title;
    }

    /**
     * Returns the body of the notification.
     *
     * @return the notification message
     */
    public String getMessage() {
        return this.message;
    }

    /**
     * Returns the timestamp indicating when the notification was created.
     *
     * @return the notification timestamp
     */
    public Timestamp getTimestamp() {
        return this.timestamp;
    }

    /**
     * Returns the event ID associated with this notification.
     *
     * @return the event ID
     */
    public String getEventID() {
        return this.eventID;
    }


    /**
     * Returns the user ID (or device ID) of the notification recipient.
     *
     * @return the user ID
     */
    public String getUserID(){ return this.userID;}

    /**
     * Sets the unique notification ID.
     *
     * @param notificationID the notification ID to set
     */
    public void setNotificationID(String notificationID) {
        this.notificationID = notificationID;
    }

    /**
     * Sets the title or subject of the notification.
     *
     * @param title the title to set
     */
    public void setTitle(String title){this.title = title;}

    /**
     * Sets the body of the notification.
     *
     * @param message the message to set
     */
    public void setMessage(String message){this.message = message;}

    /**
     * Sets the timestamp indicating when the notification was created.
     *
     * @param timestamp the timestamp to set
     */
    public void setTimestamp(Timestamp timestamp){this.timestamp = timestamp;}

    /**
     * Sets the event ID associated with this notification.
     *
     * @param eventID the event ID to set
     */
    public void setEventID(String eventID){this.eventID = eventID;}

    /**
     * Sets the user ID (or device ID) of the notification recipient.
     *
     * @param userID the user ID to set
     */
    public void setUserID(String userID){this.userID = userID;}
}
