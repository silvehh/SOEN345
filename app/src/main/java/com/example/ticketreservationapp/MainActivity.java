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
    int check;

    boolean isAllFieldsCheck = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

            isAllFieldsCheck = CheckAllFields();

            if(isAllFieldsCheck) {
                Intent intent = new Intent(MainActivity.this, EventListActivity.class);
                startActivity(intent);
            }

        });
    }

    private boolean CheckAllFields() {
        if (etName.length() == 0) {
            etName.setError("This field is required");
            return false;
        }

        if (check == R.id.btnEmail) {
            if (etEmail.length() == 0) {
                etEmail.setError("Email is required");
                return false;
            }
        }

        if (check == R.id.btnPhone) {
            if (etPhone.length() != 10) {
                etEmail.setError("Valid phone number is required");
                return false;
            }
        }

        if (etPassword.length() == 0) {
            etPassword.setError("Password is required");
            return false;
        } else if (etPassword.length() < 8) {
            etPassword.setError("Password must be minimum 8 characters");
            return false;
        }

        // after all validation return true.
        return true;
    }



}