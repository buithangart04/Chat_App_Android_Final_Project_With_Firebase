package com.example.authproject.utilities;

import java.util.HashMap;

public class Constants {
    public static final String KEY_COLLECTION_USERS = "users";
    public static final String KEY_PREFERENCE_NAME = "firebase";
    public static final String KEY_NAME = "fullName";
    public static final String KEY_USER = "user";
    public static final String KEY_USER_EMAIL = "email";
    public static final String KEY_COLLECTION_CHAT = "chat";
    public static final String KEY_FCM_TOKEN = "fcm_token";
    public static final String KEY_USER_ID= "user_id";

    public static final String KEY_SENDER_EMAIL = "senderEmail";

    public static final String KEY_RECEIVER_EMAIL = "receiverEmail";
    public static final String KEY_MESSAGE = "message";
    public static final String KEY_TIMESTAMP = "timestamp";

    public static final String REMOTE_MSG_AUTHORIZATION = "Authorization";
    public static final String REMOTE_MSG_CONTENT_TYPE = "Content-Type";

    public static final String REMOTE_MSG_TYPE = "type";
    public static final String REMOTE_MSG_INVITATION = "invitation";
    public static final String REMOTE_MSG_MEETING_TYPE = "meetingType";
    public static final String REMOTE_MSG_INVITER_TOKEN= "inviterToken";
    public static final String REMOTE_MSG_DATA = "data";
    public static final String REMOTE_MSG_REGISTRATION_IDS = "registration_ids";

    public static HashMap<String, String> getRemoteMessageHeaders() {
        HashMap<String,String> headers = new HashMap<>();
        headers.put(
               Constants.REMOTE_MSG_AUTHORIZATION,
               "key=AAAAmt_76_g:APA91bERsi79KfJ1x5fBQAhdUyRXqgB1rP2c4LnMsASqtLpkaLUQxxdc3JsD6G0dFxm2XfTHaiK94-aN39-14Y2A8DzNOmbk6VNaw8qH_F9uRgdm1n6LN4Ejj5hQaEqGAb2Wes5FyxJc"
        );
        headers.put(Constants.REMOTE_MSG_CONTENT_TYPE,"application/json");
        return headers;
    }
}
