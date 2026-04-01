package com.example.ticketreservationapp;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.FirebaseFirestore;
import android.widget.Spinner;

import java.util.Arrays;

public class AddEventActivity extends AppCompatActivity {

    private TextInputEditText etEventName, etDate, etTime, etVenue, etTickets, etPrice;
    private TextInputLayout tilEventName, tilVenue, tilTickets, tilPrice;
    private Spinner spinnerCategory;
    private MaterialButton btnPublish, btnCancelEvent;
    private EventFormHelper formHelper;
    private readWrite rw;
    private Event eventToEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        rw = new readWrite(FirebaseFirestore.getInstance());

        initViews();
        formHelper = new EventFormHelper(
                this,
                etEventName,
                etDate,
                etTime,
                etVenue,
                etTickets,
                etPrice,
                tilEventName,
                tilVenue,
                tilTickets,
                tilPrice,
                spinnerCategory
        );
        formHelper.setupCategorySpinner();
        formHelper.setupDateTimePickers();

        eventToEdit = (Event) getIntent().getSerializableExtra("edit_event");
        if (eventToEdit != null) {
            setupForEdit(eventToEdit);
        }

        findViewById(R.id.tvBack).setOnClickListener(v -> finish());

        btnPublish.setOnClickListener(v -> {
            if (formHelper.validateFields()) {
                Event event = formHelper.createEvent();
                if (eventToEdit != null) {
                    event.setId(eventToEdit.getId());
                    rw.updateEvent(event).addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Event updated!", Toast.LENGTH_SHORT).show();
                        finish();
                    }).addOnFailureListener(e -> {
                        Toast.makeText(this, "Failed to update event: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
                } else {
                    rw.addEvent(event).addOnSuccessListener(documentReference -> {
                        Toast.makeText(this, "Event published and saved!", Toast.LENGTH_SHORT).show();
                        finish();
                    }).addOnFailureListener(e -> {
                        Toast.makeText(this, "Failed to save event: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
                }
            }
        });

        btnCancelEvent.setOnClickListener(v -> {
            if (eventToEdit != null && eventToEdit.getId() != null) {
                rw.deleteEvent(eventToEdit.getId()).addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Event cancelled and deleted!", Toast.LENGTH_SHORT).show();
                    finish();
                }).addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to cancel event: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void setupForEdit(Event event) {
        ((TextView) findViewById(R.id.tvTitle)).setText("Edit Event");
        btnPublish.setText("Update Event");
        btnCancelEvent.setVisibility(View.VISIBLE);

        etEventName.setText(event.getEventName());
        etDate.setText(event.getDate());
        etTime.setText(event.getTime());
        etVenue.setText(event.getVenue());
        etTickets.setText(String.valueOf(event.getTickets()));
        etPrice.setText(String.valueOf(event.getPrice()));

        String[] categories = {"Select category", "Music", "Sports", "Movies", "Travel"};
        int index = Arrays.asList(categories).indexOf(event.getCategory());
        if (index >= 0) {
            spinnerCategory.setSelection(index);
        }
    }

    private void initViews() {
        etEventName     = findViewById(R.id.etEventName);
        etDate          = findViewById(R.id.etDate);
        etTime          = findViewById(R.id.etTime);
        etVenue         = findViewById(R.id.etVenue);
        etTickets       = findViewById(R.id.etTickets);
        etPrice         = findViewById(R.id.etPrice);
        tilEventName    = findViewById(R.id.tilEventName);
        tilVenue        = findViewById(R.id.tilVenue);
        tilTickets      = findViewById(R.id.tilTickets);
        tilPrice        = findViewById(R.id.tilPrice);
        spinnerCategory = findViewById(R.id.spinnerCategory);
        btnPublish      = findViewById(R.id.btnPublish);
        btnCancelEvent  = findViewById(R.id.btnCancelEvent);
    }
}
