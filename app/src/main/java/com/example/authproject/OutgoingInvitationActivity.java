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
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.authproject.models.User;
import com.example.authproject.network.ApiClient;
import com.example.authproject.network.ApiService;
import com.example.authproject.utilities.PreferenceManager;
import com.example.authproject.utilities.ProjectStorage;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

import org.jitsi.meet.sdk.JitsiMeetActivity;
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OutgoingInvitationActivity extends AppCompatActivity {

    private PreferenceManager preferenceManager;
    private String inviterToken = null;
    private String meetingRoom = null;
    private String meetingType = null;

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

        ImageView imageAvatar = findViewById(R.id.imageAvatar);
        TextView textUsername = findViewById(R.id.textUsername);
        TextView textEmail = findViewById(R.id.textEmail);

        String avatarUrl = getIntent().getStringExtra(ProjectStorage.KEY_AVATAR);
        if (avatarUrl != null) {
//            new DownloadImageTask((ImageView) findViewById(R.id.imageAvatar))
//                    .execute(avatarUrl);
            imageAvatar.setImageBitmap(getBitmapFromURL(avatarUrl));
        }
        User user = (User) getIntent().getSerializableExtra("user");
        if (user != null) {
            textUsername.setText(user.getFullName());
            textEmail.setText(user.getEmail());
        }

        ImageView imageStopInvitation = findViewById(R.id.imageStopInvitation);
        imageStopInvitation.setOnClickListener(v -> {
            if (user != null) {
                cancelInvitation(user.token);
            }
        });
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            inviterToken = task.getResult();
                            if (meetingType != null && user != null) {
                                initiateMeeting(meetingType, user.token);
                            }
                        }
                    }
                });

    }

    public static Bitmap getBitmapFromURL(String src) {
        try {
            Log.e("src",src);
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            Log.e("Bitmap","returned");
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("Exception",e.getMessage());
            return null;
        }
    }

    private void initiateMeeting(String meetingType, String receiverToken) {
        try {
            JSONArray tokens = new JSONArray();
            tokens.put(receiverToken);
            JSONObject body = new JSONObject();
            JSONObject data = new JSONObject();
            data.put(ProjectStorage.REMOTE_MSG_TYPE, ProjectStorage.REMOTE_MSG_INVITATION);
            data.put(ProjectStorage.REMOTE_MSG_MEETING_TYPE, meetingType);
            data.put(ProjectStorage.KEY_NAME, preferenceManager.getString(ProjectStorage.KEY_NAME));
            data.put(ProjectStorage.KEY_USER_EMAIL, preferenceManager.getString(ProjectStorage.KEY_USER_EMAIL));
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

    private void cancelInvitation(String receiverToken) {
        try {
            JSONArray tokens = new JSONArray();
            tokens.put(receiverToken);

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
                    Toast.makeText(context, "Invitation rejected", Toast.LENGTH_SHORT).show();
                    finish();
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
}