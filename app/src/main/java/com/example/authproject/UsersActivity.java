package com.example.authproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


import com.example.authproject.adapters.UsersAdapter;
import com.example.authproject.databinding.ActivityUsersBinding;
import com.example.authproject.listeners.UserListener;
import com.example.authproject.models.User;
import com.example.authproject.utilities.Constants;
import com.example.authproject.utilities.PreferenceManager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class UsersActivity extends AppCompatActivity implements UserListener {
    private FirebaseUser user;
    private DatabaseReference reference;
    private String userID;
    private Button btnLogout;
    private ActivityUsersBinding binding;
    private PreferenceManager preferenceManager;
    FirebaseFirestore database = FirebaseFirestore.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        binding = ActivityUsersBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager = new PreferenceManager(getApplicationContext());
        Intent intent = getIntent();
//
        preferenceManager.putString(Constants.KEY_USER_EMAIL,intent.getStringExtra("email"));
        database.collection("users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if(document.getString("email").equals(intent.getStringExtra("email"))){
                                    preferenceManager.putString(Constants.KEY_NAME,document.getString(Constants.KEY_NAME));
                                }

                            }
                        } else {
                            showErrorMessage();
                        }
                    }
                });
        setListeners();
        getUsers();
    }

    private void setListeners(){
        binding.imageBack.setOnClickListener(v -> onBackPressed());
    }

    private void getUsers(){
        loading(true);

        database.collection(Constants.KEY_COLLECTION_USERS)
                .get()
                .addOnCompleteListener(task ->{
                   loading(false);
                   String currentUserId = preferenceManager.getString(Constants.KEY_USER_EMAIL);
                   if(task.isSuccessful() && task.getResult()!=null){
                       List<User> users = new ArrayList<>();
                       for(QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()){
                           if(currentUserId.equals(queryDocumentSnapshot.getData().get("email"))){
                               continue;
                           }
                           User user = new User();
                            user.setFullName(queryDocumentSnapshot.getString(Constants.KEY_NAME));
                           user.setEmail(queryDocumentSnapshot.getString(Constants.KEY_USER_EMAIL));


                            users.add(user);

                       }
                       if(users.size()>0){
                           UsersAdapter usersAdapter = new UsersAdapter(users,this);
                           binding.usersRecyclerView.setAdapter(usersAdapter);
                           binding.usersRecyclerView.setVisibility(View.VISIBLE);

                       }else{
                           showErrorMessage();
                       }

                   }else{
                       showErrorMessage();
                   }
                });

    }
    private void showErrorMessage(){
        binding.textErrorMessage.setText(String.format("%s","No user available"));
        binding.textErrorMessage.setVisibility(View.VISIBLE);
    }
    private void loading(Boolean isLoading){
        if(isLoading){
            binding.progressBar.setVisibility(View.VISIBLE);
        }else{
            binding.progressBar.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onUserCLick(User user) {
        Intent intent = new Intent(getApplicationContext(),ChatActivity.class);
        intent.putExtra(Constants.KEY_USER,user);
        startActivity(intent);
        finish();
    }
}