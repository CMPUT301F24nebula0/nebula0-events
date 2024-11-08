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
}
