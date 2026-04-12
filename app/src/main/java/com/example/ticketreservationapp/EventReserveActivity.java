package com.example.ticketreservationapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class EventReserveActivity extends AppCompatActivity {

    private readWrite rw;
    TextView evTitle, evCategory, evDate, evTime, evVenue, evPrice;

    MaterialButton btnReserve;
    private Event eventToReserve;

    private User currentUser;

    private NotificationService notificationService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reserve_event);

        rw = new readWrite(FirebaseFirestore.getInstance());
        notificationService = new NotificationService();

        initViews();

        eventToReserve = (Event) getIntent().getSerializableExtra("reserve_event");

        currentUser = (User) getIntent().getSerializableExtra("user");

        assert currentUser != null;
        boolean isReserved = rw.isReserved(eventToReserve, currentUser);
        assert eventToReserve != null;
        evTitle.setText(eventToReserve.getEventName());
        evCategory.setText(eventToReserve.getCategory());
        evDate.setText("Date: " + eventToReserve.getDate());
        evTime.setText("Time: " + eventToReserve.getTime());
        evVenue.setText("Venue: " + eventToReserve.getVenue());
        evPrice.setText("Price: " + String.valueOf(eventToReserve.getPrice()));

        System.out.println(isReserved);

        if(isReserved) {
            btnReserve.setText("Cancel Reservation");
        }

        btnReserve.setOnClickListener(v -> {

            if(isReserved){
                rw.cancelEvent(eventToReserve, currentUser);
                ArrayList<String> newEvents = currentUser.getEvents();

                newEvents.remove(eventToReserve.getId());
                currentUser.setEvents(newEvents);
                notificationService.sendConfirmation(currentUser.getEmail(), currentUser.getPhone(), eventToReserve.getEventName(), isReserved);
            } else {
                rw.reserveEvent(eventToReserve, currentUser);
                ArrayList<String> newEvents = currentUser.getEvents();

                if(newEvents == null) {
                    newEvents = new ArrayList<>();
                }

                newEvents.add(eventToReserve.getId());
                currentUser.setEvents(newEvents);
                notificationService.sendConfirmation(currentUser.getEmail(), currentUser.getPhone(), eventToReserve.getEventName(), isReserved);
            }

            Intent intent = new Intent(this, EventListActivity.class);
            intent.putExtra("user", currentUser);
            startActivity(intent);
                }
        );

    }

    private void initViews() {
        evTitle = findViewById(R.id.evTitle);
        evCategory = findViewById(R.id.evCategory);
        evDate = findViewById(R.id.evDate);
        evTime = findViewById(R.id.evTime);
        evVenue = findViewById(R.id.evVenue);
        evPrice = findViewById(R.id.evPrice);
        btnReserve = findViewById(R.id.btnReserve);
    }
}
