package com.example.pickme_nebula0;

import androidx.test.espresso.Espresso;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.action.ViewActions.*;
import static androidx.test.espresso.assertion.ViewAssertions.*;
import static androidx.test.espresso.matcher.ViewMatchers.*;

import com.example.pickme_nebula0.admin.activities.AdminHomeActivity;
import com.example.pickme_nebula0.user.activities.UserInfoActivity;

@RunWith(AndroidJUnit4.class)
public class UserUpdateProfileTest {

    @Rule
    public ActivityScenarioRule<UserInfoActivity> activityRule =
            new ActivityScenarioRule<>(UserInfoActivity.class);

    @Test
    public void updateProfileTest () {
        // Update name
        Espresso.onView(withId(R.id.editTextUserInfoName))
                .perform(clearText(), typeText("ssss"));

        // Update email
        Espresso.onView(withId(R.id.editTextUserInfoEmail))
                .perform(clearText(), typeText("sss@example.com"));

        // Remove Picture
        Espresso.onView(withText("Remove Picture"))
                .perform(click());

        // Press Confirm
        Espresso.onView(withText("Confirm"))
                .perform(click());

    }

}
