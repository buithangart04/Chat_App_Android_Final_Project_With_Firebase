package com.example.authproject.adapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.authproject.databinding.ItemContainerUserBinding;
import com.example.authproject.databinding.ItemGroupUserBinding;
import com.example.authproject.listeners.UserListener;
import com.example.authproject.models.User;


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class GroupUserAdapter extends RecyclerView.Adapter<GroupUserAdapter.UserViewHolder> implements Filterable {
    private List<User> users;
    private List<User> userSearch;
    private List<User> userGroup = new ArrayList<>();

    public GroupUserAdapter(List<User> users) {
        this.users = users;
        this.userSearch = users;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemGroupUserBinding itemContainerUserBinding = ItemGroupUserBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new UserViewHolder(itemContainerUserBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {

        holder.setUserData(userSearch.get(position), holder);

        if (userGroup != null) {
            Log.d("tag", userGroup.toString());
        }

        Log.d("tag", "onBindViewHolder");

    }

    @Override
    public int getItemCount() {
        Log.d("tag", "getItemCount");
        return userSearch.size();
    }

    @Override
    public Filter getFilter() {
        Log.d("tag", "getFilter");
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {

                if (charSequence.toString().isEmpty()) {
                    userSearch = users;
                } else {
                    List<User> listFilter = new ArrayList<>();
                    for (User user : users) {
                        if (user.getFullName().toLowerCase().contains(charSequence.toString().toLowerCase())) {
                            listFilter.add(user);
                        }
                    }
                    userSearch = listFilter;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = userSearch;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {

                userSearch = (List<User>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }


    class UserViewHolder extends RecyclerView.ViewHolder {
        ItemGroupUserBinding binding;

        UserViewHolder(ItemGroupUserBinding itemContainerUserBinding) {
            super(itemContainerUserBinding.getRoot());
            binding = itemContainerUserBinding;
        }

        void setUserData(User user, UserViewHolder holder) {
            binding.textName.setText(user.getFullName());
            binding.checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (binding.checkBox.isChecked()) {
                        userSearch.remove(holder.getAdapterPosition());
                        userGroup.add(userSearch.get(holder.getAdapterPosition()));
                        binding.checkBox.setChecked(false);
                        notifyDataSetChanged();
                    }
                }
            });

        }
    }
}
