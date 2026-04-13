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
    private FirebaseFirestore db;
    readWrite rw;

    @Before
    public void setUp() {
        Intents.init();

        intending(hasComponent(MainActivity.class.getName()))
                .respondWith(new Instrumentation.ActivityResult(Activity.RESULT_OK, null));
        intending(hasComponent(EventListActivity.class.getName()))
                .respondWith(new Instrumentation.ActivityResult(Activity.RESULT_OK, null));
        intending(hasComponent(AdminDashboardActivity.class.getName()))
                .respondWith(new Instrumentation.ActivityResult(Activity.RESULT_OK, null));



        if (FirebaseApp.getApps(InstrumentationRegistry.getInstrumentation().getTargetContext()).isEmpty()) {
            FirebaseApp.initializeApp(InstrumentationRegistry.getInstrumentation().getTargetContext());
        }


        db = FirebaseFirestore.getInstance();

        try{
            db.useEmulator("10.0.2.2", 8080);
        }
        catch (Exception e) {

        }

        try {
            FirebaseFirestoreSettings settings =
                    new FirebaseFirestoreSettings.Builder()
                            .setPersistenceEnabled(false)
                            .build();
            db.setFirestoreSettings(settings);
        } catch (IllegalStateException ignored) {
            // Firestore settings can only be set once
        }
        rw = new readWrite(db);
    }

    @After
    public void tearDown() {
        release();
    }

    @Test
    public void testDisplaysEventDetailsCorrectly() {

        Event event = buildEvent(
                "event-1",
                "Basketball Night",
                "Sports",
                "2026-04-20",
                "7:00 PM",
                "Main Gym",
                15.0
        );

        User user = buildUser("test@gmail.com", "5145551234", new ArrayList<>());

        launch(event, user);

        onView(withId(R.id.evTitle)).check(matches(withText("Basketball Night")));
        onView(withId(R.id.evCategory)).check(matches(withText("Sports")));
        onView(withId(R.id.evDate)).check(matches(withText("Date: 2026-04-20")));
        onView(withId(R.id.evTime)).check(matches(withText("Time: 7:00 PM")));
        onView(withId(R.id.evVenue)).check(matches(withText("Venue: Main Gym")));
        onView(withId(R.id.evPrice)).check(matches(withText("Price: 15.0")));
    }

    @Test
    public void testButtonShowsCancelReservationWhenAlreadyReserved() {

        Event event = buildEvent(
                "event-2",
                "Movie Night",
                "Entertainment",
                "2026-04-22",
                "8:00 PM",
                "Hall A",
                10.0
        );

        ArrayList<String> events = new ArrayList<>();
        events.add("event-2");

        User user = buildUser("test@gmail.com", "+15145551234", events);

        launch(event, user);

        onView(withId(R.id.btnReserve))
                .check(matches(withText("Cancel Reservation")));
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

    private Event buildEvent(String id,
                             String name,
                             String category,
                             String date,
                             String time,
                             String venue,
                             double price) {
        Event event = new Event();
        event.setId(id);
        event.setEventName(name);
        event.setCategory(category);
        event.setDate(date);
        event.setTime(time);
        event.setVenue(venue);
        event.setPrice(price);
        return event;
    }

    private User buildUser(String email, String phone, ArrayList<String> events) {
        User user = new User();
        user.setEmail(email);
        user.setPhone(phone);
        user.setEvents(events);
        return user;
    }
}
