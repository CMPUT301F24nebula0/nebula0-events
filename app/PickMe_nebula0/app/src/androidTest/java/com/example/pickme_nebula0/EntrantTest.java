package com.example.pickme_nebula0;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.swipeLeft;
import static androidx.test.espresso.action.ViewActions.swipeRight;
import static androidx.test.espresso.assertion.ViewAssertions.matches;

import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.junit.Assert.assertTrue;
import static java.lang.Thread.sleep;

import com.example.pickme_nebula0.entrant.activities.EntrantHomeActivity;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;

import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.pickme_nebula0.entrant.activities.EntrantHomeActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;


@RunWith(AndroidJUnit4.class)
public class EntrantTest {

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

        Thread.sleep(2000);
    }

@Test
public void testWaitlistPlacement() throws InterruptedException {
    // 1. Assume the user is already in the waitlist section after scanning the QR code.

    // 2. Wait for UI to load
    Thread.sleep(2000);



    // 4. Verify with Firestore Database if the event is indeed in the waitlist with the correct user ID
    String eventId = "123"; // This would typically be fetched from the UI dynamically
    String userId = "123";  // Replace with the actual user ID used in your app

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CountDownLatch latch = new CountDownLatch(1);
    final boolean[] isUserInWaitlist = {false};

    db.collection("events").document(eventId).get()
            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot.exists()) {
                        // Assuming waitlist is stored as an array of user IDs
                        if (documentSnapshot.contains("waitlist")) {
                            java.util.List<String> waitlist = (java.util.List<String>) documentSnapshot.get("waitlist");
                            if (waitlist != null && waitlist.contains(userId)) {
                                isUserInWaitlist[0] = true;
                            }
                        }
                    }
                    latch.countDown();  // Signal that the task is complete
                }
            });

    // Wait for the Firestore task to complete
    latch.await(10, TimeUnit.SECONDS);

    // Assert that the user is in the waitlist for this event in the Firestore database
    assertTrue("The user should be in the waitlist for the event in Firestore", isUserInWaitlist[0]);
}
}
