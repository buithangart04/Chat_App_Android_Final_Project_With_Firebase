package com.example.authproject.models;

import java.io.Serializable;

public class User implements Serializable {
    private String id;
    private String fullName;
    private String email;
    public String token;
    private String uri;

    public User() {
    }

    public User(String fullName, String email) {
        this.fullName = fullName;
        this.email = email;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }
}
