package com.example.ticketreservationapp;

import java.io.Serializable;

public class Event implements Serializable {

    private String id;
    private String eventName;
    private String category;
    private String date;
    private String time;
    private String venue;
    private int tickets;
    private double price;

    public Event() {
        // Required for Firestore
    }

    public Event(String eventName, String category, String date, String time, String venue, int tickets, double price) {
        this.eventName = eventName;
        this.category = category;
        this.date = date;
        this.time = time;
        this.venue = venue;
        this.tickets = tickets;
        this.price = price;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getVenue() {
        return venue;
    }

    public void setVenue(String venue) {
        this.venue = venue;
    }

    public int getTickets() {
        return tickets;
    }

    public void setTickets(int tickets) {
        this.tickets = tickets;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
