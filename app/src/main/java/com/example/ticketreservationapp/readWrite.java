package com.example.ticketreservationapp;


import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class readWrite {
    FirebaseFirestore db;

    public readWrite(FirebaseFirestore db) {
        this.db = db;
    }
    Task<DocumentReference> registerUser(User user) {
           return db.collection("users").add(user);
    }

    public Task<DocumentReference> addEvent(Event event) {
        return db.collection("events").add(event);
    }

    public CollectionReference getEventsCollection() {
        return db.collection("events");
    }

    public Task<Void> updateEvent(Event event) {
        return db.collection("events").document(event.getId()).set(event);
    }

    public Task<Void> deleteEvent(String eventId) {
        return db.collection("events").document(eventId).delete();
    }

    public Task<Void> deleteUser(String userId) {
        return db.collection("users").document(userId).delete();
    }

    public boolean isReserved(Event event, User user) {
        boolean isReserved = false;

        ArrayList<String> events = user.getEvents();

        if(events != null){
            for(String event1 : events) {
                if(event1.equals(event.getId())) {
                    isReserved = true;
                    break;
                }
            }
        }

        return isReserved;
    }

    public void reserveEvent(Event event, User user) {
        String field;
        String variable;
        if(user.getPhone() == null || user.getPhone().isEmpty()) {
            field = "email";
            variable = user.getEmail();
        } else {
            field = "phone";
            variable = user.getPhone();
        }

        db.collection("users").whereEqualTo(field, variable).whereEqualTo("password", user.getPassword()).get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for(DocumentSnapshot document : queryDocumentSnapshots) {
                        String docId = document.getId();

                        db.collection("users").document(docId).update("events", FieldValue.arrayUnion(event.getId()));
                        System.out.println("Successful add");
                    }
                });
        db.collection("events").document(event.getId()).update("tickets", event.getTickets() - 1);

    }

    public void cancelEvent(Event event, User user) {
        String field;
        String variable;
        if(user.getPhone() == null || user.getPhone().isEmpty()) {
            field = "email";
            variable = user.getEmail();
        } else {
            field = "phone";
            variable = user.getPhone();
        }

        db.collection("users").whereEqualTo(field, variable).whereEqualTo("password", user.getPassword()).get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for(DocumentSnapshot document : queryDocumentSnapshots) {
                        String docId = document.getId();

                        db.collection("users").document(docId).update("events", FieldValue.arrayRemove(event.getId()));
                        System.out.println("Successful cancellation");
                    }
                });
        db.collection("events").document(event.getId()).update("tickets", event.getTickets() + 1);
    }

    void signIn(String etEmail, String etPhone, String etPassword, SignInCallback callback) {
        String field;
        String variable;
        if(etPhone == null || etPhone.isEmpty()) {
            field = "email";
            variable = etEmail;
        } else {
            field = "phone";
            variable = etPhone;
        }
        db.collection("users")
                .whereEqualTo(field, variable)
                .whereEqualTo("password", etPassword)
                .get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (task.getResult() != null && !task.getResult().isEmpty()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("Firestore", "Login success. User ID: " + document.getId());
                                String name = document.getString("name");
                                Log.d("Firestore", "Welcome " + name);
                                Log.d("is admin", String.valueOf(document.getBoolean("admin")));

                                User user = task.getResult().getDocuments().get(0).toObject(User.class);

                                assert user != null;
                                Log.d("is admin", String.valueOf(user.getEmail()));
                                callback.result(user);

                                break;
                            }
                        } else {
                            Log.d("Firestore", "Login failed: invalid email or password");
                            callback.result(null);
                        }
                    } else {
                        Log.e("Firestore", "Login error", task.getException());
                        callback.result(null);
                    }
                });

    }
}
