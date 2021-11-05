package com.example.authproject.models;

import java.io.Serializable;
import java.util.List;

public class Group implements Serializable  {
    public String groupId;
    public String groupName;
    public List<String> participant;
    public String groupURI;
    public List<String> admin;

    public Group() {
    }

    public Group(String groupId, String groupName, List<String> participant, List<String> admin) {
        this.groupId = groupId;
        this.groupName = groupName;
        this.participant = participant;
        this.admin = admin;
    }
}
