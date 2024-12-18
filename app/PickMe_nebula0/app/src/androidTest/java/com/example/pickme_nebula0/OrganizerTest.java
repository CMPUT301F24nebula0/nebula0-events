package com.example.pickme_nebula0;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.IdlingResource;
import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.*;
import static androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static androidx.test.espresso.matcher.ViewMatchers.*;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.not;

import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.contrib.PickerActions;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.widget.DatePicker;
import androidx.recyclerview.widget.RecyclerView;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.runner.lifecycle.ActivityLifecycleMonitorRegistry;
import androidx.test.runner.lifecycle.Stage;

import android.view.View;
import android.widget.DatePicker;
import android.widget.Toast;

import org.hamcrest.Matcher;
import com.example.pickme_nebula0.event.Event;
import com.example.pickme_nebula0.event.EventDetailActivity;
import com.example.pickme_nebula0.organizer.activities.OrganizerHomeActivity;
import com.example.pickme_nebula0.organizer.fragments.OrganizerOngoingFragment;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class OrganizerTest {
    private ActivityResultLauncher<PickVisualMediaRequest> pickMedia;

    @Rule
    public ActivityScenarioRule<OrganizerHomeActivity> activityScenarioRule =
            new ActivityScenarioRule<>(OrganizerHomeActivity.class);

    /**
     * Test case for creating an event successfully.
     * This test navigates to the event creation screen, fills in event details, selects a date, and submits the form.
     * The form fields include event name, description, date, geolocation requirement, waitlist capacity, and number of attendees.
     */
    @Test
    public void testCreateEventSuccessfully() throws InterruptedException {
        // Navigate to the event creation screen
        onView(withId(R.id.view_pager))
                .perform(swipeLeft()); // Swipe to ongoing events section
        Thread.sleep(1000);



        // Click on the "Create Event" button
        onView(withId(R.id.createEventButton))
                .perform(click());

        // Fill in event details
        onView(withId(R.id.event_name_field))
                .perform(typeText("Field hockey -23"), closeSoftKeyboard());
        onView(withId(R.id.event_description_field))
                .perform(typeText("A community gathering to discuss future events."), closeSoftKeyboard());

        // Select a date using the date picker
        onView(withId(R.id.event_date_field))
                .perform(click());
        Thread.sleep(1000); // Wait for the date picker to open

        // Interact with the DatePicker dialog to set a specific date
        // Adjust to match your test date
        onView(withClassName(Matchers.equalTo(DatePicker.class.getName())))
                .perform(PickerActions.setDate(2024, 12, 25)); // Example: December 25, 2023

        // Confirm the selected date
        onView(withText("OK")).perform(click());

        // Set other event details
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

        // Navigate to the ongoing events section to confirm the event creation
        onView(withId(R.id.view_pager))
                .perform(swipeLeft());
        Thread.sleep(3000);
    }

    /**
     * Custom action to click on a child view within a view hierarchy by specifying its ID.
     * This can be useful when interacting with specific child views inside complex layouts, such as RecyclerView items.
     */
    public class CustomViewActions {
        public ViewAction clickChildViewWithId(final int id) {
            return new ViewAction() {
                @Override
                public Matcher<View> getConstraints() {
                    return isDisplayed(); // Ensures that the view is displayed before interacting
                }

                @Override
                public String getDescription() {
                    return "Click on a child view with specified id.";
                }

                @Override
                public void perform(UiController uiController, View view) {
                    View childView = view.findViewById(id);
                    if (childView != null) {
                        childView.performClick();
                    }
                }
            };
        }
    }

    /**
     * Test case for navigating to ongoing events.
     * This test swipes to the ongoing events section, selects the first event, and clicks the "Message to Entrants" button.
     * It then fills in and sends a message to all entrants.
     */
    @Test
    public void testSendMassageToAllEntrants() throws InterruptedException {
        // Swipe to the "Ongoing Events" section
        onView(withId(R.id.view_pager))
                .perform(ViewActions.swipeLeft());
        Thread.sleep(3000);

        // Find the first item in the RecyclerView and perform a click
        onView(allOf(withId(R.id.ongoing_events_recycler_view), isDisplayed()))
                .check(matches(isDisplayed()))
                .perform(click());
        Thread.sleep(2000);

        // Click on the "Message to Entrants" button
        onView(withId(R.id.button_ed_msgEntrants)) // Replace with actual ID of the button
                .perform(click());

        Thread.sleep(1000); // Wait briefly to ensure the action completes

        // Generate random subject and message content
        String randomSubject = "Subject: " + UUID.randomUUID().toString();
        String randomMessage = "Message: " + UUID.randomUUID().toString();

        // Enter the subject line
        onView(withId(R.id.editText_nc_subjectLine)) // ID of the subject line input field
                .perform(ViewActions.typeText(randomSubject), ViewActions.closeSoftKeyboard());

        // Enter the message content
        onView(withId(R.id.editText_nc_message)) // ID of the message input field
                .perform(ViewActions.typeText(randomMessage), ViewActions.closeSoftKeyboard());

        // Click on the "Send to All Entrants" button
        onView(withId(R.id.button_nc_notifAll)) // ID for the "Send to All Entrants" button
                .perform(click());

        System.out.println("Message created and sent to all entrants successfully.");
    }

    /**
     * Test case for navigating to ongoing events.
     * This test swipes to the ongoing events section, selects the first event, and clicks the "Message to Entrants" button.
     * It then fills in and sends a message to Waitlisted entrants.
     */
    @Test
    public void testSendMassageToWaitlistEntrants() throws InterruptedException {
        // Swipe to the "Ongoing Events" section
        onView(withId(R.id.view_pager))
                .perform(ViewActions.swipeLeft());
        Thread.sleep(3000);

        // Find the first item in the RecyclerView and perform a click
        onView(allOf(withId(R.id.ongoing_events_recycler_view), isDisplayed()))
                .check(matches(isDisplayed()))
                .perform(click());
        Thread.sleep(2000);

        // Click on the "Message to Entrants" button
        onView(withId(R.id.button_ed_msgEntrants)) // Replace with actual ID of the button
                .perform(click());

        Thread.sleep(1000); // Wait briefly to ensure the action completes

        // Generate random subject and message content
        String randomSubject = "Subject: " + UUID.randomUUID().toString();
        String randomMessage = "Message: " + UUID.randomUUID().toString();

        // Enter the subject line
        onView(withId(R.id.editText_nc_subjectLine)) // ID of the subject line input field
                .perform(ViewActions.typeText(randomSubject), ViewActions.closeSoftKeyboard());

        // Enter the message content
        onView(withId(R.id.editText_nc_message)) // ID of the message input field
                .perform(ViewActions.typeText(randomMessage), ViewActions.closeSoftKeyboard());

        // Click on the "Send to All Entrants" button
        onView(withId(R.id.button_nc_notifWaitlist)) // ID for the "Send to All Entrants" button
                .perform(click());

        System.out.println("Message created and sent to waitlisted entrants successfully.");

    }

    /**
     * Test case for navigating to ongoing events.
     * This test swipes to the ongoing events section, selects the first event, and clicks the "Message to Entrants" button.
     * It then fills in and sends a message to Confirmed entrants.
     */
    @Test
    public void testSendMassageToConfirmedEntrants() throws InterruptedException {
        // Swipe to the "Ongoing Events" section
        onView(withId(R.id.view_pager))
                .perform(ViewActions.swipeLeft());
        Thread.sleep(3000);

        // Find the first item in the RecyclerView and perform a click
        onView(allOf(withId(R.id.ongoing_events_recycler_view), isDisplayed()))
                .check(matches(isDisplayed()))
                .perform(click());
        Thread.sleep(2000);

        // Click on the "Message to Entrants" button
        onView(withId(R.id.button_ed_msgEntrants)) // Replace with actual ID of the button
                .perform(click());

        Thread.sleep(1000); // Wait briefly to ensure the action completes

        // Generate random subject and message content
        String randomSubject = "Subject: " + UUID.randomUUID().toString();
        String randomMessage = "Message: " + UUID.randomUUID().toString();

        // Enter the subject line
        onView(withId(R.id.editText_nc_subjectLine)) // ID of the subject line input field
                .perform(ViewActions.typeText(randomSubject), ViewActions.closeSoftKeyboard());

        // Enter the message content
        onView(withId(R.id.editText_nc_message)) // ID of the message input field
                .perform(ViewActions.typeText(randomMessage), ViewActions.closeSoftKeyboard());

        // Click on the "Send to Confirmed Entrants" button
        onView(withId(R.id.button_nc_notifConfirmed)) // ID for the "Send to All Entrants" button
                .perform(click());

        System.out.println("Message created and sent to all entrants successfully.");

    }
    /**
     * Test case for navigating to ongoing events.
     * This test swipes to the ongoing events section, selects the first event, and clicks the "Message to Entrants" button.
     * It then fills in and sends a message to Waitlisted entrants.
     */
    @Test
    public void testSendMassageToUnconfirmedEntrants() throws InterruptedException {
        // Swipe to the "Ongoing Events" section
        onView(withId(R.id.view_pager))
                .perform(ViewActions.swipeLeft());
        Thread.sleep(3000);

        // Find the first item in the RecyclerView and perform a click
        onView(allOf(withId(R.id.ongoing_events_recycler_view), isDisplayed()))
                .check(matches(isDisplayed()))
                .perform(click());
        Thread.sleep(2000);

        // Click on the "Message to Entrants" button
        onView(withId(R.id.button_ed_msgEntrants)) // Replace with actual ID of the button
                .perform(click());

        Thread.sleep(1000); // Wait briefly to ensure the action completes

        // Generate random subject and message content
        String randomSubject = "Subject: " + UUID.randomUUID().toString();
        String randomMessage = "Message: " + UUID.randomUUID().toString();

        // Enter the subject line
        onView(withId(R.id.editText_nc_subjectLine)) // ID of the subject line input field
                .perform(ViewActions.typeText(randomSubject), ViewActions.closeSoftKeyboard());

        // Enter the message content
        onView(withId(R.id.editText_nc_message)) // ID of the message input field
                .perform(ViewActions.typeText(randomMessage), ViewActions.closeSoftKeyboard());

        // Click on the "Send to Unconfirmed Entrants" button
        onView(withId(R.id.button_nc_notifUnconfirmed)) // ID for the "Send to All Entrants" button
                .perform(click());

        System.out.println("Message created and sent to all entrants successfully.");

    }
    /**
     * Test case for navigating to the past events section and waiting for 3 seconds.
     * This test swipes to the past events section to verify navigation functionality.
     */
    @Test
    public void testNavigateToPastgingEventsAndWait() throws InterruptedException {
        // Navigate to past events
        onView(withId(R.id.view_pager))
                .perform(swipeRight()); // Swipe to past events section

        // Wait for 3 seconds to simulate load or view time
        Thread.sleep(3000);
    }

    @Test
    public void testSampleEntrants() throws InterruptedException {
        // Navigate to the "Ongoing Events" section
        onView(withId(R.id.view_pager))
                .perform(swipeLeft());
        Thread.sleep(2000);

        // Select the first event
        onView(withId(R.id.ongoing_events_recycler_view))
                .perform(actionOnItemAtPosition(0, click()));
        Thread.sleep(2000);

        // Click on "Sample Entrants" button
        onView(withId(R.id.button_sample_entrants))
                .perform(click());
        Thread.sleep(2000);

    }
// set up poster selection

    @Test
    /**
     * Test case for updating the poster for a event
     * This test navagate to an event then select the poster and wait for a time for user to select a poster then update the poster for the event .
     */
    public void testUpdatePoster() throws InterruptedException {
        // Navigate to the "Ongoing Events" section
        onView(withId(R.id.view_pager))
                .perform(swipeLeft());
        Thread.sleep(2000);

        // Select the first event from the list
        onView(withId(R.id.ongoing_events_recycler_view))
                .perform(actionOnItemAtPosition(0, click()));
        Thread.sleep(2000);

        // Click on the "Select New Poster" button
        onView(withId(R.id.buttonNewPosterEventDetail))
                .perform(click());

        // Wait for 4 sec  to allow the user to select a media file
        Thread.sleep(4000);
        System.out.println("Poster updated successfully.");



    }


    /**
     * Test case to verify that the organizer can navigate to the "Waitlisted" tab
     * and view the list of waitlisted entrants for a specific event.
     *
     * <p>This test performs the following steps:</p>

     *
     * @throws InterruptedException if the thread sleep is interrupted during test execution.
     */
    @Test
    public void testNavigateToWaitlistedEntrants() throws InterruptedException {
        // Step 1: Navigate to the "Ongoing Events" section
        onView(withId(R.id.view_pager))
                .perform(swipeLeft());
        Thread.sleep(2000); // Allow the UI to settle

        // Step 2: Select the first event from the list
        onView(withId(R.id.ongoing_events_recycler_view))
                .perform(actionOnItemAtPosition(0, click()));
        Thread.sleep(2000); // Wait for event details screen to load

        // Step 3: Navigate to the "Participants" tab
        onView(withText("Participants")) // Tab text in the UI
                .perform(click());
        Thread.sleep(2000); // Allow the Participants tab to load

        // Step 4: Navigate to the "Waitlisted" tab
        onView(withText("Waitlisted")) // Tab text in the Participants screen
                .perform(click());
        Thread.sleep(2000);

        // Step 5: Verify that the waitlisted entrants are displayed
        onView(withId(R.id.waitlisted_recycler_view)) // RecyclerView displaying waitlisted entrants
                .check(matches(isDisplayed()));

        // Step 6: Select the first waitlisted entrant
        onView(withId(R.id.waitlisted_recycler_view))
                .perform(actionOnItemAtPosition(0, click())); // Select the first entrant
        Thread.sleep(2000); // Wait for the details screen to load

        // Step 7: Interact with the "Map" button
        onView(withId(R.id.button_map)) // Replace with the actual ID of the Map button
                .perform(click());
        Thread.sleep(2000); // Simulate viewing the map

        // Step 8: Navigate back to the waitlisted entrants screen
        onView(withId(R.id.back_button)) // Replace with the actual ID of the Back button
                .perform(click());
        Thread.sleep(2000); // Wait for the navigation to complete

        // Step 9: Select the "Cancel Entrant" button
        onView(withId(R.id.button_cancel_selected_entrant)) // Replace with the actual ID of the Cancel Entrant button
                .perform(click());

        // Step 10: Verify that the entrant is removed from the waitlisted list
        onView(withId(R.id.waitlisted_recycler_view))
                .check(matches(not(hasDescendant(withText("Sina")))));
    }
    @Test
    /**
     * Test case to verify that the organizer can navigate to the "Selected" tab
     * and view the list of selected entrants for a specific event.

     * @throws InterruptedException if the thread sleep is interrupted during test execution.
     */
    public void testNavigateToSelectedEntrants() throws InterruptedException {
        // Step 1: Navigate to the "Ongoing Events" section
        onView(withId(R.id.view_pager))
                .perform(swipeLeft());
        Thread.sleep(2000); // Allow the UI to settle

        // Step 2: Select the first event from the list
        onView(withId(R.id.ongoing_events_recycler_view))
                .perform(actionOnItemAtPosition(0, click()));
        Thread.sleep(2000); // Wait for event details screen to load

        // Step 3: Navigate to the "Participants" tab
        onView(withText("Participants"))
                .perform(click());
        Thread.sleep(2000);


        // Step 4: Navigate to the "Waitlisted" tab
        onView(withText("selected")) // Tab text in the Participants screen
                .perform(click());
        Thread.sleep(2000);

        // Step 5: Verify that the selected entrants are displayed
        onView(withId(R.id.selected_recycler_view)) // Replace with the correct RecyclerView ID
                .check(matches(isDisplayed()));

        // Step 6: Select a specific entrant from the "Selected" list
        onView(withId(R.id.selected_recycler_view))
                .perform(actionOnItemAtPosition(0, click()));
        Thread.sleep(2000);

        // Step 7: Interact with the "Map" button in the entrant details screen
        onView(withId(R.id.button_map))
                .perform(click());
        Thread.sleep(2000);

        // Step 8: Navigate back
        onView(withId(R.id.back_button))
                .perform(click());
        Thread.sleep(2000);
    }
    /**
     * Test case to verify that the organizer can navigate to the "Cancelled" tab
     * and view the list of cancelled entrants for a specific event.
     *

     *
     * @throws InterruptedException if the thread sleep is interrupted during test execution.
     */
    @Test

    public void testNavigateToCancelledEntrants() throws InterruptedException {
        // Step 1: Navigate to the "Ongoing Events" section
        onView(withId(R.id.view_pager))
                .perform(swipeLeft());
        Thread.sleep(2000); // Allow the UI to settle

        // Step 2: Select the first event from the list
        onView(withId(R.id.ongoing_events_recycler_view))
                .perform(actionOnItemAtPosition(0, click()));
        Thread.sleep(2000); // Wait for event details screen to load

        // Step 3: Navigate to the "Participants" tab
        onView(withText("Participants"))
                .perform(click());
        Thread.sleep(2000);

        // Step 4: Navigate to the "Cancelled" tab

        // Step 4: Navigate to the "canceled" tab
        onView(withText("Cancelled")) // Tab text in the Participants screen
                .perform(click());
        Thread.sleep(2000);



        // Step 5: Select a specific entrant from the "Cancelled" list
        onView(withId(R.id.cancelled_recycler_view))
                .perform(actionOnItemAtPosition(0, click()));
        Thread.sleep(1000);

        // Step 7: Interact with the "Map" button in the entrant details screen
        onView(withId(R.id.button_map))
                .perform(click());
        Thread.sleep(1000);

        // Step 8: Navigate back
        onView(withId(R.id.back_button))
                .perform(click());
        Thread.sleep(1000);
    }









}
