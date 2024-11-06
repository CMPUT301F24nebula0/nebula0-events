package com.example.pickme_nebula0.notification;

import com.google.firebase.Timestamp;

/**
 * Notification class
 */
public class Notification {
    private String notificationID;
    private String title;
    private  String message;
    private Timestamp timestamp;

    /**
     * Constructor
     */
    public Notification() {
        notificationID = null;
    }

    /**
     * Get notification ID
     * @return notification ID
     */
    public String getNotificationID() {
        return this.notificationID;
    }

    /**
     * Set notification ID
     * @param notificationID notification ID
     */
    public void setNotificationID(String notificationID) {
        this.notificationID = notificationID;
    }
}
