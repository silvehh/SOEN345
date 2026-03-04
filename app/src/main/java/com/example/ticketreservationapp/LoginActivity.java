package com.example.ticketreservationapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class LoginActivity extends AppCompatActivity {

    TextInputEditText etEmail, etPassword, etPhone;
    int check = R.id.btnEmail;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

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
            Intent intent = new Intent(LoginActivity.this, EventListActivity.class);
            startActivity(intent);
        });


        // Register button navigates to events screen
        Button btnRegister = findViewById(R.id.btnRegister);
        btnRegister.setOnClickListener(v -> {
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
        });


    }
}
