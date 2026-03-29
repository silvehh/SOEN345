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

import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

    private FirebaseFirestore db;
    @Rule
    public ActivityScenarioRule<MainActivity> activityRule =
            new ActivityScenarioRule<>(MainActivity.class);

    @Before
    public void setUp() {
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

        Intents.init();
    }

    @After
    public void tearDown() {
        Intents.release();
    }

    @Test
    public void testEmailVisibility() {
        onView(withId(R.id.btnEmail)).perform(click());
        onView(withId(R.id.etEmail)).check(matches(isDisplayed()));
    }

    @Test
    public void testPhoneVisibility() {
        onView(withId(R.id.btnPhone)).perform(click());
        onView(withId(R.id.etPhone)).check(matches(isDisplayed()));
    }

    @Test
    public void testLoginView() {
        onView(withId(R.id.btnLogin)).perform(click());
        intended(hasComponent(LoginActivity.class.getName()));
    }

    @Test
    public void testRegisterButtonDisplayedAndClickable() {
        onView(withId(R.id.btnRegister)).perform(scrollTo());
        onView(withId(R.id.btnRegister)).check(matches(isDisplayed()));
        onView(withId(R.id.btnRegister)).perform(click());
    }

    @Test
    public void testRegisterButtonClickAfterTypingWithNonExistentUser() {
        onView(withId(R.id.etName))
                .perform(scrollTo(), typeText("john"), closeSoftKeyboard());
        onView(withId(R.id.etEmail))
                .perform(scrollTo(), typeText("test@test.com"), closeSoftKeyboard());

        onView(withId(R.id.etPassword))
                .perform(scrollTo(), typeText("123456789"), closeSoftKeyboard());

        onView(withId(R.id.btnRegister))
                .perform(scrollTo(), click());
    }
}
