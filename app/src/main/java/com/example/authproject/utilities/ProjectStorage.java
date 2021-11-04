package com.example.authproject.utilities;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;

public class ProjectStorage {
    public static final String KEY_PREFERENCE_NAME = "firebase";
    // Key users in firebase
    public static final String KEY_COLLECTION_USERS = "users";
    public static final String KEY_NAME = "fullName";
    public static final String KEY_USER = "user";
    public static final String KEY_USER_EMAIL = "email";
    public static final String KEY_FCM_TOKEN = "token";
    public static final String KEY_USER_ID = "id";
    public static final String KEY_AVATAR = "uri";

    //Color
    public static final String KEY_COLOR_DISABLED = "#d7dadc";
    public static final String KEY_COLOR_NAVIGATE = "#0f7dd6";
    //Key chat in firebase
    public static final String KEY_COLLECTION_CHAT = "chat";
    public static final String KEY_SENDER_EMAIL = "senderEmail";
    public static final String KEY_RECEIVER_EMAIL = "receiverEmail";
    public static final String KEY_MESSAGE = "message";
    public static final String KEY_TIMESTAMP = "timestamp";
    public static final String KEY_MESSAGE_TYPE = "type";
    public static final String KEY_FILE_NAME= "fileName";

    //Key group in firebase
    public static final String KEY_GROUP_ID = "groupId";
    public static final String KEY_GROUP_PARTICIPANT = "participant";
    public static final String KEY_GROUP_CURRENT_PARTICIPANT = "current_participant";
    public static final String KEY_COLLECTION_GROUP = "group";
    public static final String KEY_GROUP_ADMIN = "admin";
    public static final String KEY_GROUP_NAME = "groupName";
    public static final String KEY_GROUP_URI = "groupURI";

    //Key call in firebase
    public static final String REMOTE_MSG_AUTHORIZATION = "Authorization";
    public static final String REMOTE_MSG_CONTENT_TYPE = "Content-Type";

    public static final String REMOTE_MSG_TYPE = "type";
    public static final String REMOTE_MSG_INVITATION = "invitation";
    public static final String REMOTE_MSG_MEETING_TYPE = "meetingType";
    public static final String REMOTE_MSG_INVITER_TOKEN = "inviterToken";
    public static final String REMOTE_MSG_DATA = "data";
    public static final String REMOTE_MSG_REGISTRATION_IDS = "registration_ids";
    public static final String REMOTE_MSG_INVITATION_RESPONSE = "invitationResponse";
    public static final String REMOTE_MSG_INVITATION_ACCEPTED= "accepted";
    public static final String REMOTE_MSG_INVITATION_REJECTED = "rejected";
    public static final String REMOTE_MSG_INVITATION_CANCELLED = "cancelled";

    public static final String REMOTE_MSG_MEETING_ROOM = "meetingRoom";

    // access firebase database
    public static  FirebaseFirestore DATABASE_REFERENCE ;
    public static  StorageReference STORAGE_REFERENCE ;
    public static DocumentReference DOCUMENT_REFERENCE;

    // constructor
    static {
        DATABASE_REFERENCE = FirebaseFirestore.getInstance();
        STORAGE_REFERENCE =  FirebaseStorage.getInstance().getReference();
    }

    public static HashMap<String, String> getRemoteMessageHeaders() {
        HashMap<String,String> headers = new HashMap<>();
        headers.put(
                ProjectStorage.REMOTE_MSG_AUTHORIZATION,
                "key=AAAAmt_76_g:APA91bERsi79KfJ1x5fBQAhdUyRXqgB1rP2c4LnMsASqtLpkaLUQxxdc3JsD6G0dFxm2XfTHaiK94-aN39-14Y2A8DzNOmbk6VNaw8qH_F9uRgdm1n6LN4Ejj5hQaEqGAb2Wes5FyxJc"
        );
        headers.put(ProjectStorage.REMOTE_MSG_CONTENT_TYPE,"application/json");
        return headers;
    }

    public static final int PICK_IMAGE_REQUEST = 2;

}
