package com.example.ticketreservationapp;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.FirebaseFirestore;
import android.widget.Spinner;

public class AddEventActivity extends AppCompatActivity {

    private TextInputEditText etEventName, etDate, etTime, etVenue, etTickets, etPrice;
    private TextInputLayout tilEventName, tilVenue, tilTickets, tilPrice;
    private Spinner spinnerCategory;
    private MaterialButton btnPublish;
    private EventFormHelper formHelper;
    private readWrite rw;

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

        findViewById(R.id.tvBack).setOnClickListener(v -> finish());

        btnPublish.setOnClickListener(v -> {
            if (formHelper.validateFields()) {
                Event event = formHelper.createEvent();
                if (event != null) {
                    rw.addEvent(event).addOnSuccessListener(documentReference -> {
                        Toast.makeText(this, "Event published and saved!", Toast.LENGTH_SHORT).show();
                        finish();
                    }).addOnFailureListener(e -> {
                        Toast.makeText(this, "Failed to save event: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
                }
            }
        });
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
    }
}
