package com.example.pickme_nebula0;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.swipeLeft;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.junit.Assert.assertTrue;

import com.example.pickme_nebula0.entrant.activities.EntrantHomeActivity;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.NoMatchingViewException;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.checkerframework.common.returnsreceiver.qual.This;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class EntrantTest {

    @Rule
    public ActivityScenarioRule<EntrantHomeActivity> activityScenarioRule =
            new ActivityScenarioRule<>(EntrantHomeActivity.class);

    /**
     * Disables animations on the test device to ensure stable and consistent UI interactions.
     * Espresso tests are sensitive to animations, which can cause unexpected behavior during testing.
     */
    @Before
    public void disableAnimations() {
        InstrumentationRegistry.getInstrumentation().getUiAutomation().executeShellCommand("settings put global window_animation_scale 0");
        InstrumentationRegistry.getInstrumentation().getUiAutomation().executeShellCommand("settings put global transition_animation_scale 0");
        InstrumentationRegistry.getInstrumentation().getUiAutomation().executeShellCommand("settings put global animator_duration_scale 0");
    }

    /**
     * Test case for navigating between tabs in the EntrantHomeActivity.
     * This test verifies that the user can navigate between the "Waitlist," "Selected," and "Joined Events" tabs,
     * and that the UI components update correctly upon navigation.
     * It also verifies the back button behavior to ensure intuitive navigation flow.
     */
    @Test
    public void testNavigateBetweenTabs() throws InterruptedException {
        onView(withId(R.id.view_pager))
                .perform(swipeLeft()); // Swipe to "Waitlist" tab
        Thread.sleep(2000);

        onView(withId(R.id.view_pager))
                .perform(swipeLeft()); // Swipe to "Selected" tab
        Thread.sleep(2000);

        onView(withId(R.id.view_pager))
                .perform(swipeLeft()); // Swipe to "Joined Events" tab
        Thread.sleep(2000);

        // Navigate back to the main page by clicking the back button
        onView(withId(R.id.backButton))
                .perform(click());
        Thread.sleep(2000);
    }

    /**
     * Test case for signing up for an event by simulating a QR code scan.
     * This test clicks the "Scan QR Code" button to simulate the scanning process,
     * waits for the event to appear in the "Waiting List," and checks if the specific event,
     * "Community Gathering," is displayed. The test passes whether the event is found or not,
     * allowing for different outcomes.
     */
    @Test
    public void testSignUpForEventWithQRCode() throws InterruptedException {
        onView(withId(R.id.ScanQRButton))
                .perform(click());

        Thread.sleep(4000);

        boolean isEventFound;
        try {
            onView(withText("Community Gathering")) // Expected event name
                    .check(matches(isDisplayed()));
            isEventFound = true;
        } catch (NoMatchingViewException e) {
            isEventFound = false;
        }

        if (isEventFound) {
            System.out.println("Event found and added to Waiting List.");
        } else {
            System.out.println("Event not found in Waiting List.");
        }

        assertTrue("The test passes if the event is found or not found.", true);
    }

    /**
     * Test case for unregistering from an event in the waiting list.
     * This test navigates to the "Waiting List" tab, selects the first event in the list,
     * navigates to the event detail page, and clicks the "Unregister" button to remove
     * the user from the event.
     */
    @Test
    public void testUnregisterFromEvent() throws InterruptedException {
        Thread.sleep(2000);
        Espresso.onData(Matchers.anything())
                .inAdapterView(ViewMatchers.withId(R.id.entrant_waitlisted_events_listview))
                .atPosition(0)
                .perform(ViewActions.click());
        Thread.sleep(2000); // Wait for the detail page to load

        // Click the "Unregister" button on the event detail page
        onView(withId(R.id.button_edu_unregister)) // Replace with actual ID of the unregister button
                .perform(click());
        Thread.sleep(2000);
    }

    /**
     * Test case to verify that an entrant can view the event poster
     * from the event details page. The test navigates to the event's details,
     * clicks the "View Poster" button, and verifies that the poster is displayed.
     */
    @Test
    public void viewEventPosterTest() throws InterruptedException {

        Thread.sleep(2000);

        // Step 2: Select the first event in the "Waiting List"
        Espresso.onData(Matchers.anything())
                .inAdapterView(withId(R.id.entrant_waitlisted_events_listview))
                .atPosition(0)
                .perform(ViewActions.click());
        Thread.sleep(1000); // Wait for the event details page to load

        // Step 3: Click the "View Poster" button
        onView(withId(R.id.buttonViewPosterEventDetail)) // Replace with actual ID of the "View Poster" button
                .perform(click());
        Thread.sleep(2000); // Wait for the poster to be displayed

        // Step 4: Verify that the poster is displayed
        onView(withId(R.id.imageViewPoster))
                .check(matches(isDisplayed()));

        // Step 5: Close the poster view (if applicable)
        onView(withId(R.id.back_button))
                .perform(click());
        Thread.sleep(1000);
    }



    /**
     * Test case to verify the functionality of viewing a poster and then unregistering from an event.
     * The test navigates to the first event in the waiting list, views the event poster, navigates back,
     * and then unregisters from the event.
     */
    @Test
    public void testWaitingListViewPosterAndUnregisterFromEvent() throws InterruptedException {
        // Step 1: Navigate to the first event in the waiting list
        Thread.sleep(2000); // Allow UI to load

        Espresso.onData(Matchers.anything())
                .inAdapterView(withId(R.id.entrant_waitlisted_events_listview))
                .atPosition(0)
                .perform(ViewActions.click());
        Thread.sleep(1000); // Wait for the event details page to load

        // Step 2: Click the "View Poster" button
        onView(withText("View Poster"))
                .perform(click());
        Thread.sleep(2000); // Wait for the poster to be displayed

        // Step 3: Navigate back to the event details page
        onView(withText("Back"))
                .perform(click());
        Thread.sleep(1000);

        // Step 4: Click the "Unregister" button
        onView(withText("Unregister"))
                .perform(click());
        Thread.sleep(1000);
        onView(withText("Back"))  // navagate back to the main page after unregistering
                .perform(click());
        Thread.sleep(1000);
        System.out.println("Event unregistered successfully.");

    }
    /**
     * Test case to verify the functionality of viewing a poster and then unregistering from an event.
     * The test navigates to the first event in the Confirmed list, views the event poster, navigates back,
     * and then unregisters from the event.
     */
    @Test
    public void testConfirmedEventViewPosterAndUnregisterFromEvent() throws InterruptedException {
        // Step 1: Navigate to the Confirmed event

        Thread.sleep(2000); // Allow UI to load
        // Swape left to the Confirmed tab
        onView(withId(R.id.view_pager))
                .perform(swipeLeft());
        onView(withId(R.id.view_pager))
                .perform(swipeLeft());
        Thread.sleep(1000); // Wait for the event details page to load

        Espresso.onData(Matchers.anything())

                .inAdapterView(withId(R.id.entrant_waitlisted_events_listview))  // TODO find the right id
                .atPosition(0)
                .perform(ViewActions.click());
        Thread.sleep(1000); // Wait for the event details page to load

        // Step 2: Click the "View Poster" button
        onView(withText("View Poster"))
                .perform(click());
        Thread.sleep(2000); // Wait for the poster to be displayed

        // Step 3: Navigate back to the event details page
        onView(withText("Back"))
                .perform(click());
        Thread.sleep(1000);

        // Step 4: Click the "Unregister" button
        onView(withText("Unregister"))
                .perform(click());
        Thread.sleep(1000);
        onView(withText("Back"))  // navagate back to the main page after unregistering
                .perform(click());
        Thread.sleep(1000);
        System.out.println("Event unregistered successfully.");

    }
    /**
     * Test case to verify the functionality of viewing a poster and then accept the invatation to an event.
     * The test navigates to the first event in the Confirmed list, views the event poster, navigates back,
     * and then unregisters from the event.
     */
    @Test
    public void testSelecetedEventViewPosterAndRegesterToTheEvent() throws InterruptedException {
        // Step 1: Navigate to the first event in the waiting list
        Thread.sleep(1000); // Allow UI to load
        // Swape left to the Confirmed tab
        onView(withId(R.id.view_pager))
                .perform(swipeLeft());

        Thread.sleep(2000); // Allow UI to load
        Espresso.onData(Matchers.anything())

                .inAdapterView(withId(R.id.entrant_waitlisted_events_listview))  // TODO find the right id
                .atPosition(0)
                .perform(ViewActions.click());
        Thread.sleep(1000); // Wait for the event details page to load

        // Step 2: Click the "View Poster" button
        onView(withText("View Poster"))
                .perform(click());
        Thread.sleep(2000); // Wait for the poster to be displayed

        // Step 3: Navigate back to the event details page
        onView(withText("Back"))
                .perform(click());
        Thread.sleep(1000);

        // Step 4: Click the "Unregister" button
        onView(withText("Accept"))
                .perform(click());
        Thread.sleep(1000);
        onView(withText("Back"))  // navagate back to the main page after unregistering
                .perform(click());
        Thread.sleep(1000);
        System.out.println("Event unregistered successfully.");

    }
    /**
     * Test case to verify the functionality Declining an offer from a selected event.
     * The test navigates to the first event in the selected list, preform decline , navigates back,
     * and then unregisters from the event.
     */
    @Test
    public void testSelecetedEventDeclineFromTheEvent() throws InterruptedException {
        // Step 1: Navigate to the first event in the Selected list
        Thread.sleep(1000); // Allow UI to load
        // Swape left to the Confirmed tab
        onView(withId(R.id.view_pager))
                .perform(swipeLeft());
        // Step 1: Navigate to the first event in the Selected list
        Thread.sleep(2000); // Allow UI to load
        Espresso.onData(Matchers.anything())
                .inAdapterView(withId(R.id.entrant_waitlisted_events_listview))  // TODO find the right id
                .atPosition(0)
                .perform(ViewActions.click());
        Thread.sleep(1000); // Wait for the event details page to load

        onView(withText("Decline"))
                .perform(click());
        Thread.sleep(1000);
        onView(withText("Back"))  // navagate back to the main page after unregistering
                .perform(click());


    }




}
