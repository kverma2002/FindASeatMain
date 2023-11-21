package com.example.findaseat;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject;
import androidx.test.uiautomator.UiObjectNotFoundException;
import androidx.test.uiautomator.UiSelector;

import org.junit.Rule;
import org.junit.Test;

public class bottomNavigationBarTest {

    @Rule
    public ActivityScenarioRule<MainActivity> mActivityScenarioRule =
            new ActivityScenarioRule<>(MainActivity.class);

    @Test
    public void onLoginPage(){
        onView(ViewMatchers.withId(R.id.profile))
                .perform(click());
        bottomNavigationViewIsDisplayed();
    }
    @Test
    public void onRegister(){
        onView(ViewMatchers.withId(R.id.profile))
                .perform(click());
        onView(ViewMatchers.withId(R.id.createNew))
                .perform(click());
        bottomNavigationViewIsDisplayed();
    }
    //Default page is map page
    @Test
    public void onMap(){
        bottomNavigationViewIsDisplayed();
    }
    @Test
    public void onBuilding() throws UiObjectNotFoundException {
        UiDevice device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        UiObject marker = device.findObject(new UiSelector().descriptionContains("Doheny"));
        marker.click();
        try {
            Thread.sleep(4000); // Wait for 4 seconds
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        bottomNavigationViewIsDisplayed();
    }
    public void bottomNavigationViewIsDisplayed() {
        onView(withId(R.id.bottomNavigationView)).check(matches(isDisplayed()));
        onView(withId(R.id.profile)).check(matches(isDisplayed()));
        onView(withId(R.id.map)).check(matches(isDisplayed()));
    }

}
