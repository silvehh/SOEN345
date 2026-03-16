package com.example.ticketreservationapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;


import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    TextInputEditText etName, etEmail, etPassword, etPhone;
    int check = R.id.btnEmail;
    boolean isAllFieldsCheck = false;

    User user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        checkAllFields checkClass = new checkAllFields();

        super.onCreate(savedInstanceState);
        boolean isAdmin = getIntent().getBooleanExtra("isAdmin", false);

        readWrite rw = new readWrite();
        setContentView(R.layout.activity_register);
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
                etName = findViewById(R.id.etName);
                etPassword = findViewById(R.id.etPassword);
                if(check == R.id.btnEmail) {
                    etEmail = findViewById(R.id.etEmail);
                } else {
                    etPhone = findViewById(R.id.etPhone);
                }

            }
        });

        etName = findViewById(R.id.etName);
        etPassword = findViewById(R.id.etPassword);
        if(check == R.id.btnEmail) {
            etEmail = findViewById(R.id.etEmail);
        } else {
            etPhone = findViewById(R.id.etPhone);
        }



        //load login Screen on button tap

        Button btnLogin = findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        });


        // Register button navigates to events screen
        Button btnRegister = findViewById(R.id.btnRegister);
        btnRegister.setOnClickListener(v -> {
            isAllFieldsCheck = checkClass.CheckAllFields(etName, etEmail, etPhone, etPassword, check);
            if(isAllFieldsCheck) {
                if(check == R.id.btnEmail) {
                    user = new User(Objects.requireNonNull(etName.getText()).toString(), Objects.requireNonNull(etEmail.getText()).toString(), null, Objects.requireNonNull(etPassword.getText()).toString(), isAdmin, null);
                } else {
                    user = new User(Objects.requireNonNull(etName.getText()).toString(), null, Objects.requireNonNull(etPhone.getText()).toString(), Objects.requireNonNull(etPassword.getText()).toString(), isAdmin, null);
                }
                rw.registerUser(user);
                Intent intent;
                if(isAdmin) {
                    intent = new Intent(MainActivity.this, AdminDashboardActivity.class);

                } else {
                    intent = new Intent(MainActivity.this, EventListActivity.class);

                }
                intent.putExtra("user", user);
                startActivity(intent);
                finish();
            }

        });


    }
}



