package com.example.authproject.adapters;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.authproject.databinding.FileChooserViewholderBinding;

import java.util.List;


public class FileChooserAdapter extends RecyclerView.Adapter<FileChooserAdapter.FileChooserViewHolder> {
    List<Uri> listFileSelected;
    public FileChooserAdapter( List<Uri> listFileSelected){
        this.listFileSelected= listFileSelected;
    }

    @NonNull
    @Override
    public FileChooserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        FileChooserViewholderBinding binding= FileChooserViewholderBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false);
        return new FileChooserViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull FileChooserViewHolder holder, int position) {
        holder.setFileData(listFileSelected.get(position),position);
    }

    @Override
    public int getItemCount() {
        return listFileSelected.size();
    }
    class FileChooserViewHolder extends RecyclerView.ViewHolder{
        FileChooserViewholderBinding binding ;
        public FileChooserViewHolder(FileChooserViewholderBinding binding) {
            super(binding.getRoot());
            this.binding= binding;
        }
        public void setFileData(Uri uri,int pos ){
            binding.fileName.setText(uri.getLastPathSegment());
            binding.layoutCancel.setOnClickListener(v->onCancel(pos));
        }
        public void onCancel(int pos){
            listFileSelected.remove(pos);
            notifyItemRemoved(pos);
            notifyItemRangeChanged(pos,listFileSelected.size());
        }
    }
}
