package com.example.authproject.models;

import java.util.List;

public class Group {
    public String groupId;
    public String groupName;
    public List<String> user;
    public String uri;
    public List<String> admin;

    public Group(String groupId, String groupName, List<String> user, List<String> admin) {
        this.groupId = groupId;
        this.groupName = groupName;
        this.user = user;
        this.admin = admin;
    }
}
