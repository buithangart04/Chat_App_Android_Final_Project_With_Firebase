package com.example.authproject.models;

import java.util.List;

public class Group {
    private String id;
    private String groupName;
    private List<String> email;
    private String uri;
    private List<String> admin;

    public Group() {
    }

    public Group(String id, String groupName, List<String> email, String uri, List<String> admin) {
        this.id = id;
        this.groupName = groupName;
        this.email = email;
        this.uri = uri;
        this.admin = admin;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public List<String> getEmail() {
        return email;
    }

    public List<String> getAdmin() {
        return admin;
    }

    public void setAdmin(List<String> admin) {
        this.admin = admin;
    }

    public void setEmail(List<String> email) {
        this.email = email;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }
}
