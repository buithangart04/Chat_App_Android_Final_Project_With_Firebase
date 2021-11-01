package com.example.authproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.example.authproject.adapters.ParticipantAdapter;
import com.example.authproject.databinding.ActivityGroupParticipantBinding;
import com.example.authproject.models.User;
import com.example.authproject.utilities.ProjectStorage;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import org.w3c.dom.Text;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ListGroupParticipantActivity extends AppCompatActivity {
    private List<String> participant;
    private List<User> users;
    private List<String> admin;
    private String groupID;
    private String currentUserId;
    private RecyclerView recyclerView;
    private Text textAdd;
    private ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_participant);
        recyclerView = findViewById(R.id.recyclerViewParticipant);
//        textAdd = findViewById(R.id.text_add_to_group);
        progressBar = findViewById(R.id.progressBar2);
        init();
        setListener();
        getListParticipant();
    }

    private void getListParticipant() {
        users = new ArrayList<>();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(
                getApplicationContext());
        loading(true);
        ProjectStorage.DATABASE_REFERENCE.collection(ProjectStorage.KEY_COLLECTION_USERS)
                .get().addOnCompleteListener(task -> {
            loading(false);
            if (task.isSuccessful() && task.getResult() != null) {
                for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                    for (String s : participant) {
                        if (s.toLowerCase().equals(queryDocumentSnapshot.getData().get(ProjectStorage.KEY_USER_EMAIL))) {
                            User user = new User();
                            user.setFullName(queryDocumentSnapshot.getString(ProjectStorage.KEY_NAME));
                            user.setEmail(queryDocumentSnapshot.getString(ProjectStorage.KEY_USER_EMAIL));
                            user.setUri(queryDocumentSnapshot.getString(ProjectStorage.KEY_AVATAR));
                            users.add(user);
                        }
                    }
                }
                if (users.size() > 0) {
                    ParticipantAdapter participantAdapter = new ParticipantAdapter(users);
                    recyclerView.setLayoutManager(linearLayoutManager);
                    recyclerView.setAdapter(participantAdapter);
                    recyclerView.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void loading(Boolean isLoading) {
        if (isLoading) {
            progressBar.setVisibility(View.VISIBLE);
        } else {
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

    private void init() {

        Intent intent = getIntent();
        groupID = intent.getStringExtra(ProjectStorage.KEY_GROUP_ID);
        currentUserId = intent.getStringExtra(ProjectStorage.KEY_USER_EMAIL);
//       textAdd.setTextColor(Color.parseColor(ProjectStorage.KEY_COLOR_NAVIGATE));
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