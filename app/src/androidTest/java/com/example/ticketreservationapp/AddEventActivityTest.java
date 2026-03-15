package com.example.ticketreservationapp;

import androidx.test.espresso.action.ViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class AddEventActivityTest {

    @Rule
    public ActivityScenarioRule<AddEventActivity> activityRule =
            new ActivityScenarioRule<>(AddEventActivity.class);

    @Test
    public void testPublishWithEmptyNameShowsError() {
        onView(withId(R.id.btnPublish)).perform(click());
        onView(withId(R.id.tilEventName))
                .check(matches(TestUtils.hasTextInputLayoutError("Event name is required")));
    }

    @Test
    public void testPublishWithEmptyVenueShowsError() {
        onView(withId(R.id.etEventName))
                .perform(typeText("Jazz Festival"), closeSoftKeyboard());

        onView(withId(R.id.spinnerCategory)).perform(click());
        onView(withText("Music")).perform(click());

        onView(withId(R.id.etDate))
                .perform(ViewActions.replaceText("03/15/2026"), closeSoftKeyboard());

        onView(withId(R.id.etTime))
                .perform(ViewActions.replaceText("7:00 PM"), closeSoftKeyboard());

        onView(withId(R.id.btnPublish)).perform(click());

        onView(withId(R.id.tilVenue))
                .check(matches(TestUtils.hasTextInputLayoutError("Venue is required")));
    }

    @Test
    public void testBackButtonIsDisplayed() {
        onView(withId(R.id.tvBack)).check(matches(isDisplayed()));
    }

    @Test
    public void testBackButtonFinishesActivity() {
        onView(withId(R.id.tvBack)).perform(click());
    }

    @Test
    public void testAllFieldsDisplayed() {
        onView(withId(R.id.tilEventName)).check(matches(isDisplayed()));
        onView(withId(R.id.spinnerCategory)).check(matches(isDisplayed()));
        onView(withId(R.id.tilDate)).check(matches(isDisplayed()));
        onView(withId(R.id.tilTime)).check(matches(isDisplayed()));
        onView(withId(R.id.tilVenue)).check(matches(isDisplayed()));
        onView(withId(R.id.tilTickets)).check(matches(isDisplayed()));
        onView(withId(R.id.tilPrice)).check(matches(isDisplayed()));
        onView(withId(R.id.btnPublish)).check(matches(isDisplayed()));
    }
}