package com.example.authproject.adapters;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.authproject.ChatActivity;
import com.example.authproject.databinding.ItemMessageBinding;
import com.example.authproject.databinding.ItemReceiverMessageBinding;
import com.example.authproject.models.ChatMessage;
import com.example.authproject.utilities.FileUtilities;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
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
        public void setViewGone(){
            binding.exoPlayer.setVisibility(View.GONE);
            binding.fileParent.setVisibility(View.GONE);
            binding.imageMessage.setVisibility(View.GONE);
            binding.textMessage.setVisibility(View.GONE);
        }
        void setData(ChatMessage chatMessage){
            setViewGone();
            if (chatMessage.type.contains("text")) {
                binding.textMessage.setVisibility(View.VISIBLE);
                binding.textMessage.setText(chatMessage.message);
            }
            else if(chatMessage.type.contains("image")){
                binding.imageMessage.setVisibility(View.VISIBLE);
                Picasso.get().load(chatMessage.message).into(binding.imageMessage);
            }
            else if(chatMessage.type.contains("file")){
                binding.fileParent.setVisibility(View.VISIBLE);
                binding.fileName.setText(chatMessage.fileName);
                binding.fileParent.setOnClickListener(v->new FileUtilities().onDownloadFile(itemView.getContext(),chatMessage.message ));
            }else if(chatMessage.type.contains("video")){
                binding.exoPlayer.setVisibility(View.VISIBLE);
                new FileUtilities().loadVideoToView(itemView.getContext(),binding.exoPlayer,chatMessage.message);
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
        public void setViewGone(){
            binding.exoPlayer.setVisibility(View.GONE);
            binding.fileParent.setVisibility(View.GONE);
            binding.imageMessage.setVisibility(View.GONE);
            binding.textReceiveMessage.setVisibility(View.GONE);
        }
        void setData(ChatMessage chatMessage){
            setViewGone();
            if (chatMessage.type.contains("text")){
                binding.textReceiveMessage.setVisibility(View.VISIBLE);
                binding.textReceiveMessage.setText(chatMessage.message);
            }
            else if(chatMessage.type.contains("image")){
                binding.imageMessage.setVisibility(View.VISIBLE);
                Picasso.get().load(chatMessage.message).into(binding.imageMessage);
            }
            else if(chatMessage.type.contains("file")){
                binding.fileParent.setVisibility(View.VISIBLE);
                binding.fileName.setText(chatMessage.fileName);
                binding.fileParent.setOnClickListener(v->new FileUtilities().onDownloadFile(itemView.getContext(),chatMessage.message ));
            }else if(chatMessage.type.contains("video")){
                binding.exoPlayer.setVisibility(View.VISIBLE);
                new FileUtilities().loadVideoToView(itemView.getContext(),binding.exoPlayer,chatMessage.message);
            }
            binding.textDatetime.setText(chatMessage.dateTime);
        }
    }
}
