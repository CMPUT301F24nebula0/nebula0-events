package com.example.pickme_nebula0.notification;

/**
 * Notification class
 */
public class Notification {
    private String notificationID;

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
