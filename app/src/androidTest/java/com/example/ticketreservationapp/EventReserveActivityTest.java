package com.example.ticketreservationapp;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.init;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.Intents.intending;
import static androidx.test.espresso.intent.Intents.release;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

@RunWith(AndroidJUnit4.class)
public class EventReserveActivityTest {
    private Event event1, event2;
    private User user;

    @Before
    public void setUp() {
        Intents.init();

        user = new User("John Doe", "e@e.com", null, "12345678", false, null);
        event1 = new Event("Test Event", "Music", "12/12/2025", "8:00 PM", "Test Venue", 100, 50.0);
        event2 = new Event("Test Event2", "Music", "12/12/2025", "8:00 PM", "Test Venue", 100, 50.0);
    }

    @After
    public void tearDown() {
        release();
    }

    @Test
    public void testDisplaysEventDetailsCorrectly() {

        event1.setId("event1");


        user.setEvents(new ArrayList<>());

        launch(event1, user);

        onView(withId(R.id.evTitle)).check(matches(withText("Test Event")));
        onView(withId(R.id.evCategory)).check(matches(withText("Music")));
        onView(withId(R.id.evDate)).check(matches(withText("Date: 12/12/2025")));
        onView(withId(R.id.evTime)).check(matches(withText("Time: 8:00 PM")));
        onView(withId(R.id.evVenue)).check(matches(withText("Venue: Test Venue")));
        onView(withId(R.id.evPrice)).check(matches(withText("Price: 50.0")));
    }

    @Test
    public void testButtonShowsCancelReservationWhenAlreadyReserved() {

        event2.setId("event2");

        ArrayList<String> events = new ArrayList<>();
        events.add(event2.getId());

        user.setEvents(events);

        launch(event2, user);

        onView(withId(R.id.btnReserve))
                .check(matches(withText("Cancel Reservation")));
    }

    @Test
    public void testButtonShowsReserve() {
        event1.setId("event1");


        user.setEvents(new ArrayList<>());

        launch(event1, user);

        onView(withId(R.id.btnReserve))
                .check(matches(withText("Reserve Event")));
    }

    private void launch(Event event, User user) {
        Intent intent = new Intent(
                InstrumentationRegistry.getInstrumentation().getTargetContext(),
                EventReserveActivity.class
        );
        intent.putExtra("reserve_event", event);
        intent.putExtra("user", user);

        ActivityScenario.launch(intent);
    }

}
