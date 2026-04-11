package com.example.ticketreservationapp;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;


import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.intent.Intents.intending;
import static androidx.test.espresso.intent.matcher.IntentMatchers.isInternal;
import static org.hamcrest.Matchers.not;
import android.app.Activity;
import android.app.Instrumentation;

import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

import java.util.HashMap;
import java.util.Map;


@RunWith(AndroidJUnit4.class)
public class LoginActivityTest {
    private FirebaseFirestore db;

    @Rule
    public ActivityScenarioRule<LoginActivity> activityRule =
            new ActivityScenarioRule<>(LoginActivity.class);

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

        Map<String, Object> user = new HashMap<>();
        user.put("name", "Test Admin");
        user.put("email", "test@a.com");
        user.put("phone", null);
        user.put("password", "12345678");
        user.put("admin", true);

        Map<String, Object> user2 = new HashMap<>();
        user2.put("name", "Test User");
        user2.put("email", "user@a.com");
        user2.put("phone", null);
        user2.put("password", "12345678");
        user2.put("admin", false);

        db.collection("users")
                .document()
                .set(user);
        db.collection("users")
                .document()
                .set(user2);
    }

    @After
    public void tearDown() {
        Intents.release();
    }

//    @Test
//    public void testEmailVisibility() {
//        onView(withId(R.id.btnEmail)).perform(click());
//        onView(withId(R.id.etEmail)).perform(scrollTo());
//        onView(withId(R.id.etEmail)).check(matches(isDisplayed()));
//    }
//
//    @Test
//    public void testPhoneVisibility() {
//        onView(withId(R.id.btnPhone)).perform(click());
//        onView(withId(R.id.etPhone)).check(matches(isDisplayed()));
//    }


    @Test
    public void testRegisterView() {
        onView(withId(R.id.btnRegister)).perform(scrollTo(), click());
        intended(hasComponent(MainActivity.class.getName()));
    }

    @Test
    public void testLoginButtonDisplayedAndClickable() {
        onView(withId(R.id.btnLogin)).perform(scrollTo());
        onView(withId(R.id.btnLogin)).check(matches(isDisplayed()));

    }

    @Test
    public void testLoginButtonClickAfterTypingWithNonExistentUser() {
        onView(withId(R.id.etEmail))
                .perform(scrollTo(), typeText("test3@test3.com"), closeSoftKeyboard());

        onView(withId(R.id.etPassword))
                .perform(scrollTo(), typeText("123456789"), closeSoftKeyboard());

        onView(withId(R.id.btnLogin))
                .perform(scrollTo(), click());
    }

    @Test
    public void testLoginButtonClickAfterTypingWithInvalidUser() {
        onView(withId(R.id.etEmail))
                .perform(scrollTo(), typeText("test@a.com"), closeSoftKeyboard());

        onView(withId(R.id.etPassword))
                .perform(scrollTo(), typeText("12345678"), closeSoftKeyboard());

        onView(withId(R.id.btnLogin))
                .perform(scrollTo(), click());

    }

    @Test
    public void testLoginButtonClickAfterTypingWithRealUser() {
        onView(withId(R.id.etEmail))
                .perform(scrollTo(), typeText("user@a.com"), closeSoftKeyboard());

        onView(withId(R.id.etPassword))
                .perform(scrollTo(), typeText("12345678"), closeSoftKeyboard());

        onView(withId(R.id.btnLogin))
                .perform(scrollTo(), click());
    }


}
