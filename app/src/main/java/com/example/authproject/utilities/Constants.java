package com.example.authproject.utilities;

import com.google.firebase.firestore.FirebaseFirestore;

public class Constants {
    public static final String KEY_COLLECTION_USERS = "users";
    public static final String KEY_PREFERENCE_NAME = "firebase";
    public static final String KEY_NAME = "fullName";
    public static final String KEY_USER = "user";
    public static final String KEY_USER_AGE = "age";
    public static final String KEY_USER_EMAIL = "email";
    public static final String KEY_USER_STATUS = "status";
    public static final String KEY_COLLECTION_CHAT = "chat";
    public static final String KEY_SENDER_EMAIL = "senderEmail";
    public static final String KEY_RECEIVER_EMAIL = "receiverEmail";
    public static final String KEY_MESSAGE = "message";
    public static final String KEY_TIMESTAMP = "timestamp";
    public static FirebaseFirestore DATABASE;

    static {
        DATABASE = FirebaseFirestore.getInstance();
    }

}
