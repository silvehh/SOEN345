package com.example.ticketreservationapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class MainActivity extends AppCompatActivity {

    TextInputEditText etName, etEmail, etPassword, etPhone;
    int check = R.id.btnEmail;
    boolean isAllFieldsCheck = false;
    boolean isAdmin = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        checkAllFields checkClass = new checkAllFields();
        readWrite rw = new readWrite();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Read the flag from LandingActivity
        isAdmin = getIntent().getBooleanExtra("isAdmin", false);

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

        etName = findViewById(R.id.etName);
        etPassword = findViewById(R.id.etPassword);
        if(check == R.id.btnEmail) {
            etEmail = findViewById(R.id.etEmail);
        } else {
            etPhone = findViewById(R.id.etPhone);
        }

        // Register button navigates to events screen
        Button btnRegister = findViewById(R.id.btnRegister);
        btnRegister.setOnClickListener(v -> {

            isAllFieldsCheck = checkClass.CheckAllFields(etName, etEmail, etPhone, etPassword, check);
            rw.registerUser(etName, etEmail, etPhone, etPassword, check);
            if (isAllFieldsCheck) {
                if (isAdmin) {
                    // Admin → go to Admin Dashboard
                    startActivity(new Intent(MainActivity.this, AdminDashboardActivity.class));
                } else {
                    // Regular user → go to Events list
                    startActivity(new Intent(MainActivity.this, EventListActivity.class));
                }
            }

        });
    }

}