package com.example.ticketreservationapp;

public class Event {

    private final String eventName;
    private final String category;
    private final String date;
    private final String time;
    private final String venue;
    private final int tickets;
    private final double price;

    public Event(String eventName, String category, String date, String time, String venue, int tickets, double price) {
        this.eventName = eventName;
        this.category = category;
        this.date = date;
        this.time = time;
        this.venue = venue;
        this.tickets = tickets;
        this.price = price;
    }

    public String getEventName() {
        return eventName;
    }

    public String getCategory() {
        return category;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getVenue() {
        return venue;
    }

    public int getTickets() {
        return tickets;
    }

    public double getPrice() {
        return price;
    }
}
