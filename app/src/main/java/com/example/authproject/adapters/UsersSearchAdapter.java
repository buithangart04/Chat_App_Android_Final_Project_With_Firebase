package com.example.authproject.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.authproject.MainActivity;
import com.example.authproject.NavigatorActivity;
import com.example.authproject.databinding.ItemContainerUserBinding;
import com.example.authproject.databinding.ItemSearchUserBinding;
import com.example.authproject.listeners.UserListener;
import com.example.authproject.models.User;
import com.example.authproject.utilities.PreferenceManager;
import com.example.authproject.utilities.ProjectStorage;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class UsersSearchAdapter extends RecyclerView.Adapter<UsersSearchAdapter.UserViewHolder>  {

    private final List<User> users;
    private FirebaseFirestore firestore;

    public UsersSearchAdapter(List<User> users) {
        this.users = users;

    }

    @NonNull
    @Override
    public UsersSearchAdapter.UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemSearchUserBinding itemSearchUserBinding = ItemSearchUserBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new UserViewHolder(itemSearchUserBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull UsersSearchAdapter.UserViewHolder holder, int position) {
        holder.setUserData(users.get(position));
    }

    @Override
    public int getItemCount() {
        return users.size();
    }
    class UserViewHolder extends RecyclerView.ViewHolder{
        ItemSearchUserBinding binding;
        UserViewHolder(ItemSearchUserBinding itemSearchUserBinding){
            super(itemSearchUserBinding.getRoot());
            binding = itemSearchUserBinding;

        }
        void setUserData(User user){
            PreferenceManager preferenceManager = PreferenceManager.getInstance();
            String userEmail = preferenceManager.getString(ProjectStorage.KEY_USER_EMAIL);
            firestore = FirebaseFirestore.getInstance();

            binding.textName.setText(user.getFullName());
            binding.textEmail.setText(user.getEmail());
            Picasso.get().load(user.getUri()).fit().into(binding.imageProfile);
            ProjectStorage.DATABASE_REFERENCE.collection(ProjectStorage.KEY_FRIEND)
                    .get()
                    .addOnCompleteListener(task ->{
                        if(task.isSuccessful() && task.getResult()!=null){
                            for(QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()){
                                if(userEmail.toLowerCase().equals(queryDocumentSnapshot.getData().get("senderEmail").toString().toLowerCase()) &&
                                user.getEmail().toLowerCase().equals(queryDocumentSnapshot.getData().get("receiverEmail").toString().toLowerCase())){

                                    if(queryDocumentSnapshot.getData().get("status").equals("sent")){
                                        binding.btnAdd.setText("Cancel");
                                    }else if(queryDocumentSnapshot.getData().get("status").equals("friend")){
                                        binding.btnAdd.setText("Friend");
                                        binding.btnAdd.setEnabled(false);
                                    }else{
                                        binding.btnAdd.setText("Add Friend");
                                    }

                                }else if(userEmail.toLowerCase().equals(queryDocumentSnapshot.getData().get("receiverEmail").toString().toLowerCase()) &&
                                        user.getEmail().toLowerCase().equals(queryDocumentSnapshot.getData().get("senderEmail").toString().toLowerCase())){

                                    if(queryDocumentSnapshot.getData().get("status").equals("sent")){
                                        binding.btnAdd.setText("Accept");
                                    }else if(queryDocumentSnapshot.getData().get("status").equals("friend")){
                                        binding.btnAdd.setText("Friend");
                                        binding.btnAdd.setEnabled(false);
                                    }else{
                                        binding.btnAdd.setText("Add Friend");
                                    }

                                }
                            }
                        }
                    });

            binding.btnAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(binding.btnAdd.getText().equals("Cancel")){
                        binding.btnAdd.setText("Add Friend");
                        firestore.collection(ProjectStorage.KEY_FRIEND).whereEqualTo("senderEmail",userEmail)
                                .whereEqualTo("receiverEmail",user.getEmail().toString())
                                .get().addOnCompleteListener(task ->{
                            if(task.isSuccessful() && task.getResult()!=null) {
                                DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                                String documentID = documentSnapshot.getId();
                                firestore.collection(ProjectStorage.KEY_FRIEND).document(documentID)
                                        .delete();
                            }

                        });
                    }else if (binding.btnAdd.getText().equals("Accept")){
                        binding.btnAdd.setText("Friend");
                        binding.btnAdd.setEnabled(false);
                        Map<String,Object> status = new HashMap<>();
                        status.put("status","friend");
                        firestore.collection(ProjectStorage.KEY_FRIEND).whereEqualTo("senderEmail",user.getEmail().toString())
                                .whereEqualTo("receiverEmail",userEmail)
                                .get().addOnCompleteListener(task ->{
                            if(task.isSuccessful() && task.getResult()!=null) {
                                DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                                String documentID = documentSnapshot.getId();
                                firestore.collection(ProjectStorage.KEY_FRIEND).document(documentID)
                                        .update(status);
                            }

                        });
                    }else if(binding.btnAdd.getText().equals("Add Friend")){
                        String senderEmail= PreferenceManager.getInstance().getString(ProjectStorage.KEY_USER_EMAIL);
                        String receiverEmail = user.getEmail().toString();
                        ProjectStorage.DATABASE_REFERENCE.collection(ProjectStorage.KEY_FRIEND)
                                .whereEqualTo(ProjectStorage.KEY_RECEIVER_EMAIL,receiverEmail)
                                .whereEqualTo(ProjectStorage.KEY_SENDER_EMAIL,senderEmail)
                                .get()
                                .addOnCompleteListener(task->{
                                   if(task.isSuccessful()) {
                                       QuerySnapshot querySnapshot=task.getResult();
                                       if(querySnapshot.getDocuments().size()==0){
                                           binding.btnAdd.setText("Cancel");
                                           Map<String,String> addFriend = new HashMap<>();
                                           addFriend.put("receiverEmail",receiverEmail);
                                           addFriend.put("senderEmail",senderEmail);
                                           addFriend.put("status","sent");
                                           firestore.collection(ProjectStorage.KEY_FRIEND).add(addFriend);
                                       }
                                   }
                                });


                    }
                }
            });
        }
    }


}
