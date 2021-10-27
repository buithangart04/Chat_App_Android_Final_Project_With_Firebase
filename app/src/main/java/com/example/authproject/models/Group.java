package com.example.authproject.models;

import java.util.List;

public class Group {
    private String id;
    private String groupName;
    private List<String> user;
    private String uri;
    private List<String> admin;

    public Group() {
    }

    public Group(String id, String groupName, List<String> user, String uri, List<String> admin) {
        this.id = id;
        this.groupName = groupName;
        this.user = user;
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

    public List<String> getUser() {
        return user;
    }

    public List<String> getAdmin() {
        return admin;
    }

    public void setAdmin(List<String> admin) {
        this.admin = admin;
    }

    public void setUser(List<String> user) {
        this.user = user;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }
}
