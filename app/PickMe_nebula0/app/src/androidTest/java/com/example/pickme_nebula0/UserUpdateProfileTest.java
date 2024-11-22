package com.example.pickme_nebula0;

import static android.app.PendingIntent.getActivity;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.*;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.matcher.ViewMatchers.*;

import static com.google.common.base.CharMatcher.is;

import static java.util.function.Predicate.not;

import com.example.pickme_nebula0.user.activities.UserInfoActivity;

import java.util.UUID;

@RunWith(AndroidJUnit4.class)
public class UserUpdateProfileTest {

    @Rule
    public ActivityScenarioRule<UserInfoActivity> activityRule =
            new ActivityScenarioRule<>(UserInfoActivity.class);

    @Test
    public void testUserUpdateProfile()  throws InterruptedException {
        // Check that initial screen elements are displayed
        onView(withId(R.id.editTextUserInfoName)).check(matches(isDisplayed()));
        onView(withId(R.id.editTextUserInfoEmail)).check(matches(isDisplayed()));
        onView(withId(R.id.editTextUserInfoPhone)).check(matches(isDisplayed()));
        onView(withId(R.id.checkBoxUserInfoNotifEnabled)).check(matches(isDisplayed()));
        onView(withId(R.id.buttonUserInfoConfirm)).check(matches(isDisplayed()));
        onView(withId(R.id.buttonUserInfoCancel)).check(matches(isDisplayed()));


        // Enter user information
        onView(withId(R.id.editTextUserInfoName)).perform(typeText("Test User"), closeSoftKeyboard());
        Thread.sleep(1000);

        onView(withId(R.id.editTextUserInfoEmail)).perform(typeText("test.user@example.com"), closeSoftKeyboard());
        Thread.sleep(1000);

        onView(withId(R.id.editTextUserInfoPhone)).perform(typeText("1234567890"), closeSoftKeyboard());

        // Toggle the checkbox
        onView(withId(R.id.checkBoxUserInfoNotifEnabled)).perform(click());
        Thread.sleep(1000);

        // Confirm the updates
        onView(withId(R.id.buttonUserInfoConfirm)).perform(click());
        Thread.sleep(1000);


    }
    @Test
    public void testCreateFacilityWithRandomFields() {
        // Navigate to "Create Facility" screen (assuming a button or navigation method exists)
        onView(withId(R.id.buttonUserInfoManageFacility)).perform(click());

        // Generate random name and address
        String randomName = "Facility_" + UUID.randomUUID().toString().substring(0, 8);
        String randomAddress = "Address_" + UUID.randomUUID().toString().substring(0, 8);

        // Input random name
        onView(withId(R.id.editText_fi_name))
                .perform(typeText(randomName), closeSoftKeyboard());

        // Input random address
        onView(withId(R.id.editText_fi_address))
                .perform(typeText(randomAddress), closeSoftKeyboard());

        // Click the "Create" button
        onView(withId(R.id.button_fi_confirm)).perform(click());




    }
}