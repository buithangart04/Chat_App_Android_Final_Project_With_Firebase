package com.example.authproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.authproject.databinding.ActivityCreateGroupChatBinding;
import com.example.authproject.databinding.ActivityCreateGroupChatNameAcitivityBinding;
import com.example.authproject.models.ChatMessage;
import com.example.authproject.models.Group;
import com.example.authproject.models.GroupChatMessage;
import com.example.authproject.models.User;
import com.example.authproject.utilities.FunctionalUtilities;
import com.example.authproject.utilities.ProjectStorage;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CreateGroupActivity extends AppCompatActivity {
    private ActivityCreateGroupChatNameAcitivityBinding binding;
    private List<User> userGroup = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group_chat_name_acitivity);
        binding = ActivityCreateGroupChatNameAcitivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
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
        loading(true);
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
        binding.textCreateGroup.setOnClickListener(view -> createGroup());
    }

    private void loading(Boolean isLoading) {
        if (isLoading) {
            binding.progressBarGroup3.setVisibility(View.VISIBLE);
        } else {
            binding.progressBarGroup3.setVisibility(View.INVISIBLE);
        }
    }

    private void createGroup() {
        String groupID = FunctionalUtilities.generateId("group");
        String groupName = binding.textGroupName.getText().toString();
        String uri = "image";

        List<String> userEmails = new ArrayList<>();
        userEmails.add("remylia2k@gmail.com");
        userEmails.add("meo2@gmail.com");
        userEmails.add("meomeo@gmail.com");

        List<String> admin = new ArrayList<>();
        admin.add("remylia2k@gmail.com");

        Group group = new Group(groupID, groupName, userEmails, uri,admin);
        GroupChatMessage groupChatMessage = new GroupChatMessage("remylia2k@gmail.com",
                "Unnecessary Message", new Date().toString());

        ProjectStorage.DOCUMENT_REFERENCE = FirebaseFirestore.getInstance()
                .document(ProjectStorage.KEY_COLLECTION_GROUP + "/" + groupID);

        ProjectStorage.DOCUMENT_REFERENCE.set(group)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(getApplicationContext(), "Group has been created successfully!", Toast.LENGTH_LONG).show();
                    ProjectStorage.DOCUMENT_REFERENCE
                            .collection(ProjectStorage.KEY_COLLECTION_GROUP_MESSAGE)
                            .document("Unnecessary Message")
                            .set(groupChatMessage);
                    Intent intent = new Intent(getApplicationContext(),GroupChatActivity.class);
                    startActivity(intent);
                    finish();
                }).addOnFailureListener(e ->
                Toast.makeText(getApplicationContext(), "Failed: " + e, Toast.LENGTH_LONG).show());

    }
}