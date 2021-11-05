package com.example.authproject.utilities;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ProjectStorage {
    public static final String KEY_PREFERENCE_NAME = "firebase";
    // Key users in firebase
    public static final String KEY_COLLECTION_USERS = "users";
    public static final String KEY_NAME = "fullName";
    public static final String KEY_USER = "user";
    public static final String KEY_USER_EMAIL = "email";
    public static final String KEY_FRIEND = "friend";
    //Key chat in firebase
    public static final String KEY_COLLECTION_CHAT = "chat";

    public static final String KEY_SENDER_EMAIL = "senderEmail";

    public static final String KEY_RECEIVER_EMAIL = "receiverEmail";
    public static final String KEY_MESSAGE = "message";
    public static final String KEY_TIMESTAMP = "timestamp";
    public static final String KEY_MESSAGE_TYPE = "type";
    // access firebase database
    public static  FirebaseFirestore DATABASE_REFERENCE ;
    public static  StorageReference STORAGE_REFERENCE ;
    // constructor
    static {
        DATABASE_REFERENCE = FirebaseFirestore.getInstance();
        STORAGE_REFERENCE =  FirebaseStorage.getInstance().getReference();
    }

    public static final int PICK_IMAGE_REQUEST = 2;

}
