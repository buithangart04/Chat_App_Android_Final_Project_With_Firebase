package com.example.authproject;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.authproject.adapters.UsersSearchAdapter;
import com.example.authproject.models.User;
import com.example.authproject.utilities.PreferenceManager;
import com.example.authproject.utilities.ProjectStorage;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;


public class UsersFragment extends Fragment {
    private EditText search_users;
    private List<User> users = new ArrayList<>();
    private UsersSearchAdapter usersSearchAdapter;
    private TextView textErrorMessage;
    private RecyclerView recyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_users, container, false);

        recyclerView =  view.findViewById(R.id.recycler_view_search);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        search_users = view.findViewById(R.id.search_users);
        textErrorMessage = view.findViewById(R.id.textErrorMessage);

        search_users.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                searchUsers(charSequence.toString().toLowerCase());

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        return view;

    }

    private void searchUsers(String s) {
        users.clear();

        ProjectStorage.DATABASE_REFERENCE.collection(ProjectStorage.KEY_COLLECTION_USERS)
                .get()
                .addOnCompleteListener(task ->{
                    if(task.isSuccessful() && task.getResult()!=null){

                        for(QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()){

                            if(queryDocumentSnapshot.getData().get(ProjectStorage.KEY_USER_EMAIL).toString().toLowerCase().contains(s.toLowerCase())
                                    && !queryDocumentSnapshot.getData().get(ProjectStorage.KEY_USER_EMAIL)
                                    .toString().toLowerCase().equals(PreferenceManager.getInstance()
                                            .getString(ProjectStorage.KEY_USER_EMAIL))){

                                User user = new User();
                                user.setFullName(queryDocumentSnapshot.getString(ProjectStorage.KEY_NAME));
                                user.setEmail(queryDocumentSnapshot.getString(ProjectStorage.KEY_USER_EMAIL));
                                user.setUri(queryDocumentSnapshot.getString(ProjectStorage.KEY_AVATAR));
                                users.add(user);
                            }
                        }
                        if(users.size()>0){
                            usersSearchAdapter = new UsersSearchAdapter(users);
                            recyclerView.setAdapter(usersSearchAdapter);
                        }else{
                            showErrorMessage();
                        }

                    }else{
                        showErrorMessage();
                    }
                });




    }
    private void showErrorMessage(){
        textErrorMessage.setText(String.format("%s","No user found"));
        textErrorMessage.setVisibility(View.VISIBLE);
    }


}