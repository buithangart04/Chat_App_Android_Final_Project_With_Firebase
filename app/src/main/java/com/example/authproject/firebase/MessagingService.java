package com.example.authproject.firebase;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationManagerCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.authproject.InComingInvitationActivity;
import com.example.authproject.R;
import com.example.authproject.utilities.ProjectStorage;
import com.google.firebase.messaging.Constants;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Random;

public class MessagingService extends FirebaseMessagingService {
    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        Log.d("FCM", "message : " + token);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        String type = remoteMessage.getData().get(ProjectStorage.REMOTE_MSG_TYPE);
        if (type != null) {
            if (type.equals(ProjectStorage.REMOTE_MSG_INVITATION)) {
                Intent intent = new Intent(getApplicationContext(), InComingInvitationActivity.class);
                intent.putExtra(
                        ProjectStorage.REMOTE_MSG_MEETING_TYPE,
                        remoteMessage.getData().get(ProjectStorage.REMOTE_MSG_MEETING_TYPE)
                );
                intent.putExtra(
                        ProjectStorage.KEY_AVATAR,
                        remoteMessage.getData().get(ProjectStorage.KEY_AVATAR)
                );
                intent.putExtra(
                        ProjectStorage.KEY_NAME,
                        remoteMessage.getData().get(ProjectStorage.KEY_NAME)
                );
                intent.putExtra(
                        ProjectStorage.KEY_USER_EMAIL,
                        remoteMessage.getData().get(ProjectStorage.KEY_USER_EMAIL)
                );
                intent.putExtra(
                        ProjectStorage.REMOTE_MSG_INVITER_TOKEN,
                        remoteMessage.getData().get(ProjectStorage.REMOTE_MSG_INVITER_TOKEN)
                );
                intent.putExtra(
                        ProjectStorage.REMOTE_MSG_MEETING_ROOM,
                        remoteMessage.getData().get(ProjectStorage.REMOTE_MSG_MEETING_ROOM)
                );
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            } else if (type.equals(ProjectStorage.REMOTE_MSG_INVITATION_RESPONSE)) {
                Intent intent = new Intent(ProjectStorage.REMOTE_MSG_INVITATION_RESPONSE);
                intent.putExtra(
                        ProjectStorage.REMOTE_MSG_INVITATION_RESPONSE,
                        remoteMessage.getData().get(ProjectStorage.REMOTE_MSG_INVITATION_RESPONSE)
                );
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
            }
        } else {
            String title = remoteMessage.getNotification().getTitle();
            String text = remoteMessage.getNotification().getBody();
            final String ChanelID = "HEADS_UP_NOTI";
            NotificationChannel channel = new NotificationChannel(
                    ChanelID,
                    "Heads up notification",
                    NotificationManager.IMPORTANCE_HIGH
            );
            getSystemService(NotificationManager.class).createNotificationChannel(channel);
            Notification.Builder notification = new Notification.Builder(this, ChanelID)
                    .setContentTitle(title)
                    .setContentText(text)
                    .setSmallIcon(R.drawable.ic_launcher_background)
                    .setAutoCancel(true);
            NotificationManagerCompat.from(this).notify(new Random().nextInt() + 1000, notification.build());
            super.onMessageReceived(remoteMessage);
            Log.d("FCM", "message : " + remoteMessage.getNotification().getBody());
        }
    }
}
