package com.example.findaseat;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.Matchers.allOf;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.espresso.matcher.BoundedMatcher;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class ReserveHistoryPageTest {

    @Rule
    public ActivityScenarioRule<MainActivity> mActivityScenarioRule =
            new ActivityScenarioRule<>(MainActivity.class);

    @Before
    public void setup(){
        onView(ViewMatchers.withId(R.id.profile))
                .perform(click());
        onView(withId(R.id.email)).perform(typeText("lihongyi@usc.edu"));
        onView(withId(R.id.password)).perform(typeText("123456"),closeSoftKeyboard());
        onView(withId(R.id.login)).perform(click());
        try {
            Thread.sleep(3000); // Wait for 3 seconds
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    //check if clicking reserve history works correctly
    public void ReserveHistoryisDisplayed() {
        onView(withId(R.id.reserveHistory)).perform(click());
        try {
            Thread.sleep(2000); // Wait for 2 seconds
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //
        onView(withId(R.id.history)).check(matches(isDisplayed()));
        onView(withText("Reservation History")).check(matches(isDisplayed()));

        //The corresponding reservation history info of the logged in user should be displayed
        onView(withId(R.id.pastInfo))
                .perform(RecyclerViewActions.scrollToPosition(0));

        onView(withId(R.id.pastInfo))
                .check(matches(atPosition(0, allOf(
                        hasDescendant(withText("Epstein Plaza")),
                        hasDescendant(withText("North Plaza Area")),
                        hasDescendant(withText("11-19"))))));


        //check bottom navigation bar
        bottomNavigationViewIsDisplayed();
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

    //check if the bottom menu is correctly displayed in the map homepage
    public void bottomNavigationViewIsDisplayed() {
        onView(withId(R.id.bottomNavigationView)).check(matches(isDisplayed()));
        onView(withId(R.id.profile)).check(matches(isDisplayed()));
        onView(withId(R.id.map)).check(matches(isDisplayed()));
    }
}
