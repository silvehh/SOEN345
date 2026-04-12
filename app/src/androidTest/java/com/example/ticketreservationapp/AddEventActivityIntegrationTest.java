package com.example.ticketreservationapp;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.matcher.RootMatchers.isPlatformPopup;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import android.content.Context;
import android.content.Intent;

import androidx.lifecycle.Lifecycle;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.lang.reflect.Field;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

@RunWith(AndroidJUnit4.class)
public class AddEventActivityIntegrationTest {

    @Test
    public void publishInAddMode_callsAddEventWithFormValues() throws Exception {
        RecordingReadWrite fakeReadWrite = new RecordingReadWrite();

        try (ActivityScenario<AddEventActivity> scenario = ActivityScenario.launch(AddEventActivity.class)) {
            scenario.onActivity(activity -> injectReadWrite(activity, fakeReadWrite));

            fillRequiredFields(
                    "Integration Concert",
                    "12/31/2026",
                    "9:30 PM",
                    "Bell Centre",
                    "450",
                    "99.95"
            );

            onView(withId(R.id.btnPublish)).perform(click());

            waitForCondition(() -> fakeReadWrite.getAddCallCount() == 1, 5000,
                    "Timed out waiting for add flow to call addEvent()");

            Event addedEvent = fakeReadWrite.getAddedEvent();
            assertEquals("Integration Concert", addedEvent.getEventName());
            assertEquals("Music", addedEvent.getCategory());
            assertEquals("12/31/2026", addedEvent.getDate());
            assertEquals("9:30 PM", addedEvent.getTime());
            assertEquals("Bell Centre", addedEvent.getVenue());
            assertEquals(450, addedEvent.getTickets());
            assertEquals(99.95, addedEvent.getPrice(), 0.001);

            waitForCondition(() -> scenario.getState() == Lifecycle.State.DESTROYED, 5000,
                    "Timed out waiting for AddEventActivity to finish after publish");
        }
    }

    @Test
    public void publishInEditMode_callsUpdateEventWithExistingId() throws Exception {
        Event originalEvent = new Event("Original Concert", "Music", "01/15/2026", "7:00 PM", "Original Venue", 100, 40.0);
        originalEvent.setId("edit-event-123");

        RecordingReadWrite fakeReadWrite = new RecordingReadWrite();

        try (ActivityScenario<AddEventActivity> scenario = ActivityScenario.launch(getEditIntent(originalEvent))) {
            scenario.onActivity(activity -> injectReadWrite(activity, fakeReadWrite));

            onView(withId(R.id.etEventName)).perform(replaceText("Updated Concert"), closeSoftKeyboard());
            onView(withId(R.id.etVenue)).perform(replaceText("Updated Venue"), closeSoftKeyboard());
            onView(withId(R.id.etTickets)).perform(replaceText("275"), closeSoftKeyboard());
            onView(withId(R.id.etPrice)).perform(replaceText("120.50"), closeSoftKeyboard());

            onView(withId(R.id.btnPublish)).perform(click());

            waitForCondition(() -> fakeReadWrite.getUpdateCallCount() == 1, 5000,
                    "Timed out waiting for edit flow to call updateEvent()");

            Event updatedEvent = fakeReadWrite.getUpdatedEvent();
            assertEquals("edit-event-123", updatedEvent.getId());
            assertEquals("Updated Concert", updatedEvent.getEventName());
            assertEquals("Music", updatedEvent.getCategory());
            assertEquals("01/15/2026", updatedEvent.getDate());
            assertEquals("7:00 PM", updatedEvent.getTime());
            assertEquals("Updated Venue", updatedEvent.getVenue());
            assertEquals(275, updatedEvent.getTickets());
            assertEquals(120.50, updatedEvent.getPrice(), 0.001);

            waitForCondition(() -> scenario.getState() == Lifecycle.State.DESTROYED, 5000,
                    "Timed out waiting for AddEventActivity to finish after update");
        }
    }

    private Intent getEditIntent(Event event) {
        Context context = ApplicationProvider.getApplicationContext();
        Intent intent = new Intent(context, AddEventActivity.class);
        intent.putExtra("edit_event", event);
        return intent;
    }

    private void fillRequiredFields(
            String eventName,
            String date,
            String time,
            String venue,
            String tickets,
            String price
    ) {
        onView(withId(R.id.etEventName)).perform(replaceText(eventName), closeSoftKeyboard());
        selectMusicCategory();
        onView(withId(R.id.etDate)).perform(replaceText(date), closeSoftKeyboard());
        onView(withId(R.id.etTime)).perform(replaceText(time), closeSoftKeyboard());
        onView(withId(R.id.etVenue)).perform(replaceText(venue), closeSoftKeyboard());
        onView(withId(R.id.etTickets)).perform(replaceText(tickets), closeSoftKeyboard());
        onView(withId(R.id.etPrice)).perform(replaceText(price), closeSoftKeyboard());
    }

    private void selectMusicCategory() {
        onView(withId(R.id.spinnerCategory)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is("Music")))
                .inRoot(isPlatformPopup())
                .perform(click());
    }

    private void injectReadWrite(AddEventActivity activity, readWrite replacement) {
        try {
            Field readWriteField = AddEventActivity.class.getDeclaredField("rw");
            readWriteField.setAccessible(true);
            readWriteField.set(activity, replacement);
        } catch (ReflectiveOperationException e) {
            throw new AssertionError("Unable to replace AddEventActivity readWrite dependency", e);
        }
    }

    private void waitForCondition(Condition condition, long timeout, String timeoutMessage) throws Exception {
        long endTime = System.currentTimeMillis() + timeout;
        while (System.currentTimeMillis() < endTime) {
            if (condition.isSatisfied()) {
                return;
            }
            Thread.sleep(100);
        }
        fail(timeoutMessage);
    }

    private interface Condition {
        boolean isSatisfied();
    }

    private static class RecordingReadWrite extends readWrite {
        private final AtomicReference<Event> addedEvent = new AtomicReference<>();
        private final AtomicReference<Event> updatedEvent = new AtomicReference<>();
        private final AtomicInteger addCallCount = new AtomicInteger();
        private final AtomicInteger updateCallCount = new AtomicInteger();

        RecordingReadWrite() {
            super(FirebaseFirestore.getInstance());
        }

        @Override
        public Task<DocumentReference> addEvent(Event event) {
            addedEvent.set(copyEvent(event));
            addCallCount.incrementAndGet();
            DocumentReference reference = FirebaseFirestore.getInstance()
                    .collection("events")
                    .document("fake-event-id");
            return Tasks.forResult(reference);
        }

        @Override
        public Task<Void> updateEvent(Event event) {
            updatedEvent.set(copyEvent(event));
            updateCallCount.incrementAndGet();
            return Tasks.forResult(null);
        }

        Event getAddedEvent() {
            return addedEvent.get();
        }

        Event getUpdatedEvent() {
            return updatedEvent.get();
        }

        int getAddCallCount() {
            return addCallCount.get();
        }

        int getUpdateCallCount() {
            return updateCallCount.get();
        }

        private Event copyEvent(Event source) {
            Event copy = new Event(
                    source.getEventName(),
                    source.getCategory(),
                    source.getDate(),
                    source.getTime(),
                    source.getVenue(),
                    source.getTickets(),
                    source.getPrice()
            );
            copy.setId(source.getId());
            return copy;
        }
    }
}
