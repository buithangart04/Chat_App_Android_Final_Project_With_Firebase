package com.example.authproject;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.authproject.adapters.UsersAdapter;
import com.example.authproject.databinding.ActivityUsersBinding;
import com.example.authproject.databinding.FragmentUsersBinding;
import com.example.authproject.listeners.UserListener;
import com.example.authproject.models.User;
import com.example.authproject.utilities.PreferenceManager;
import com.example.authproject.utilities.ProjectStorage;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;


public class UsersFragment extends Fragment implements UserListener {
    EditText search_users;
    private PreferenceManager preferenceManager;
    private FragmentUsersBinding binding;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_users, container, false);
        search_users = view.findViewById(R.id.search_users);
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
        ProjectStorage.DATABASE_REFERENCE.collection(ProjectStorage.KEY_COLLECTION_USERS).whereEqualTo("email",s.toLowerCase())
                .get()
                .addOnCompleteListener(task ->{

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


                            users.add(user);

                        }
                        if(users.size()>0){
                            UsersAdapter usersAdapter = new UsersAdapter(users,this);
                            binding.recyclerViewSearch.setAdapter(usersAdapter);
                            binding.recyclerViewSearch.setVisibility(View.VISIBLE);

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

    @Override
    public void onUserCLick(User user) {

    }
}