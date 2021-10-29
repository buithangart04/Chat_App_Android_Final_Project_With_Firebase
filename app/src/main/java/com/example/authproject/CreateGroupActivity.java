package com.example.authproject;

import static com.example.authproject.utilities.ProjectStorage.PICK_IMAGE_REQUEST;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.authproject.adapters.ParticipantAdapter;
import com.example.authproject.databinding.ActivityCreateGroupChatNameAcitivityBinding;
import com.example.authproject.listeners.UploadFileSuccessListener;
import com.example.authproject.models.Group;
import com.example.authproject.models.User;
import com.example.authproject.utilities.FileUtilities;
import com.example.authproject.utilities.FunctionalUtilities;
import com.example.authproject.utilities.ProjectStorage;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class CreateGroupActivity extends AppCompatActivity implements UploadFileSuccessListener {
    private ActivityCreateGroupChatNameAcitivityBinding binding;
    private List<User> chosenUser;
    private List<String> user;
    private List<String> admin;
    private Group group;
    private String groupID;
    private String groupName;
    private String currentUserId;
    private Uri imgData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group_chat_name_acitivity);
        binding = ActivityCreateGroupChatNameAcitivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        loading(true);
        init();
        getUserGroup();
        setListener();
    }

    private void init() {
        LinearLayoutManager linearLayoutManagerUser = new LinearLayoutManager(
                getApplicationContext());
        linearLayoutManagerUser.setReverseLayout(true);
        binding.recyclerUser.setLayoutManager(linearLayoutManagerUser);
    }

    private void getUserGroup() {
        loading(false);
        Bundle bundle = getIntent().getExtras();
        chosenUser = (List<User>) bundle.getSerializable(ProjectStorage.KEY_GROUP_PARTICIPANT);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(
                getApplicationContext());

        binding.recyclerUser.setLayoutManager(linearLayoutManager);
        ParticipantAdapter participantAdapter = new ParticipantAdapter(chosenUser);
        binding.recyclerUser.setAdapter(participantAdapter);
        binding.recyclerUser.setVisibility(View.VISIBLE);
    }

    private void setListener() {
        binding.textGroupName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().equals("")) {
                    binding.textCreateGroup.setClickable(false);
                    binding.textCreateGroup.setTextColor(Color.parseColor(ProjectStorage.KEY_COLOR_DISABLED));
                    return;
                }
                binding.textCreateGroup.setTextColor(Color.parseColor(ProjectStorage.KEY_COLOR_NAVIGATE));
                binding.textCreateGroup.setClickable(true);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        binding.textCreateGroup.setOnClickListener(view -> addGroupToFireStore());
        binding.textPreviousGroup.setOnClickListener(view -> onBackPressed());
        binding.imgGroupAvatar.setOnClickListener(view -> selectImage());
    }

    private void loading(Boolean isLoading) {
        if (isLoading) {
            binding.progressBarGroup3.setVisibility(View.VISIBLE);
        } else {
            binding.progressBarGroup3.setVisibility(View.INVISIBLE);
        }
    }


    private void addGroupToFireStore() {
        groupID = FunctionalUtilities.generateId("group");
        groupName = binding.textGroupName.getText().toString();
        Intent createGroupUser = getIntent();
        currentUserId = createGroupUser.getStringExtra(ProjectStorage.KEY_USER_EMAIL);
        user = new ArrayList<>();
        for (User u : chosenUser) {
            user.add(u.getEmail());
        }
        user.add(currentUserId);
        admin = new ArrayList<>();
        admin.add(currentUserId);
        group = new Group(groupID, groupName, user, admin);

        new FileUtilities()
                .uploadFile(CreateGroupActivity.this, CreateGroupActivity.this, imgData);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            imgData = data.getData();
            binding.imgGroupAvatar.setImageURI(imgData);
        }
    }

    private void selectImage() {
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onUploadFileSuccess(Uri u) {
        group.uri = u.toString();
        ProjectStorage.DOCUMENT_REFERENCE = FirebaseFirestore.getInstance()
                .document(ProjectStorage.KEY_COLLECTION_GROUP + "/" + groupID);
        ProjectStorage.DOCUMENT_REFERENCE.set(group)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(getApplicationContext(), "Group has been created successfully!", Toast.LENGTH_LONG).show();
                    Intent createGroupIntent = new Intent(getApplicationContext(), GroupChatActivity.class);
                    createGroupIntent.putExtra(ProjectStorage.KEY_GROUP_ID, groupID);
                    createGroupIntent.putExtra(ProjectStorage.KEY_GROUP_NAME, groupName);
                    createGroupIntent.putExtra(ProjectStorage.KEY_USER_EMAIL, currentUserId);
                    startActivity(createGroupIntent);
                    finish();
                }).addOnFailureListener(e ->
                Toast.makeText(getApplicationContext(), "Failed: " + e, Toast.LENGTH_LONG).show());
    }

}