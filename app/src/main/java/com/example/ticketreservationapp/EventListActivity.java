package com.example.ticketreservationapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class EventListActivity extends AppCompatActivity implements EventAdapter.OnEventClickListener {

    private RecyclerView recyclerEvents;
    private EventAdapter adapter;
    private List<Event> allEvents = new ArrayList<>();
    private List<Event> filteredEvents = new ArrayList<>();
    private readWrite rw;
    private User currentUser;

    private EventFilterHelper filterHelper;
    private EditText etSearch;
    private Spinner spinnerDate;
    private Spinner spinnerLocation;
    private Button btnClearFilters;
    private ChipGroup chipGroupFilters;
    private TextView tvResultsCount;
    private TextView tvNoResults;

    private boolean isReservation = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_list);

        currentUser = (User) getIntent().getSerializableExtra("user");
        rw = new readWrite(FirebaseFirestore.getInstance());

        initViews();
        setupRecyclerView();
        setupUserWelcome();
        setupBottomNavigation();
        
        filterHelper = new EventFilterHelper(allEvents);
        fetchEvents();
    }

    private void initViews() {
        recyclerEvents = findViewById(R.id.recyclerEvents);
        etSearch = findViewById(R.id.etSearch);
        spinnerDate = findViewById(R.id.spinnerDate);
        spinnerLocation = findViewById(R.id.spinnerLocation);
        btnClearFilters = findViewById(R.id.btnClearFilters);
        chipGroupFilters = findViewById(R.id.chipGroupFilters);
        tvResultsCount = findViewById(R.id.tvResultsCount);
        tvNoResults = findViewById(R.id.tvNoResults);
    }

    @SuppressLint("SetTextI18n")
    private void setupUserWelcome() {
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
                    if(isReservation) {
                        for(String eventId : currentUser.getEvents()){
                            if(eventId.equals(event.getId())) {
                                allEvents.add(event);
                            }
                        }
                    } else {
                        allEvents.add(event);
                    }

                }
                sortEventsByDate(allEvents);
                filterHelper.setAllEvents(allEvents);
                
                // Setup filters after events are loaded
                setupSearchFilter();
                setupDateFilter();
                setupLocationFilter();
                setupCategoryChips();
                setupClearFiltersButton();
                
                applyFilters();
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

    private void setupSearchFilter() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterHelper.setSearchQuery(s.toString());
                applyFilters();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void setupDateFilter() {
        List<String> dates = new ArrayList<>();
        dates.add("All Dates");
        dates.addAll(filterHelper.getAllDates());

        ArrayAdapter<String> dateAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, dates);
        dateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDate.setAdapter(dateAdapter);

        spinnerDate.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedDate = position == 0 ? "" : dates.get(position);
                filterHelper.setSelectedDate(selectedDate);
                applyFilters();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void setupLocationFilter() {
        List<String> locations = new ArrayList<>();
        locations.add("All Locations");
        locations.addAll(filterHelper.getAllLocations());

        ArrayAdapter<String> locationAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, locations);
        locationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLocation.setAdapter(locationAdapter);

        spinnerLocation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedLocation = position == 0 ? "" : locations.get(position);
                filterHelper.setSelectedLocation(selectedLocation);
                applyFilters();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void setupCategoryChips() {
        int[] chipIds = {
                R.id.chipAll, R.id.chipMusic, R.id.chipSports, R.id.chipMovies, R.id.chipTravel
        };

        chipGroupFilters.setOnCheckedStateChangeListener((group, checkedIds) -> {
            // Update visual state manually for all chips
            for (int id : chipIds) {
                Chip chip = findViewById(id);
                if (chip != null) {
                    chip.setChipBackgroundColorResource(
                            checkedIds.contains(id) ? R.color.chip_selected : R.color.chip_unselected
                    );
                    chip.setTextColor(checkedIds.contains(id)
                            ? getColor(android.R.color.white)
                            : getColor(android.R.color.black));
                }
            }

            if (checkedIds.isEmpty()) {
                // Nothing selected — snap back to All
                Chip chipAll = findViewById(R.id.chipAll);
                if (chipAll != null) chipAll.setChecked(true);
                filterHelper.setSelectedCategory("All");
            } else {
                Chip selectedChip = findViewById(checkedIds.get(0));
                if (selectedChip != null) {
                    filterHelper.setSelectedCategory(selectedChip.getText().toString());
                }
            }
            applyFilters();
        });
    }

    private void setupClearFiltersButton() {
        btnClearFilters.setOnClickListener(v -> clearAllFilters());
    }

    private void clearAllFilters() {
        etSearch.setText("");
        spinnerDate.setSelection(0);
        spinnerLocation.setSelection(0);
        
        Chip chipAll = findViewById(R.id.chipAll);
        if (chipAll != null) {
            chipAll.setChecked(true);
        }
        
        filterHelper.clearAllFilters();
        applyFilters();
    }

    @SuppressLint("SetTextI18n")
    private void applyFilters() {
        List<Event> filtered = filterHelper.applyFilters();
        filteredEvents.clear();
        filteredEvents.addAll(filtered);
        adapter.notifyDataSetChanged();

        // Update results count
        int count = filtered.size();
        tvResultsCount.setText("(" + count + " results)");

        // Show/hide no results message
        if (count == 0) {
            tvNoResults.setVisibility(View.VISIBLE);
            recyclerEvents.setVisibility(View.GONE);
        } else {
            tvNoResults.setVisibility(View.GONE);
            recyclerEvents.setVisibility(View.VISIBLE);
        }

        // Show/hide clear filters button
        if (filterHelper.hasActiveFilters()) {
            btnClearFilters.setVisibility(View.VISIBLE);
        } else {
            btnClearFilters.setVisibility(View.GONE);
        }
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);
        bottomNav.setSelectedItemId(R.id.nav_home);

        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                isReservation = false;
                fetchEvents();
                return true;
            } else if (id == R.id.nav_search) {
                return true;
            } else if (id == R.id.nav_reservation) {
                isReservation = true;
                fetchEvents();
                return true;
            }
            return false;
        } );
    }

    @Override
    public void onEventClick(Event event) {
        if (currentUser != null && currentUser.getAdmin()) {
            Intent intent = new Intent(this, AddEventActivity.class);
            intent.putExtra("edit_event", event);
            startActivity(intent);
        } else {
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
        fetchEvents();
    }
}
