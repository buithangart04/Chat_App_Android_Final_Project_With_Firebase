package com.example.authproject;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.authproject.utilities.Constants;

public class InComingInvitationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in_coming_invitation);

        ImageView imageMeetingType = findViewById(R.id.imageMeetingType);
        String meetingType = getIntent().getStringExtra(Constants.REMOTE_MSG_MEETING_TYPE);

        if (meetingType != null) {
            if (meetingType.equals("video")){
                imageMeetingType.setImageResource(R.drawable.ic_video);
            }
        }

        TextView textUsername = findViewById(R.id.textUsername);
        TextView textEmail = findViewById(R.id.textEmail);

        textUsername.setText(getIntent().getStringExtra(Constants.KEY_NAME));
        textEmail.setText(getIntent().getStringExtra(Constants.KEY_USER_EMAIL));
    }
}