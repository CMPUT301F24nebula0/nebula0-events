package com.example.pickme_nebula0;

/**
 * AdminRole
 */
public class AdminRole {
    private String adminID;

    /**
     * Constructor
     */
    public AdminRole() {
        adminID = null;
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

    // US 03.01.01 As an administrator, I want to be able to remove events.
    public void removeEvent(Event event) {
        // in future, remove event
    }

    //US 03.02.01 As an administrator, I want to be able to remove profiles.
    public void removeUser(User user) {
        // in future, remove user
    }

    //US 03.03.01 As an administrator, I want to be able to remove images.
    public void removeImage(String image) {
        // in future, remove image
    }

    //US 03.03.02 As an administrator, I want to be able to remove hashed QR code data
    public void removeHashedQRCodeData(String hashData) {
        // in future, remove hash data
    }

    //US 03.04.01 As an administrator, I want to be able to browse events.

    //US 03.05.01 As an administrator, I want to be able to browse profiles.
    //
    //US 03.06.01 As an administrator, I want to be able to browse images.
    //
    //US 03.07.01 As an administrator I want to remove facilities that violate app policy
}
