package com.example.ticketreservationapp;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

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
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;

import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.concurrent.TimeUnit;

@RunWith(AndroidJUnit4.class)
public class AddEventActivityTest {

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

        try {
            // Delete test events created by these tests
            Tasks.await(db.collection("events")
                    .whereEqualTo("eventName", "Concert")
                    .get()
                    .continueWithTask(task -> {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot doc : task.getResult()) {
                                doc.getReference().delete();
                            }
                        }
                        return null;
                    }), 10, TimeUnit.SECONDS);
        } catch (Exception e) {
            e.printStackTrace();
        }
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
                .perform(typeText("Jazz Festival"), closeSoftKeyboard());

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
        onView(withId(R.id.etEventName)).perform(typeText("Concert"), closeSoftKeyboard());

        selectMusicCategory();

        onView(withId(R.id.etDate)).perform(replaceText("12/12/2025"), closeSoftKeyboard());
        onView(withId(R.id.etTime)).perform(replaceText("8:00 PM"), closeSoftKeyboard());
        onView(withId(R.id.etVenue)).perform(typeText("Madison Square Garden"), closeSoftKeyboard());
        onView(withId(R.id.etTickets)).perform(typeText("500"), closeSoftKeyboard());
        onView(withId(R.id.etPrice)).perform(typeText("75.00"), closeSoftKeyboard());

        onView(withId(R.id.btnPublish)).perform(click());
    }

    @Test
    public void testPublishWithNonNumericTicketsShowsError() {
        onView(withId(R.id.etEventName)).perform(typeText("Concert"), closeSoftKeyboard());

        selectMusicCategory();

        onView(withId(R.id.etDate)).perform(replaceText("12/12/2025"), closeSoftKeyboard());
        onView(withId(R.id.etTime)).perform(replaceText("8:00 PM"), closeSoftKeyboard());
        onView(withId(R.id.etVenue)).perform(typeText("Madison Square Garden"), closeSoftKeyboard());
        onView(withId(R.id.etTickets)).perform(replaceText("five hundred"), closeSoftKeyboard());
        onView(withId(R.id.etPrice)).perform(typeText("75.00"), closeSoftKeyboard());

        onView(withId(R.id.btnPublish)).perform(click());

        onView(withId(R.id.tilTickets))
                .check(matches(TestUtils.hasTextInputLayoutError("Ticket count must be a whole number")));
    }

    @Test
    public void testPublishWithNonNumericPriceShowsError() {
        onView(withId(R.id.etEventName)).perform(typeText("Concert"), closeSoftKeyboard());

        selectMusicCategory();

        onView(withId(R.id.etDate)).perform(replaceText("12/12/2025"), closeSoftKeyboard());
        onView(withId(R.id.etTime)).perform(replaceText("8:00 PM"), closeSoftKeyboard());
        onView(withId(R.id.etVenue)).perform(typeText("Madison Square Garden"), closeSoftKeyboard());
        onView(withId(R.id.etTickets)).perform(typeText("500"), closeSoftKeyboard());
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
        onData(allOf(is(instanceOf(String.class)), is("Music"))).perform(click());
    }
}
