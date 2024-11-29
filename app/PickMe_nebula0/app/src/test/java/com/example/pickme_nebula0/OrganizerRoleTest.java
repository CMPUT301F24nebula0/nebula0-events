package com.example.pickme_nebula0;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;

import com.example.pickme_nebula0.entrant.EntrantRole;
import com.example.pickme_nebula0.event.Event;
import com.example.pickme_nebula0.organizer.OrganizerRole;

import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;

import java.util.ArrayList;

public class  OrganizerRoleTest {
    private OrganizerRole organizer;
    private Event testEvent;

    @BeforeEach
    public void setUp() {
        organizer = new OrganizerRole();
        organizer.setOrganizerID("test_organizer_001");
        testEvent = new Event();
        testEvent.setEventID("test_event_001");
        testEvent.setEventName("Test Event");
        testEvent.setWaitlistCapacity(100);
    }


}
