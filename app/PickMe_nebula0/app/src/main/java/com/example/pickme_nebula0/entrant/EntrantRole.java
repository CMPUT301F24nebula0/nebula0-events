package com.example.pickme_nebula0.entrant;

import com.example.pickme_nebula0.user.User;
import java.util.Objects;

/**
 * EntrantRole
 *
 * This class represents the role of an entrant in the application.
 * It extends the `User` class, inheriting user-related properties such as name, email, phone number, and profile picture.
 *
 * Additional Features:
 * - Provides a unique identifier (`entrantID`) specific to entrants.
 * - Implements `equals` to compare `EntrantRole` objects based on their `entrantID`.
 *
 * @see User
 */
public class EntrantRole extends User {
    private String entrantID;

    /**
     * Constructor
     *
     * - Initializes an EntrantRole object with user-related details and a specific entrant role identifier.
     * - Calls the superclass constructor (`User`) to set shared user properties.
     *
     * @param userID The unique ID of the user.
     * @param name The name of the user.
     * @param email The email address of the user.
     * @param phoneNumber The phone number of the user.
     * @param profilePicture The URL or path to the user's profile picture.
     */
    public EntrantRole(String userID, String name, String email, String phoneNumber, String profilePicture) {
        // Call the superclass constructor (User) with parameters
        super(userID, name, email, phoneNumber, profilePicture);
        this.entrantID = null; // Initialize entrantID as null or another value
    }

    /**
     * Get the entrant's unique identifier.
     *
     * @return entrantID The unique identifier for the entrant.
     */
    public String getEntrantID() {
        return this.entrantID;
    }

    /**
     * Checks for equality between two `EntrantRole` objects.
     *
     * - Two `EntrantRole` objects are considered equal if their `entrantID` fields are the same.
     *
     * @param o The object to compare.
     * @return `true` if the objects are equal based on `entrantID`, otherwise `false`.
     */
    @Override
    public boolean equals(Object o) {
        if (o == this) { return true; }
        if (!(o instanceof EntrantRole)) { return false; }
        EntrantRole entrant = (EntrantRole) o;
        return Objects.equals(this.getEntrantID(), entrant.getEntrantID());
    }


}
