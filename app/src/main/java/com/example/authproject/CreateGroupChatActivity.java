package com.example.authproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import com.example.authproject.DAO.UserDAO;
import com.example.authproject.R;
import com.example.authproject.adapters.GroupUserAdapter;
import com.example.authproject.adapters.UsersAdapter;
import com.example.authproject.databinding.ActivityCreateGroupChatBinding;
import com.example.authproject.databinding.ActivityUsersBinding;
import com.example.authproject.listeners.UserListener;
import com.example.authproject.models.User;
import com.example.authproject.utilities.Constants;
import com.example.authproject.utilities.PreferenceManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class CreateGroupChatActivity extends AppCompatActivity {

    private ActivityCreateGroupChatBinding binding;
    private PreferenceManager preferenceManager;
    private FirebaseFirestore database = FirebaseFirestore.getInstance();
    private GroupUserAdapter groupUserAdapter;
    private List<User> users = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCreateGroupChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager = new PreferenceManager(getApplicationContext());
        Intent intent = getIntent();
        ((LinearLayoutManager) binding.userRecycleView.getLayoutManager()).setStackFromEnd(true);
        preferenceManager.putString(Constants.KEY_USER_EMAIL, intent.getStringExtra("email"));
        database.collection("users")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            if (document.getString("email").equals(intent.getStringExtra("email"))) {
                                preferenceManager.putString(Constants.KEY_NAME, document.getString(Constants.KEY_NAME));
                            }

                        }
                    } else {
                        showErrorMessage();
                    }
                });

        getUsers();
        searchUser(users);
    }

    private void getUsers() {
        loading(true);

        database.collection(Constants.KEY_COLLECTION_USERS)
                .get()
                .addOnCompleteListener(task -> {
                    loading(false);
                    String currentUserId = preferenceManager.getString(Constants.KEY_USER_EMAIL);
                    if (task.isSuccessful() && task.getResult() != null) {

                        for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                            if (currentUserId.equals(queryDocumentSnapshot.getData().get("email"))) {
                                continue;
                            }
                            User user = new User();
                            user.setFullName(queryDocumentSnapshot.getString(Constants.KEY_NAME));
                            user.setEmail(queryDocumentSnapshot.getString(Constants.KEY_USER_EMAIL));
                            users.add(user);
                        }

                        if (users.size() > 0) {
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