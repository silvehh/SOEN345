package com.example.ticketreservationapp;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import android.content.Context;
import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.lifecycle.Lifecycle;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.firebase.firestore.FirebaseFirestore;

import java.lang.reflect.Field;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

@RunWith(AndroidJUnit4.class)
public class AddEventEditModeTest {

    @BeforeClass
    public static void configureFirestore() {
        TestUtils.getTestFirestore();
    }

    private Intent getEditIntent(Event event) {
        Context context = ApplicationProvider.getApplicationContext();
        Intent intent = new Intent(context, AddEventActivity.class);
        intent.putExtra("edit_event", event);
        return intent;
    }

    @Test
    public void testCancelButtonVisibleInEditMode() {
        Event event = new Event("Original Name", "Music", "01/01/2025", "10:00 AM", "Original Venue", 100, 20.0);
        event.setId("test_event_id");
        try (ActivityScenario<AddEventActivity> scenario = ActivityScenario.launch(getEditIntent(event))) {
            onView(withId(R.id.btnCancelEvent)).perform(scrollTo());
            onView(withId(R.id.btnCancelEvent)).check(matches(isDisplayed()));
            onView(withId(R.id.tvTitle)).check(matches(withText("Edit Event")));
            onView(withId(R.id.btnPublish)).perform(scrollTo());
            onView(withId(R.id.btnPublish)).check(matches(withText("Update Event")));
        }
    }

    @Test
    public void testCancelButtonNotVisibleInAddMode() {
        try (ActivityScenario<AddEventActivity> scenario = ActivityScenario.launch(AddEventActivity.class)) {
            onView(withId(R.id.btnCancelEvent)).check(matches(not(isDisplayed())));
            onView(withId(R.id.tvTitle)).check(matches(withText("Add Event")));
        }
    }
    
    @Test
    public void testCancelButtonClickFinishesActivityAndDeletesEvent() throws Exception {
        Event event = new Event("Cancel Event " + System.currentTimeMillis(), "Music", "01/01/2025", "10:00 AM", "Original Venue", 100, 20.0);
        event.setId("cancel-event-" + System.currentTimeMillis());
        RecordingReadWrite fakeReadWrite = new RecordingReadWrite();

        try (ActivityScenario<AddEventActivity> scenario = ActivityScenario.launch(getEditIntent(event))) {
            scenario.onActivity(activity -> injectReadWrite(activity, fakeReadWrite));
            onView(withId(R.id.btnCancelEvent)).perform(scrollTo(), click());

            waitForCondition(() -> fakeReadWrite.getDeleteCallCount() == 1, 5000,
                    "Timed out waiting for cancel flow to call deleteEvent()");
            assertEquals(event.getId(), fakeReadWrite.getDeletedEventId());

            waitForCondition(() -> scenario.getState() == Lifecycle.State.DESTROYED, 5000,
                    "Timed out waiting for AddEventActivity to finish after cancel");
        }
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
        private final AtomicReference<String> deletedEventId = new AtomicReference<>();
        private final AtomicInteger deleteCallCount = new AtomicInteger();

        RecordingReadWrite() {
            super(FirebaseFirestore.getInstance());
        }

        @Override
        public Task<Void> deleteEvent(String eventId) {
            deletedEventId.set(eventId);
            deleteCallCount.incrementAndGet();
            return Tasks.forResult(null);
        }

        String getDeletedEventId() {
            return deletedEventId.get();
        }

        int getDeleteCallCount() {
            return deleteCallCount.get();
        }
    }
}
