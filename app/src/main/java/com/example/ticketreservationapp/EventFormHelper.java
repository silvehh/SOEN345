package com.example.ticketreservationapp;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import java.util.Calendar;

public class EventFormHelper {

    private final Context context;
    private final TextInputEditText etEventName;
    private final TextInputEditText etDate;
    private final TextInputEditText etTime;
    private final TextInputEditText etVenue;
    private final TextInputEditText etTickets;
    private final TextInputEditText etPrice;
    private final TextInputLayout tilEventName;
    private final TextInputLayout tilVenue;
    private final TextInputLayout tilTickets;
    private final TextInputLayout tilPrice;
    private final Spinner spinnerCategory;

    public EventFormHelper(
            Context context,
            TextInputEditText etEventName,
            TextInputEditText etDate,
            TextInputEditText etTime,
            TextInputEditText etVenue,
            TextInputEditText etTickets,
            TextInputEditText etPrice,
            TextInputLayout tilEventName,
            TextInputLayout tilVenue,
            TextInputLayout tilTickets,
            TextInputLayout tilPrice,
            Spinner spinnerCategory
    ) {
        this.context = context;
        this.etEventName = etEventName;
        this.etDate = etDate;
        this.etTime = etTime;
        this.etVenue = etVenue;
        this.etTickets = etTickets;
        this.etPrice = etPrice;
        this.tilEventName = tilEventName;
        this.tilVenue = tilVenue;
        this.tilTickets = tilTickets;
        this.tilPrice = tilPrice;
        this.spinnerCategory = spinnerCategory;
    }

    public void setupCategorySpinner() {
        String[] categories = {"Select category", "Music", "Sports", "Movies", "Travel"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context,
                android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter);
    }

    public void setupDateTimePickers() {
        etDate.setOnClickListener(v -> {
            Calendar cal = Calendar.getInstance();
            new DatePickerDialog(context, (view, year, month, day) ->
                    etDate.setText(String.format("%02d/%02d/%d", month + 1, day, year)),
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH)).show();
        });

        etTime.setOnClickListener(v -> {
            Calendar cal = Calendar.getInstance();
            new TimePickerDialog(context, (view, hour, minute) -> {
                String amPm = hour >= 12 ? "PM" : "AM";
                int hour12 = hour > 12 ? hour - 12 : (hour == 0 ? 12 : hour);
                etTime.setText(String.format("%d:%02d %s", hour12, minute, amPm));
            }, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), false).show();
        });
    }

    public boolean validateFields() {
        if (getTrimmedText(etEventName).isEmpty()) {
            tilEventName.setError("Event name is required");
            return false;
        }
        tilEventName.setError(null);

        if (spinnerCategory.getSelectedItemPosition() == 0) {
            Toast.makeText(context, "Please select a category", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (getTrimmedText(etDate).isEmpty()) {
            Toast.makeText(context, "Please select a date", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (getTrimmedText(etTime).isEmpty()) {
            Toast.makeText(context, "Please select a time", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (getTrimmedText(etVenue).isEmpty()) {
            tilVenue.setError("Venue is required");
            return false;
        }
        tilVenue.setError(null);

        if (getTrimmedText(etTickets).isEmpty()) {
            tilTickets.setError("Ticket count is required");
            return false;
        }
        tilTickets.setError(null);

        if (getTrimmedText(etPrice).isEmpty()) {
            tilPrice.setError("Price is required");
            return false;
        }
        tilPrice.setError(null);

        return true;
    }

    public Event createEvent() {
        String ticketsValue = getTrimmedText(etTickets);
        String priceValue = getTrimmedText(etPrice);

        int tickets = Integer.parseInt(ticketsValue);
        double price = Double.parseDouble(priceValue);

        return new Event(
                getTrimmedText(etEventName),
                spinnerCategory.getSelectedItem().toString(),
                getTrimmedText(etDate),
                getTrimmedText(etTime),
                getTrimmedText(etVenue),
                tickets,
                price
        );
    }

    private String getTrimmedText(TextInputEditText input) {
        if (input.getText() == null) {
            return "";
        }
        return input.getText().toString().trim();
    }
}
