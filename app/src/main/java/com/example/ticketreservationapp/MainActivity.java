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
    EditText codeEt;
    Button sendBtn;
    Button verifyBtn;
    int check = R.id.btnEmail;
    boolean isAllFieldsCheck = false;

    private FirebaseAuth auth;
    private String verificationId; // set when code is sent
    private PhoneAuthProvider.ForceResendingToken resendToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        checkAllFields checkClass = new checkAllFields();

        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this); //temporary fix, will be dealt with later
        readWrite rw = new readWrite();
        setContentView(R.layout.activity_register);

        auth = FirebaseAuth.getInstance();

        TextInputLayout tilEmail = findViewById(R.id.tilEmail);
        TextInputLayout tilPhone = findViewById(R.id.tilPhone);
        TextInputLayout tilPasswrd = findViewById(R.id.tilPassword);
        LinearLayout otpView = findViewById(R.id.otpView);
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

                if(check == R.id.btnEmail) {
                    etEmail = findViewById(R.id.etEmail);
                    etPassword = findViewById(R.id.etPassword);
                } else {
                    etPhone = findViewById(R.id.etPhone);
                }
            }
        });

        etName = findViewById(R.id.etName);



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
                if(check == R.id.btnPhone) { //OTP code
                    codeEt = findViewById(R.id.otp);
                    sendBtn = findViewById(R.id.btnResend);
                    verifyBtn = findViewById(R.id.btnVerify);
                    otpView.setVisibility(View.VISIBLE);
                    sendPhoneVerification(etPhone, codeEt, sendBtn, verifyBtn);


                }
                rw.registerUser(etName, etEmail, etPhone, etPassword, check);
                Intent intent = new Intent(MainActivity.this, EventListActivity.class);
                startActivity(intent);
            }

        });


    }


    void sendPhoneVerification(TextInputEditText etPhone, EditText codeEt, Button sendBtn, Button verifyBtn) {
        PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks =
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {
                        // Auto-retrieval or instant validation (some devices/numbers)
                        signInWithPhoneCredential(credential);
                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        Toast.makeText(MainActivity.this,
                                "Verification failed: " + e.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onCodeSent(@NonNull String verifId,
                                           @NonNull PhoneAuthProvider.ForceResendingToken token) {
                        verificationId = verifId;
                        resendToken = token;
                        Toast.makeText(MainActivity.this,
                                "Code sent!",
                                Toast.LENGTH_SHORT).show();
                    }
                };
        sendBtn.setOnClickListener(v -> {
            String phone = Objects.requireNonNull(etPhone.getText()).toString().trim();
            phone = "+1" + phone;
            if (phone.isEmpty() || !phone.startsWith("+")) {
                Toast.makeText(this, "Enter phone in E.164 format, e.g. +15145551234",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            PhoneAuthOptions options =
                    PhoneAuthOptions.newBuilder(auth)
                            .setPhoneNumber(phone)
                            .setTimeout(60L, TimeUnit.SECONDS)
                            .setActivity(this)
                            .setCallbacks(callbacks)
                            .build();

            PhoneAuthProvider.verifyPhoneNumber(options);
        });

        verifyBtn.setOnClickListener(v -> {
            String code = codeEt.getText().toString().trim();
            if (verificationId == null) {
                Toast.makeText(this, "Send the code first.", Toast.LENGTH_SHORT).show();
                return;
            }
            if (code.length() < 6) {
                Toast.makeText(this, "Enter the 6-digit code.", Toast.LENGTH_SHORT).show();
                return;
            }

            PhoneAuthCredential credential =
                    PhoneAuthProvider.getCredential(verificationId, code);
            signInWithPhoneCredential(credential);
        });
    }
    void signInWithPhoneCredential(PhoneAuthCredential credential) {

        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Signed in!", Toast.LENGTH_SHORT).show();
                        // Go to your next screen
                        // startActivity(new Intent(this, HomeActivity.class));
                        // finish();
                    } else {
                        Toast.makeText(this,
                                "Sign-in failed: " + (task.getException() != null ? task.getException().getMessage() : ""),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }
    }





