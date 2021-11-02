package com.example.authproject;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.example.authproject.adapters.ParticipantAdapter;
import com.example.authproject.models.User;
import com.example.authproject.utilities.ProjectStorage;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;


public class GroupAdminFragment extends Fragment {
    private RecyclerView recyclerView;
    private ProgressBar progressBarAdmin;
    private List<String> admin;
    private List<User> users;

    public GroupAdminFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View v = inflater.inflate(R.layout.fragment_group_admin, container, false);
        recyclerView = v.findViewById(R.id.recyclerViewAdmin);
        progressBarAdmin = v.findViewById(R.id.progressBarAdmin);
        admin = (List<String>) getArguments().getSerializable(ProjectStorage.KEY_GROUP_ADMIN);
        getListAdmin();
        return v;
    }

    private void getListAdmin() {
        users = new ArrayList<>();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(
                getContext());
        loading(true);
        ProjectStorage.DATABASE_REFERENCE.collection(ProjectStorage.KEY_COLLECTION_USERS)
                .get().addOnCompleteListener(task -> {
            loading(false);
            if (task.isSuccessful() && task.getResult() != null) {
                for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                    for (String s : admin) {
                        if (s.toLowerCase().equals(queryDocumentSnapshot.getData().get(ProjectStorage.KEY_USER_EMAIL))) {
                            User user = new User();
                            user.setFullName(queryDocumentSnapshot.getString(ProjectStorage.KEY_NAME));
                            user.setEmail(queryDocumentSnapshot.getString(ProjectStorage.KEY_USER_EMAIL));
                            user.setUri(queryDocumentSnapshot.getString(ProjectStorage.KEY_AVATAR));
                            users.add(user);
                        }
                    }
                }
                if (users.size() > 0) {
                    ParticipantAdapter participantAdapter = new ParticipantAdapter(users);
                    recyclerView.setLayoutManager(linearLayoutManager);
                    recyclerView.setAdapter(participantAdapter);
                    recyclerView.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void loading(Boolean isLoading) {
        if (isLoading) {
            progressBarAdmin.setVisibility(View.VISIBLE);
        } else {
            progressBarAdmin.setVisibility(View.INVISIBLE);
        }
    }
}