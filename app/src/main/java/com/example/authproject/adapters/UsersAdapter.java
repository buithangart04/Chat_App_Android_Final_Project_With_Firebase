package com.example.authproject.adapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.example.authproject.databinding.ItemContainerUserBinding;
import com.example.authproject.listeners.UserListener;
import com.example.authproject.models.Group;
import com.example.authproject.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.util.List;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.UserViewHolder> {
    private final List<User> users;
    private final UserListener userListener;
    List<Group> groups;

    public UsersAdapter(List<User> users, List<Group> groups,UserListener userListener) {
        this.users = users;
        this.groups= groups;
        this.userListener=userListener;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemContainerUserBinding itemContainerUserBinding = ItemContainerUserBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new UserViewHolder(itemContainerUserBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        if(position>= users.size()){
            holder.setUserData(null,groups.get(position- users.size()));
        }else  holder.setUserData(users.get(position),null);

    }

    @Override
    public int getItemCount() {
        return users.size()+ groups.size();
    }

    class UserViewHolder extends RecyclerView.ViewHolder{
        ItemContainerUserBinding binding;
        UserViewHolder(ItemContainerUserBinding itemContainerUserBinding){
            super(itemContainerUserBinding.getRoot());
            binding = itemContainerUserBinding;

        }
        void setUserData(User user, Group group){
            String uri =null;
            if(user!=null) {
                binding.textName.setText(user.getFullName());
                binding.textEmail.setText(user.getEmail());
                binding.textEmail.setVisibility(View.VISIBLE);
                uri= user.getUri();
            }
            else {
                uri= group.groupURI;
                binding.textName.setText(group.groupName);
                binding.textEmail.setVisibility(View.GONE);
            }
            Picasso.get().load(uri).resize(binding.imageProfile.getLayoutParams().width,binding.imageProfile.getLayoutParams().height).into(binding.imageProfile);
            binding.getRoot().setOnClickListener(v -> userListener.onUserCLick(user,group));
        }
    }
    private Bitmap getUserImage(String encodedImage){
        byte[] bytes = Base64.decode(encodedImage,Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes,0,bytes.length);
    }
}
