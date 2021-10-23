package com.example.authproject;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.authproject.adapters.ChosenGroupUserAdapter;
import com.example.authproject.adapters.GroupUserAdapter;
import com.example.authproject.databinding.ActivityCreateGroupChatBinding;
import com.example.authproject.models.User;
import com.example.authproject.utilities.PreferenceManager;
import com.example.authproject.utilities.ProjectStorage;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CreateGroupChatActivity extends AppCompatActivity {

    private ActivityCreateGroupChatBinding binding;
    private PreferenceManager preferenceManager;
    private GroupUserAdapter groupUserAdapter;
    private ChosenGroupUserAdapter chosenGroupUserAdapter;
    private List<User> users = new ArrayList<>();
    private List<User> userGroup = new ArrayList<>();

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
        LinearLayoutManager linearLayoutManagerUser = new LinearLayoutManager(
                getApplicationContext());
        linearLayoutManagerUser.setReverseLayout(true);
        LinearLayoutManager linearLayoutManagerUserGroup = new LinearLayoutManager(
                getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
        binding.userRecycleView.setLayoutManager(linearLayoutManagerUser);
        binding.userGroupRecylerView.setLayoutManager(linearLayoutManagerUserGroup);

        User u = new User("hung", "1", "1", "1");
        User u2 = new User("hung", "1", "1", "1");
        userGroup.add(u);
        userGroup.add(u2);

        chosenGroupUserAdapter = new ChosenGroupUserAdapter(userGroup);
        binding.userGroupRecylerView.setAdapter(chosenGroupUserAdapter);
        binding.userGroupRecylerView.setVisibility(View.VISIBLE);

    }

    private void getUsers() {
        loading(true);

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
                            groupUserAdapter = new GroupUserAdapter(users);
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
        groupUserAdapter = new GroupUserAdapter(users);
        binding.searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                groupUserAdapter.getFilter().filter(charSequence);

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

}