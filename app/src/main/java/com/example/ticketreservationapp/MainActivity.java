package com.example.ticketreservationapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.*;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    TextInputEditText etName, etEmail, etPassword, etPhone;
    int check = R.id.btnEmail;
    boolean isAllFieldsCheck = false;

    private PhoneAuthProvider.ForceResendingToken resendToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        checkAllFields checkClass = new checkAllFields();

        super.onCreate(savedInstanceState);
         //temporary fix, will be dealt with later
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

                rw.registerUser(etName, etEmail, etPhone, etPassword, check);
                Intent intent = new Intent(MainActivity.this, EventListActivity.class);
                startActivity(intent);
            }

        });


    }
}



