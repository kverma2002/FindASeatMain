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

public class RegisterTest {

    @Rule
    public ActivityScenarioRule<MainActivity> mActivityScenarioRule =
            new ActivityScenarioRule<>(MainActivity.class);

    //If want to run multiple times, need to change the firstName, lastName, email used for register,
    //and the following fields below,like Register2, Tester2, testregister2@usc.edu
    @Test
    public void RegisterTest(){
        onView(ViewMatchers.withId(R.id.profile))
                .perform(click());
        onView(ViewMatchers.withId(R.id.createNew))
                .perform(click());
        onView(withId(R.id.firstName)).perform(typeText("Register"));
        onView(withId(R.id.lastName)).perform(typeText("Tester"),closeSoftKeyboard());
        onView(withId(R.id.email)).perform(typeText("testregister@usc.edu"),closeSoftKeyboard());
        onView(withId(R.id.uscid)).perform(typeText("1234567890"),closeSoftKeyboard());
        onView(withId(R.id.affiliation)).perform(typeText("student"),closeSoftKeyboard());
        onView(withId(R.id.password)).perform(typeText("123456"),closeSoftKeyboard());
        onView(ViewMatchers.withId(R.id.createAccount))
                .perform(click());

        try {
            Thread.sleep(6000); // Sleep for 6 second
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //Now login with the new account info.
        onView(withId(R.id.email)).perform(typeText("testregister@usc.edu"));
        onView(withId(R.id.password)).perform(typeText("123456"),closeSoftKeyboard());
        onView(withId(R.id.login)).perform(click());

        try {
            Thread.sleep(4000); // Sleep for 4 second
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //The profile page should show the info of the new user
        onView(withId(R.id.name)).check(matches(withText("Register Tester")));
        onView(withId(R.id.uscid)).check(matches(withText("ID:1234567890")));
        onView(withId(R.id.affiliation)).check(matches(withText("student")));
        onView(withId(R.id.reserveHistory)).check(matches(isDisplayed()));
    }
}
