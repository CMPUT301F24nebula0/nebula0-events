package com.example.pickme_nebula0.organizer.exceptions;

import android.content.Context;
import android.widget.Toast;

import com.example.pickme_nebula0.SharedDialogue;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class OrganizerExceptions {

    boolean validEvent;
    SimpleDateFormat dateFormat;
    Date selectedDate;
    Calendar selectedCalendar;
    Calendar currentCalendar;
    int radius;
    int waitlistCap;
    int attendees;

    // validating input
    public boolean validateEventCreationUserInput(
            Context context,
            String deviceID,
            String eventName,
            String eventDescription,
            String eventDate,
            boolean geolocationRequired,
            String geolocationRequirement,
            boolean waitlistCapacityRequired,
            String waitlistCapacity,
            String numberOfAttendees
    ) {
        // TODO: The textbox for warning message is not big enough to display all the warnings. Make it bigger
        StringBuilder warningMessage = new StringBuilder("The event cannot be created for the following reason(s):");

        // Assume event is valid until proven otherwise
        validEvent = true;

        // validate deviceID
        if (!validateDeviceID(deviceID)) {
            warningMessage.append("\n- The deviceID could not be retrieved.");
            validEvent = false;
        }

        // validate event name
        if (!validateEventName(eventName)) {
            warningMessage.append("\n- The event name cannot be blank.");
            validEvent = false;
        }

        // validate event description
        if (!validateEventDescription(eventDescription)) {
            warningMessage.append("\n- The event description cannot be blank.");
            validEvent = false;
        }

        // validate event date
        if (!validateEventDate(eventDate)) {
            warningMessage.append("\n- The event date cannot be blank and must be in the future.");
            validEvent = false;
        }

        // validate geolocation requirement
        if (geolocationRequired) {
            if (!validateGeolocationRequirement(geolocationRequirement)) {
                warningMessage.append("\n- The geolocation requirement cannot be blank and must be a positive number.");
                validEvent = false;
            }
        }

        // validate number of attendees
        if (!validateNumberOfAttendees(numberOfAttendees)) {
            warningMessage.append("\n- The number of attendees cannot be blank and must be a positive number.");
            validEvent = false;
        }

        // validate waitlist capacity
        if (waitlistCapacityRequired && !validateWaitlistCapacity(waitlistCapacity)) {
            warningMessage.append("\n- The waitlist capacity cannot be blank and must be a positive number.");
            validEvent = false;
        }

        // validate waitlist capacity is greater than or equal to number of attendees
        if (waitlistCapacityRequired && !validateWaitlistCapacityGreaterThanNumberOfAttendees(waitlistCapacity, numberOfAttendees)) {
            warningMessage.append("\n- The waitlist capacity must be equal to or greater than the number of attendees.");
            validEvent = false;
        }

        // Display warning message if event is not valid
        if (!validEvent) {
            SharedDialogue.showInvalidDataAlert(warningMessage.toString(),context);
        }

        return validEvent;
    }


    private boolean validateDeviceID(String deviceID) {
        return deviceID != null && !deviceID.isEmpty();
    }

    private boolean validateEventName(String eventName) {
        return eventName != null && !eventName.isEmpty();
    }

    private boolean validateEventDescription(String eventDescription) {
        return eventDescription != null && !eventDescription.isEmpty();
    }

    private boolean validateEventDate(String eventDate) {
        if (eventDate.isEmpty()) {
            return false;
        } else {
            try {
                dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                selectedDate = dateFormat.parse(eventDate);

                // Remove time components for comparison
                selectedCalendar = Calendar.getInstance();
                selectedCalendar.setTime(selectedDate);
                selectedCalendar.set(Calendar.HOUR_OF_DAY, 0);
                selectedCalendar.set(Calendar.MINUTE, 0);
                selectedCalendar.set(Calendar.SECOND, 0);
                selectedCalendar.set(Calendar.MILLISECOND, 0);

                currentCalendar = Calendar.getInstance();
                currentCalendar.set(Calendar.HOUR_OF_DAY, 0);
                currentCalendar.set(Calendar.MINUTE, 0);
                currentCalendar.set(Calendar.SECOND, 0);
                currentCalendar.set(Calendar.MILLISECOND, 0);

                if (selectedCalendar.before(currentCalendar)) {
                    return false;
                }
            } catch (ParseException e) {
                return false;
            }
        }
        return true;
    }

    private boolean validateGeolocationRequirement(String geolocationRequirement) {
        if (geolocationRequirement.isEmpty()) {
            return false;
        } else {
            try {
                radius = Integer.parseInt(geolocationRequirement);
                if (radius < 0) {
                    return false;
                }
            } catch (NumberFormatException e) {
                return false;
            }
        }
        return true;
    }

    private boolean validateNumberOfAttendees(String numberOfAttendees) {
        if (numberOfAttendees.isEmpty()) {
            return false;
        } else {
            try {
                attendees = Integer.parseInt(numberOfAttendees);
                if (attendees < 0) {
                    return false;
                }
            } catch (NumberFormatException e) {
                return false;
            }
        }
        return true;
    }

    private boolean validateWaitlistCapacity(String waitlistCapacity) {
        if (waitlistCapacity.isEmpty()) {
            return false;
        } else {
            try {
                waitlistCap = Integer.parseInt(waitlistCapacity);
                if (waitlistCap < 0) {
                    return false;
                }
            } catch (NumberFormatException e) {
                return false;
            }
        }
        return true;
    }

    private boolean validateWaitlistCapacityGreaterThanNumberOfAttendees(String waitlistCapacity, String numberOfAttendees) {
        try {
            waitlistCap = Integer.parseInt(waitlistCapacity);
            attendees = Integer.parseInt(numberOfAttendees);
            if (waitlistCap < attendees) {
                return false;
            }
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }
}
