package com.example.pickme_nebula0;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import com.example.pickme_nebula0.event.Event;

import java.sql.Date;
import java.text.SimpleDateFormat;

public class eventCreationTest {

    private Event event;

    @Before
    public void setUp() {
        event = new Event();
    }

    @After
    public void tearDown() {
        event = null;
    }

    @Test
    public void testCreateEventSuccessfully() {
        // Set event details
        event.setEventName("Community Gathering");
        event.setEventDescription("A community gathering to discuss neighborhood plans.");
        event.setEventDate(Date.valueOf("2024-12-15"));
        event.setGeolocationRequirement(10);
        event.setWaitlistCapacity(5);
        event.setNumberOfAttendees(50);

        // Verify event details
        assertEquals("Community Gathering", event.getEventName());
        assertEquals("A community gathering to discuss neighborhood plans.", event.getEventDescription());

        // Convert Date to String for comparison
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String expectedDateString = dateFormat.format(Date.valueOf("2024-12-15"));
        assertEquals(expectedDateString, dateFormat.format(event.getEventDate()));

        assertEquals(10, event.getGeolocationRequirement());
        assertEquals(5, event.getWaitlistCapacity());
        assertEquals(50, event.getNumberOfAttendees());
    }


}