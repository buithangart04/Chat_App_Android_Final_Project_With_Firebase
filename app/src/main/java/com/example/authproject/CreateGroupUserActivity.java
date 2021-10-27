package com.example.authproject;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.authproject.adapters.ChosenGroupUserAdapter;
import com.example.authproject.adapters.GroupUserAdapter;
import com.example.authproject.databinding.ActivityCreateGroupChatBinding;
import com.example.authproject.listeners.GetUserGroupListener;
import com.example.authproject.models.User;
import com.example.authproject.utilities.PreferenceManager;
import com.example.authproject.utilities.ProjectStorage;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class CreateGroupUserActivity extends AppCompatActivity implements GetUserGroupListener {

    private ActivityCreateGroupChatBinding binding;
    private PreferenceManager preferenceManager;
    private GroupUserAdapter groupUserAdapter;
    private ChosenGroupUserAdapter chosenGroupUserAdapter;
    private List<User> users;
    private List<User> chosenUsers;
    private User user;
    private String currentUserId;
    private String searchChar = "";
    private LinearLayoutManager linearLayoutManagerUser;
    private LinearLayoutManager linearLayoutManagerUserGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCreateGroupChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        innit();
        getCurrentUser();
        getUsers();
        searchUser(users);
    }

    private void getCurrentUser() {
        preferenceManager = new PreferenceManager(getApplicationContext());
        Intent intent = getIntent();
        ((LinearLayoutManager) binding.userRecycleView.getLayoutManager()).setStackFromEnd(true);
        preferenceManager.putString(ProjectStorage.KEY_USER_EMAIL, intent.getStringExtra("email"));
        ProjectStorage.DATABASE_REFERENCE.collection("users")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            if (document.getString("email").equals(intent.getStringExtra("email"))) {
                                preferenceManager.putString(ProjectStorage.KEY_NAME, document.getString(ProjectStorage.KEY_NAME));
                            }

                        }
                    } else {
                        showErrorMessage();
                    }
                });
    }

    private void innit() {
        linearLayoutManagerUser = new LinearLayoutManager(
                getApplicationContext());
        linearLayoutManagerUser.setReverseLayout(true);

        linearLayoutManagerUserGroup = new LinearLayoutManager(
                getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);

        chosenUsers = new ArrayList<>();
        chosenGroupUserAdapter = new ChosenGroupUserAdapter(chosenUsers, this);

        binding.userGroupRecylerView.setAdapter(chosenGroupUserAdapter);
        binding.userGroupRecylerView.setVisibility(View.VISIBLE);
        binding.userGroupRecylerView.setLayoutManager(linearLayoutManagerUserGroup);
        binding.userRecycleView.setLayoutManager(linearLayoutManagerUser);
    }

    private void getUsers() {
        loading(true);
        users = new ArrayList<>();
        ProjectStorage.DATABASE_REFERENCE.collection(ProjectStorage.KEY_COLLECTION_USERS)
                .get()
                .addOnCompleteListener(task -> {
                    loading(false);
                    String currentUserEmail = preferenceManager.getString(ProjectStorage.KEY_USER_EMAIL);
                    if (task.isSuccessful() && task.getResult() != null) {

                        for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                            if (currentUserEmail.equals(queryDocumentSnapshot.getData().get("email"))) {
                                continue;
                            }
                            User user = new User();
                            user.setFullName(queryDocumentSnapshot.getString(ProjectStorage.KEY_NAME));
                            user.setEmail(queryDocumentSnapshot.getString(ProjectStorage.KEY_USER_EMAIL));
                            users.add(user);
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
                Log.d("TAG", searchChar);
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
        currentUserId = preferenceManager.getString(ProjectStorage.KEY_USER_EMAIL);
        Bundle bundle = new Bundle();
        bundle.putSerializable(ProjectStorage.KEY_GROUP_PARTICIPANT, (ArrayList<? extends Serializable>) chosenUsers);
        Intent intent = new Intent(getApplicationContext(), CreateGroupActivity.class);
        intent.putExtras(bundle);
        intent.putExtra(ProjectStorage.KEY_USER_EMAIL, currentUserId);
        startActivity(intent);
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
                if (chosenUsers.size() >= 2) {
                    binding.textCreateGroupNext.setTextColor(Color.parseColor(ProjectStorage.KEY_COLOR_NAVIGATE));
                    binding.textCreateGroupNext.setClickable(true);
                    binding.textCreateGroupNext.setOnClickListener(view -> saveUserGroup());
                }
                break;
            case "remove":
                users.add(user);
                groupUserAdapter.getFilter().filter(searchChar);
                binding.userRecycleView.scrollToPosition(users.size() - 1);
                groupUserAdapter.notifyDataSetChanged();
                if (chosenUsers.size() <= 2) {
                    binding.textCreateGroupNext.setTextColor(Color.parseColor(ProjectStorage.KEY_COLOR_DISABLED));
                    binding.textCreateGroupNext.setClickable(false);
                }
                break;
            default:
                break;
        }

    }

}