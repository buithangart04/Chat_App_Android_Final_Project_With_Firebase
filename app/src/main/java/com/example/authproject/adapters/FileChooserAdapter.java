package com.example.authproject.adapters;

import android.net.Uri;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.authproject.databinding.FileChooserViewholderBinding;

import java.util.List;


public class FileChooserAdapter extends RecyclerView.Adapter<FileChooserAdapter.FileChooserViewHolder> {
    List<Pair<Uri,String>> listFileSelected;
    public FileChooserAdapter( List<Pair<Uri,String>> listFileSelected){
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
        public void setFileData(Pair<Uri,String> pair,int pos ){
            binding.fileName.setText(pair.second.split("[|]")[0]);
            binding.layoutCancel.setOnClickListener(v->onCancel(pos));
        }
        public void onCancel(int pos){
            listFileSelected.remove(pos);
            notifyItemRemoved(pos);
            notifyItemRangeChanged(pos,listFileSelected.size());
        }
    }
}
