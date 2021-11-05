package com.example.authproject;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.authproject.adapters.UsersAdapter;
import com.example.authproject.listeners.GetUserSuccessListener;
import com.example.authproject.listeners.UserListener;
import com.example.authproject.models.User;
import com.example.authproject.utilities.FunctionalUtilities;
import com.example.authproject.utilities.PreferenceManager;
import com.example.authproject.utilities.ProjectStorage;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


public class MessageFragment extends Fragment implements UserListener, GetUserSuccessListener {
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private TextView textErrorMessage;
    private ImageView imageBack;
    private PreferenceManager preferenceManager;
    UsersAdapter usersAdapter;
    List<User> users ;
    private FunctionalUtilities functionalUtilities = new FunctionalUtilities();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_message, container, false);
        recyclerView = view.findViewById(R.id.usersRecyclerView);
        progressBar = view.findViewById(R.id.progressBar);
        textErrorMessage = view.findViewById(R.id.textErrorMessage);
        imageBack = view.findViewById(R.id.imageBack);
        preferenceManager = PreferenceManager.getInstance();
        String userEmail = preferenceManager.getString(ProjectStorage.KEY_USER_EMAIL);
        users = new ArrayList<>();
        usersAdapter = new UsersAdapter(users,this);
        recyclerView.setAdapter(usersAdapter);
        recyclerView.setVisibility(View.VISIBLE);
        ProjectStorage.DATABASE_REFERENCE.collection(ProjectStorage.KEY_COLLECTION_USERS)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if(document.getString("email").equals(userEmail)){
                                    preferenceManager.putString(ProjectStorage.KEY_NAME,document.getString(ProjectStorage.KEY_NAME));
                                }
                            }
                        } else {
                            showErrorMessage();
                        }
                    }
                });
        setListeners();
        getUsers();
        return view;
    }

    private void getUsers() {
        boolean hasFriend[] = new boolean[]{false};
        loading(true);
        String currentUserEmail = preferenceManager.getString(ProjectStorage.KEY_USER_EMAIL);
        ProjectStorage.DATABASE_REFERENCE.collection(ProjectStorage.KEY_FRIEND)
                .get()
                .addOnCompleteListener(task ->{
                    loading(false);

                    if(task.isSuccessful() && task.getResult()!=null){

                        for(QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()){
                            if((currentUserEmail.toLowerCase().equals(queryDocumentSnapshot.getData().get("senderEmail").toString().toLowerCase()) ||
                                    currentUserEmail.toLowerCase().equals(queryDocumentSnapshot.getData().get("receiverEmail").toString().toLowerCase())) &&
                                    "friend".equals(queryDocumentSnapshot.getData().get("status").toString().toLowerCase())){
                                String email ="";
                                if(currentUserEmail.equals(queryDocumentSnapshot.getData().get("senderEmail"))){
                                    email = queryDocumentSnapshot.getData().get("receiverEmail").toString();
                                }else{
                                    email = queryDocumentSnapshot.getData().get("senderEmail").toString();
                                }
                                hasFriend[0]=true;
                                functionalUtilities.getUserByEmail(email,this);
                            }
                        }
                        if(!hasFriend[0]){
                            showErrorMessage();
                        }

                    }else{
                        showErrorMessage();
                    }
                });
    }

    private void setListeners() {
        imageBack.setOnClickListener(v -> getActivity().onBackPressed());
    }


    private void showErrorMessage(){
        textErrorMessage.setText(String.format("%s","No user available"));
        textErrorMessage.setVisibility(View.VISIBLE);
    }
    private void loading(Boolean isLoading){
        if(isLoading){
            progressBar.setVisibility(View.VISIBLE);
        }else{
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onUserCLick(User user) {
        Intent intent = new Intent(getActivity(),ChatActivity.class);
        intent.putExtra(ProjectStorage.KEY_USER,user);
        startActivity(intent);
    }

    @Override
    public void onGetUserSuccess(User user) {
        users.add(user);
        usersAdapter.notifyDataSetChanged();
    }
}