package com.example.ticketreservationapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class EventListActivity extends AppCompatActivity {

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_list);

        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);
        bottomNav.setSelectedItemId(R.id.nav_home);

        User user = (User) getIntent().getSerializableExtra("user");

        TextView welcome = findViewById(R.id.welcomeText);

        if(user != null) {
            welcome.setText("Welcome, " + user.getName());
        }

        Button btnSignOut = findViewById(R.id.signOut);

        btnSignOut.setOnClickListener(v -> startActivity(new Intent(this, LandingActivity.class)));

        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                return true;
            } else if (id == R.id.nav_search) {
                return true;
            } else return id == R.id.nav_reservation;
        });
    }
}