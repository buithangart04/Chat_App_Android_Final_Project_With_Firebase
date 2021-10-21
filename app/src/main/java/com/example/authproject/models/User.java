package com.example.authproject.models;

import java.io.Serializable;

public class User implements Serializable {
    private String fullName;
    private String age;
    private String email;
    private String imgUrl;
    public String token;

    public User() {
    }

    public User(String fullName, String age, String email, String imgUrl) {
        this.fullName = fullName;
        this.age = age;
        this.email = email;
        this.imgUrl = imgUrl;
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

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }
}
