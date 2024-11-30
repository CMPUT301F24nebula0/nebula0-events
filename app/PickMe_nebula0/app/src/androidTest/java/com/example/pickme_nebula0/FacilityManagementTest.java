package com.example.pickme_nebula0;

import androidx.test.espresso.Espresso;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.action.ViewActions.*;
import static androidx.test.espresso.assertion.ViewAssertions.*;
import static androidx.test.espresso.matcher.ViewMatchers.*;

import com.example.pickme_nebula0.user.activities.UserInfoActivity;

@RunWith(AndroidJUnit4.class)
public class FacilityManagementTest {

    @Rule
    public ActivityTestRule<UserInfoActivity> activityRule =
            new ActivityTestRule<>(UserInfoActivity.class);

    @Test
    public void createFacilityTest () {
        // Navigate to Create or Manage Facility
        Espresso.onView(withText("Create or Manage a Facility"))
                .perform(click());

        // Enter Facility Name
        Espresso.onView(withId(R.id.editText_fi_name))
                .perform(typeText("New Facility"));

        // Enter Facility Address
        Espresso.onView(withId(R.id.editText_fi_address))
                .perform(typeText("123 Facility Street"));

        // Confirm creation
        Espresso.onView(withText("Confirm"))
                .perform(click());

    }
}
