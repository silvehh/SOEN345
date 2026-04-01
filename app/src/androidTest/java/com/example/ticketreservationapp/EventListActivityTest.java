package com.example.ticketreservationapp;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasExtra;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.allOf;

import android.content.Context;
import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class EventListActivityTest {

    @Before
    public void setUp() {
        Intents.init();
    }

    @After
    public void tearDown() {
        Intents.release();
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
        // This test assumes at least one event is displayed in the list.
        // Since we are using Firestore, this might need idling resources or a mock.
        // For a basic UI intent test, we can launch the activity and click if data exists.
        
        try (ActivityScenario<EventListActivity> scenario = ActivityScenario.launch(getIntentWithAdminUser())) {
            // We wait a bit for Firestore (ideally use IdlingResource)
            Thread.sleep(2000); 

            onView(withId(R.id.recyclerEvents)).perform(click());
            
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