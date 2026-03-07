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

    boolean isAllFieldsCheck = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        checkAllFields checkClass = new checkAllFields();

        setContentView(R.layout.activity_login);
        readWrite rw = new readWrite();
        TextInputLayout tilEmail = findViewById(R.id.tilEmail);
        TextInputLayout tilPhone = findViewById(R.id.tilPhone);
        TextInputLayout tilPasswrd = findViewById(R.id.tilPassword);
        MaterialButtonToggleGroup toggle = findViewById(R.id.toggleContactType);

        // Switch between email and phone fields
        toggle.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            check = checkedId;
            if (isChecked) {
                if (checkedId == R.id.btnEmail) {
                    tilEmail.setVisibility(View.VISIBLE);
                    tilPhone.setVisibility(View.GONE);
                    tilPasswrd.setVisibility(View.VISIBLE);
                } else if (checkedId == R.id.btnPhone) {
                    tilPhone.setVisibility(View.VISIBLE);
                    tilEmail.setVisibility(View.GONE);
                    tilPasswrd.setVisibility(View.GONE);
                }
            }
        });


        etPassword = findViewById(R.id.etPassword);
        if(check == R.id.btnEmail) {
            etEmail = findViewById(R.id.etEmail);
            etPassword = findViewById(R.id.etPassword);
        } else {
            etPhone = findViewById(R.id.etPhone);
        }



        Button btnLogin = findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(v -> {
            isAllFieldsCheck = checkClass.CheckAllFields(null, etEmail, etPhone, etPassword, check);
            if(isAllFieldsCheck) {
                rw.signIn(etEmail, etPhone, etPassword, check);
                Intent intent = new Intent(LoginActivity.this, EventListActivity.class);
                startActivity(intent);

            }


        });


        // Register button navigates to events screen
        Button btnRegister = findViewById(R.id.btnRegister);
        btnRegister.setOnClickListener(v -> {
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
        });


    }
}
