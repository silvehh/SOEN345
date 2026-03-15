package com.example.ticketreservationapp;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import java.util.Calendar;

public class AddEventActivity extends AppCompatActivity {

    private TextInputEditText etEventName, etDate, etTime, etVenue, etTickets, etPrice;
    private TextInputLayout tilEventName, tilVenue, tilTickets, tilPrice;
    private Spinner spinnerCategory;
    private MaterialButton btnPublish;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        initViews();
        setupCategorySpinner();
        setupDateTimePickers();

        findViewById(R.id.tvBack).setOnClickListener(v -> finish());

        btnPublish.setOnClickListener(v -> {
            if (validateFields()) {
                Toast.makeText(this, "Event published!", Toast.LENGTH_SHORT).show();
                finish();
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

    private void setupCategorySpinner() {
        String[] categories = {"Select category", "Music", "Sports", "Movies", "Travel"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter);
    }

    private void setupDateTimePickers() {
        etDate.setOnClickListener(v -> {
            Calendar cal = Calendar.getInstance();
            new DatePickerDialog(this, (view, year, month, day) ->
                    etDate.setText(String.format("%02d/%02d/%d", month + 1, day, year)),
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH)).show();
        });

        etTime.setOnClickListener(v -> {
            Calendar cal = Calendar.getInstance();
            new TimePickerDialog(this, (view, hour, minute) -> {
                String amPm = hour >= 12 ? "PM" : "AM";
                int hour12 = hour > 12 ? hour - 12 : (hour == 0 ? 12 : hour);
                etTime.setText(String.format("%d:%02d %s", hour12, minute, amPm));
            }, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), false).show();
        });
    }

    private boolean validateFields() {
        if (etEventName.getText().toString().trim().isEmpty()) {
            tilEventName.setError("Event name is required");
            return false;
        }
        tilEventName.setError(null);

        if (spinnerCategory.getSelectedItemPosition() == 0) {
            Toast.makeText(this, "Please select a category", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (etDate.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Please select a date", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (etTime.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Please select a time", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (etVenue.getText().toString().trim().isEmpty()) {
            tilVenue.setError("Venue is required");
            return false;
        }
        tilVenue.setError(null);

        if (etTickets.getText().toString().trim().isEmpty()) {
            tilTickets.setError("Ticket count is required");
            return false;
        }
        tilTickets.setError(null);

        if (etPrice.getText().toString().trim().isEmpty()) {
            tilPrice.setError("Price is required");
            return false;
        }
        tilPrice.setError(null);

        return true;
    }
}