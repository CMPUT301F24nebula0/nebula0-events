package com.example.pickme_nebula0;

import androidx.test.espresso.ViewInteraction;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.*;
import static androidx.test.espresso.matcher.ViewMatchers.*;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.allOf;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.swipeLeft;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;

import androidx.recyclerview.widget.RecyclerView;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import android.view.View;
import org.hamcrest.Matcher;
import com.example.pickme_nebula0.event.Event;
import com.example.pickme_nebula0.organizer.activities.OrganizerHomeActivity;
import com.example.pickme_nebula0.organizer.fragments.OrganizerOngoingFragment;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class OrganizerTest {


    @Rule
    public ActivityScenarioRule<OrganizerHomeActivity> activityScenarioRule =
            new ActivityScenarioRule<>(OrganizerHomeActivity.class);

    @Test
    public void testCreateEventSuccessfully() throws InterruptedException {
        // Navigate to the event creation screen

        // Navigate to the event creation screen
        onView(withId(R.id.backButton))
                .perform(click());

        // Fill in event details
        onView(withId(R.id.event_name_field))
                .perform(typeText("Community Gathering"), closeSoftKeyboard());
        onView(withId(R.id.event_description_field))
                .perform(typeText("A community gathering to discuss neighborhood plans."), closeSoftKeyboard());
        // Use a date picker to select a date
        onView(withId(R.id.event_date_field))
                .perform(click());

        onView(withId(R.id.geolocation_requirement_switch))
                .perform(click());
        onView(withId(R.id.geolocation_requirement_field))
                .perform(typeText("10"), closeSoftKeyboard());
        onView(withId(R.id.waitlist_capacity_required_switch))
                .perform(click());
        onView(withId(R.id.waitlist_capacity_field))
                .perform(typeText("100"), closeSoftKeyboard());
        onView(withId(R.id.number_of_attendees_field))
                .perform(typeText("50"), closeSoftKeyboard());

        // Submit the form
        onView(withId(R.id.event_creation_submit_button))
                .perform(click());


        onView(withId(R.id.view_pager))
                .perform(swipeLeft());
        Thread.sleep(3000);
        // Assuming 0 is the position for ongoing events



    }
    @Test
    public void testNavigateToOngingEventsAndWait() throws InterruptedException {
        // Navigate to past events
        onView(withId(R.id.view_pager))
                .perform(swipeLeft()); // Swipe to ongoing events section

        // Wait for 3 seconds

    }
    @Test
    public void testNavigateToPastgingEventsAndWait() throws InterruptedException {
        // Navigate to past events
        onView(withId(R.id.view_pager))
                .perform(swipeRight()); // Swipe to ongoing events section

        // Wait for 3 seconds
        Thread.sleep(3000);
    }
//    @Test
//    public void testViewAndNavigateEventDetails() {
//        // Navigate to ongoing events
//        onView(withId(R.id.view_pager))
//                .perform(swipeLeft()); // Swipe to ongoing events section
//        // Select an event named "curling" from the ongoing events list
//        onView(withId(R.id.past_events_recycler_view))
//                .perform(clone(withText("curling"), click()));
//
//        // Verify that the event details are displayed
//        onView(withText("Event Name: curling"))
//                .check(matches(isDisplayed()));
//        onView(withText("Description: egdhfgudyrrydyrdydry"))
//                .check(matches(isDisplayed()));
//        onView(withText("Date: Sat Nov 30 00:00:00 MST 2024"))
//                .check(matches(isDisplayed()));
//        onView(withText("Geolocation Required: No"))
//                .check(matches(isDisplayed()));
//        onView(withText("Waitlist Capacity Required: Yes"))
//                .check(matches(isDisplayed()));
//
//        // Click on the "Participants" button to view participants
//        onView(withId(R.id.participantsButton))
//                .perform(click());
//
//        // Verify that the participants screen is displayed with tabs for statuses
//        onView(withText("Waitlisted"))
//                .check(matches(isDisplayed()));
//        onView(withText("Selected"))
//                .check(matches(isDisplayed()));
//        onView(withText("Enrolled"))
//                .check(matches(isDisplayed()));
//        onView(withText("Cancelled"))
//                .check(matches(isDisplayed()));
//
//        // Navigate back to the event details screen
//        onView(withId(R.id.backButton))
//                .perform(click());
//
//        // Verify that we are back on the event details screen
//        onView(withText("Event Name: curling"))
//                .check(matches(isDisplayed()));
//    }

}