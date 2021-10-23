package com.example.authproject.models;

import java.io.Serializable;

public class User implements Serializable {
    private String fullName;
    private String age;
    private String email;
    private String uri;

    public User() {
    }

    public User(String fullName, String age, String email) {
        this.fullName = fullName;
        this.age = age;
        this.email = email;
    }

    public User(String fullName, String age, String email, String uri) {
        this.fullName = fullName;
        this.age = age;
        this.uri = uri;
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
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
