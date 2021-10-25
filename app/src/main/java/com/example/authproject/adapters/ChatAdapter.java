package com.example.authproject.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.authproject.databinding.ItemMessageBinding;
import com.example.authproject.databinding.ItemReceiverMessageBinding;
import com.example.authproject.models.ChatMessage;
import com.squareup.picasso.Picasso;

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
            if (chatMessage.type.contains("text")) {
                binding.textMessage.setVisibility(View.VISIBLE);
                binding.textMessage.setText(chatMessage.message);
                binding.imageMessage.setVisibility(View.GONE);
            }
            else if(chatMessage.type.contains("image")){
                binding.imageMessage.setVisibility(View.VISIBLE);
                binding.textMessage.setVisibility(View.GONE);
                Picasso.get().load(chatMessage.message).into(binding.imageMessage);
            }
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
            if (chatMessage.type.contains("text")){
                binding.textReceiveMessage.setVisibility(View.VISIBLE);
                binding.imageMessage.setVisibility(View.GONE);
                binding.textReceiveMessage.setText(chatMessage.message);
            }
            else if(chatMessage.type.contains("image")){
                binding.imageMessage.setVisibility(View.VISIBLE);
                binding.textReceiveMessage.setVisibility(View.GONE);
                Picasso.get().load(chatMessage.message).into(binding.imageMessage);
            }
            binding.textDatetime.setText(chatMessage.dateTime);
        }
    }
}
