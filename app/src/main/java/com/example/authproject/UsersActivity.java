package com.example.authproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;


import com.example.authproject.DAO.UserDAO;
import com.example.authproject.adapters.UsersAdapter;
import com.example.authproject.databinding.ActivityUsersBinding;
import com.example.authproject.listeners.UserListener;
import com.example.authproject.models.User;
import com.example.authproject.utilities.ProjectStorage;
import com.example.authproject.utilities.PreferenceManager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
<<<<<<< HEAD
import com.google.firebase.firestore.DocumentReference;
=======
>>>>>>> d7eca48be3ed0febb1979cdb518126795365d0c6
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UsersActivity extends AppCompatActivity implements UserListener {

    private ActivityUsersBinding binding;
    private PreferenceManager preferenceManager;
<<<<<<< HEAD
    private FirebaseFirestore database = FirebaseFirestore.getInstance();
    private UserDAO userDAO = new UserDAO();

=======
>>>>>>> d7eca48be3ed0febb1979cdb518126795365d0c6
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        binding = ActivityUsersBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager = new PreferenceManager(getApplicationContext());
        Intent intent = getIntent();
<<<<<<< HEAD

        preferenceManager.putString(Constants.KEY_USER_EMAIL, intent.getStringExtra("email"));
        database.collection("users")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            if (document.getString("email").equals(intent.getStringExtra("email"))) {
                                preferenceManager.putString(Constants.KEY_NAME, document.getString(Constants.KEY_NAME));
=======
//
        preferenceManager.putString(ProjectStorage.KEY_USER_EMAIL,intent.getStringExtra("email"));
        ProjectStorage.DATABASE_REFERENCE.collection("users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if(document.getString("email").equals(intent.getStringExtra("email"))){
                                    preferenceManager.putString(ProjectStorage.KEY_NAME,document.getString(ProjectStorage.KEY_NAME));
                                }

>>>>>>> d7eca48be3ed0febb1979cdb518126795365d0c6
                            }

                        }
                    } else {
                        showErrorMessage();
                    }
                });
        userDAO.setUserStatus("ONLINE");
        setListeners();
        getUsers();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        logout();
        Log.d("TAG", "onDestroy");
    }

    private void logout() {
        userDAO.setUserStatus("OFFLINE");
        FirebaseAuth.getInstance().signOut();
    }

    private void setListeners() {
        binding.imageBack.setOnClickListener(view -> {
            logout();
            onBackPressed();
        });
    }

    private void getUsers() {
        loading(true);

        ProjectStorage.DATABASE_REFERENCE.collection(ProjectStorage.KEY_COLLECTION_USERS)
                .get()
<<<<<<< HEAD
                .addOnCompleteListener(task -> {
                    loading(false);
                    String currentUserId = preferenceManager.getString(Constants.KEY_USER_EMAIL);
                    if (task.isSuccessful() && task.getResult() != null) {
                        List<User> users = new ArrayList<>();
                        for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                            if (currentUserId.equals(queryDocumentSnapshot.getData().get("email"))) {
                                continue;
                            }
                            User user = new User();
                            user.setFullName(queryDocumentSnapshot.getString(Constants.KEY_NAME));
                            user.setEmail(queryDocumentSnapshot.getString(Constants.KEY_USER_EMAIL));
=======
                .addOnCompleteListener(task ->{
                   loading(false);
                   String currentUserId = preferenceManager.getString(ProjectStorage.KEY_USER_EMAIL);
                   if(task.isSuccessful() && task.getResult()!=null){
                       List<User> users = new ArrayList<>();
                       for(QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()){
                           if(currentUserId.equals(queryDocumentSnapshot.getData().get("email"))){
                               continue;
                           }
                           User user = new User();
                            user.setFullName(queryDocumentSnapshot.getString(ProjectStorage.KEY_NAME));
                           user.setEmail(queryDocumentSnapshot.getString(ProjectStorage.KEY_USER_EMAIL));

>>>>>>> d7eca48be3ed0febb1979cdb518126795365d0c6

                            users.add(user);

                        }
                        if (users.size() > 0) {
                            UsersAdapter usersAdapter = new UsersAdapter(users, this);
                            binding.usersRecyclerView.setAdapter(usersAdapter);
                            binding.usersRecyclerView.setVisibility(View.VISIBLE);

                        } else {
                            showErrorMessage();
                        }

                    } else {
                        showErrorMessage();
                    }
                });

    }

    private void showErrorMessage() {
        binding.textErrorMessage.setText(String.format("%s", "No user available"));
        binding.textErrorMessage.setVisibility(View.VISIBLE);
    }

    private void loading(Boolean isLoading) {
        if (isLoading) {
            binding.progressBar.setVisibility(View.VISIBLE);
        } else {
            binding.progressBar.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onUserCLick(User user) {
<<<<<<< HEAD
        Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
        intent.putExtra(Constants.KEY_USER, user);
=======
        Intent intent = new Intent(getApplicationContext(),ChatActivity.class);
        intent.putExtra(ProjectStorage.KEY_USER,user);
>>>>>>> d7eca48be3ed0febb1979cdb518126795365d0c6
        startActivity(intent);
        finish();
    }
}