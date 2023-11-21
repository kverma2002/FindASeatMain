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

public class InvalidRegisterTest {

    @Rule
    public ActivityScenarioRule<MainActivity> mActivityScenarioRule =
            new ActivityScenarioRule<>(MainActivity.class);

    //We miss some fields when registering for new account. The registration should fail and we stay on this page.
    @Test
    public void RegisterTest(){
        onView(ViewMatchers.withId(R.id.profile))
                .perform(click());
        onView(ViewMatchers.withId(R.id.createNew))
                .perform(click());

        onView(withId(R.id.firstName)).perform(typeText("Register"));
        onView(withId(R.id.lastName)).perform(typeText("Tester"),closeSoftKeyboard());
        onView(withId(R.id.uscid)).perform(typeText("1234567890"),closeSoftKeyboard());
        onView(withId(R.id.affiliation)).perform(typeText("student"),closeSoftKeyboard());
        onView(ViewMatchers.withId(R.id.createAccount))
                .perform(click());

        try {
            Thread.sleep(4000); // Sleep for 4 second
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //Check the UI on the register page.
        onView(withId(R.id.firstName)).check(matches(isDisplayed()));
        onView(withId(R.id.lastName)).check(matches(isDisplayed()));
        onView(withId(R.id.uscid)).check(matches(isDisplayed()));

    }

}
