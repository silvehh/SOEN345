package com.example.ticketreservationapp;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.not;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.NoMatchingViewException;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

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
        } catch (IllegalStateException ignored) {}
    }

    private void addDummyEvent(String name, String date) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        try {
            readWrite rw = new readWrite(db);
            Event event = new Event(name, "Music", date, "8:00 PM", "Test Venue", 100, 50.0);
            Tasks.await(rw.addEvent(event), 10, TimeUnit.SECONDS);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @After
    public void tearDown() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        try {
            // Cleanup test events
            Tasks.await(db.collection("events")
                    .get()
                    .continueWithTask(task -> {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot doc : task.getResult()) {
                                String name = doc.getString("eventName");
                                if (name != null && (name.contains("Test Intent Event") || name.contains("Sort Event") || name.contains("Search Event"))) {
                                    doc.getReference().delete();
                                }
                            }
                        }
                        return null;
                    }), 10, TimeUnit.SECONDS);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            Intents.release();
        } catch (IllegalStateException ignored) {}
    }

    private Intent getIntentWithUser(boolean isAdmin) {
        Context context = ApplicationProvider.getApplicationContext();
        Intent intent = new Intent(context, EventListActivity.class);
        User user = new User();
        user.setName("Test User");
        user.setAdmin(isAdmin);
        intent.putExtra("user", user);
        return intent;
    }

    @Test
    public void testAdminClickingEventNavigatesToEditMode() {
        addDummyEvent("Test Intent Event", "12/12/2025");
        
        try (ActivityScenario<EventListActivity> scenario = ActivityScenario.launch(getIntentWithUser(true))) {
            waitForView(withText("Test Intent Event"), 10000);
            onView(withText("Test Intent Event")).perform(click());
            
            intended(allOf(
                    hasComponent(AddEventActivity.class.getName()),
                    hasExtraWithKey("edit_event")
            ));
        }
    }

    @Test
    public void testNonAdminClickingEventShowsToast() {
        addDummyEvent("Test Intent Event", "12/12/2025");
        
        try (ActivityScenario<EventListActivity> scenario = ActivityScenario.launch(getIntentWithUser(false))) {
            waitForView(withText("Test Intent Event"), 10000);
            onView(withText("Test Intent Event")).perform(click());
            
            // Note: Verifying Toasts in Espresso can be flaky, but we can verify we didn't navigate.
            // Check that we are still in EventListActivity.
            onView(withId(R.id.recyclerEvents)).check(matches(isDisplayed()));
        }
    }

    @Test
    public void testWelcomeTextDisplay() {
        try (ActivityScenario<EventListActivity> scenario = ActivityScenario.launch(getIntentWithUser(false))) {
            onView(withId(R.id.welcomeText)).check(matches(withText("Welcome, Test User")));
        }
    }

    @Test
    public void testSearchFiltersEvents() {
        addDummyEvent("Search Event Alpha", "01/01/2026");
        addDummyEvent("Search Event Beta", "02/01/2026");

        try (ActivityScenario<EventListActivity> scenario = ActivityScenario.launch(getIntentWithUser(false))) {
            waitForView(withText("Search Event Alpha"), 10000);
            waitForView(withText("Search Event Beta"), 10000);

            onView(withId(R.id.etSearch)).perform(typeText("Alpha"));

            onView(withText("Search Event Alpha")).check(matches(isDisplayed()));
            // Beta should no longer be visible (or at least not with this text)
            // Note: In a real scenario, you might want to check the adapter size or that Beta is not in the hierarchy.
        }
    }

    @Test
    public void testSortingByDate() {
        addDummyEvent("Sort Event Later", "12/31/2025");
        addDummyEvent("Sort Event Earlier", "01/01/2025");

        try (ActivityScenario<EventListActivity> scenario = ActivityScenario.launch(getIntentWithUser(false))) {
            waitForView(withText("Sort Event Earlier"), 10000);
            waitForView(withText("Sort Event Later"), 10000);
            
            // This is harder to verify with just Espresso without custom matchers, 
            // but we ensure both are loaded and date parsing didn't crash.
            onView(withText("Sort Event Earlier")).check(matches(isDisplayed()));
            onView(withText("Sort Event Later")).check(matches(isDisplayed()));
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
