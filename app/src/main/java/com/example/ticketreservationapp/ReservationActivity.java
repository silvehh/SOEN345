package com.example.ticketreservationapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ReservationActivity extends AppCompatActivity implements EventAdapter.OnEventClickListener{
    private RecyclerView recyclerEvents;
    private EventAdapter adapter;
    private List<Event> allEvents = new ArrayList<>();
    private List<Event> filteredEvents = new ArrayList<>();
    private readWrite rw;
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_list);

        currentUser = (User) getIntent().getSerializableExtra("user");
        rw = new readWrite(FirebaseFirestore.getInstance());

        initViews();
        setupRecyclerView();
        fetchEvents();
        setupSearch();

        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);
        bottomNav.setSelectedItemId(R.id.nav_home);

        TextView welcome = findViewById(R.id.welcomeText);
        if (currentUser != null) {
            welcome.setText("Welcome, " + currentUser.getName());
        }

        Button btnSignOut = findViewById(R.id.signOut);
        btnSignOut.setOnClickListener(v -> {
            startActivity(new Intent(this, LandingActivity.class));
            finishAffinity();
        });
    }

    private void initViews() {
        recyclerEvents = findViewById(R.id.recyclerEvents);
    }

    private void setupRecyclerView() {
        adapter = new EventAdapter(filteredEvents, this);
        recyclerEvents.setAdapter(adapter);
    }

    private void fetchEvents() {
        rw.getEventsCollection().get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                allEvents.clear();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Event event = document.toObject(Event.class);
                    event.setId(document.getId());
                    for(String eventId : currentUser.getEvents()){
                        if(eventId.equals(event.getId())) {
                            allEvents.add(event);
                        }
                    }
                }
                sortEventsByDate(allEvents);
                filteredEvents.clear();
                filteredEvents.addAll(allEvents);
                adapter.notifyDataSetChanged();
            } else {
                Log.e("Firestore", "Error getting events", task.getException());
                Toast.makeText(this, "Failed to load events", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sortEventsByDate(List<Event> events) {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
        Collections.sort(events, (e1, e2) -> {
            try {
                Date d1 = sdf.parse(e1.getDate());
                Date d2 = sdf.parse(e2.getDate());
                if (d1 == null || d2 == null) return 0;
                return d1.compareTo(d2);
            } catch (ParseException e) {
                return 0;
            }
        });
    }

    private void setupSearch() {
        EditText etSearch = findViewById(R.id.etSearch);
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void filter(String text) {
        filteredEvents.clear();
        if (text.isEmpty()) {
            filteredEvents.addAll(allEvents);
        } else {
            for (Event event : allEvents) {
                if (event.getEventName().toLowerCase().contains(text.toLowerCase())) {
                    filteredEvents.add(event);
                }
            }
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onEventClick(Event event) {
        if (currentUser != null && currentUser.getAdmin()) {
            Intent intent = new Intent(this, AddEventActivity.class);
            intent.putExtra("edit_event", event);
            startActivity(intent);
        } else {
            // User view: navigate to event details or booking
            Toast.makeText(this, "Selected: " + event.getEventName(), Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(this, EventReserveActivity.class);
            intent.putExtra("reserve_event", event);
            intent.putExtra("user", currentUser);
            startActivity(intent);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchEvents(); // Refresh list on return
    }
}
