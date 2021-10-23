package com.example.authproject.adapters;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;


public class FileChooserAdapter extends RecyclerView.Adapter<FileChooserAdapter.FileChooserViewHolder> {
    List<String> fileName;
    @NonNull
    @Override
    public FileChooserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull FileChooserViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }
    class FileChooserViewHolder extends RecyclerView.ViewHolder{

        public FileChooserViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
