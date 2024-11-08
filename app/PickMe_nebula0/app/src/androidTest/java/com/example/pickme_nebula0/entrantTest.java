package com.example.pickme_nebula0;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.swipeLeft;
import static androidx.test.espresso.action.ViewActions.swipeRight;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.Intents.intending;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static java.lang.Thread.sleep;

import com.example.pickme_nebula0.entrant.activities.EntrantHomeActivity;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.intent.matcher.IntentMatchers;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.pickme_nebula0.entrant.activities.EntrantHomeActivity;

import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(AndroidJUnit4.class)
public class entrantTest {

    @Rule
    public ActivityScenarioRule<EntrantHomeActivity> activityScenarioRule =
            new ActivityScenarioRule<>(EntrantHomeActivity.class);


/*
UI Navigation Tests
Navigate Between Tabs: Test navigation between the "Waitlist," "Selected," and "Joined Events" tabs to ensure that UI components update correctly.
Back Button Behavior: Verify the behavior of the back button in all screens to make sure the navigation flow is intuitive.
Tab State Retention: Check if the selected tab and event data are retained during activity recreation, such as after screen rotation.


 */
@Test
public void testNavigateBetweenTabs () throws InterruptedException {
    // Navigate to "Waitlist" tab and verify it's displayed
    onView(withId(R.id.view_pager))
            .perform(swipeLeft()); // Swipe to ongoing events section
    Thread.sleep(2000);

    onView(withId(R.id.view_pager))
            .perform(swipeLeft()); // Swipe to ongoing events section
    // Navigate to "Selected" tab and verify it's displayed
  // create a threed and wait for 1 s
    Thread.sleep(2000);

    onView(withId(R.id.view_pager))
            .perform(swipeLeft()); // Swipe to ongoing events section
    Thread.sleep(2000);

    // navagete to main page  by clicking back button
    onView(withId(R.id.backButton))
            .perform(click());



}
// a user can remove them self from wait list
    @Test
    public void testWaitlistPlacement() throws InterruptedException{
        // Click on "Scan QR Code" button to join an event
        // onView(withId(R.id.ScanQRButton)).perform(click());
        // Assume Already joined the event using QR Code Now check the waitlist


        // Assume scanning has placed the user on the waitlist due to no immediate slot
        // Navigate to the "Waitlist" tab as the defult is waitlist we dont need to swape to particular pager


        // Check if the specific event is now listed under the first listview in the waitlist

        // Swipe to ongoing events section
        Thread.sleep(2000);
        // Check if the first element in the ListView matches "Event 1"
        onData(Matchers.anything())
                .inAdapterView(withId(R.id.entrant_waitlisted_events_listview))  // Replace with your ListView ID
                .atPosition(0)
                .check(matches(withText("Event 1")));
    }

    @Test
    public void joinEventAndCheckWaitlist() {
        // 1. Click on the "Scan QR Code" button to initiate the camera scanning
        onView(withId(R.id.ScanQRButton)).perform(click());
        // Assume Already joined the event using QR Code Now check the waitlist

        // 2. Simulate QR code scanning using Intents
        // Mocking the QR scanning intent response
        Intent resultData = new Intent();
        resultData.putExtra("scanned_event_id", "Event 1"); // Assume the QR code corresponds to "Event 1"
        Instrumentation.ActivityResult result = new Instrumentation.ActivityResult(Activity.RESULT_OK, resultData);

        // Set up an Intent response that matches the QR scanning activity
        intending(IntentMatchers.anyIntent()).respondWith(result);

        // Verify if the correct intent was sent out for QR scanning
        intended(IntentMatchers.hasAction("android.media.action.IMAGE_CAPTURE"));

        // 3. Assume after scanning, the user is added to the waitlist
        // Navigate to the "Waitlist" tab to verify the joined event
        onView(withText("Waitlist")).perform(swipeLeft());

        Intents.release();
    }




}