package com.example.findaseat;


import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.Matchers.not;


import android.os.Bundle;
import android.view.View;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.recyclerview.widget.RecyclerView;
import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.espresso.matcher.BoundedMatcher;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject;
import androidx.test.uiautomator.UiObjectNotFoundException;
import androidx.test.uiautomator.UiSelector;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(AndroidJUnit4.class)
public class BuildingPageNotLoggedInTest {


    FragmentScenario<BuildingFragment> scenario;
    @Rule
    public ActivityScenarioRule<MainActivity> activityRule = new ActivityScenarioRule<>(MainActivity.class);
    private View decorView;


    //Click on the building marker without logging in
    @Before
    public void setUp() throws UiObjectNotFoundException {
        UiDevice device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        UiObject marker = device.findObject(new UiSelector().descriptionContains("Doheny"));
        marker.click();
        try {
            Thread.sleep(4000); // Wait for 4 seconds
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    //Without logging in, the user should still be able to see the info of the building.
    //Check whether UI on the building fragment displayed properly
    @Test
    public void testBuildingInfo() throws UiObjectNotFoundException {

        onView(withId(R.id.buildingPicture)).check(matches(isDisplayed()));
        onView(withId(R.id.buildingName)).check(matches(withText("Dohney Library")));
        onView(withId(R.id.textAddress)).check(matches(withText("3550 Trousdale Pkwy, Los Angeles, CA 90089")));
        onView(withId(R.id.hours)).check(matches(withText("08:00 AM - 10:00 PM")));
        onView(withId(R.id.textDescription)).check(matches(withText("The Edward L. Doheny Jr. Memorial Library is a library located in the center of campus at the University of Southern California (USC).")));
    }

    //check the seats are loaded correctly into the recycler view.
    @Test
    public void testRoomInRecyclerView() {

        //There are 7 seats in Doheny library. Pick 2 example to show that information are updated correctly,
        //And the list is scrollable.
        onView(withId(R.id.seatRecyclerView))
                .perform(RecyclerViewActions.scrollToPosition(0));

        onView(withId(R.id.seatRecyclerView))
                .check(matches(atPosition(0, hasDescendant(withText("Grand Study Hall")))));

        onView(withId(R.id.seatRecyclerView))
                .perform(RecyclerViewActions.scrollToPosition(5));

        onView(withId(R.id.seatRecyclerView))
                .check(matches(atPosition(5, hasDescendant(withText("Doheny Courtyard")))));


    }
    public static Matcher<View> atPosition(final int position, final Matcher<View> itemMatcher) {
        return new BoundedMatcher<View, RecyclerView>(RecyclerView.class) {
            @Override
            public void describeTo(Description description) {
                description.appendText("has item at position " + position + ": ");
                itemMatcher.describeTo(description);
            }
            @Override
            protected boolean matchesSafely(final RecyclerView view) {
                RecyclerView.ViewHolder viewHolder = view.findViewHolderForAdapterPosition(position);
                if (viewHolder == null) {
                    return false;
                }
                return itemMatcher.matches(viewHolder.itemView);
            }
        };
    }
    //Not logged in, clicking on the seat card should not continue
    @Test
    public void clickSeatNotLoggedIn() {

        try {
            Thread.sleep(4000); // Sleep for 5 second
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        onView(withId(R.id.seatRecyclerView))
                .perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));

        onView(withId(R.id.seatRecyclerView))
                .perform(RecyclerViewActions.actionOnItemAtPosition(3, click()));

        onView(withId(R.id.seatRecyclerView))
                .perform(RecyclerViewActions.actionOnItemAtPosition(6, click()));

        //Still on building fragment, not transferred to detailed seat fragment.
        onView(withId(R.id.buildingName)).check(matches(withText("Dohney Library")));

    }


}
