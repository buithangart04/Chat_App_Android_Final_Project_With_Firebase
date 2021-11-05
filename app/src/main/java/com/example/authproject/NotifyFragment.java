package com.example.authproject;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.authproject.adapters.UsersSearchAdapter;
import com.example.authproject.listeners.GetUserSuccessListener;
import com.example.authproject.models.User;
import com.example.authproject.utilities.FunctionalUtilities;
import com.example.authproject.utilities.PreferenceManager;
import com.example.authproject.utilities.ProjectStorage;
import com.example.authproject.utilities.UserUtilities;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;


public class NotifyFragment extends Fragment implements GetUserSuccessListener{
    private List<User> users;
    private UsersSearchAdapter usersSearchAdapter;
    private RecyclerView recyclerView;
    private TextView textErrorMessage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view =  inflater.inflate(R.layout.fragment_notify, container, false);
        recyclerView =  view.findViewById(R.id.recycler_view_notify);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        textErrorMessage = view.findViewById(R.id.textErrorMessage);
        users  = new ArrayList<>();
        usersSearchAdapter = new UsersSearchAdapter(users);
        recyclerView.setAdapter(usersSearchAdapter);

        loadNotify();
        return view;
    }

    private void loadNotify() {
        users.clear();
        PreferenceManager preferenceManager = PreferenceManager.getInstance();
        String userEmail = preferenceManager.getString(ProjectStorage.KEY_USER_EMAIL);
        ProjectStorage.DATABASE_REFERENCE.collection(ProjectStorage.KEY_FRIEND)
                .get()
                .addOnCompleteListener(task ->{
                    if(task.isSuccessful() && task.getResult()!=null){
                        for(QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()){
                            if(userEmail.toLowerCase().equals(queryDocumentSnapshot.getData().get("receiverEmail").toString().toLowerCase()) &&
                                "sent".equals(queryDocumentSnapshot.getData().get("status").toString().toLowerCase())){
                                    new UserUtilities().
                                            getUserByCondition(this
                                                    ,new Pair<>(ProjectStorage.KEY_USER_EMAIL,
                                                            queryDocumentSnapshot.getData().get("senderEmail").toString()));
                            }
                        }
                    }else{
                        showErrorMessage();
                    }
                });
    }
    private void showErrorMessage(){
        textErrorMessage.setText(String.format("%s","No request add friend"));
        textErrorMessage.setVisibility(View.VISIBLE);
    }

    @Override
    public void onGetUserSuccess(User user) {
        users.add(user);
        usersSearchAdapter.notifyDataSetChanged();
    }
}