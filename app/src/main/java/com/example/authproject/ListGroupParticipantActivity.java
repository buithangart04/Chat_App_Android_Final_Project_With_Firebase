package com.example.authproject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.authproject.utilities.ProjectStorage;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ListGroupParticipantActivity extends AppCompatActivity {
    private List<String> participant;
    private List<String> admin;
    private String groupID;
    private String currentUserId;
    //    private TextView textAdd;
    private FrameLayout frameLayoutAll;
    private FrameLayout frameLayoutAdmin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_participant);
        frameLayoutAll = findViewById(R.id.frameLayoutAll);
        frameLayoutAdmin = findViewById(R.id.frameLayoutAdmin);
//        textAdd = findViewById(R.id.text_add_to_group);
        init();
        setListener();
    }
    private void init() {
        Intent intent = getIntent();
        groupID = intent.getStringExtra(ProjectStorage.KEY_GROUP_ID);
        currentUserId = intent.getStringExtra(ProjectStorage.KEY_USER_EMAIL);
        ProjectStorage.DOCUMENT_REFERENCE = FirebaseFirestore.getInstance()
                .document(ProjectStorage.KEY_COLLECTION_GROUP + "/" + groupID);
        ProjectStorage.DOCUMENT_REFERENCE.addSnapshotListener((value, error) -> {
            if (value.exists()) {
                admin = (List<String>) value.get(ProjectStorage.KEY_GROUP_ADMIN);
                participant = (List<String>) value.get(ProjectStorage.KEY_GROUP_PARTICIPANT);
            } else if (error != null) {
                Log.w("TAG", error);
            }
        });
    }

    private void setListener() {
//        textAdd.setOnClickListener(v -> addParticipant());
        frameLayoutAll.setOnClickListener(v -> replaceFragment(new GroupParticipantFragment()));
        frameLayoutAdmin.setOnClickListener(v -> replaceFragment(new GroupAdminFragment()));
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Bundle bundle = new Bundle();
        bundle.putSerializable(ProjectStorage.KEY_GROUP_ADMIN, (ArrayList<? extends Serializable>) admin);
        bundle.putSerializable(ProjectStorage.KEY_GROUP_PARTICIPANT, (ArrayList<? extends Serializable>) participant);
        fragment.setArguments(bundle);
        fragmentTransaction.replace(R.id.frameLayoutFragment, fragment);
        fragmentTransaction.commit();
    }
    private void addParticipant() {
        Intent intent = new Intent(ListGroupParticipantActivity.this, AddParticipantActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(ProjectStorage.KEY_GROUP_CURRENT_PARTICIPANT, (ArrayList<? extends Serializable>) participant);
        intent.putExtras(bundle);
        intent.putExtra(ProjectStorage.KEY_USER_EMAIL, currentUserId);
        intent.putExtra(ProjectStorage.KEY_GROUP_ID, groupID);
        intent.putExtra(ProjectStorage.REMOTE_MSG_TYPE, "current");
        startActivity(intent);
    }

}