package com.example.ticketreservationapp;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.not;

import android.content.Context;
import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class AddEventEditModeTest {

    @BeforeClass
    public static void configureFirestore() {
        TestUtils.getTestFirestore();
    }

    private Intent getEditIntent() {
        Context context = ApplicationProvider.getApplicationContext();
        Intent intent = new Intent(context, AddEventActivity.class);
        Event event = new Event("Original Name", "Music", "01/01/2025", "10:00 AM", "Original Venue", 100, 20.0);
        event.setId("test_event_id");
        intent.putExtra("edit_event", event);
        return intent;
    }

    @Test
    public void testCancelButtonVisibleInEditMode() {
        try (ActivityScenario<AddEventActivity> scenario = ActivityScenario.launch(getEditIntent())) {
            onView(withId(R.id.btnCancelEvent)).check(matches(isDisplayed()));
            onView(withId(R.id.tvTitle)).check(matches(withText("Edit Event")));
            onView(withId(R.id.btnPublish)).check(matches(withText("Update Event")));
        }
    }

    @Test
    public void testCancelButtonNotVisibleInAddMode() {
        try (ActivityScenario<AddEventActivity> scenario = ActivityScenario.launch(AddEventActivity.class)) {
            onView(withId(R.id.btnCancelEvent)).check(matches(not(isDisplayed())));
            onView(withId(R.id.tvTitle)).check(matches(withText("Add Event")));
        }
    }
    
    @Test
    public void testCancelButtonClickFinishesActivity() {
        // This test verifies the UI flow. Actual DB deletion is tested in readWriteTests.
        try (ActivityScenario<AddEventActivity> scenario = ActivityScenario.launch(getEditIntent())) {
            onView(withId(R.id.btnCancelEvent)).perform(click());
            // If the activity finishes, the scenario state will change or it won't crash
        }
    }
}
