package com.example.findaseat;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withSpinnerText;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.containsString;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject;
import androidx.test.uiautomator.UiObjectNotFoundException;
import androidx.test.uiautomator.UiSelector;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class MakeReservationTest {
    @Rule
    public ActivityScenarioRule<MainActivity> mActivityScenarioRule =
            new ActivityScenarioRule<>(MainActivity.class);

    @Before
    //We go to the seat page of the first seat in Leavey Library
    public void setup() throws UiObjectNotFoundException {
        onView(ViewMatchers.withId(R.id.profile))
                .perform(click());
        onView(withId(R.id.email)).perform(typeText("lihongyi@usc.edu"));
        onView(withId(R.id.password)).perform(typeText("123456"),closeSoftKeyboard());
        onView(withId(R.id.login)).perform(click());
        try {
            Thread.sleep(3000); // Wait for 1 seconds
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        onView(ViewMatchers.withId(R.id.map))
                .perform(click());
        UiDevice device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        UiObject marker = device.findObject(new UiSelector().descriptionContains("Leavey"));
        marker.click();
        try {
            Thread.sleep(3000); // Wait for 3 seconds
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(withId(R.id.seatRecyclerView))
                .perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        try {
            Thread.sleep(2000); // Wait for 2 seconds
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    //Note that this test is creating a reservation from 21:30 to 22:00 at the first seat in Leavey. If current time is after 21:30,
    //or there is overlapping reservation existing, then the reservation won't be created, and the test will return error.
    @Test
    public void makeReservation(){
        //checking the start time spinner is working properly
        onView(withId(R.id.startTimeSpinner)).perform(click());
        onData(anything()).atPosition(1).perform(click());
        onView(withId(R.id.startTimeSpinner)).check(matches(withSpinnerText(containsString("08:30"))));
        onView(withId(R.id.startTimeSpinner)).perform(click());
        onData(anything()).atPosition(27).perform(click());
        onView(withId(R.id.startTimeSpinner)).check(matches(withSpinnerText(containsString("21:30"))));//pick 21:30

        //checking the duration time spinner is working properly
        onView(withId(R.id.durationSpinner)).perform(click());
        onData(anything()).atPosition(1).perform(click());
        onView(withId(R.id.durationSpinner)).check(matches(withSpinnerText(containsString("01:00"))));
        onView(withId(R.id.durationSpinner)).perform(click());
        onData(anything()).atPosition(0).perform(click());
        onView(withId(R.id.durationSpinner)).check(matches(withSpinnerText(containsString("00:30"))));//pick 30 minutes


        onView(withId(R.id.reserve)).perform(click());
        try {
            Thread.sleep(3000); // Wait for 3 seconds
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //Checking if the reservation we just created appear in the recycler view
        onView(withId(R.id.currentReservation))
                .check(matches(allOf(hasDescendant(withText("21:30")),hasDescendant(withText("22:00")))));
        checkInHistory();

    }

    //check that the created reservation will show up in history page
    public void checkInHistory(){
        onView(ViewMatchers.withId(R.id.profile))
                .perform(click());
        onView(withId(R.id.reserveHistory)).perform(click());
        onView(withId(R.id.upcomingReservation))
                .check(matches(allOf(
                        hasDescendant(withText("Leavey")),
                        hasDescendant(withText("21:30")),
                        hasDescendant(withText("First Floor Study Room")))));


        try {
            Thread.sleep(2000); // Wait for 2 seconds
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        cancel();
    }

    //Click cancel and click "No" or "Yes" on the Alert dialog, the upcoming list and past list will be updated accordingly
    public void cancel(){
        //Should do nothing here since we choose no
        onView(ViewMatchers.withId(R.id.cancel)).perform(click());
        try {
            Thread.sleep(1000); // Wait for 1 seconds
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(withText("No")).perform(click());
        //Regret about cancel, so the reservation is still in the upcoming list
        onView(withId(R.id.upcomingReservation))
                .check(matches(allOf(
                        hasDescendant(withText("Leavey")),
                        hasDescendant(withText("21:30")),
                        hasDescendant(withText("First Floor Study Room")))));

        //Here we decide to cancel it
        onView(ViewMatchers.withId(R.id.cancel)).perform(click());
        try {
            Thread.sleep(1000); // Wait for 1 seconds
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onView(withText("Yes")).perform(click());
        try {
            Thread.sleep(3000); // Wait for 3 seconds
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //Check that it's not in the upcpming list but in the past list with a "c" for cancelled.
        onView(withId(R.id.upcomingReservation)).check(matches(withItemCount(0)));
        onView(withId(R.id.pastInfo))
                .check(matches(allOf(
                        hasDescendant(withText("Leavey")),
                        hasDescendant(withText("First Floor Study Room")),
                        hasDescendant(withText("21:30")),
                        hasDescendant(withText("C")))));

    }

    public static Matcher<View> withItemCount(final int expectednum) {
        return new TypeSafeMatcher<View>() {

            @Override
            protected boolean matchesSafely(View view) {
                if (!(view instanceof RecyclerView)) {
                    return false;
                }
                RecyclerView recyclerView = (RecyclerView) view;
                RecyclerView.Adapter<?> adapter = recyclerView.getAdapter();
                if (adapter == null) {
                    return false;
                }
                return adapter.getItemCount() == expectednum;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("RecyclerView should have " + expectednum + " items");
            }
        };
    }

}
