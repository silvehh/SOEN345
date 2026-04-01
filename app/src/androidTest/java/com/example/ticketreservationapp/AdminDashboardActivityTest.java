package com.example.ticketreservationapp;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.core.app.ActivityScenario.launch;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.content.Context;
import android.content.Intent;

@RunWith(AndroidJUnit4.class)
public class AdminDashboardActivityTest {

    @Before
    public void setUp() {
        Intents.init();
    }

    @After
    public void tearDown() {
        Intents.release();
    }

    private Intent getIntentWithoutUser() {
        Context context = ApplicationProvider.getApplicationContext();
        return new Intent(context, AdminDashboardActivity.class);
    }

    private Intent getIntentWithUser(String name) {
        Context context = ApplicationProvider.getApplicationContext();
        Intent intent = new Intent(context, AdminDashboardActivity.class);

        User user = new User();
        user.setName(name);
        intent.putExtra("user", user);

        return intent;
    }

    @Test
    public void testAllViewsDisplayed() {
        try (ActivityScenario<AdminDashboardActivity> scenario =
                     ActivityScenario.launch(getIntentWithoutUser())) {

            onView(withId(R.id.adminUser)).check(matches(isDisplayed()));
            onView(withId(R.id.cardAddEvent)).check(matches(isDisplayed()));
            onView(withId(R.id.cardEditEvent)).check(matches(isDisplayed()));
            onView(withId(R.id.cardCancelEvent)).check(matches(isDisplayed()));
            onView(withId(R.id.navEvents)).check(matches(isDisplayed()));
            onView(withId(R.id.signOutbtn)).check(matches(isDisplayed()));
        }
    }

    @Test
    public void testUserNameDisplayedWhenUserPassed() {
        try (ActivityScenario<AdminDashboardActivity> scenario =
                     ActivityScenario.launch(getIntentWithUser("Joseph"))) {

            onView(withId(R.id.adminUser))
                    .check(matches(withText("ADMIN: Joseph")));
        }
    }

    @Test
    public void testActivityLaunchesWithoutUser() {
        try (ActivityScenario<AdminDashboardActivity> scenario =
                     ActivityScenario.launch(getIntentWithoutUser())) {

            onView(withId(R.id.adminUser)).check(matches(isDisplayed()));
        }
    }

    @Test
    public void testAddEventCardNavigatesToAddEventActivity() {
        try (ActivityScenario<AdminDashboardActivity> scenario =
                     ActivityScenario.launch(getIntentWithoutUser())) {

            onView(withId(R.id.cardAddEvent)).perform(click());
            intended(hasComponent(AddEventActivity.class.getName()));
        }
    }

    @Test
    public void testEditEventCardNavigatesToEventList() {
        try (ActivityScenario<AdminDashboardActivity> scenario =
                     ActivityScenario.launch(getIntentWithoutUser())) {

            onView(withId(R.id.cardEditEvent)).perform(click());
            intended(hasComponent(EventListActivity.class.getName()));
        }
    }

    @Test
    public void testCancelEventCardNavigatesToEventList() {
        try (ActivityScenario<AdminDashboardActivity> scenario =
                     ActivityScenario.launch(getIntentWithoutUser())) {

            onView(withId(R.id.cardCancelEvent)).perform(click());
            intended(hasComponent(EventListActivity.class.getName()));
        }
    }

    @Test
    public void testNavEventsNavigatesToEventList() {
        try (ActivityScenario<AdminDashboardActivity> scenario =
                     ActivityScenario.launch(getIntentWithoutUser())) {

            onView(withId(R.id.navEvents)).perform(click());
            intended(hasComponent(EventListActivity.class.getName()));
        }
    }

    @Test
    public void testSignOutNavigatesToLandingActivity() {
        try (ActivityScenario<AdminDashboardActivity> scenario =
                     ActivityScenario.launch(getIntentWithoutUser())) {

            onView(withId(R.id.signOutbtn)).perform(click());
            intended(hasComponent(LandingActivity.class.getName()));
        }
    }
}
