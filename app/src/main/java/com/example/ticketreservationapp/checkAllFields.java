package com.example.ticketreservationapp;

import com.google.android.material.textfield.TextInputEditText;

public class checkAllFields {

    public boolean CheckAllFields(TextInputEditText etName, TextInputEditText etEmail, TextInputEditText etPhone, TextInputEditText etPassword, int check) {
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
