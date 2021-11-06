package com.example.authproject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.authproject.network.ApiClient;
import com.example.authproject.network.ApiService;
import com.example.authproject.utilities.ProjectStorage;
import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import org.jitsi.meet.sdk.JitsiMeetConferenceOptions;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import org.jitsi.meet.sdk.JitsiMeetActivity;

public class InComingInvitationActivity extends AppCompatActivity {
    private String meetingType = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in_coming_invitation);

        ImageView imageMeetingType = findViewById(R.id.imageMeetingType);
        meetingType = getIntent().getStringExtra(ProjectStorage.REMOTE_MSG_MEETING_TYPE);

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
        TextView textIncomingMeetingInvitation = findViewById(R.id.textIncomingMeetingInvitation);

        String avatarUrl = getIntent().getStringExtra(ProjectStorage.KEY_AVATAR);
        if (avatarUrl != null) {
            Transformation transformation = new RoundedTransformationBuilder()
                    .borderColor(Color.BLACK)
                    .borderWidthDp(3)
                    .cornerRadiusDp(30)
                    .oval(false)
                    .build();
            Picasso.get()
                    .load(avatarUrl)
                    .fit()
                    .transform(transformation)
                    .into(imageAvatar);
        }
        if (getIntent().getStringExtra(ProjectStorage.KEY_USER_EMAIL).isEmpty()) {
            textIncomingMeetingInvitation.setText("Incoming group call invitation");
            textEmail.setText("Group");
        } else {
            textEmail.setText(getIntent().getStringExtra(ProjectStorage.KEY_USER_EMAIL));
        }
        textUsername.setText(getIntent().getStringExtra(ProjectStorage.KEY_NAME));

        ImageView imageAcceptInvitation = findViewById(R.id.imageAcceptInvitation);
        imageAcceptInvitation.setOnClickListener(view -> {
            sendInvitationResponse(
                    ProjectStorage.REMOTE_MSG_INVITATION_ACCEPTED,
                    getIntent().getStringExtra(ProjectStorage.REMOTE_MSG_INVITER_TOKEN)
            );
        });

        ImageView imageRejectInvitation = findViewById(R.id.imageRejectInvitation);
        imageRejectInvitation.setOnClickListener(view -> {
            sendInvitationResponse(
                    ProjectStorage.REMOTE_MSG_INVITATION_REJECTED,
                    getIntent().getStringExtra(ProjectStorage.REMOTE_MSG_INVITER_TOKEN)
            );
        });
    }

    private void sendInvitationResponse(String type, String receiverToken) {
        try {
            JSONArray tokens = new JSONArray();
            tokens.put(receiverToken);

            JSONObject body = new JSONObject();
            JSONObject data = new JSONObject();

            data.put(ProjectStorage.REMOTE_MSG_TYPE, ProjectStorage.REMOTE_MSG_INVITATION_RESPONSE);
            data.put(ProjectStorage.REMOTE_MSG_INVITATION_RESPONSE, type);

            body.put(ProjectStorage.REMOTE_MSG_DATA, data);
            body.put(ProjectStorage.REMOTE_MSG_REGISTRATION_IDS, tokens);

            sendRemoteMessage(body.toString(), type);

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
                    if (type.equals(ProjectStorage.REMOTE_MSG_INVITATION_ACCEPTED)) {
                        try {
                            URL serverUrl = new URL("https://meet.jit.si/");
                            JitsiMeetConferenceOptions.Builder builder = new JitsiMeetConferenceOptions.Builder();
                            builder.setServerURL(serverUrl);
                            builder.setWelcomePageEnabled(false);
                            builder.setFeatureFlag("invite.enabled",false);
                            builder.setFeatureFlag("raise-hand.enabled", false);
                            builder.setRoom(getIntent().getStringExtra(ProjectStorage.REMOTE_MSG_MEETING_ROOM));
                            if (meetingType.equals("audio")) {
                                builder.setFeatureFlag("video-mute.enabled", false);
                                builder.setFeatureFlag("reactions.enabled", false);
                                builder.setFeatureFlag("video-share.enabled", false);
                                builder.setFeatureFlag("android.screensharing.enabled", false);
                                builder.setFeatureFlag("android.screensharing.enabled", false);
                                builder.setVideoMuted(true);
                            }
                            JitsiMeetActivity.launch(InComingInvitationActivity.this, builder.build());
                            finish();
                        } catch (Exception exception) {
                            Toast.makeText(InComingInvitationActivity.this, exception.getMessage(), Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    } else {
                        Toast.makeText(InComingInvitationActivity.this, "Invitation Rejected", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                } else {
                    Toast.makeText(InComingInvitationActivity.this, response.message(), Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                Toast.makeText(InComingInvitationActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private BroadcastReceiver invitationResponseReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String type = intent.getStringExtra(ProjectStorage.REMOTE_MSG_INVITATION_RESPONSE);
            if (type != null) {
                if (type.equals(ProjectStorage.REMOTE_MSG_INVITATION_CANCELLED)) {
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