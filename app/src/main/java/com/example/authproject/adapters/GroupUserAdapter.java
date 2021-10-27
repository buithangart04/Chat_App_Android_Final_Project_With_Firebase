package com.example.authproject.adapters;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.authproject.databinding.ItemGroupUserBinding;
import com.example.authproject.listeners.GetUserGroupListener;
import com.example.authproject.models.User;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class GroupUserAdapter extends RecyclerView.Adapter<GroupUserAdapter.UserViewHolder> implements Filterable {
    private List<User> users;
    private List<User> userSearch;
    private List<User> chosenUsers = new ArrayList<>();
    private GetUserGroupListener getUserGroupListener;
    private boolean isTextSearchEmpty = true;

    public GroupUserAdapter(List<User> users, GetUserGroupListener getUserGroupListener) {
        this.users = users;
        this.userSearch = users;
        this.getUserGroupListener = getUserGroupListener;
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
    }

    @Override
    public int getItemCount() {
        return userSearch.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                if (charSequence.toString().isEmpty()) {
                    isTextSearchEmpty = true;
                    userSearch = users;
                } else {
                    isTextSearchEmpty = false;
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


    public class UserViewHolder extends RecyclerView.ViewHolder {
        ItemGroupUserBinding binding;

        UserViewHolder(ItemGroupUserBinding itemContainerUserBinding) {
            super(itemContainerUserBinding.getRoot());
            binding = itemContainerUserBinding;
        }

        private int getUserPos(User userSearch) {
            int pos = 0;
            for (User u : users) {
                if (u.getEmail().equals(userSearch.getEmail())) {
                    pos = users.indexOf(u);
                }
            }
            return pos;
        }


        private void setUserData(User user, UserViewHolder holder) {
            binding.textName.setText(user.getFullName());
            binding.checkBox.setOnClickListener(view -> {
                if (binding.checkBox.isChecked()) {
                    int position = holder.getAdapterPosition();

                    chosenUsers.add(userSearch.get(position));
                    getUserGroupListener.onClick(userSearch.get(position));
                    getUserGroupListener.onClickUser("add");

                    if (!isTextSearchEmpty) {
                        users.remove(getUserPos(userSearch.get(position)));
                    }

                    userSearch.remove(position);
                    binding.checkBox.setChecked(false);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, userSearch.size());

                }
            });

        }
    }
}
