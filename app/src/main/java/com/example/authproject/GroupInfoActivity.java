package com.example.authproject;

import static com.example.authproject.utilities.ProjectStorage.PICK_IMAGE_REQUEST;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.authproject.databinding.ActivityGroupInfoBinding;
import com.example.authproject.listeners.UploadFileSuccessListener;
import com.example.authproject.utilities.FileUtilities;
import com.example.authproject.utilities.PreferenceManager;
import com.example.authproject.utilities.ProjectStorage;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class GroupInfoActivity extends AppCompatActivity implements UploadFileSuccessListener {
    private ActivityGroupInfoBinding binding;
    private List<String> participantId;
    private List<String> adminId;
    private String groupId;
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            imgData = data.getData();
            binding.imgGroupAvatar2.setImageURI(imgData);
            new FileUtilities()
                    .uploadFile(GroupInfoActivity.this, GroupInfoActivity.this, imgData);
        }
    }

    private void init() {
        currentUserId = "us3ddd37ba-6f4b-45b0-ad5d-788a2cca5601";
        groupId = "gr12cd7bd3-aac0-4a9b-bf5a-079c37e59be2";
        ProjectStorage.DOCUMENT_REFERENCE = FirebaseFirestore.getInstance()
                .document(ProjectStorage.KEY_COLLECTION_GROUP + "/" + groupId);
        ProjectStorage.DOCUMENT_REFERENCE.addSnapshotListener((value, error) -> {
            if (value.exists()) {
                groupName = value.getString(ProjectStorage.KEY_GROUP_NAME);
                adminId = (List<String>) value.get(ProjectStorage.KEY_GROUP_ADMIN);
                participantId = (List<String>) value.get(ProjectStorage.KEY_GROUP_PARTICIPANT);
                uri = value.getString(ProjectStorage.KEY_GROUP_URI);
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
        binding.imageBack.setOnClickListener(v -> onBackPressed());
        binding.textChangeGroupName.setOnClickListener(v -> changeGroupName());
        binding.imgGroupAvatar2.setOnClickListener(v -> changeGroupImage());
    }

    private void changeGroupImage() {
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST);

    }

    private void changeGroupName() {
        EditText edt = new EditText(getApplicationContext());
        edt.setLayoutParams(new LinearLayout.LayoutParams(100, LinearLayout.LayoutParams.WRAP_CONTENT));
        String groupName = binding.textGroupName.getText().toString();
        edt.setText(groupName);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Edit Group Name");
        builder.setView(edt);
        builder.setPositiveButton("Change", (dialog, which) -> {
            ProjectStorage.DOCUMENT_REFERENCE
                    .update(ProjectStorage.KEY_GROUP_NAME
                            , edt.getText().toString());
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void seeListMember() {
        Intent intent = new Intent(getApplicationContext(), ListGroupParticipantActivity.class);
        intent.putExtra(ProjectStorage.KEY_USER_ID, currentUserId);
        intent.putExtra(ProjectStorage.KEY_GROUP_ID, groupId);
        startActivity(intent);
    }

    private void addParticipant() {
        Intent intent = new Intent(GroupInfoActivity.this, AddParticipantActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(ProjectStorage.KEY_GROUP_CURRENT_PARTICIPANT, (ArrayList<? extends Serializable>) participantId);
        intent.putExtras(bundle);
        intent.putExtra(ProjectStorage.KEY_USER_ID, currentUserId);
        intent.putExtra(ProjectStorage.KEY_GROUP_ID, groupId);
        intent.putExtra(ProjectStorage.REMOTE_MSG_TYPE, "current");
        startActivity(intent);
    }

    private void seeListPhoto() {
    }

    private void leaveGroup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Leave Group ?");
        if (adminId.contains(currentUserId)) {
            if (adminId.size() == 1) {
                builder.setMessage("Are you sure want to leave ? You won't get any new message and " +
                        "all participants will be added as admin");
                builder.setPositiveButton("LEAVE", (dialog, which) -> {
                    ProjectStorage.DOCUMENT_REFERENCE.update(ProjectStorage.KEY_GROUP_ADMIN, FieldValue.arrayRemove(currentUserId));
                    ProjectStorage.DOCUMENT_REFERENCE.update(ProjectStorage.KEY_GROUP_PARTICIPANT, FieldValue.arrayRemove(currentUserId));
                    participantId.remove(currentUserId);
                    adminId = participantId;
                    for (String s : participantId) {
                        ProjectStorage.DOCUMENT_REFERENCE.update(ProjectStorage.KEY_GROUP_ADMIN, FieldValue.arrayUnion(s));
                    }
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    finish();
                });
            }
            if (adminId.size() >= 2) {
                builder.setMessage("Are you sure want to leave ? You won't get any new message");
                builder.setPositiveButton("LEAVE", (dialog, which) -> {
                    ProjectStorage.DOCUMENT_REFERENCE.update(ProjectStorage.KEY_GROUP_ADMIN, FieldValue.arrayRemove(currentUserId));
                    ProjectStorage.DOCUMENT_REFERENCE.update(ProjectStorage.KEY_GROUP_PARTICIPANT, FieldValue.arrayRemove(currentUserId));
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    finish();
                });
            }
        } else {
            builder.setMessage("Are you sure want to leave ? You won't get any new message");
            builder.setPositiveButton("LEAVE", (dialog, which) -> {
                ProjectStorage.DOCUMENT_REFERENCE.update(ProjectStorage.KEY_GROUP_PARTICIPANT, FieldValue.arrayRemove(currentUserId));
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            });
        }
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        AlertDialog alertDialog = builder.create();
        alertDialog.show();


    }

    @Override
    public void onUploadFileSuccess(Uri uri, Object[] params) {
        ProjectStorage.DOCUMENT_REFERENCE.update(ProjectStorage.KEY_GROUP_URI, uri.toString());
    }
}