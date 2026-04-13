package com.example.ticketreservationapp;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

@RunWith(AndroidJUnit4.class)
public class readWriteTests {

    private FirebaseFirestore db;
    private readWrite rw;
    private User user;
    private User user2;
    private User admin;
    private User admin2;
    private List<String> userIdsToDelete;

    @Before
    public void setUp() {
        if (FirebaseApp.getApps(InstrumentationRegistry.getInstrumentation().getTargetContext()).isEmpty()) {
            FirebaseApp.initializeApp(InstrumentationRegistry.getInstrumentation().getTargetContext());
        }

        db = FirebaseFirestore.getInstance();

        try {
            db.useEmulator("10.0.2.2", 8080);
        } catch (IllegalStateException ignored) {
        }

        try {
            FirebaseFirestoreSettings settings =
                    new FirebaseFirestoreSettings.Builder()
                            .setPersistenceEnabled(false)
                            .build();
            db.setFirestoreSettings(settings);
        } catch (IllegalStateException ignored) {
        }

        rw = new readWrite(db);
        userIdsToDelete = new ArrayList<>();

        user = new User("John Doe", "e@e.com", null, "12345678", false, null);
        user2 = new User("John Doe", null, "1234567890", "12345678", false, null);
        admin = new User("Jane Doe", "a@a.com", null, "12345678", true, null);
        admin2 = new User("Jane Doe", null, "0987654321", "12345678", true, null);
    }

    @After
    public void tearDown() {
        for (String id : userIdsToDelete) {
            try {
                Tasks.await(rw.deleteUser(id), 5, TimeUnit.SECONDS);
            } catch (Exception e) {
                Log.e("readWriteTests", "Failed to delete user: " + id, e);
            }
        }
    }

