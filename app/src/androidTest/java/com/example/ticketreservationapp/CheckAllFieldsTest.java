package com.example.ticketreservationapp;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import android.content.Context;
import android.view.ContextThemeWrapper;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.google.android.material.textfield.TextInputEditText;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.atomic.AtomicBoolean;

@RunWith(AndroidJUnit4.class)
public class CheckAllFieldsTest {

    private checkAllFields validator;
    private TextInputEditText etName;
    private TextInputEditText etEmail;
    private TextInputEditText etPhone;
    private TextInputEditText etPassword;

    @Before
    public void setUp() {
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        Context themedContext = new ContextThemeWrapper(context, androidx.appcompat.R.style.Theme_AppCompat);
        validator = new checkAllFields();

        runOnMainSync(() -> {
            etName = new TextInputEditText(themedContext);
            etEmail = new TextInputEditText(themedContext);
            etPhone = new TextInputEditText(themedContext);
            etPassword = new TextInputEditText(themedContext);
        });
    }

    @Test
    public void returnsFalse_whenNameIsEmpty() {
        setText(etName, "");
        setText(etEmail, "a@b.com");
        setText(etPhone, "1234567890");
        setText(etPassword, "password123");

        boolean result = validateOnMainThread(R.id.btnEmail);

        assertFalse(result);
    }

    @Test
    public void returnsFalse_whenEmailModeAndEmailIsEmpty() {
        setText(etName, "John");
        setText(etEmail, "");
        setText(etPhone, "1234567890");
        setText(etPassword, "password123");

        boolean result = validateOnMainThread(R.id.btnEmail);

        assertFalse(result);
    }

    @Test
    public void returnsFalse_whenPhoneModeAndPhoneLengthIsInvalid() {
        setText(etName, "John");
        setText(etEmail, "a@b.com");
        setText(etPhone, "12345");
        setText(etPassword, "password123");

        boolean result = validateOnMainThread(R.id.btnPhone);

        assertFalse(result);
    }

    @Test
    public void returnsFalse_whenPasswordIsEmpty() {
        setText(etName, "John");
        setText(etEmail, "a@b.com");
        setText(etPhone, "1234567890");
        setText(etPassword, "");

        boolean result = validateOnMainThread(R.id.btnEmail);

        assertFalse(result);
    }

    @Test
    public void returnsFalse_whenPasswordShorterThanEight() {
        setText(etName, "John");
        setText(etEmail, "a@b.com");
        setText(etPhone, "1234567890");
        setText(etPassword, "1234567");

        boolean result = validateOnMainThread(R.id.btnEmail);

        assertFalse(result);
    }

    @Test
    public void returnsTrue_whenAllFieldsValidInEmailMode() {
        setText(etName, "John");
        setText(etEmail, "a@b.com");
        setText(etPhone, "1234567890");
        setText(etPassword, "password123");

        boolean result = validateOnMainThread(R.id.btnEmail);

        assertTrue(result);
    }

    @Test
    public void returnsTrue_whenAllFieldsValidInPhoneMode() {
        setText(etName, "John");
        setText(etEmail, "");
        setText(etPhone, "1234567890");
        setText(etPassword, "password123");

        boolean result = validateOnMainThread(R.id.btnPhone);

        assertTrue(result);
    }

    private void setText(TextInputEditText view, String value) {
        runOnMainSync(() -> view.setText(value));
    }

    private boolean validateOnMainThread(int check) {
        AtomicBoolean result = new AtomicBoolean(false);
        runOnMainSync(() -> result.set(validator.CheckAllFields(etName, etEmail, etPhone, etPassword, check)));
        return result.get();
    }

    private void runOnMainSync(Runnable action) {
        InstrumentationRegistry.getInstrumentation().runOnMainSync(action);
    }
}