package com.example.ticketreservationapp;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.NoMatchingViewException;
import androidx.test.espresso.ViewAssertion;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.FirebaseFirestore;

import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.TimeUnit;

@RunWith(AndroidJUnit4.class)
public class EventListActivityTest {

    @Before
    public void setUp() {
        try {
            Intents.init();
        } catch (IllegalStateException ignored) {}
        
        setupFirestoreEmulator();
    }

    private void setupFirestoreEmulator() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        try {
            db.useEmulator("10.0.2.2", 8080);
        } catch (IllegalStateException ignored) {
            // Emulator already set up or settings already applied
        }
    }

    private void addDummyEvent() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        // Clear events first to ensure our dummy is there and easy to find
        try {
            // We can't easily delete a collection in Firestore from client without a loop, 
            // but we can at least try to add our event.
            readWrite rw = new readWrite(db);
            Event event = new Event("Test Intent Event", "Music", "12/12/2025", "8:00 PM", "Test Venue", 100, 50.0);
            Tasks.await(rw.addEvent(event), 10, TimeUnit.SECONDS);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @After
    public void tearDown() {
        try {
            Intents.release();
        } catch (IllegalStateException ignored) {}
    }

    private Intent getIntentWithAdminUser() {
        Context context = ApplicationProvider.getApplicationContext();
        Intent intent = new Intent(context, EventListActivity.class);
        User user = new User();
        user.setName("Admin");
        user.setAdmin(true);
        intent.putExtra("user", user);
        return intent;
    }

    @Test
    public void testAdminClickingEventNavigatesToEditMode() {
        addDummyEvent();
        
        try (ActivityScenario<EventListActivity> scenario = ActivityScenario.launch(getIntentWithAdminUser())) {
            // Wait for the event to appear in the list (up to 10 seconds)
            waitForView(withText("Test Intent Event"), 10000);

            // Click the item
            onView(withText("Test Intent Event")).perform(click());
            
            intended(allOf(
                    hasComponent(AddEventActivity.class.getName()),
                    hasExtraWithKey("edit_event")
            ));
        }
    }

    private void waitForView(Matcher<View> viewMatcher, long timeout) {
        long startTime = System.currentTimeMillis();
        long endTime = startTime + timeout;
        while (System.currentTimeMillis() < endTime) {
            try {
                onView(viewMatcher).check(matches(isDisplayed()));
                return;
            } catch (NoMatchingViewException | AssertionError e) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException ignored) {}
            }
        }
        // One last try to throw the exception if still not found
        onView(viewMatcher).check(matches(isDisplayed()));
    }

    private static Matcher<Intent> hasExtraWithKey(String key) {
        return new org.hamcrest.TypeSafeMatcher<Intent>() {
            @Override
            public void describeTo(org.hamcrest.Description description) {
                description.appendText("has extra with key: " + key);
            }
            @Override
            protected boolean matchesSafely(Intent item) {
                return item != null && item.hasExtra(key);
            }
        };
    }
}
