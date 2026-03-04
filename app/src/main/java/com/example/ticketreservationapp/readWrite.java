package com.example.ticketreservationapp;


import android.app.Activity;
import android.widget.Toast;
import com.google.android.gms.tasks.OnSuccessListener;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.auth.*;

import java.util.Objects;


public class readWrite extends Activity {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    void registerUser(TextInputEditText etName, TextInputEditText etEmail, TextInputEditText etPhone, TextInputEditText etPassword, int check) {

        if(check == R.id.btnEmail) {
            mAuth.createUserWithEmailAndPassword(Objects.requireNonNull(etEmail.getText()).toString(), Objects.requireNonNull(etPassword.getText()).toString())
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser newUser = FirebaseAuth.getInstance().getCurrentUser();
                            assert newUser != null;
                            String id = newUser.getUid();
                            User user = new User(id, Objects.requireNonNull(etName.getText()).toString(), Objects.requireNonNull(etEmail.getText()).toString(), null, Objects.requireNonNull(etPassword.getText()).toString());
                            db.collection("users").document("email").collection(id).document("userInfo").set(user);

                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(readWrite.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });

            } else {

            User user = new User("", Objects.requireNonNull(etName.getText()).toString(), null, Objects.requireNonNull(etPhone.getText()).toString(), Objects.requireNonNull(etPassword.getText()).toString());
            db.collection("users").document("phone").set(user);
        }

    }

    void findUser(TextInputEditText etEmail, TextInputEditText etPhone, TextInputEditText etPassword, int check) {
        if(check == R.id.btnEmail) {
            DocumentReference doc = db.collection("users").document("email");
            doc.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    User user = documentSnapshot.toObject(User.class);

                }
            });
        }

    }
}
