package com.example.ticketreservationapp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import android.content.Context;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RunWith(AndroidJUnit4.class)
public class NotificationServiceTests {

    private FirebaseFirestore db;
    private NotificationService service;

    @Before
    public void setUp() throws Exception {
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();

        if (FirebaseApp.getApps(context).isEmpty()) {
            FirebaseApp.initializeApp(context);
        }

        db = FirebaseFirestore.getInstance();
        try{
            db.useEmulator("10.0.2.2", 8080);
        }
        catch (Exception ignored) {

        }

        try {
            FirebaseFirestoreSettings settings =
                    new FirebaseFirestoreSettings.Builder()
                            .setPersistenceEnabled(false)
                            .build();
            db.setFirestoreSettings(settings);
        } catch (IllegalStateException ignored) {

        }

        service = new NotificationService();
    }

    @After
    public void tearDown() throws Exception {
        if (db != null) {
            Tasks.await(db.terminate());
        }
    }

    @Test
    public void queueEmail_writesCorrectDocument() throws Exception {
        DocumentReference ref = Tasks.await(service.queueEmail(
                "test@gmail.com",
                "Confirmation: Concert",
                "Your event \"Concert\" has been confirmed.",
                "<p>Your event <strong>Concert</strong> has been confirmed.</p>"
        ));

        DocumentSnapshot snap = Tasks.await(ref.get());

        assertTrue(snap.exists());

        List<String> to = (List<String>) snap.get("to");
        Map<String, Object> message = (Map<String, Object>) snap.get("message");

        assertNotNull(to);
        assertEquals("test@gmail.com", to.get(0));
        assertEquals("Confirmation: Concert", message.get("subject"));
        assertEquals("Your event \"Concert\" has been confirmed.", message.get("text"));
        assertEquals("<p>Your event <strong>Concert</strong> has been confirmed.</p>", message.get("html"));
        assertEquals("confirmation", snap.getString("type"));
    }

    @Test
    public void queueSms_writesCorrectDocument() throws Exception {
        DocumentReference ref = Tasks.await(service.queueSms(
                "5141112222",
                "Your event \"Concert\" has been confirmed."
        ));

        DocumentSnapshot snap = Tasks.await(ref.get());

        assertTrue(snap.exists());
        assertEquals("5141112222", snap.getString("to"));
        assertEquals("Your event \"Concert\" has been confirmed.", snap.getString("body"));
        assertEquals("confirmation", snap.getString("type"));
    }

    @Test
    public void sendConfirmation_withEmailAndPhone_callsBoth() throws Exception {
        FakeNotificationService service = new FakeNotificationService();

        Tasks.await(service.sendConfirmation(
                "test@gmail.com",
                "5141112222",
                "Concert",
                false
        ));

        assertEquals(1, service.emailCalls.size());
        assertEquals(1, service.smsCalls.size());

        EmailCall email = service.emailCalls.get(0);
        SmsCall sms = service.smsCalls.get(0);

        assertEquals("test@gmail.com", email.to);
        assertEquals("Confirmation: Concert", email.subject);
        assertEquals("Your event \"Concert\" has been confirmed.", email.textBody);
        assertEquals("<p>Your event <strong>Concert</strong> has been confirmed.</p>", email.htmlBody);

        assertEquals("5141112222", sms.phone);
        assertEquals("Your event \"Concert\" has been confirmed.", sms.body);
    }

    @Test
    public void sendConfirmation_withOnlyEmail_callsOnlyEmail() throws Exception {
        FakeNotificationService service = new FakeNotificationService();

        Tasks.await(service.sendConfirmation(
                "test@gmail.com",
                null,
                "Concert",
                false
        ));

        assertEquals(1, service.emailCalls.size());
        assertEquals(0, service.smsCalls.size());
    }

    @Test
    public void sendConfirmation_withOnlyPhone_callsOnlySms() throws Exception {
        FakeNotificationService service = new FakeNotificationService();

        Tasks.await(service.sendConfirmation(
                null,
                "5141112222",
                "Concert",
                false
        ));

        assertEquals(0, service.emailCalls.size());
        assertEquals(1, service.smsCalls.size());
    }

    @Test
    public void sendConfirmation_withCancelledEvent_usesCancelledHtml() throws Exception {
        FakeNotificationService service = new FakeNotificationService();

        Tasks.await(service.sendConfirmation(
                "test@gmail.com",
                "5141112222",
                "Concert",
                true
        ));

        EmailCall email = service.emailCalls.get(0);
        assertEquals("<p>Your event <strong>Concert</strong> has been cancelled.</p>", email.htmlBody);
    }

    static class FakeNotificationService extends NotificationService {
        List<EmailCall> emailCalls = new ArrayList<>();
        List<SmsCall> smsCalls = new ArrayList<>();

        FakeNotificationService() {
            super();
        }

        @Override
        public Task<DocumentReference> queueEmail(String toEmail, String subject, String textBody, String htmlBody) {
            emailCalls.add(new EmailCall(toEmail, subject, textBody, htmlBody));
            return Tasks.forResult(null);
        }

        @Override
        public Task<DocumentReference> queueSms(String phoneNumber, String messageBody) {
            smsCalls.add(new SmsCall(phoneNumber, messageBody));
            return Tasks.forResult(null);
        }
    }

    static class EmailCall {
        String to;
        String subject;
        String textBody;
        String htmlBody;

        EmailCall(String to, String subject, String textBody, String htmlBody) {
            this.to = to;
            this.subject = subject;
            this.textBody = textBody;
            this.htmlBody = htmlBody;
        }
    }

    static class SmsCall {
        String phone;
        String body;

        SmsCall(String phone, String body) {
            this.phone = phone;
            this.body = body;
        }
}}
