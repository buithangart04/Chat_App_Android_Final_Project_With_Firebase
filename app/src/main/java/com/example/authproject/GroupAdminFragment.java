package com.example.authproject;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.authproject.adapters.ParticipantAdapter;
import com.example.authproject.databinding.DialogGroupActionAllBinding;
import com.example.authproject.listeners.UserListener;
import com.example.authproject.models.Group;
import com.example.authproject.models.User;
import com.example.authproject.utilities.ProjectStorage;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;


public class GroupAdminFragment extends Fragment implements UserListener {
    private RecyclerView recyclerView;
    private ProgressBar progressBarAdmin;
    private List<String> adminId;
    private List<String> participantId;
    private List<User> users;
    private String currentUserId;
    private String groupId;
    private DialogGroupActionAllBinding binding;
    private boolean isAdmin = true;

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
        adminId = (List<String>) getArguments().getSerializable(ProjectStorage.KEY_GROUP_ADMIN);
        participantId = (List<String>) getArguments().getSerializable(ProjectStorage.KEY_GROUP_CURRENT_PARTICIPANT);
        currentUserId = getArguments().getString(ProjectStorage.KEY_USER_ID);
        groupId = getArguments().getString(ProjectStorage.KEY_GROUP_ID);
        getListAdmin();
        isAdmin = adminId.contains(currentUserId) ? true : false;
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
                    for (String s : adminId) {
                        if (s.toLowerCase().equals(queryDocumentSnapshot.getData().get(ProjectStorage.KEY_USER_ID))) {
                            User user = new User();
                            user.setFullName(queryDocumentSnapshot.getString(ProjectStorage.KEY_NAME));
                            user.setEmail(queryDocumentSnapshot.getString(ProjectStorage.KEY_USER_EMAIL));
                            user.setId(queryDocumentSnapshot.getString(ProjectStorage.KEY_USER_ID));
                            user.setUri(queryDocumentSnapshot.getString(ProjectStorage.KEY_AVATAR));
                            users.add(user);
                        }
                    }
                }
                if (users.size() > 0) {
                    ParticipantAdapter participantAdapter = new ParticipantAdapter(users,this);
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

    @Override
    public void onUserCLick(User user, Group group) {
        if (isAdmin) {
            ProjectStorage.DOCUMENT_REFERENCE = FirebaseFirestore.getInstance()
                    .document(ProjectStorage.KEY_COLLECTION_GROUP + "/" + groupId);
            binding = DialogGroupActionAllBinding.inflate(getLayoutInflater());
            Intent intent = new Intent(getContext(), GroupInfoActivity.class);
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setView(binding.getRoot());
            binding.textAllUser.setText(user.getFullName());
            Picasso.get().load(user.getUri())
                    .resize(binding.imgAllUser.getLayoutParams().width,
                            binding.imgAllUser.getLayoutParams().height)
                    .into(binding.imgAllUser);


            if(user.getId().equals(currentUserId)){
                binding.textRemoveGroup.setText("Leave Group");
                binding.textRemoveGroup.setOnClickListener(v -> {
                    ProjectStorage.DOCUMENT_REFERENCE.update(ProjectStorage.KEY_GROUP_PARTICIPANT
                            , FieldValue.arrayRemove(user.getId()));
                    ProjectStorage.DOCUMENT_REFERENCE.update(ProjectStorage.KEY_GROUP_ADMIN
                            , FieldValue.arrayRemove(user.getId()));
                    startActivity(intent);
                    Toast.makeText(getContext(),"Leave Group successfully", Toast.LENGTH_SHORT).show();
                });
            }
            if (adminId.contains(user.getId())) {
                binding.textAdminAction.setText("Remove Admin");
                binding.textAdminAction.setOnClickListener(v -> {
                    ProjectStorage.DOCUMENT_REFERENCE.update(ProjectStorage.KEY_GROUP_ADMIN
                            , FieldValue.arrayRemove(user.getId()));
                    startActivity(intent);
                    Toast.makeText(getContext(),"Remove from Admin successfully", Toast.LENGTH_SHORT).show();
                });
                binding.textRemoveGroup.setOnClickListener(v -> {
                    ProjectStorage.DOCUMENT_REFERENCE.update(ProjectStorage.KEY_GROUP_PARTICIPANT
                            , FieldValue.arrayRemove(user.getId()));
                    ProjectStorage.DOCUMENT_REFERENCE.update(ProjectStorage.KEY_GROUP_ADMIN
                            , FieldValue.arrayRemove(user.getId()));
                    startActivity(intent);
                    Toast.makeText(getContext(),"Remove from Group successfully", Toast.LENGTH_SHORT).show();
                });
            }
            if (adminId.size() == 1) {
                binding.textAdminAction.setOnClickListener(v -> Toast.makeText(getContext(),
                        "You are the last admin of the group. Add more to remove", Toast.LENGTH_SHORT).show());
                binding.textRemoveGroup.setOnClickListener(v -> {
                    ProjectStorage.DOCUMENT_REFERENCE.update(ProjectStorage.KEY_GROUP_ADMIN, FieldValue.arrayRemove(currentUserId));
                    ProjectStorage.DOCUMENT_REFERENCE.update(ProjectStorage.KEY_GROUP_PARTICIPANT, FieldValue.arrayRemove(currentUserId));
                    participantId.remove(currentUserId);
                    adminId = participantId;
                    for (String s : participantId) {
                        ProjectStorage.DOCUMENT_REFERENCE.update(ProjectStorage.KEY_GROUP_ADMIN, FieldValue.arrayUnion(s));
                    }
                    startActivity(intent);
                    Toast.makeText(getContext(), "Remove Group successfully", Toast.LENGTH_SHORT).show();
                });
            }
            else {
                binding.textRemoveGroup.setOnClickListener(v -> {
                    ProjectStorage.DOCUMENT_REFERENCE.update(ProjectStorage.KEY_GROUP_PARTICIPANT
                            , FieldValue.arrayRemove(user.getId()));
                    ProjectStorage.DOCUMENT_REFERENCE.update(ProjectStorage.KEY_GROUP_ADMIN
                            , FieldValue.arrayRemove(user.getId()));
                    startActivity(intent);
                    Toast.makeText(getContext(),"Remove from Group successfully", Toast.LENGTH_SHORT).show();
                });
                binding.textAdminAction.setOnClickListener(v -> {
                    ProjectStorage.DOCUMENT_REFERENCE.update(ProjectStorage.KEY_GROUP_ADMIN
                            , FieldValue.arrayUnion(user.getId()));
                    startActivity(intent);
                    Toast.makeText(getContext(),"Add into Admin successfully", Toast.LENGTH_SHORT).show();
                });

            }
            builder.show();
        }
    }
}