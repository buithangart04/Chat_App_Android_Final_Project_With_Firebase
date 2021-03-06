package com.example.authproject.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.authproject.databinding.ItemContainerUserBinding;
import com.example.authproject.listeners.UserListener;
import com.example.authproject.models.User;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ParticipantAdapter extends RecyclerView.Adapter<ParticipantAdapter.UserViewHolder> {
    private final List<User> chosenUser;
    private final UserListener userListener;

    public ParticipantAdapter(List<User> chosenUser, UserListener userListener) {
        this.chosenUser = chosenUser;
        this.userListener = userListener;

    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemContainerUserBinding itemContainerUserBinding = ItemContainerUserBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new ParticipantAdapter.UserViewHolder(itemContainerUserBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ParticipantAdapter.UserViewHolder holder, int position) {
        holder.setUserData(chosenUser.get(position));
    }

    @Override
    public int getItemCount() {
        return chosenUser.size();
    }

    class UserViewHolder extends RecyclerView.ViewHolder {
        ItemContainerUserBinding binding;

        UserViewHolder(ItemContainerUserBinding itemContainerUserBinding) {
            super(itemContainerUserBinding.getRoot());
            binding = itemContainerUserBinding;
        }

        void setUserData(User user) {
            binding.textName.setText(user.getFullName());
            binding.textEmail.setText(user.getEmail());
            Picasso.get().load(user.getUri())
                    .resize(binding.imageProfile.getLayoutParams().width,
                            binding.imageProfile.getLayoutParams().height)
                    .into(binding.imageProfile);
            binding.getRoot().setOnClickListener(v -> userListener.onUserCLick(user,null));
        }
    }
}
