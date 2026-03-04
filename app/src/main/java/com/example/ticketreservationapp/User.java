package com.example.ticketreservationapp;

public class User {

    private String id;
    private String name;
    private String email;
    private String phone;
    private String password;
    public User() {}

    public User(String id, String name, String email, String phone, String password) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.password = password;
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


    public String getId() {
        return id;
    }
}
