package com.example.ticketreservationapp;

import android.widget.Spinner;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Assert;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.isPlatformPopup;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@RunWith(AndroidJUnit4.class)
public class AddEventActivityTest {

    private final List<String> eventNamesToDelete = new ArrayList<>();

    @BeforeClass
    public static void configureFirestore() {
        TestUtils.getTestFirestore();
    }

    @Rule
    public ActivityScenarioRule<AddEventActivity> activityRule =
            new ActivityScenarioRule<>(AddEventActivity.class);

    @After
    public void cleanup() {
        FirebaseFirestore db = TestUtils.getTestFirestore();
        Set<String> namesToDelete = new LinkedHashSet<>(eventNamesToDelete);
        namesToDelete.add("Concert");

        try {
            for (String eventName : namesToDelete) {
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
        eventNamesToDelete.clear();
    }

    @Test
    public void testPublishWithEmptyNameShowsError() {
        onView(withId(R.id.btnPublish)).perform(click());
        onView(withId(R.id.tilEventName))
                .check(matches(TestUtils.hasTextInputLayoutError("Event name is required")));
    }

    @Test
    public void testPublishWithEmptyVenueShowsError() {
        onView(withId(R.id.etEventName))
                .perform(replaceText("Jazz Festival"), closeSoftKeyboard());

        selectMusicCategory();

        onView(withId(R.id.etDate))
                .perform(replaceText("03/15/2026"), closeSoftKeyboard());

        onView(withId(R.id.etTime))
                .perform(replaceText("7:00 PM"), closeSoftKeyboard());

        onView(withId(R.id.btnPublish)).perform(click());

        onView(withId(R.id.tilVenue))
                .check(matches(TestUtils.hasTextInputLayoutError("Venue is required")));
    }

    @Test
    public void testSuccessfulPublish() {
        String eventName = "Concert " + System.currentTimeMillis();
        eventNamesToDelete.add(eventName);

        onView(withId(R.id.etEventName)).perform(replaceText(eventName), closeSoftKeyboard());

        selectMusicCategory();

        onView(withId(R.id.etDate)).perform(replaceText("12/12/2025"), closeSoftKeyboard());
        onView(withId(R.id.etTime)).perform(replaceText("8:00 PM"), closeSoftKeyboard());
        onView(withId(R.id.etVenue)).perform(replaceText("Madison Square Garden"), closeSoftKeyboard());
        onView(withId(R.id.etTickets)).perform(replaceText("500"), closeSoftKeyboard());
        onView(withId(R.id.etPrice)).perform(replaceText("75.00"), closeSoftKeyboard());

        onView(withId(R.id.btnPublish)).perform(click());

        QuerySnapshot snapshot = waitForEventByName(eventName, 30000);
        assertEquals(1, snapshot.size());

        DocumentSnapshot document = snapshot.getDocuments().get(0);
        assertEquals(eventName, document.getString("eventName"));
        assertEquals("Music", document.getString("category"));
        assertEquals("12/12/2025", document.getString("date"));
        assertEquals("8:00 PM", document.getString("time"));
        assertEquals("Madison Square Garden", document.getString("venue"));
        Assert.assertEquals(500L, ((Number) document.get("tickets")).longValue());
        Assert.assertEquals(75.00, ((Number) document.get("price")).doubleValue(), 0.001);
    }

    @Test
    public void testPublishWithNonNumericTicketsShowsError() {
        onView(withId(R.id.etEventName)).perform(replaceText("Concert"), closeSoftKeyboard());

        selectMusicCategory();

        onView(withId(R.id.etDate)).perform(replaceText("12/12/2025"), closeSoftKeyboard());
        onView(withId(R.id.etTime)).perform(replaceText("8:00 PM"), closeSoftKeyboard());
        onView(withId(R.id.etVenue)).perform(replaceText("Madison Square Garden"), closeSoftKeyboard());
        onView(withId(R.id.etTickets)).perform(replaceText("five hundred"), closeSoftKeyboard());
        onView(withId(R.id.etPrice)).perform(replaceText("75.00"), closeSoftKeyboard());

        onView(withId(R.id.btnPublish)).perform(click());

        onView(withId(R.id.tilTickets))
                .check(matches(TestUtils.hasTextInputLayoutError("Ticket count must be a whole number")));
    }

    @Test
    public void testPublishWithNonNumericPriceShowsError() {
        onView(withId(R.id.etEventName)).perform(replaceText("Concert"), closeSoftKeyboard());

        selectMusicCategory();

        onView(withId(R.id.etDate)).perform(replaceText("12/12/2025"), closeSoftKeyboard());
        onView(withId(R.id.etTime)).perform(replaceText("8:00 PM"), closeSoftKeyboard());
        onView(withId(R.id.etVenue)).perform(replaceText("Madison Square Garden"), closeSoftKeyboard());
        onView(withId(R.id.etTickets)).perform(replaceText("500"), closeSoftKeyboard());
        onView(withId(R.id.etPrice)).perform(replaceText("free"), closeSoftKeyboard());

        onView(withId(R.id.btnPublish)).perform(click());

        onView(withId(R.id.tilPrice))
                .check(matches(TestUtils.hasTextInputLayoutError("Price must be a valid number")));
    }

    @Test
    public void testBackButtonIsDisplayed() {
        onView(withId(R.id.tvBack)).check(matches(isDisplayed()));
    }

    @Test
    public void testBackButtonFinishesActivity() {
        onView(withId(R.id.tvBack)).perform(click());
    }

    @Test
    public void testAllFieldsDisplayed() {
        onView(withId(R.id.tilEventName)).check(matches(isDisplayed()));
        onView(withId(R.id.spinnerCategory)).check(matches(isDisplayed()));
        onView(withId(R.id.tilDate)).check(matches(isDisplayed()));
        onView(withId(R.id.tilTime)).check(matches(isDisplayed()));
        onView(withId(R.id.tilVenue)).check(matches(isDisplayed()));
        onView(withId(R.id.tilTickets)).check(matches(isDisplayed()));
        onView(withId(R.id.tilPrice)).check(matches(isDisplayed()));
        onView(withId(R.id.btnPublish)).check(matches(isDisplayed()));
    }

    private void selectMusicCategory() {
        onView(withId(R.id.spinnerCategory)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is("Music")))
                .inRoot(isPlatformPopup())
                .perform(click());
        onView(withId(R.id.spinnerCategory)).check((view, noViewFoundException) -> {
            if (noViewFoundException != null) {
                throw noViewFoundException;
            }
            Spinner spinner = (Spinner) view;
            assertEquals("Music", spinner.getSelectedItem().toString());
        });
    }

    private QuerySnapshot waitForEventByName(String eventName, long timeout) {
        FirebaseFirestore db = TestUtils.getTestFirestore();
        long endTime = System.currentTimeMillis() + timeout;
        while (System.currentTimeMillis() < endTime) {
            try {
                QuerySnapshot snapshot = Tasks.await(
                        db.collection("events").whereEqualTo("eventName", eventName).get(),
                        5,
                        TimeUnit.SECONDS
                );
                if (!snapshot.isEmpty()) {
                    return snapshot;
                }
            } catch (Exception ignored) {}

            try {
                Thread.sleep(250);
            } catch (InterruptedException ignored) {}
        }
        fail("Timed out waiting for published event: " + eventName);
        return null;
    }
}
