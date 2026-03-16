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
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;

@RunWith(AndroidJUnit4.class)
public class MainActivityTest {
    @Rule
    public ActivityScenarioRule<MainActivity> activityRule =
            new ActivityScenarioRule<>(MainActivity.class);

    @Before
    public void setUp() {
        Intents.init();
    }

    @After
    public void tearDown() {
        Intents.release();
    }

    @Test
    public void testEmailVisibility() {
        onView(withId(R.id.btnEmail)).perform(click());
        onView(withId(R.id.etEmail)).check(matches(isDisplayed()));
    }

    @Test
    public void testPhoneVisibility() {
        onView(withId(R.id.btnPhone)).perform(click());
        onView(withId(R.id.etPhone)).check(matches(isDisplayed()));
    }

    @Test
    public void testLoginView() {
        onView(withId(R.id.btnLogin)).perform(click());
        intended(hasComponent(LoginActivity.class.getName()));
    }
}
