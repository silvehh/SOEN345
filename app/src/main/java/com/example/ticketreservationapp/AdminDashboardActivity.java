package com.example.ticketreservationapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class AdminDashboardActivity extends AppCompatActivity {

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        User user = (User) getIntent().getSerializableExtra("user");

        TextView welcome = findViewById(R.id.adminUser);
        if(user != null) {
            welcome.setText("ADMIN: " + user.getName());
        }

        LinearLayout cardAddEvent    = findViewById(R.id.cardAddEvent);
        LinearLayout cardEditEvent   = findViewById(R.id.cardEditEvent);
        LinearLayout cardCancelEvent = findViewById(R.id.cardCancelEvent);
        LinearLayout navEvents       = findViewById(R.id.navEvents);
        Button btnSignOut = findViewById(R.id.signOutbtn);

        btnSignOut.setOnClickListener(v -> startActivity(new Intent(this, LandingActivity.class)));

        cardAddEvent.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddEventActivity.class);
            intent.putExtra("user", user);
            startActivity(intent);
        });

        cardEditEvent.setOnClickListener(v -> {
            Intent intent = new Intent(this, EventListActivity.class);
            intent.putExtra("user", user);
            startActivity(intent);
        });

        cardCancelEvent.setOnClickListener(v -> {
            Intent intent = new Intent(this, EventListActivity.class);
            intent.putExtra("user", user);
            startActivity(intent);
        });

        navEvents.setOnClickListener(v -> {
            Intent intent = new Intent(this, EventListActivity.class);
            intent.putExtra("user", user);
            startActivity(intent);
        });
    }
}