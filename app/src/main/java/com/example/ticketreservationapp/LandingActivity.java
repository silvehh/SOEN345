package com.example.ticketreservationapp;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;

public class LandingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MaterialButton btnUserLogin  = findViewById(R.id.btnUserLogin);
        MaterialButton btnAdminLogin = findViewById(R.id.btnAdminLogin);

        btnUserLogin.setOnClickListener(v -> {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.putExtra("isAdmin", false);
            startActivity(intent);
        });

        btnAdminLogin.setOnClickListener(v -> {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.putExtra("isAdmin", true);
            startActivity(intent);
        });
    }
}