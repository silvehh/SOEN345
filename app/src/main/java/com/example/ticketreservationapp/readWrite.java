package com.example.ticketreservationapp;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class readWrite {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    void registerUser(TextInputEditText etName, TextInputEditText etEmail, TextInputEditText etPhone, TextInputEditText etPassword, int check) {



        if(check == R.id.btnEmail) {
            User user = new User(Objects.requireNonNull(etName.getText()).toString(), Objects.requireNonNull(etEmail.getText()).toString(), null, Objects.requireNonNull(etPassword.getText()).toString());
            db.collection("users").document("email").set(user);
        } else {
            User user = new User(Objects.requireNonNull(etName.getText()).toString(), null, Objects.requireNonNull(etPhone.getText()).toString(), Objects.requireNonNull(etPassword.getText()).toString());
            db.collection("users").document("phone").set(user);
        }

    }
}
