package com.example.authproject;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.authproject.adapters.ChosenGroupUserAdapter;
import com.example.authproject.adapters.GroupUserAdapter;
import com.example.authproject.databinding.ActivityCreateGroupChatBinding;
import com.example.authproject.listeners.GetUserGroupListener;
import com.example.authproject.models.User;
import com.example.authproject.utilities.PreferenceManager;
import com.example.authproject.utilities.ProjectStorage;
import com.example.authproject.utilities.UserUtilities;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


public class AddParticipantActivity extends AppCompatActivity implements GetUserGroupListener {

    private ActivityCreateGroupChatBinding binding;
    private GroupUserAdapter groupUserAdapter;
    private ChosenGroupUserAdapter chosenGroupUserAdapter;
    private List<User> users;
    private List<String> participantId;
    private List<User> chosenUsers;
    private User user;
    private String currentUserId;
    private String types;
    private String searchChar = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCreateGroupChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        init();
        getUsers(types);
        searchUser(users);
        setListener();
    }

    private void setListener() {
        binding.imageBack2.setOnClickListener(v-> onBackPressed());
    }

    private void init() {
        Intent intent = getIntent();
        types = intent.getStringExtra(ProjectStorage.REMOTE_MSG_TYPE);
        if (types == null) {
            types = "new";
        }
        if (types.equalsIgnoreCase("current")) {
            binding.textCreateGroupNext.setText("ADD");
        }
        chosenUsers = new ArrayList<>();
        chosenGroupUserAdapter = new ChosenGroupUserAdapter(chosenUsers, this);
        binding.userGroupRecylerView.setAdapter(chosenGroupUserAdapter);
        binding.userGroupRecylerView.setVisibility(View.VISIBLE);

        binding.userGroupRecylerView.setLayoutManager(new LinearLayoutManager(
                getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));

        binding.userRecycleView.setLayoutManager( new LinearLayoutManager(
                getApplicationContext(), LinearLayoutManager.VERTICAL, false));
    }

    private void getUsers(String type) {
        loading(true);
        users = new ArrayList<>();
        ProjectStorage.DATABASE_REFERENCE.collection(ProjectStorage.KEY_COLLECTION_USERS)
                .get()
                .addOnCompleteListener(task -> {
                    loading(false);
                    currentUserId = PreferenceManager.getInstance().getString(ProjectStorage.KEY_USER_ID);
                    if (task.isSuccessful() && task.getResult() != null) {
                        switch (type) {
                            case "new":
                                users = new UserUtilities().getListUsers(currentUserId, task);
                                break;
                            case "current":
                                for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                                    User u = new User();
                                    u.setFullName(queryDocumentSnapshot.getString(ProjectStorage.KEY_NAME));
                                    u.setEmail(queryDocumentSnapshot.getString(ProjectStorage.KEY_USER_EMAIL));
                                    u.setId(queryDocumentSnapshot.getString(ProjectStorage.KEY_USER_ID));
                                    u.setUri(queryDocumentSnapshot.getString(ProjectStorage.KEY_AVATAR));
                                    users.add(u);
                                }
                                Bundle bundle = getIntent().getExtras();
                                participantId = (List<String>) bundle.getSerializable(ProjectStorage.KEY_GROUP_CURRENT_PARTICIPANT);
                                for (String s : participantId) {
                                    users = users.stream().filter(u -> !u.getId().equalsIgnoreCase(s)).collect(Collectors.toList());
                                }
                                break;
                        }
                        if (users.size() > 0) {
                            Collections.reverse(users);
                            groupUserAdapter = new GroupUserAdapter(users, this);
                            binding.userRecycleView.setAdapter(groupUserAdapter);
                            binding.userRecycleView.setVisibility(View.VISIBLE);
                        } else {
                            showErrorMessage();
                        }
                    } else {
                        showErrorMessage();
                    }
                });
    }

    private void searchUser(List<User> users) {
        groupUserAdapter = new GroupUserAdapter(users, this);
        binding.searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                groupUserAdapter.getFilter().filter(charSequence);
                if (charSequence.toString().equals("")) {
                    searchChar = "";
                    binding.userRecycleView.scrollToPosition(users.size() - 1);
                }
                searchChar = charSequence.toString();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void showErrorMessage() {
        binding.textViewError.setText(String.format("%s", "No user available"));
        binding.textViewError.setVisibility(View.VISIBLE);
    }

    private void loading(Boolean isLoading) {
        if (isLoading) {
            binding.progressBarGroup.setVisibility(View.VISIBLE);
        } else {
            binding.progressBarGroup.setVisibility(View.INVISIBLE);
        }
    }

    private void saveUserGroup() {
        if (!types.equalsIgnoreCase("current")) {
            currentUserId = PreferenceManager.getInstance().getString(ProjectStorage.KEY_USER_ID);
            Bundle bundle = new Bundle();
            bundle.putSerializable(ProjectStorage.KEY_GROUP_PARTICIPANT, (ArrayList<? extends Serializable>) chosenUsers);
            Intent createGroupIntent = new Intent(getApplicationContext(), CreateGroupActivity.class);
            createGroupIntent.putExtras(bundle);
            createGroupIntent.putExtra(ProjectStorage.KEY_USER_ID, currentUserId);
            startActivity(createGroupIntent);
        }

    }

    private void saveUserToCurrentGroup() {
        Intent intent = getIntent();
        String groupID = intent.getStringExtra(ProjectStorage.KEY_GROUP_ID);
        participantId = new ArrayList<>();
        //Update to database
        ProjectStorage.DOCUMENT_REFERENCE = FirebaseFirestore.getInstance()
                .document(ProjectStorage.KEY_COLLECTION_GROUP + "/" + groupID);
        for (User u : chosenUsers) {
            ProjectStorage.DOCUMENT_REFERENCE.update(ProjectStorage.KEY_GROUP_PARTICIPANT
                    , FieldValue.arrayUnion(u.getId()));
        }
        Bundle bundle = new Bundle();
        bundle.putSerializable(ProjectStorage.KEY_GROUP_PARTICIPANT, (ArrayList<? extends Serializable>) chosenUsers);
        Intent i = new Intent(getApplicationContext(), GroupInfoActivity.class);
        i.putExtra(ProjectStorage.KEY_USER_ID, currentUserId);
        startActivity(i);
        Toast.makeText(getApplicationContext(), "Add successfully!", Toast.LENGTH_LONG).show();
        finish();
    }

    @Override
    public void onClick(User u) {
        user = u;
    }

    @Override
    public void onClickUser(String type) {
        switch (type) {
            case "add":
                chosenUsers.add(user);
                chosenGroupUserAdapter.notifyDataSetChanged();
                if (types.equalsIgnoreCase("current")) {
                    if (chosenUsers.size() >= 1) {
                        binding.textCreateGroupNext.setTextColor(Color.parseColor(ProjectStorage.KEY_COLOR_NAVIGATE));
                        binding.textCreateGroupNext.setClickable(true);
                        binding.textCreateGroupNext.setOnClickListener(v -> {
                            saveUserToCurrentGroup();
                        });
                    }

                } else {
                    if (chosenUsers.size() >= 2) {
                        binding.textCreateGroupNext.setTextColor(Color.parseColor(ProjectStorage.KEY_COLOR_NAVIGATE));
                        binding.textCreateGroupNext.setClickable(true);
                        binding.textCreateGroupNext.setOnClickListener(v -> {
                            saveUserGroup();
                        });
                    }
                }
                break;
            case "remove":
                users.add(user);
                groupUserAdapter.getFilter().filter(searchChar);
                binding.userRecycleView.scrollToPosition(users.size() - 1);
                groupUserAdapter.notifyDataSetChanged();
                if (types.equalsIgnoreCase("current")) {
                    if (chosenUsers.size() <= 1) {
                        binding.textCreateGroupNext.setTextColor(Color.parseColor(ProjectStorage.KEY_COLOR_DISABLED));
                        binding.textCreateGroupNext.setClickable(false);
                    }
                    Log.d("TAG", "Remove CURRENT");
                } else {
                    if (chosenUsers.size() <= 2) {
                        binding.textCreateGroupNext.setTextColor(Color.parseColor(ProjectStorage.KEY_COLOR_DISABLED));
                        binding.textCreateGroupNext.setClickable(false);
                    }
                }

                break;
            default:
                break;
        }

    }

}