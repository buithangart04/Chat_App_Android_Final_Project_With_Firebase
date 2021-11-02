package com.example.authproject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.example.authproject.databinding.ActivityGroupInfoBinding;
import com.example.authproject.utilities.PreferenceManager;
import com.example.authproject.utilities.ProjectStorage;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class GroupInfoActivity extends AppCompatActivity {
    private ActivityGroupInfoBinding binding;
    private PreferenceManager preferenceManager;
    private List<String> participant;
    private List<String> admin;
    private String groupID;
    private String groupName;
    private String currentUserId;
    private Uri imgData;
    private String uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_info);
        binding = ActivityGroupInfoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        init();
        setListener();
    }

    private void init() {
        Intent intent = getIntent();
//        groupID = intent.getStringExtra(ProjectStorage.KEY_GROUP_ID);
        currentUserId = intent.getStringExtra(ProjectStorage.KEY_USER_EMAIL);
        groupID = "gr3ab54cef-e25e-44a1-94c3-59aa4d95995a";
        ProjectStorage.DOCUMENT_REFERENCE = FirebaseFirestore.getInstance()
                .document(ProjectStorage.KEY_COLLECTION_GROUP + "/" + groupID);
        ProjectStorage.DOCUMENT_REFERENCE.addSnapshotListener((value, error) -> {
            if (value.exists()) {
                groupName = value.getString(ProjectStorage.KEY_GROUP_NAME);
                admin = (List<String>) value.get(ProjectStorage.KEY_GROUP_ADMIN);
                participant = (List<String>) value.get(ProjectStorage.KEY_GROUP_PARTICIPANT);
                uri = value.getString(ProjectStorage.KEY_AVATAR);
                binding.textGroupName.setText(groupName);
                Picasso.get().load(uri)
                        .resize(binding.imgGroupAvatar2.getLayoutParams().width,
                                binding.imgGroupAvatar2.getLayoutParams().height)
                        .into(binding.imgGroupAvatar2);
            } else if (error != null) {
                Log.w("TAG", error);
            }
        });
    }

    private void setListener() {
        binding.textListMember.setOnClickListener(v -> seeListMember());
        binding.textAddParticipant.setOnClickListener(v -> addParticipant());
        binding.textSeePhoto.setOnClickListener(v -> seeListPhoto());
        binding.textLeave.setOnClickListener(v -> leaveGroup());
    }

    private void seeListMember() {
        Intent intent = new Intent(getApplicationContext(), ListGroupParticipantActivity.class);
        intent.putExtra(ProjectStorage.KEY_USER_EMAIL, currentUserId);
        intent.putExtra(ProjectStorage.KEY_GROUP_ID, groupID);
        startActivity(intent);
    }

    private void addParticipant() {
        Intent intent = new Intent(GroupInfoActivity.this, AddParticipantActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(ProjectStorage.KEY_GROUP_CURRENT_PARTICIPANT, (ArrayList<? extends Serializable>) participant);
        intent.putExtras(bundle);
        intent.putExtra(ProjectStorage.KEY_USER_EMAIL, currentUserId);
        intent.putExtra(ProjectStorage.KEY_GROUP_ID, groupID);
        intent.putExtra(ProjectStorage.REMOTE_MSG_TYPE, "current");
        startActivity(intent);
    }

    private void seeListPhoto() {
    }

    private void leaveGroup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Leave Group ?");
        if (admin.size() == 1) {
            builder.setMessage("Are you sure want to leave ? You won't get any new message and " +
                    "all participants will be added as admin");
            builder.setPositiveButton("LEAVE", (dialog, which) -> {
                ProjectStorage.DOCUMENT_REFERENCE.update(ProjectStorage.KEY_GROUP_ADMIN, FieldValue.arrayRemove(currentUserId));
                ProjectStorage.DOCUMENT_REFERENCE.update(ProjectStorage.KEY_GROUP_PARTICIPANT, FieldValue.arrayRemove(currentUserId));
                participant.remove(currentUserId);
                admin = participant;
                for (String s : participant) {
                    ProjectStorage.DOCUMENT_REFERENCE.update(ProjectStorage.KEY_GROUP_ADMIN, FieldValue.arrayUnion(s));
                }
            });
        } else {
            builder.setMessage("Are you sure want to leave ? You won't get any new message");
            builder.setPositiveButton("LEAVE", (dialog, which) -> {
                if (admin.contains(currentUserId)) {
                    ProjectStorage.DOCUMENT_REFERENCE.update(ProjectStorage.KEY_GROUP_ADMIN, FieldValue.arrayRemove(currentUserId));
                }
                ProjectStorage.DOCUMENT_REFERENCE.update(ProjectStorage.KEY_GROUP_PARTICIPANT, FieldValue.arrayRemove(currentUserId));
            });
        }
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finish();

    }
}