package com.example.ticketreservationapp;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.assertFalse;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.concurrent.ExecutionException;

@RunWith(AndroidJUnit4.class)

public class readWriteTests {

    private FirebaseFirestore db;

    private readWrite rw;
    private User user;
    private User user2;
    private User admin;
    private User admin2;

    @Before
    public void setUp() {
        if (FirebaseApp.getApps(InstrumentationRegistry.getInstrumentation().getTargetContext()).isEmpty()) {
            FirebaseApp.initializeApp(InstrumentationRegistry.getInstrumentation().getTargetContext());
        }

        db = FirebaseFirestore.getInstance();

        db.useEmulator("10.0.2.2", 8080);

        FirebaseFirestoreSettings settings =
                new FirebaseFirestoreSettings.Builder()
                        .setPersistenceEnabled(false)
                        .build();
        db.setFirestoreSettings(settings);

        rw = new readWrite();

        user = new User("John Doe", "e@e.com", null, "12345678", false, null);
        user2 = new User("John Doe", null, "1234567890", "12345678", false, null);
        admin = new User("Jane Doe", "a@a.com", null, "12345678", true, null);
        admin2 = new User("Jane Doe", null, "1234567890", "12345678", true, null);

    }

    @Test
    public void registerUserEmail() throws ExecutionException, InterruptedException {
        rw.registerUser(user);
        QuerySnapshot snapshot = Tasks.await(db.collection("users").whereEqualTo("email", user.getEmail()).whereEqualTo("password", user.getPassword()).get());

        assertFalse(snapshot.isEmpty());

    }

    @Test
    public void registerUserPhone() throws ExecutionException, InterruptedException {
        rw.registerUser(user2);
        QuerySnapshot snapshot = Tasks.await(db.collection("users").whereEqualTo("phone", user.getEmail()).whereEqualTo("password", user.getPassword()).get());

        assertFalse(snapshot.isEmpty());

    }

    @Test
    public void registerAdminEmail() throws ExecutionException, InterruptedException {
        rw.registerUser(admin);
        QuerySnapshot snapshot = Tasks.await(db.collection("users").whereEqualTo("email", user.getEmail()).whereEqualTo("password", user.getPassword()).get());

        assertFalse(snapshot.isEmpty());

    }

    @Test
    public void registerAdminPhone() throws ExecutionException, InterruptedException {
        rw.registerUser(admin2);
        QuerySnapshot snapshot = Tasks.await(db.collection("users").whereEqualTo("email", user.getEmail()).whereEqualTo("password", user.getPassword()).get());

        assertFalse(snapshot.isEmpty());

    }

    @Test
    public void signInUserEmail() {

        db.collection("users").add(user);
        rw.signIn(user.getEmail(), user.getPhone(), user.getPassword(), Assert::assertNotNull);

    }

    @Test
    public void signInUserPhone() {

        db.collection("users").add(user2);
        rw.signIn(user2.getEmail(), user2.getPhone(), user2.getPassword(), Assert::assertNotNull);
    }

    @Test
    public void signInAdminEmail() {

        db.collection("users").add(admin);
        rw.signIn(admin.getEmail(), admin.getPhone(), admin.getPassword(), Assert::assertNotNull);
    }

    @Test
    public void signInAdminPhone() {

        db.collection("users").add(admin2);
        rw.signIn(admin2.getEmail(), admin2.getPhone(), admin2.getPassword(), Assert::assertNotNull);
    }

    @Test
    public void incorrectSignIn() {
        db.collection("users").add(user);
        rw.signIn(user2.getEmail(), user2.getPhone(), user2.getPassword(), Assert::assertNull);
    }





}
