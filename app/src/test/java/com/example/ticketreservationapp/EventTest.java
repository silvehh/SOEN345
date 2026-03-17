package com.example.ticketreservationapp;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class EventTest {

    @Test
    public void constructor_assignsAllFields() {
        Event event = new Event(
                "Jazz Festival",
                "Music",
                "03/15/2026",
                "7:00 PM",
                "Place des Arts",
                250,
                79.99
        );

        assertEquals("Jazz Festival", event.getEventName());
        assertEquals("Music", event.getCategory());
        assertEquals("03/15/2026", event.getDate());
        assertEquals("7:00 PM", event.getTime());
        assertEquals("Place des Arts", event.getVenue());
        assertEquals(250, event.getTickets());
        assertEquals(79.99, event.getPrice(), 0.001);
    }
}
