package com.example.authproject.models;

public class GroupChatMessage {
    public String senderId, message, dateTime, type,receiverId;

    public GroupChatMessage(String senderId, String message, String dateTime, String type,String receiverId) {
        this.senderId = senderId;
        this.message = message;
        this.dateTime = dateTime;
        this.type = type;
        this.receiverId=receiverId;
    }

    public GroupChatMessage() {
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
