package com.example.authproject;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.authproject.utilities.ProjectStorage;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ListGroupParticipantActivity extends AppCompatActivity {
    private List<String> participantId;
    private List<String> adminId;
    private String groupId;
    private String currentUserId;
    private FrameLayout frameLayoutAll;
    private FrameLayout frameLayoutAdmin;
    private ImageView imageViewBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_participant);
        frameLayoutAll = findViewById(R.id.frameLayoutAll);
        frameLayoutAdmin = findViewById(R.id.frameLayoutAdmin);
        imageViewBack = findViewById(R.id.imageBack3);
        init();
        setListener();
    }
    private void init() {
        Intent intent = getIntent();
        groupId = intent.getStringExtra(ProjectStorage.KEY_GROUP_ID);
        currentUserId = intent.getStringExtra(ProjectStorage.KEY_USER_ID);
        ProjectStorage.DOCUMENT_REFERENCE = FirebaseFirestore.getInstance()
                .document(ProjectStorage.KEY_COLLECTION_GROUP + "/" + groupId);
        ProjectStorage.DOCUMENT_REFERENCE.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    adminId = (List<String>) document.get(ProjectStorage.KEY_GROUP_ADMIN);
                    participantId = (List<String>) document.get(ProjectStorage.KEY_GROUP_PARTICIPANT);
                    frameLayoutAll.setBackground(getDrawable(R.drawable.background_curved_group_button));
                    frameLayoutAdmin.setBackgroundColor(Color.WHITE);
                    replaceFragment(new GroupParticipantFragment());
                } else {
                    Log.d("TAG", "No such document");
                }
            } else {
                Log.d("TAG", "get failed with ", task.getException());
            }
        });
    }

    private void setListener() {
        findViewById(R.id.text_add_to_group).setOnClickListener(v -> addParticipant());
        frameLayoutAll.setOnClickListener(v -> {
            frameLayoutAll.setBackground(getDrawable(R.drawable.background_curved_group_button));
            frameLayoutAdmin.setBackgroundColor(Color.WHITE);
            replaceFragment(new GroupParticipantFragment());
        });
        frameLayoutAdmin.setOnClickListener(v -> {
            frameLayoutAdmin.setBackground(getDrawable(R.drawable.background_curved_group_button));
            frameLayoutAll.setBackgroundColor(Color.WHITE);
            replaceFragment(new GroupAdminFragment());
        });
        imageViewBack.setOnClickListener(v -> onBackPressed());
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Bundle bundle = new Bundle();
        bundle.putSerializable(ProjectStorage.KEY_GROUP_ADMIN, (ArrayList<? extends Serializable>) adminId);
        bundle.putSerializable(ProjectStorage.KEY_GROUP_CURRENT_PARTICIPANT, (ArrayList<? extends Serializable>) participantId);
        bundle.putString(ProjectStorage.KEY_USER_ID, currentUserId);
        bundle.putString(ProjectStorage.KEY_GROUP_ID, groupId);
        fragment.setArguments(bundle);
        fragmentTransaction.replace(R.id.frameLayoutFragment, fragment).commit();
    }

    private void addParticipant() {
        Intent intent = new Intent(ListGroupParticipantActivity.this, AddParticipantActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(ProjectStorage.KEY_GROUP_CURRENT_PARTICIPANT, (ArrayList<? extends Serializable>) participantId);
        intent.putExtras(bundle);
        intent.putExtra(ProjectStorage.KEY_USER_ID, currentUserId);
        intent.putExtra(ProjectStorage.KEY_GROUP_ID, groupId);
        intent.putExtra(ProjectStorage.REMOTE_MSG_TYPE, "current");
        startActivity(intent);
    }

}