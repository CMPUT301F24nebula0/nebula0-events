package com.example.pickme_nebula0.entrant;

import com.example.pickme_nebula0.user.User;
import java.util.Objects;

/**
 * EntrantRole
 */
public class EntrantRole extends User {
    private String entrantID;

    /**
     * Constructor
     */
    public EntrantRole(String userID, String name, String email, String phoneNumber, String profilePicture) {
        // Call the superclass constructor (User) with parameters
        super(userID, name, email, phoneNumber, profilePicture);
        this.entrantID = null; // Initialize entrantID as null or another value
    }

    /**
     * Get entrantID
     * @return entrantID entrantID
     */
    public String getEntrantID() {
        return this.entrantID;
    }


    @Override
    public boolean equals(Object o) {
        if (o == this) { return true; }
        if (!(o instanceof EntrantRole)) { return false; }
        EntrantRole entrant = (EntrantRole) o;
        return Objects.equals(this.getEntrantID(), entrant.getEntrantID());
    }


}
