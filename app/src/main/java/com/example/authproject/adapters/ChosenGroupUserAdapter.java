package com.example.authproject.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.authproject.databinding.ItemChosenGroupUserBinding;
import com.example.authproject.models.User;

import java.util.List;

public class ChosenGroupUserAdapter extends RecyclerView.Adapter<ChosenGroupUserAdapter.UserViewHolder> {
    private List<User> userGroup;
    private List<User> chosenUsers;
    public ChosenGroupUserAdapter(List<User> userGroup) {
        this.userGroup = userGroup;
        this.chosenUsers=userGroup;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemChosenGroupUserBinding itemChosenGroupUserBinding = ItemChosenGroupUserBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new ChosenGroupUserAdapter.UserViewHolder(itemChosenGroupUserBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        holder.setUserData(chosenUsers.get(position),holder);
    }

    @Override
    public int getItemCount() {
        return chosenUsers.size();
    }

    class UserViewHolder extends RecyclerView.ViewHolder {
        ItemChosenGroupUserBinding binding;

        UserViewHolder(ItemChosenGroupUserBinding itemChosenGroupUserBinding) {
            super(itemChosenGroupUserBinding.getRoot());
            binding = itemChosenGroupUserBinding;
        }


        void setUserData(User user, UserViewHolder holder) {
            binding.textName2.setText(user.getFullName());
            binding.layoutCancel.setOnClickListener(view -> {
                int position = holder.getAdapterPosition();
                chosenUsers.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, chosenUsers.size());
            });
        }
    }
}
