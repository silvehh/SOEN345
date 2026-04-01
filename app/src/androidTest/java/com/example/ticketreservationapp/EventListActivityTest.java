package com.example.ticketreservationapp;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

import android.content.Context;
import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.FirebaseFirestore;

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
            // Emulator already set up
        }
    }

    private void addDummyEvent() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        readWrite rw = new readWrite(db);
        Event event = new Event("Test Intent Event", "Music", "12/12/2025", "8:00 PM", "Test Venue", 100, 50.0);
        try {
            Tasks.await(rw.addEvent(event), 5, TimeUnit.SECONDS);
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
            // We wait a bit for Firestore to fetch data
            Thread.sleep(2000); 

            // Click the item with our dummy event name
            onView(withText("Test Intent Event")).perform(click());
            
            intended(allOf(
                    hasComponent(AddEventActivity.class.getName()),
                    hasExtraWithKey("edit_event")
            ));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // Helper matcher because hasExtra(key, value) requires a value
    private static org.hamcrest.Matcher<Intent> hasExtraWithKey(String key) {
        return new org.hamcrest.TypeSafeMatcher<Intent>() {
            @Override
            public void describeTo(org.hamcrest.Description description) {
                description.appendText("has extra with key: " + key);
            }
            @Override
            protected boolean matchesSafely(Intent item) {
                return item.hasExtra(key);
            }
        };
    }
}
