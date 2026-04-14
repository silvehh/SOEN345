package com.example.ticketreservationapp;

import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.not;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.NoMatchingViewException;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

@RunWith(AndroidJUnit4.class)
public class EventListActivityTest {

    private final List<String> eventNamesToDelete = new ArrayList<>();

    @Before
    public void setUp() {
        try {
            Intents.init();
        } catch (IllegalStateException ignored) {}

        TestUtils.getTestFirestore();
    }

    private void addDummyEvent(String name, String date) {
        try {
            FirebaseFirestore db = TestUtils.getTestFirestore();
            readWrite rw = new readWrite(db);
            Event event = new Event(name, "Music", date, "8:00 PM", "Test Venue", 100, 50.0);
            Tasks.await(rw.addEvent(event), 10, TimeUnit.SECONDS);
            eventNamesToDelete.add(name);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @After
    public void tearDown() {
        try {
            FirebaseFirestore db = TestUtils.getTestFirestore();
            for (String eventName : eventNamesToDelete) {
                QuerySnapshot snapshot = Tasks.await(
                        db.collection("events").whereEqualTo("eventName", eventName).get(),
                        10,
                        TimeUnit.SECONDS
                );
                for (QueryDocumentSnapshot doc : snapshot) {
                    Tasks.await(doc.getReference().delete(), 10, TimeUnit.SECONDS);
                }
            }
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
        String eventName = uniqueEventName("Test Intent Event");
        addDummyEvent(eventName, "12/12/2025");
        
        try (ActivityScenario<EventListActivity> scenario = ActivityScenario.launch(getIntentWithUser(true))) {
            waitForView(withText(eventName), 10000);
            onView(withText(eventName)).perform(click());
            
            intended(allOf(
                    hasComponent(AddEventActivity.class.getName()),
                    hasExtraWithKey("edit_event")
            ));
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
        String suffix = String.valueOf(System.currentTimeMillis());
        String alphaName = "Search Event Alpha " + suffix;
        String betaName = "Search Event Beta " + suffix;
        addDummyEvent(alphaName, "01/01/2026");
        addDummyEvent(betaName, "02/01/2026");

        try (ActivityScenario<EventListActivity> scenario = ActivityScenario.launch(getIntentWithUser(false))) {
            waitForAdapterToContain(scenario, Arrays.asList(alphaName, betaName), 10000);

            onView(withId(R.id.etSearch)).perform(replaceText(alphaName), closeSoftKeyboard());

            waitForAdapterEventNames(scenario, Arrays.asList(alphaName), 10000);
            assertEquals(Arrays.asList(alphaName), getAdapterEventNames(scenario));
        }
    }

    @Test
    public void testSortingByDate() {
        String suffix = String.valueOf(System.currentTimeMillis());
        String groupName = "Sort Group " + suffix;
        String laterName = groupName + " Later";
        String earlierName = groupName + " Earlier";
        addDummyEvent(laterName, "12/31/2025");
        addDummyEvent(earlierName, "01/01/2025");

        try (ActivityScenario<EventListActivity> scenario = ActivityScenario.launch(getIntentWithUser(false))) {
            waitForAdapterToContain(scenario, Arrays.asList(earlierName, laterName), 10000);

            onView(withId(R.id.etSearch)).perform(replaceText(groupName), closeSoftKeyboard());

            waitForAdapterEventNames(scenario, Arrays.asList(earlierName, laterName), 10000);
            assertEquals(Arrays.asList(earlierName, laterName), getAdapterEventNames(scenario));
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

    private void waitForAdapterToContain(ActivityScenario<EventListActivity> scenario, List<String> expectedNames, long timeout) {
        long endTime = System.currentTimeMillis() + timeout;
        while (System.currentTimeMillis() < endTime) {
            List<String> currentNames = getAdapterEventNames(scenario);
            if (currentNames.containsAll(expectedNames)) {
                return;
            }
            sleepBriefly();
        }
        fail("Timed out waiting for adapter to contain: " + expectedNames + ". Current: " + getAdapterEventNames(scenario));
    }

    private void waitForAdapterEventNames(ActivityScenario<EventListActivity> scenario, List<String> expectedNames, long timeout) {
        long endTime = System.currentTimeMillis() + timeout;
        while (System.currentTimeMillis() < endTime) {
            List<String> currentNames = getAdapterEventNames(scenario);
            if (currentNames.equals(expectedNames)) {
                return;
            }
            sleepBriefly();
        }
        fail("Timed out waiting for exact adapter names: " + expectedNames + ". Current: " + getAdapterEventNames(scenario));
    }

    private List<String> getAdapterEventNames(ActivityScenario<EventListActivity> scenario) {
        AtomicReference<List<String>> namesRef = new AtomicReference<>(new ArrayList<>());
        scenario.onActivity(activity -> {
            RecyclerView recyclerView = activity.findViewById(R.id.recyclerEvents);
            EventAdapter adapter = (EventAdapter) recyclerView.getAdapter();
            namesRef.set(readAdapterEventNames(adapter));
        });
        return namesRef.get();
    }

    @SuppressWarnings("unchecked")
    private List<String> readAdapterEventNames(EventAdapter adapter) {
        List<String> names = new ArrayList<>();
        if (adapter == null) {
            return names;
        }

        try {
            Field eventListField = EventAdapter.class.getDeclaredField("eventList");
            eventListField.setAccessible(true);
            List<Event> events = (List<Event>) eventListField.get(adapter);
            for (Event event : events) {
                names.add(event.getEventName());
            }
            return names;
        } catch (ReflectiveOperationException e) {
            throw new AssertionError("Unable to inspect recycler adapter contents", e);
        }
    }

    private String uniqueEventName(String prefix) {
        return prefix + " " + System.currentTimeMillis();
    }

    private void sleepBriefly() {
        try {
            Thread.sleep(250);
        } catch (InterruptedException ignored) {}
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
