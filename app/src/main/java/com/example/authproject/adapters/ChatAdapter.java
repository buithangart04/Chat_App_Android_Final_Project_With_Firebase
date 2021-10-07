package com.example.authproject.adapters;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.authproject.databinding.ItemMessageBinding;
import com.example.authproject.databinding.ItemReceiverMessageBinding;
import com.example.authproject.models.ChatMessage;
import com.example.authproject.utilities.Utilites;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final List<ChatMessage> chatMessages ;
    private String senderEmail ;

    public static  final int TYPE_SENT=1 ;
    public static  final int TYPE_RECEIVER=2 ;

    public ChatAdapter(List<ChatMessage> chatMessages, String senderEmail ) {
        this.chatMessages = chatMessages;
        this.senderEmail = senderEmail;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType==TYPE_SENT){
            return new SentMessageViewHolder
                    (ItemMessageBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false)
                    );
        }else {
            return new ReceiverMessageViewHolder(
                    ItemReceiverMessageBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false)
            );
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(getItemViewType(position)==TYPE_SENT){
            ((SentMessageViewHolder) holder).setData(chatMessages.get(position));
        }else {
            ((ReceiverMessageViewHolder) holder).setData(chatMessages.get(position));
        }

    }

    @Override
    public int getItemCount() {
        return chatMessages.size();
    }

    @Override
    public int getItemViewType(int position) {
        if(chatMessages.get(position).senderEmail.equals(senderEmail) ){
            return TYPE_SENT;
        }else {
            return TYPE_RECEIVER;
        }

    }

    static class  SentMessageViewHolder extends RecyclerView.ViewHolder{
        private final ItemMessageBinding binding;
        public SentMessageViewHolder(@NonNull ItemMessageBinding itemMessageBinding) {
            super(itemMessageBinding.getRoot());
            binding= itemMessageBinding;
        }
        void setData(ChatMessage chatMessage){
            binding.textMessage.setText(chatMessage.message);
            binding.textDatetime.setText(chatMessage.dateTime);
        }
    }
    static class  ReceiverMessageViewHolder extends RecyclerView.ViewHolder{
        private final ItemReceiverMessageBinding binding;
        public ReceiverMessageViewHolder(@NonNull ItemReceiverMessageBinding itemReceiverMessageBinding) {
            super(itemReceiverMessageBinding.getRoot());
            binding= itemReceiverMessageBinding;
        }
        void setData(ChatMessage chatMessage){
            binding.textReceiveMessage.setText(chatMessage.message);
            binding.textDatetime.setText(chatMessage.dateTime);
        }
    }
}
