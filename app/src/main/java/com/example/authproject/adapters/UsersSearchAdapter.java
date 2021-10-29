package com.example.authproject.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.authproject.databinding.ItemContainerUserBinding;
import com.example.authproject.listeners.UserListener;
import com.example.authproject.models.User;

import java.util.List;

public class UsersSearchAdapter extends RecyclerView.Adapter<UsersSearchAdapter.UserViewHolder>  {

    private final List<User> users;
    private final UserListener userListener;
    public UsersSearchAdapter(List<User> users,UserListener userListener) {
        this.users = users;
        this.userListener=userListener;
    }

    @NonNull
    @Override
    public UsersSearchAdapter.UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemContainerUserBinding itemContainerUserBinding = ItemContainerUserBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new UsersSearchAdapter.UserViewHolder(itemContainerUserBinding);
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
        ItemContainerUserBinding binding;
        UserViewHolder(ItemContainerUserBinding itemContainerUserBinding){
            super(itemContainerUserBinding.getRoot());
            binding = itemContainerUserBinding;

        }
        void setUserData(User user){
            binding.textName.setText(user.getFullName());
            binding.textEmail.setText(user.getEmail());
            binding.getRoot().setOnClickListener(v-> userListener.onUserCLick(user));
        }
    }
}