    private User syncSignIn(String email, String phone, String password) throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<User> result = new AtomicReference<>();
        rw.signIn(email, phone, password, u -> {
            result.set(u);
            latch.countDown();
        });
        if (!latch.await(10, TimeUnit.SECONDS)) {
            throw new RuntimeException("SignIn timed out");
        }
        return result.get();
    }

    @Test
    public void registerAndSignInUserEmail() throws Exception {
        DocumentReference ref = Tasks.await(rw.registerUser(user), 10, TimeUnit.SECONDS);
        userIdsToDelete.add(ref.getId());

        User newUser = syncSignIn(user.getEmail(), user.getPhone(), user.getPassword());
        assertNotNull(newUser);
        assertEquals(user.getEmail(), newUser.getEmail());
        assertFalse(newUser.getAdmin());
    }

    @Test
    public void registerAndSignInUserPhone() throws Exception {
        DocumentReference ref = Tasks.await(rw.registerUser(user2), 10, TimeUnit.SECONDS);
        userIdsToDelete.add(ref.getId());

        User newUser = syncSignIn(user2.getEmail(), user2.getPhone(), user2.getPassword());
        assertNotNull(newUser);
        assertEquals(user2.getPhone(), newUser.getPhone());
        assertFalse(newUser.getAdmin());
    }

    @Test
    public void registerAndSignInAdminEmail() throws Exception {
        DocumentReference ref = Tasks.await(rw.registerUser(admin), 10, TimeUnit.SECONDS);
        userIdsToDelete.add(ref.getId());

        User newUser = syncSignIn(admin.getEmail(), admin.getPhone(), admin.getPassword());
        assertNotNull(newUser);
        assertEquals(admin.getEmail(), newUser.getEmail());
        assertTrue(newUser.getAdmin());
    }

    @Test
    public void registerAndSignInAdminPhone() throws Exception {
        DocumentReference ref = Tasks.await(rw.registerUser(admin2), 10, TimeUnit.SECONDS);
        userIdsToDelete.add(ref.getId());

        User newUser = syncSignIn(admin2.getEmail(), admin2.getPhone(), admin2.getPassword());
        assertNotNull(newUser);
        assertEquals(admin2.getPhone(), newUser.getPhone());
        assertTrue(newUser.getAdmin());
    }

    @Test
    public void incorrectSignIn() throws Exception {
        DocumentReference ref = Tasks.await(db.collection("users").add(user), 10, TimeUnit.SECONDS);
        userIdsToDelete.add(ref.getId());

        String invalidPassword = "invalid-" + System.currentTimeMillis();
        User result = syncSignIn(user.getEmail(), null, invalidPassword);
        assertNull(result);
    }

    @Test
    public void testAddEventPersistence() throws Exception {
        Event event = new Event("Test Event", "Music", "12/12/2025", "8:00 PM", "Test Venue", 100, 50.0);

        DocumentReference ref = Tasks.await(rw.addEvent(event), 10, TimeUnit.SECONDS);
        String createdEventId = ref.getId();
        assertNotNull(createdEventId);

        QuerySnapshot querySnapshot = Tasks.await(
                db.collection("events").whereEqualTo("eventName", "Test Event").get(),
                10, TimeUnit.SECONDS);

        assertFalse(querySnapshot.isEmpty());
        DocumentSnapshot doc = querySnapshot.getDocuments().get(0);
        assertEquals("Test Event", doc.getString("eventName"));

        Tasks.await(rw.deleteEvent(createdEventId), 10, TimeUnit.SECONDS);

        QuerySnapshot afterDelete = Tasks.await(
                db.collection("events").whereEqualTo("eventName", "Test Event").get(),
                10, TimeUnit.SECONDS);
        assertTrue(afterDelete.isEmpty());
    }

    @Test
    public void testDeleteEvent() throws Exception {
        Event event = new Event("Event to Delete", "Sports", "11/11/2025", "6:00 PM", "Stadium", 200, 40.0);

        DocumentReference ref = Tasks.await(rw.addEvent(event), 10, TimeUnit.SECONDS);
        String id = ref.getId();
        assertNotNull(id);

        Tasks.await(rw.deleteEvent(id), 10, TimeUnit.SECONDS);

        DocumentSnapshot doc = Tasks.await(
                db.collection("events").document(id).get(),
                10, TimeUnit.SECONDS);
        assertFalse(doc.exists());
    }

    @Test
    public void isReservedTest() {
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

        assertTrue(rw.isReserved(event, user));
    }

    @Test
    public void reservationTest() throws ExecutionException, InterruptedException {
        String eventId = "event1";

        Map<String, Object> userMap = new HashMap<>();
        userMap.put("email", "test@gmail.com");
        userMap.put("phone", "");
        userMap.put("password", "123456");
        userMap.put("events", new ArrayList<String>());

        Map<String, Object> eventMap = new HashMap<>();
        eventMap.put("tickets", 10);

        Tasks.await(db.collection("users").add(userMap));
        Tasks.await(db.collection("events").document(eventId).set(eventMap));

        User user = new User();
        user.setEmail("test@gmail.com");
        user.setPhone("");
        user.setPassword("123456");

        Event event = new Event();
        event.setId(eventId);
        event.setTickets(10);

        Tasks.await(rw.reserveEvent(event, user));

        QuerySnapshot userSnap = Tasks.await(
                db.collection("users")
                        .whereEqualTo("email", "test@gmail.com")
                        .whereEqualTo("password", "123456")
                        .get()
        );

        DocumentSnapshot userDoc = userSnap.getDocuments().get(0);
        List<String> events = (List<String>) userDoc.get("events");

        assertNotNull(events);
        assertTrue(events.contains(eventId));

        DocumentSnapshot eventDoc = Tasks.await(
                db.collection("events").document(eventId).get()
        );

        Long tickets = eventDoc.getLong("tickets");
        assertNotNull(tickets);
        assertEquals(9L, tickets.longValue());
    }

    @Test
    public void cancellationTest() throws ExecutionException, InterruptedException {
        String eventId = "cancelEmailEvent";

        ArrayList<String> userEvents = new ArrayList<>();
        userEvents.add(eventId);
        userEvents.add("otherEvent");

        Map<String, Object> userMap = new HashMap<>();
        userMap.put("email", "emailuser@gmail.com");
        userMap.put("phone", "");
        userMap.put("password", "123456");
        userMap.put("events", userEvents);

        Map<String, Object> eventMap = new HashMap<>();
        eventMap.put("tickets", 4);

        Tasks.await(db.collection("users").add(userMap));
        Tasks.await(db.collection("events").document(eventId).set(eventMap));

        User user = new User();
        user.setEmail("emailuser@gmail.com");
        user.setPhone("");
        user.setPassword("123456");

        Event event = new Event();
        event.setId(eventId);

        Tasks.await(rw.cancelEvent(event, user));

        QuerySnapshot userSnap = Tasks.await(
                db.collection("users")
                        .whereEqualTo("email", "emailuser@gmail.com")
                        .whereEqualTo("password", "123456")
                        .get()
        );

        assertFalse(userSnap.isEmpty());

        DocumentSnapshot userDoc = userSnap.getDocuments().get(0);
        List<String> events = (List<String>) userDoc.get("events");

        assertNotNull(events);
        assertFalse(events.contains(eventId));
        assertTrue(events.contains("otherEvent"));

        DocumentSnapshot eventDoc = Tasks.await(
                db.collection("events").document(eventId).get()
        );

        Long tickets = eventDoc.getLong("tickets");
        assertNotNull(tickets);
        assertEquals(5L, tickets.longValue());
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