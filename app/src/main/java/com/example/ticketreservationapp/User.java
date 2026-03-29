package com.example.ticketreservationapp;

import java.io.Serializable;
import java.util.ArrayList;

public class User implements Serializable {


    private String name;
    private String email;
    private String phone;
    private String password;
    private boolean admin;
    private ArrayList<String> events;
    public User( String name, String email, String phone, String password, boolean admin, ArrayList<String> events) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.password = password;
        this.admin = admin;
        this.events = events;
    }

    public User() {

    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getPassword() {
        return password;
    }

    public boolean getAdmin() {
        return admin;
    }

    public ArrayList<String> getEvents() {
        return events;
    }

    public void setName(String name) {
        this.name = name;
    }
    public void setEvents(ArrayList<String> events) {
        this.events = events;
    }
}
