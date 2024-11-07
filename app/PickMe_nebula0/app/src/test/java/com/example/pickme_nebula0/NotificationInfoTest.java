package com.example.pickme_nebula0;

import com.example.pickme_nebula0.notification.NotificationCreationActivity;

import org.junit.Test;

/**
 * Tests input validation for NotificationCreationActivity where organizers create notifications
 * @author Stephine Yearley
 * @see NotificationCreationActivity
 */
public class NotificationInfoTest {
    @Test
    public void acceptsValidInput(){
        String warn = NotificationCreationActivity.validateNotifInfo("mySubjectLine", "My single line message body");
        assert (warn.isBlank());
    }

    @Test
    public void acceptsValidInputMultilineMessage(){
        String warn = NotificationCreationActivity.validateNotifInfo("mySubjectLine", "My\nmulti line\nmessage body");
        assert (warn.isBlank());
    }

    @Test
    public void warnsNoSubjectLine(){
        String warn = NotificationCreationActivity.validateNotifInfo("", "myBody");
        assert (!warn.isBlank());
    }

    @Test
    public void warnsNoFacilityAddress(){
        String warn = NotificationCreationActivity.validateNotifInfo("mySubject", "");
        assert (!warn.isBlank());
    }
}
