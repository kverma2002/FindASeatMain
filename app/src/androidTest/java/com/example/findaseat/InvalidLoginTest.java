package com.example.findaseat;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import org.junit.Rule;
import org.junit.Test;

public class InvalidLoginTest {

    @Rule
    public ActivityScenarioRule<MainActivity> mActivityScenarioRule =
            new ActivityScenarioRule<>(MainActivity.class);

    @Test
    //check if the bottom menu is correctly displayed after login failed
    public void bottomNavigationViewIsDisplayed() {
        onView(withId(R.id.bottomNavigationView)).check(matches(isDisplayed()));
        onView(withId(R.id.profile)).check(matches(isDisplayed()));
        onView(withId(R.id.map)).check(matches(isDisplayed()));
    }
    @Test
    //check if invalid user email and password will pass the login
    public void InvalidLoginTest(){
        onView(ViewMatchers.withId(R.id.profile))
                .perform(click());
        onView(withId(R.id.email)).perform(typeText("wrongemail"));
        onView(withId(R.id.password)).perform(typeText("wrongpassword"),closeSoftKeyboard());
        onView(withId(R.id.login)).perform(click());
        try {
            Thread.sleep(3000); // Sleep for 3 second
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //Fragment UI doesn't change since the login fails and we should still stay on the login fragment
        onView(withId(R.id.email)).check(matches(isDisplayed()));
        onView(withId(R.id.password)).check(matches(isDisplayed()));
        onView(withId(R.id.login)).check(matches(isDisplayed()));
        onView(withId(R.id.createNew)).check(matches(isDisplayed()));
    }
}
