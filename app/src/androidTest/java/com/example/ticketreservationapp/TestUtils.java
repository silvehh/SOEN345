package com.example.ticketreservationapp;

import android.content.Context;
import android.view.View;

import androidx.test.platform.app.InstrumentationRegistry;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

public class TestUtils {

    private static final String FIRESTORE_EMULATOR_HOST = "10.0.2.2";
    private static final int FIRESTORE_EMULATOR_PORT = 8080;

    public static FirebaseFirestore getTestFirestore() {
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        if (FirebaseApp.getApps(context).isEmpty()) {
            FirebaseApp.initializeApp(context);
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        try {
            db.useEmulator(FIRESTORE_EMULATOR_HOST, FIRESTORE_EMULATOR_PORT);
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
        return db;
    }

    public static Matcher<View> hasTextInputLayoutError(String expectedError) {
        return new TypeSafeMatcher<View>() {
            @Override
            protected boolean matchesSafely(View view) {
                if (!(view instanceof TextInputLayout)) return false;
                CharSequence error = ((TextInputLayout) view).getError();
                if (error == null) return false;
                return expectedError.equals(error.toString());
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("TextInputLayout with error: " + expectedError);
            }
        };
    }
}
