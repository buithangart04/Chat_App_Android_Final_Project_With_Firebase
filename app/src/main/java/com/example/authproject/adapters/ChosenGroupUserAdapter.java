package com.example.authproject.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.authproject.databinding.ItemChosenGroupUserBinding;
import com.example.authproject.listeners.GetUserGroupListener;
import com.example.authproject.models.User;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ChosenGroupUserAdapter extends RecyclerView.Adapter<ChosenGroupUserAdapter.UserViewHolder> {
    private List<User> chosenUsers;
    private List<User> users = new ArrayList<>();
    private GetUserGroupListener getUserGroupListener;

    public ChosenGroupUserAdapter(List<User> chosenUsers, GetUserGroupListener getUserGroupListener) {

        this.chosenUsers = chosenUsers;
        this.getUserGroupListener = getUserGroupListener;
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
        holder.setUserData(chosenUsers.get(position), holder);
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
            Picasso.get().load(user.getUri())
                    .resize(binding.imageProfile3.getLayoutParams().width,
                            binding.imageProfile3.getLayoutParams().height)
                    .into(binding.imageProfile3);

            binding.layoutCancel.setOnClickListener(view -> {
                int position = holder.getAdapterPosition();
                users.add(chosenUsers.get(position));
                getUserGroupListener.onClick(chosenUsers.get(position));
                getUserGroupListener.onClickUser("remove");
                chosenUsers.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, chosenUsers.size());
            });
        }
    }
}
