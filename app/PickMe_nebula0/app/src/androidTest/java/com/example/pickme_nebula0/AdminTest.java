package com.example.pickme_nebula0;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.matcher.ViewMatchers;

import com.example.pickme_nebula0.admin.activities.AdminHomeActivity;
import com.example.pickme_nebula0.admin.activities.EventDetailAdminActivity;


import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class AdminTest {

    @Rule
    public ActivityScenarioRule<AdminHomeActivity> activityRule =
            new ActivityScenarioRule<>(AdminHomeActivity.class);


/*
@Author : Sina Shaban

Note : In Order to run this test first test the organizer create test to make sure
that the event is created then run this test

 */
@Test
public void testAdminNavigationFlow() throws InterruptedException {
    // Click on "Manage Events" button
    Espresso.onView(ViewMatchers.withText("Manage Events"))
            .perform(ViewActions.click());
    Thread.sleep(2000); // Wait for 2 seconds

    // Select the first event in the list
    Espresso.onData(Matchers.anything())
            .inAdapterView(ViewMatchers.withId(R.id.eventListView))
            .atPosition(0)
            .perform(ViewActions.click());
    Thread.sleep(2000); // Wait for 2 seconds

    // Navigate back to the admin home page
    Espresso.pressBack();
    Thread.sleep(2000); // Wait for 2 seconds

    // Click on "Manage Profile" button
    Espresso.onView(ViewMatchers.withText("Manage Profile"))
            .perform(ViewActions.click());
    Thread.sleep(2000); // Wait for 2 seconds

    // Select the first profile in the list
    Espresso.onData(Matchers.anything())
            .inAdapterView(ViewMatchers.withId(R.id.ProfileListView))
            .atPosition(0)
            .perform(ViewActions.click());
    Thread.sleep(2000); // Wait for 2 seconds

    // Navigate back to the admin home page
    Espresso.pressBack();
    Thread.sleep(2000); // Wait for 2 seconds

    // Click on "Manage Facilities" button
    Espresso.onView(ViewMatchers.withText("Manage Facilities"))
            .perform(ViewActions.click());
    Thread.sleep(2000); // Wait for 2 seconds

    // Optionally, perform additional actions within the "Manage Facilities" section

    // Navigate back to the admin home page
    Espresso.onView(ViewMatchers.withText("Back"))
            .perform(ViewActions.click());
    Thread.sleep(2000); // Wait for 2 seconds
}
    @Test
    public void testAdminEventFlow() throws InterruptedException {
        // Click on "Manage Events" button
        Espresso.onView(ViewMatchers.withText("Manage Events"))
                .perform(ViewActions.click());

        Thread.sleep(1000);

        // Select an event (e.g., "Field hockey")
        // Use onData to select an event (e.g., "Field hockey") from the ListView
        Espresso.onData(Matchers.anything())
                .inAdapterView(ViewMatchers.withId(R.id.eventListView))
                .atPosition(0) // Assuming "Field hockey" is at position 0
                .perform(ViewActions.click());
        Thread.sleep(2000);

        // Check if the Event Detail page is displayed
        Espresso.onView(ViewMatchers.withText("Event Info"))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
        Thread.sleep(2000);

        // Perform delete operation on event
        Espresso.onView(ViewMatchers.withText("Delete Event"))
                .perform(ViewActions.click());
        Thread.sleep(3000);

    }
    @Test
    public void testManageUserFlow() throws InterruptedException {
        // Click on "Manage Profile" button
        Espresso.onView(ViewMatchers.withText("Manage Profile"))
                .perform(ViewActions.click());
        Thread.sleep(1000);

        // Use onData to select a user profile (e.g., "Computer") from the ListView
        Espresso.onData(Matchers.anything())
                .inAdapterView(ViewMatchers.withId(R.id.ProfileListView))
                .atPosition(0) // Assuming "Computer" is at position 0
                .perform(ViewActions.click());
        Thread.sleep(2000);

        // Perform delete operation on user
        Espresso.onView(ViewMatchers.withText("Delete User"))
                .perform(ViewActions.click());
        Thread.sleep(3000);


        // Navigate back to admin home page
        Espresso.onView(ViewMatchers.withText("Back"))
                .perform(ViewActions.click());
        Thread.sleep(1000);
    }

    @Test
    public void testManageFacilitiesFlow() throws InterruptedException {
        // Click on "Manage Facilities" button
        Espresso.onView(ViewMatchers.withText("Manage Facilities"))
                .perform(ViewActions.click());
        Thread.sleep(2000); // Wait for 2 seconds

        // Select the first facility in the list
        Espresso.onData(Matchers.anything())
                .inAdapterView(ViewMatchers.withId(R.id.facilitiesListView))
                .atPosition(0)
                .perform(ViewActions.click());
        Thread.sleep(2000); // Wait for 2 seconds

        // Perform delete operation on facility
        Espresso.onView(ViewMatchers.withText("Delete Facility"))
                .perform(ViewActions.click());
        Thread.sleep(3000); // Wait for 3 seconds

        // Navigate back to admin home page
        Espresso.onView(ViewMatchers.withText("Back"))
                .perform(ViewActions.click());
        Thread.sleep(1000); // Wait for 1 second


    }
    /**
     * Test case to verify that an admin can delete an event hash
     * from the event details page. The test navigates to the "Manage Events" section,
     * selects an event, and performs the delete hash operation.
     */
    @Test
    public void testDeleteEventHash() throws InterruptedException {
        // Step 1: Navigate to the "Manage Events" section
        Espresso.onView(ViewMatchers.withId(R.id.manage_qr_code))
                .perform(ViewActions.click());
        Thread.sleep(2000); // Allow UI to load

        // Step 2: Select the first event in the list
        Espresso.onData(Matchers.anything())
                .inAdapterView(ViewMatchers.withId(R.id.QRcodeListView)) // Replace with the correct ID
                .atPosition(0)
                .perform(ViewActions.click());
        Thread.sleep(2000); // Wait for the event details page to load

        // Step 3: Click the "Delete Hash" button
        Espresso.onView(ViewMatchers.withText("Delete Event"))
                .perform(ViewActions.click());
        Thread.sleep(2000); // Simulate the deletion process

    }
    /**
     * Test case to verify that an admin can delete an event image
     * from the event details page. The test navigates to the "Manage Events" section,
     * selects an event, and performs the delete image operation.
     */
    @Test
    public void testDeleteEventImage() throws InterruptedException {
        // Step 1: Navigate to the "Manage image " section
        Espresso.onView(ViewMatchers.withId(R.id.manage_image))
                .perform(ViewActions.click());
        Thread.sleep(2000); // Allow UI to load

        // Step 2: Select the first image in the list
        Espresso.onData(Matchers.anything())
                .inAdapterView(ViewMatchers.withId(R.id.ImageListView))
                .atPosition(0)
                .perform(ViewActions.click());
        Thread.sleep(2000); // Wait for the event details page to load

        // Step 3: Click the "Delete Image" button
        Espresso.onView(ViewMatchers.withText("Delete Image")) // Replace with the exact button text
                .perform(ViewActions.click());
        Thread.sleep(3000); // Simulate the deletion process

        // Step 4: Verify the image was successfully deleted
        Espresso.onView(ViewMatchers.withText("Image Deleted Successfully")) // Replace with actual success message
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }



}
