package com.example.ticketreservationapp;

import java.io.Serializable;
import java.util.ArrayList;

public class User implements Serializable {


    private String name;
    private String email;
    private String phone;
    private String password;
    private boolean isAdmin;
    private ArrayList<String> events;
    public User() {}

    public User( String name, String email, String phone, String password, boolean isAdmin, ArrayList<String> events) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.password = password;
        this.isAdmin = isAdmin;
        this.events = events;
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


    public boolean isAdmin() {
        return isAdmin;
    }

    public ArrayList<String> getEvents() {
        return events;
    }
}
