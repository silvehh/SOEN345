package com.example.ticketreservationapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;

public class AdminDashboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        LinearLayout cardAddEvent    = findViewById(R.id.cardAddEvent);
        LinearLayout cardEditEvent   = findViewById(R.id.cardEditEvent);
        LinearLayout cardCancelEvent = findViewById(R.id.cardCancelEvent);
        LinearLayout navEvents       = findViewById(R.id.navEvents);

        cardAddEvent.setOnClickListener(v ->
                startActivity(new Intent(this, AddEventActivity.class)));

        cardEditEvent.setOnClickListener(v ->
                startActivity(new Intent(this, EventListActivity.class)));

        cardCancelEvent.setOnClickListener(v ->
                startActivity(new Intent(this, EventListActivity.class)));

        navEvents.setOnClickListener(v ->
                startActivity(new Intent(this, EventListActivity.class)));
    }
}