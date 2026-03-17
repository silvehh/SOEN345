package com.example.ticketreservationapp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import android.content.Context;
import android.view.ContextThemeWrapper;
import android.widget.Spinner;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.atomic.AtomicReference;

@RunWith(AndroidJUnit4.class)
public class EventFormHelperTest {

    private EventFormHelper helper;
    private TextInputEditText etEventName;
    private TextInputEditText etDate;
    private TextInputEditText etTime;
    private TextInputEditText etVenue;
    private TextInputEditText etTickets;
    private TextInputEditText etPrice;
    private TextInputLayout tilEventName;
    private TextInputLayout tilVenue;
    private TextInputLayout tilTickets;
    private TextInputLayout tilPrice;
    private Spinner spinnerCategory;

    @Before
    public void setUp() {
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        Context themedContext = new ContextThemeWrapper(context, androidx.appcompat.R.style.Theme_AppCompat);

        runOnMainSync(() -> {
            etEventName = new TextInputEditText(themedContext);
            etDate = new TextInputEditText(themedContext);
            etTime = new TextInputEditText(themedContext);
            etVenue = new TextInputEditText(themedContext);
            etTickets = new TextInputEditText(themedContext);
            etPrice = new TextInputEditText(themedContext);

            tilEventName = new TextInputLayout(themedContext);
            tilVenue = new TextInputLayout(themedContext);
            tilTickets = new TextInputLayout(themedContext);
            tilPrice = new TextInputLayout(themedContext);

            spinnerCategory = new Spinner(themedContext);

            tilEventName.addView(etEventName);
            tilVenue.addView(etVenue);
            tilTickets.addView(etTickets);
            tilPrice.addView(etPrice);

            helper = new EventFormHelper(
                    themedContext,
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
            helper.setupCategorySpinner();
        });
    }

    @Test
    public void setupCategorySpinner_populatesExpectedCategories() {
        assertEquals(5, spinnerCategory.getAdapter().getCount());
        assertEquals("Select category", spinnerCategory.getAdapter().getItem(0));
        assertEquals("Music", spinnerCategory.getAdapter().getItem(1));
        assertEquals("Sports", spinnerCategory.getAdapter().getItem(2));
        assertEquals("Movies", spinnerCategory.getAdapter().getItem(3));
        assertEquals("Travel", spinnerCategory.getAdapter().getItem(4));
    }

    @Test
    public void validateFields_returnsFalse_whenEventNameIsEmpty() {
        populateValidFields();
        setText(etEventName, "");

        boolean result = validateOnMainThread();

        assertFalse(result);
        assertEquals("Event name is required", tilEventName.getError());
    }

    @Test
    public void validateFields_returnsFalse_whenVenueIsEmpty() {
        populateValidFields();
        setText(etVenue, "");

        boolean result = validateOnMainThread();

        assertFalse(result);
        assertEquals("Venue is required", tilVenue.getError());
    }

    @Test
    public void validateFields_returnsFalse_whenCategoryIsNotSelected() {
        populateValidFields();
        runOnMainSync(() -> spinnerCategory.setSelection(0));

        boolean result = validateOnMainThread();

        assertFalse(result);
    }

    @Test
    public void validateFields_returnsFalse_whenDateIsEmpty() {
        populateValidFields();
        setText(etDate, "");

        boolean result = validateOnMainThread();

        assertFalse(result);
    }

    @Test
    public void validateFields_returnsFalse_whenTimeIsEmpty() {
        populateValidFields();
        setText(etTime, "");

        boolean result = validateOnMainThread();

        assertFalse(result);
    }

    @Test
    public void validateFields_returnsFalse_whenTicketsIsEmpty() {
        populateValidFields();
        setText(etTickets, "");

        boolean result = validateOnMainThread();

        assertFalse(result);
        assertEquals("Ticket count is required", tilTickets.getError());
    }

    @Test
    public void validateFields_returnsFalse_whenPriceIsEmpty() {
        populateValidFields();
        setText(etPrice, "");

        boolean result = validateOnMainThread();

        assertFalse(result);
        assertEquals("Price is required", tilPrice.getError());
    }

    @Test
    public void validateFields_returnsTrue_whenAllFieldsAreValid() {
        populateValidFields();

        boolean result = validateOnMainThread();

        assertTrue(result);
        assertNull(tilEventName.getError());
        assertNull(tilVenue.getError());
        assertNull(tilTickets.getError());
        assertNull(tilPrice.getError());
    }

    @Test
    public void createEvent_returnsEventWithFormValues() {
        populateValidFields();

        Event event = createEventOnMainThread();

        assertEquals("Jazz Festival", event.getEventName());
        assertEquals("Music", event.getCategory());
        assertEquals("03/15/2026", event.getDate());
        assertEquals("7:00 PM", event.getTime());
        assertEquals("Place des Arts", event.getVenue());
        assertEquals(250, event.getTickets());
        assertEquals(79.99, event.getPrice(), 0.001);
    }

    @Test
    public void createEvent_trimsTextValuesBeforeBuildingModel() {
        setText(etEventName, "  Jazz Festival  ");
        setText(etDate, " 03/15/2026 ");
        setText(etTime, " 7:00 PM ");
        setText(etVenue, " Place des Arts ");
        setText(etTickets, " 250 ");
        setText(etPrice, " 79.99 ");
        runOnMainSync(() -> spinnerCategory.setSelection(1));

        Event event = createEventOnMainThread();

        assertEquals("Jazz Festival", event.getEventName());
        assertEquals("03/15/2026", event.getDate());
        assertEquals("7:00 PM", event.getTime());
        assertEquals("Place des Arts", event.getVenue());
        assertEquals(250, event.getTickets());
        assertEquals(79.99, event.getPrice(), 0.001);
    }

    @Test
    public void createEvent_throwsWhenTicketsAreNotNumeric() {
        populateValidFields();
        setText(etTickets, "two hundred");

        try {
            createEventOnMainThread();
            fail("Expected NumberFormatException");
        } catch (NumberFormatException expected) {
            assertTrue(true);
        }
    }

    @Test
    public void createEvent_throwsWhenPriceIsNotNumeric() {
        populateValidFields();
        setText(etPrice, "free");

        try {
            createEventOnMainThread();
            fail("Expected NumberFormatException");
        } catch (NumberFormatException expected) {
            assertTrue(true);
        }
    }

    private void populateValidFields() {
        setText(etEventName, "Jazz Festival");
        setText(etDate, "03/15/2026");
        setText(etTime, "7:00 PM");
        setText(etVenue, "Place des Arts");
        setText(etTickets, "250");
        setText(etPrice, "79.99");
        runOnMainSync(() -> spinnerCategory.setSelection(1));
    }

    private void setText(TextInputEditText view, String value) {
        runOnMainSync(() -> view.setText(value));
    }

    private boolean validateOnMainThread() {
        AtomicReference<Boolean> result = new AtomicReference<>(false);
        runOnMainSync(() -> result.set(helper.validateFields()));
        return result.get();
    }

    private Event createEventOnMainThread() {
        AtomicReference<Event> result = new AtomicReference<>();
        runOnMainSync(() -> result.set(helper.createEvent()));
        return result.get();
    }

    private void runOnMainSync(Runnable action) {
        InstrumentationRegistry.getInstrumentation().runOnMainSync(action);
    }
}
