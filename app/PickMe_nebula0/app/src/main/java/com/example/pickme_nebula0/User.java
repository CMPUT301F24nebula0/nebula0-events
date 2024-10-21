package com.example.pickme_nebula0;

import java.util.ArrayList;
import java.util.Arrays;

public class User {
    private String userID;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String role;
    private final ArrayList<String> roles = new ArrayList<String>(Arrays.asList("admin", "entrant", "organizer"));

    public User() {
        this.userID = null;
        this.firstName = null;
        this.lastName = null;
        this.email = null;
        this.phoneNumber = null;
    }

    public User(String firstName, String lastName) {
        this.userID = null;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = null;
        this.phoneNumber = null;
    }

    public String getUserID() {
        return this.userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return this.phoneNumber;
    }

    public void setPhoneNumber() {
        this.phoneNumber = phoneNumber;
    }

    public String getRole() {
        if (this.roles.contains(this.role)) {
            return this.role;
        } else {
            // in future, throw an exception or return an error message in UI
            return "Invalid role";
        }
    }

    public void setRole(String role) {
        if (this.roles.contains(role)) {
            this.role = role;
        } else {
            // in future, throw an exception or return an error message in UI
            System.out.println("Invalid role");
        }
    }
}
