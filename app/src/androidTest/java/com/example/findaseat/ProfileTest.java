package com.example.findaseat;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import org.junit.Rule;
import org.junit.Test;

public class ProfileTest {

    @Rule
    public ActivityScenarioRule<MainActivity> mActivityScenarioRule =
            new ActivityScenarioRule<>(MainActivity.class);



    @Test
    //check if entering the correct email & password leads user to the user profile page
    public void fromLoginToProfile() {
        onView(ViewMatchers.withId(R.id.profile))
                .perform(click());
        onView(withId(R.id.email)).perform(typeText("lihongyi@usc.edu"));
        onView(withId(R.id.password)).perform(typeText("123456"),closeSoftKeyboard());
        onView(withId(R.id.login)).perform(click());
        try {
            Thread.sleep(4000); // Sleep for 4 second
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //UI on the profile page is displayed, and the logged in student's info is shown
        onView(withId(R.id.name)).check(matches(withText("Hongyi Li")));
        onView(withId(R.id.em)).check(matches(isDisplayed()));
        onView(withId(R.id.uscid)).check(matches(withText("ID:1555971729")));
        onView(withId(R.id.affiliation)).check(matches(withText("undergrad")));
        onView(withId(R.id.reserveHistory)).check(matches(isDisplayed()));


    }
}