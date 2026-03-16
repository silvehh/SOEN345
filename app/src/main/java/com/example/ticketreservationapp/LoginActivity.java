package com.example.ticketreservationapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    TextInputEditText etEmail, etPassword, etPhone;
    int check = R.id.btnEmail;

    boolean isAllFieldsCheck = false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkAllFields checkClass = new checkAllFields();
        boolean isAdmin = getIntent().getBooleanExtra("isAdmin", false);
        setContentView(R.layout.activity_login);
        readWrite rw = new readWrite();
        TextInputLayout tilEmail = findViewById(R.id.tilEmail);
        TextInputLayout tilPhone = findViewById(R.id.tilPhone);
        MaterialButtonToggleGroup toggle = findViewById(R.id.toggleContactType);

        // Switch between email and phone fields
        toggle.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            check = checkedId;
            if (isChecked) {
                if (checkedId == R.id.btnEmail) {
                    tilEmail.setVisibility(View.VISIBLE);
                    tilPhone.setVisibility(View.GONE);
                } else if (checkedId == R.id.btnPhone) {
                    tilPhone.setVisibility(View.VISIBLE);
                    tilEmail.setVisibility(View.GONE);
                }
                etPassword = findViewById(R.id.etPassword);
                if(check == R.id.btnEmail) {
                    etEmail = findViewById(R.id.etEmail);
                } else {
                    etPhone = findViewById(R.id.etPhone);
                }
            }
        });


        etPassword = findViewById(R.id.etPassword);
        if(check == R.id.btnEmail) {
            etEmail = findViewById(R.id.etEmail);
        } else {
            etPhone = findViewById(R.id.etPhone);
        }



        Button btnLogin = findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(v -> {
            isAllFieldsCheck = checkClass.CheckAllFields(null, etEmail, etPhone, etPassword, check);
            if(isAllFieldsCheck) {
                String email = null;
                String phone = null;
                if(check ==R.id.btnEmail ) {
                    email = Objects.requireNonNull(etEmail.getText()).toString();
                } else {
                    phone = Objects.requireNonNull(etPhone.getText()).toString();
                }


                String password = Objects.requireNonNull(etPassword.getText()).toString();
                rw.signIn(email, phone, password, user -> {
                    if (user != null) {
                        Log.d("Firestore", "Login success");
                        Intent intent;
                        if (isAdmin) {
                            intent = new Intent(LoginActivity.this, AdminDashboardActivity.class);
                        } else {
                            intent = new Intent(LoginActivity.this, EventListActivity.class);
                        }
                        intent.putExtra("user", user);
                        startActivity(intent);
                        finish();
                    } else {
                        Log.d("Firestore", "Login failed");
                        TextView error = findViewById(R.id.error);
                        error.setVisibility(View.VISIBLE);

                    }
                });

            }

        });


        // Register button navigates to events screen
        Button btnRegister = findViewById(R.id.btnRegister);
        btnRegister.setOnClickListener(v -> {
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                intent.putExtra("isAdmin", getIntent().getBooleanExtra("isAdmin", false));
                startActivity(intent);
        });


    }
}
