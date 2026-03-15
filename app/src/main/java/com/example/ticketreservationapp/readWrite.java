package com.example.ticketreservationapp;


import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;

import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.auth.*;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Objects;
import java.util.concurrent.TimeUnit;


public class readWrite extends Activity {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    void registerUser(TextInputEditText etName, TextInputEditText etEmail, TextInputEditText etPhone, TextInputEditText etPassword, int check) {

        if(check == R.id.btnEmail) {
            User user = new User(Objects.requireNonNull(etName.getText()).toString(), Objects.requireNonNull(etEmail.getText()).toString(), null, Objects.requireNonNull(etPassword.getText()).toString(), false, null);
            db.collection("users").add(user);
        } else {

            User user = new User(Objects.requireNonNull(etName.getText()).toString(), null, Objects.requireNonNull(etPhone.getText()).toString(), Objects.requireNonNull(etPassword.getText()).toString(), false, null);
            db.collection("users").add(user);
        }

    }

    void signIn(TextInputEditText etEmail, TextInputEditText etPhone, TextInputEditText etPassword, int check) {
        String field;
        String variable;
        if(check ==R.id.btnEmail ) {
            field = "email";
            variable = Objects.requireNonNull(etEmail.getText()).toString();
        } else {
            field = "phone";
            variable = Objects.requireNonNull(etPhone.getText()).toString();
        }
        db.collection("users")
                .whereEqualTo(field, variable)
                .whereEqualTo("password", Objects.requireNonNull(etPassword.getText()).toString())
                .get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (task.getResult() != null && !task.getResult().isEmpty()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("Firestore", "Login success. User ID: " + document.getId());
                                String name = document.getString("name");
                                Log.d("Firestore", "Welcome " + name);
                                break;
                            }
                        } else {
                            Log.d("Firestore", "Login failed: invalid email or password");
                        }
                    } else {
                        Log.e("Firestore", "Login error", task.getException());
                    }
                });

    }
}
