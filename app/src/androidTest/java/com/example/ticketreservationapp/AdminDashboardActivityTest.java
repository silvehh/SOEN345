package com.example.ticketreservationapp;

import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
public class AdminDashboardActivityTest {

    @Rule
    public ActivityScenarioRule<AdminDashboardActivity> activityRule =
            new ActivityScenarioRule<>(AdminDashboardActivity.class);

    @Before
    public void setUp() {
        Intents.init();
    }

    @After
    public void tearDown() {
        Intents.release();
    }

    @Test
    public void testAllCardsDisplayed() {
        onView(withId(R.id.cardAddEvent)).check(matches(isDisplayed()));
        onView(withId(R.id.cardEditEvent)).check(matches(isDisplayed()));
        onView(withId(R.id.cardCancelEvent)).check(matches(isDisplayed()));
    }

    @Test
    public void testAddEventCardNavigatesToAddEventActivity() {
        onView(withId(R.id.cardAddEvent)).perform(click());
        intended(hasComponent(AddEventActivity.class.getName()));
    }

    @Test
    public void testEditEventCardNavigatesToEventList() {
        onView(withId(R.id.cardEditEvent)).perform(click());
        intended(hasComponent(EventListActivity.class.getName()));
    }

    @Test
    public void testCancelEventCardNavigatesToEventList() {
        onView(withId(R.id.cardCancelEvent)).perform(click());
        intended(hasComponent(EventListActivity.class.getName()));
    }
}
