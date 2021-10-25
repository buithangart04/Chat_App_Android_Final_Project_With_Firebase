package com.example.authproject.models;

import java.util.Date;

public class GroupChatMessage {
    public String senderEmail, message, dateTime;

    public GroupChatMessage(String senderEmail, String message, String dateTime) {
        this.senderEmail = senderEmail;
        this.message = message;
        this.dateTime = dateTime;
    }

    public GroupChatMessage() {
    }

    public String getSenderEmail() {
        return senderEmail;
    }

    public void setSenderEmail(String senderEmail) {
        this.senderEmail = senderEmail;
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
}
