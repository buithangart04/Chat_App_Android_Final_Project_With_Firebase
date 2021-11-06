package com.example.authproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.authproject.models.Group;
import com.example.authproject.models.User;
import com.example.authproject.network.ApiClient;
import com.example.authproject.network.ApiService;
import com.example.authproject.utilities.PreferenceManager;
import com.example.authproject.utilities.ProjectStorage;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import org.jitsi.meet.sdk.JitsiMeetActivity;
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OutgoingInvitationActivity extends AppCompatActivity {

    private PreferenceManager preferenceManager;
    private String inviterToken = null;
    private String meetingRoom = null;
    private String meetingType = null;
    private ImageView imageAvatar;
    private TextView textUsername, textEmail, textSendingInvitation;
    private Group group;
    private CountDownTimer Timer;

    private int rejectionCount = 0;
    private int totalReceivers = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_outgoing_invitation);

        preferenceManager = new PreferenceManager(getApplicationContext());

        ImageView imageMeetingType = findViewById(R.id.imageMeetingType);
        meetingType = getIntent().getStringExtra("type");

        if (meetingType != null) {
            if (meetingType.equals("video")) {
                imageMeetingType.setImageResource(R.drawable.ic_callvideo);
            } else {
                imageMeetingType.setImageResource(R.drawable.ic_call);
            }
        }

        imageAvatar = findViewById(R.id.imageAvatar);
        textUsername = findViewById(R.id.textUsername);
        textEmail = findViewById(R.id.textEmail);
        textSendingInvitation = findViewById(R.id.textSendingInvitation);
        User user = (User) getIntent().getSerializableExtra("user");
        group = (Group) getIntent().getSerializableExtra("group");
        if (user != null) {
            textUsername.setText(user.getFullName());
            textEmail.setText(user.getEmail());
            if (user.getUri() != null) {
                Transformation transformation = new RoundedTransformationBuilder()
                        .borderColor(Color.BLACK)
                        .borderWidthDp(3)
                        .cornerRadiusDp(30)
                        .oval(false)
                        .build();
                Picasso.get()
                        .load(user.getUri())
                        .fit()
                        .transform(transformation)
                        .into(imageAvatar);
            }
        }

        if (group != null) {
            textUsername.setText(group.groupName);
            textSendingInvitation.setText("Send group call invitation");
            if (group.groupURI != null) {
                Transformation transformation = new RoundedTransformationBuilder()
                        .borderColor(Color.BLACK)
                        .borderWidthDp(3)
                        .cornerRadiusDp(30)
                        .oval(false)
                        .build();
                Picasso.get()
                        .load(group.groupURI)
                        .fit()
                        .transform(transformation)
                        .into(imageAvatar);
            }
        }

        ImageView imageStopInvitation = findViewById(R.id.imageStopInvitation);
        Timer = new CountDownTimer(30000, 1000) {
            @Override
            public void onTick(long l) {

            }

            public void onFinish() {
                if (getIntent().getBooleanExtra("isGroup",true)) {
                    Type type = new TypeToken<ArrayList<User>>(){}.getType();
                    ArrayList<User> groupUser = new Gson().fromJson(getIntent().getStringExtra("groupUser"), type);
                    cancelInvitation(null, groupUser);
                } else {
                    if (user != null) {
                        cancelInvitation(user.getToken(), null);
                    }
                }
                finish();
                cancel();
            }
        }.start();
        imageStopInvitation.setOnClickListener(v -> {
            if (getIntent().getBooleanExtra("isGroup",true)) {
                Type type = new TypeToken<ArrayList<User>>(){}.getType();
                ArrayList<User> groupUser = new Gson().fromJson(getIntent().getStringExtra("groupUser"), type);
                cancelInvitation(null, groupUser);
            } else {
                if (user != null) {
                    cancelInvitation(user.getToken(), null);
                }
            }
            Timer.cancel();
        });
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            inviterToken = task.getResult();
                            if (meetingType != null) {
                                if (getIntent().getBooleanExtra("isGroup",true)) {
                                    Type type = new TypeToken<ArrayList<User>>(){}.getType();
                                    ArrayList<User> groupUser = new Gson().fromJson(getIntent().getStringExtra("groupUser"), type);
                                    if (groupUser != null) {
                                        totalReceivers = groupUser.size();
                                    }
                                    initiateMeeting(meetingType, null, groupUser);
                                } else {
                                    if (user != null) {
                                        totalReceivers = 1;
                                        initiateMeeting(meetingType, user.getToken(), null);
                                    }
                                }
                            }
                        }
                    }
                });
    }

    private void initiateMeeting(String meetingType, String receiverToken, ArrayList<User> groupUser) {
        try {
            JSONArray tokens = new JSONArray();

            if (receiverToken != null) {
                tokens.put(receiverToken);
            }

            if (groupUser != null && groupUser.size() > 0) {
                for (int i = 0; i < groupUser.size() ; i++) {
                    tokens.put(groupUser.get(i).getToken());
                }
            }

            JSONObject body = new JSONObject();
            JSONObject data = new JSONObject();
            data.put(ProjectStorage.REMOTE_MSG_TYPE, ProjectStorage.REMOTE_MSG_INVITATION);
            data.put(ProjectStorage.REMOTE_MSG_MEETING_TYPE, meetingType);
            if (groupUser != null && groupUser.size() > 0) {
                data.put(ProjectStorage.KEY_NAME, group.groupName);
                data.put(ProjectStorage.KEY_USER_EMAIL, "");
                data.put(ProjectStorage.KEY_AVATAR, group.groupURI);
            } else {
                data.put(ProjectStorage.KEY_NAME, preferenceManager.getString(ProjectStorage.KEY_NAME));
                data.put(ProjectStorage.KEY_USER_EMAIL, preferenceManager.getString(ProjectStorage.KEY_USER_EMAIL));
                data.put(ProjectStorage.KEY_AVATAR, preferenceManager.getString(ProjectStorage.KEY_AVATAR));
            }
            data.put(ProjectStorage.REMOTE_MSG_INVITER_TOKEN, inviterToken);

            meetingRoom = preferenceManager.getString(ProjectStorage.KEY_USER_ID) + "_" +
                    UUID.randomUUID().toString().substring(0, 5);
            data.put(ProjectStorage.REMOTE_MSG_MEETING_ROOM, meetingRoom);

            body.put(ProjectStorage.REMOTE_MSG_DATA, data);
            body.put(ProjectStorage.REMOTE_MSG_REGISTRATION_IDS, tokens);

            sendRemoteMessage(body.toString(), ProjectStorage.REMOTE_MSG_INVITATION);

        } catch (Exception ex) {
            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void sendRemoteMessage(String remoteMessageBody, String type) {
        ApiClient.getClient().create(ApiService.class).sendRemoteMessage(
                ProjectStorage.getRemoteMessageHeaders(), remoteMessageBody
        ).enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (response.isSuccessful()) {
                    if (type.equals(ProjectStorage.REMOTE_MSG_INVITATION)) {
                        Toast.makeText(OutgoingInvitationActivity.this, "Invitation sent successfully", Toast.LENGTH_SHORT).show();
                    } else if (type.equals(ProjectStorage.REMOTE_MSG_INVITATION_RESPONSE)) {
                        Toast.makeText(OutgoingInvitationActivity.this, "Cancelled Invitation", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                } else {
                    Toast.makeText(OutgoingInvitationActivity.this, response.message(), Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                Toast.makeText(OutgoingInvitationActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void cancelInvitation(String receiverToken, ArrayList<User> groupUser) {
        try {
            JSONArray tokens = new JSONArray();

            if (receiverToken != null) {
                tokens.put(receiverToken);
            }
            if (groupUser != null && groupUser.size() > 0) {
                for (User user: groupUser) {
                    tokens.put(user.getToken());
                }
            }

            JSONObject body = new JSONObject();
            JSONObject data = new JSONObject();

            data.put(ProjectStorage.REMOTE_MSG_TYPE, ProjectStorage.REMOTE_MSG_INVITATION_RESPONSE);
            data.put(ProjectStorage.REMOTE_MSG_INVITATION_RESPONSE, ProjectStorage.REMOTE_MSG_INVITATION_CANCELLED);

            body.put(ProjectStorage.REMOTE_MSG_DATA, data);
            body.put(ProjectStorage.REMOTE_MSG_REGISTRATION_IDS, tokens);

            sendRemoteMessage(body.toString(), ProjectStorage.REMOTE_MSG_INVITATION_RESPONSE);

        } catch (Exception ex) {
            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private BroadcastReceiver invitationResponseReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String type = intent.getStringExtra(ProjectStorage.REMOTE_MSG_INVITATION_RESPONSE);
            if (type != null) {
                if (type.equals(ProjectStorage.REMOTE_MSG_INVITATION_ACCEPTED)) {
                    try {
                        URL serverUrl = new URL("https://meet.jit.si/");
                        JitsiMeetConferenceOptions.Builder builder = new JitsiMeetConferenceOptions.Builder();
                        builder.setServerURL(serverUrl);
                        builder.setWelcomePageEnabled(false);
                        builder.setFeatureFlag("invite.enabled",false);
                        builder.setFeatureFlag("raise-hand.enabled", false);
                        builder.setRoom(meetingRoom);
                        if (meetingType.equals("audio")) {
                            builder.setFeatureFlag("video-mute.enabled", false);
                            builder.setFeatureFlag("reactions.enabled", false);
                            builder.setFeatureFlag("video-share.enabled", false);
                            builder.setFeatureFlag("android.screensharing.enabled", false);
                            builder.setFeatureFlag("android.screensharing.enabled", false);
                            builder.setVideoMuted(true);
                        }
                        JitsiMeetActivity.launch(OutgoingInvitationActivity.this, builder.build());
                        finish();
                    } catch (Exception exception) {
                        Toast.makeText(OutgoingInvitationActivity.this, exception.getMessage(), Toast.LENGTH_SHORT).show();
                        finish();
                    }
                } else if (type.equals(ProjectStorage.REMOTE_MSG_INVITATION_REJECTED)) {
                    rejectionCount += 1;
                    if (rejectionCount == totalReceivers) {
                        Toast.makeText(context, "Invitation rejected", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
            }
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(
                invitationResponseReceiver,
                new IntentFilter(ProjectStorage.REMOTE_MSG_INVITATION_RESPONSE)
        );
    }

    @Override
    protected void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(
                invitationResponseReceiver
        );
    }

    @Override
    protected void onDestroy() {
        Timer.cancel();
        super.onDestroy();
    }
}