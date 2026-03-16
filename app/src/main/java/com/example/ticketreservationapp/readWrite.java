package com.example.ticketreservationapp;


import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class readWrite {
    FirebaseFirestore db;

    public readWrite(FirebaseFirestore db) {
        this.db = db;
    }
    Task<DocumentReference> registerUser(User user) {
           return db.collection("users").add(user);
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
                    }
                });

    }
}
