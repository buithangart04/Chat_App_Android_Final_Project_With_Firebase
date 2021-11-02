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

    public User(String id, String fullName, String email) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
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

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }
}
