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
import static org.junit.Assert.assertTrue;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.tasks.Tasks;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicReference;

@RunWith(AndroidJUnit4.class)

public class readWriteTests {

    private FirebaseFirestore db;

    private readWrite rw;
    private User user;
    private User user2;
    private User admin;
    private User admin2;

    @Before
    public void setUp(){
        if (FirebaseApp.getApps(InstrumentationRegistry.getInstrumentation().getTargetContext()).isEmpty()) {
            FirebaseApp.initializeApp(InstrumentationRegistry.getInstrumentation().getTargetContext());
        }


        db = FirebaseFirestore.getInstance();

        try{
            db.useEmulator("10.0.2.2", 8080);
        }
        catch (Exception e) {

        }


        FirebaseFirestoreSettings settings =
                new FirebaseFirestoreSettings.Builder()
                        .setPersistenceEnabled(false)
                        .build();
        db.setFirestoreSettings(settings);

        rw = new readWrite(db);

        user = new User("John Doe", "e@e.com", null, "12345678", false, null);
        user2 = new User("John Doe", null, "1234567890", "12345678", false, null);
        admin = new User("Jane Doe", "a@a.com", null, "12345678", true, null);
        admin2 = new User("Jane Doe", null, "0987654321", "12345678", true, null);

    }


    @Test
    public void registerAndSignInUserEmail() {
        rw.registerUser(user);
        rw.signIn(user.getEmail(), user.getPhone(), user.getPassword(), newUser -> {
            assertNotNull(user);
            assertEquals(user.getEmail(), newUser.getEmail());
            assertFalse(newUser.getAdmin());
        });

    }

    @Test
    public void registerAndSignInUserPhone() {
        rw.registerUser(user2);
        rw.signIn(user2.getEmail(), user2.getPhone(), user2.getPassword(), newUser -> {
            assertNotNull(newUser);
            assertEquals(user2.getPhone(), newUser.getPhone());
            assertFalse(newUser.getAdmin());
        });
    }

    @Test
    public void registerAndSignInAdminEmail() {
        rw.registerUser(admin);
        rw.signIn(admin.getEmail(), admin.getPhone(), admin.getPassword(), newUser -> {
            assertNotNull(newUser);
            assertEquals(admin.getEmail(), newUser.getEmail());
            assertTrue(newUser.getAdmin());
        });

    }

    @Test
    public void registerAndSignInAdminPhone() {
        rw.registerUser(admin2);
        rw.signIn(admin2.getEmail(), admin2.getPhone(), admin2.getPassword(), newUser -> {
            assertNotNull(newUser);
            assertEquals(admin.getPhone(), newUser.getPhone());
            assertTrue(newUser.getAdmin());
        });

    }

    @Test
    public void incorrectSignIn() {
        db.collection("users").add(user);
        rw.signIn(user2.getEmail(), user2.getPhone(), user2.getPassword(), Assert::assertNull);
    }





}
