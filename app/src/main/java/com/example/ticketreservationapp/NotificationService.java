package com.example.ticketreservationapp;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NotificationService {

    private final FirebaseFirestore db;

    public NotificationService() {
        this.db = FirebaseFirestore.getInstance();
    }

    public Task<List<Task<?>>> sendConfirmation(String email, String phone, String eventName, boolean isReserved) {
        List<Task<?>> tasks = new ArrayList<>();

        if (email != null && !email.trim().isEmpty()) {
            tasks.add(queueEmail(
                    email.trim(),
                    "Confirmation: " + eventName,
                    "Your event \"" + eventName + "\" has been confirmed.",
                    "<p>Your event <strong>" + escapeHtml(eventName) + "</strong> has" +  (isReserved ? "been cancelled" : "been confirmed") +".</p>"
            ));
        }

        if (phone != null && !phone.trim().isEmpty()) {
            tasks.add(queueSms(
                    phone.trim(),
                    "Your event \"" + eventName + "\" has been confirmed."
            ));
        }

        return Tasks.whenAllComplete(tasks).continueWith(task -> tasks);
    }

    public Task<DocumentReference> queueEmail(String toEmail,
                                              String subject,
                                              String textBody,
                                              String htmlBody) {

        Map<String, Object> emailDoc = new HashMap<>();

        List<String> recipients = new ArrayList<>();
        recipients.add(toEmail);
        emailDoc.put("to", recipients);

        Map<String, Object> message = new HashMap<>();
        message.put("subject", subject);
        message.put("text", textBody);
        message.put("html", htmlBody);

        emailDoc.put("message", message);
        emailDoc.put("createdAt", System.currentTimeMillis());
        emailDoc.put("type", "confirmation");

        return db.collection("mail").add(emailDoc);
    }

    public Task<DocumentReference> queueSms(String phoneNumber, String messageBody) {
        Map<String, Object> smsDoc = new HashMap<>();
        smsDoc.put("to", phoneNumber);
        smsDoc.put("body", messageBody);
        smsDoc.put("createdAt", System.currentTimeMillis());
        smsDoc.put("type", "confirmation");

        return db.collection("sms").add(smsDoc);
    }

    private String escapeHtml(String input) {
        if (input == null) return "";
        return input
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
}
