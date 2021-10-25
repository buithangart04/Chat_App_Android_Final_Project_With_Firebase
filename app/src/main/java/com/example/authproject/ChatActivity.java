package com.example.authproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;

import com.example.authproject.adapters.ChatAdapter;
import com.example.authproject.databinding.ActivityChatBinding;
import com.example.authproject.models.ChatMessage;
import com.example.authproject.models.User;
import com.example.authproject.utilities.Constants;
import com.example.authproject.utilities.PreferenceManager;
import com.example.authproject.utilities.Utilites;
import com.google.firebase.firestore.DocumentChange;;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class ChatActivity extends AppCompatActivity {
    private ActivityChatBinding binding;
    private User receiverUser;
    List<ChatMessage> chatMessages;
    private ChatAdapter chatAdapter;
    private PreferenceManager preferenceManager;
    private FirebaseFirestore database ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setListener();
        loadReceiversDetails();
        init();
        listenMessage();
    }
    private void init (){
        preferenceManager =new PreferenceManager(getApplicationContext());
        chatMessages = new ArrayList<>();
        chatAdapter= new ChatAdapter(chatMessages ,preferenceManager.getString(Constants.KEY_USER_EMAIL));
        binding.recMessage.setLayoutManager(new LinearLayoutManager(ChatActivity.this));
        binding.recMessage.setAdapter(chatAdapter);
        database= FirebaseFirestore.getInstance();
    }

    private void sendMessage (){
        HashMap<String,Object> message = new HashMap<>();
        message.put(Constants.KEY_SENDER_EMAIL, preferenceManager.getString(Constants.KEY_USER_EMAIL));
        message.put(Constants.KEY_RECEIVER_EMAIL , receiverUser.getEmail());
        message.put(Constants.KEY_MESSAGE,binding.inputMessage.getText().toString());
        message.put(Constants.KEY_TIMESTAMP,new Date() );
        database.collection(Constants.KEY_COLLECTION_CHAT).add(message);
        binding.inputMessage.setText(null);
    }
    public void listenMessage(){
        database.collection(Constants.KEY_COLLECTION_CHAT)
                .whereEqualTo(Constants.KEY_SENDER_EMAIL, preferenceManager.getString(Constants.KEY_USER_EMAIL))
                .whereEqualTo(Constants.KEY_RECEIVER_EMAIL, receiverUser.getEmail())
                .addSnapshotListener(eventListener);
        database.collection(Constants.KEY_COLLECTION_CHAT)
                .whereEqualTo(Constants.KEY_SENDER_EMAIL, receiverUser.getEmail())
                .whereEqualTo(Constants.KEY_RECEIVER_EMAIL,preferenceManager.getString(Constants.KEY_USER_EMAIL) )
                .addSnapshotListener(eventListener);
    }
    private final EventListener<QuerySnapshot> eventListener = (value, error)->{
        if(error!=null) return;
        if(value!=null ){
            int count = chatMessages.size();
            for(DocumentChange docs : value.getDocumentChanges()){
                if(docs.getType()==DocumentChange.Type.ADDED){
                    ChatMessage chatMessage = new ChatMessage();
                    chatMessage.senderEmail  = docs.getDocument().getString(Constants.KEY_SENDER_EMAIL);
                    chatMessage.receiverEmail  = docs.getDocument().getString(Constants.KEY_RECEIVER_EMAIL);
                    chatMessage.message  = docs.getDocument().getString(Constants.KEY_MESSAGE);
                    chatMessage.dateObject  = docs.getDocument().getDate(Constants.KEY_TIMESTAMP);
                    chatMessage.dateTime= Utilites.getDateFormat(chatMessage.dateObject);
                    chatMessages.add(chatMessage);
                }

            }
            Collections.sort(chatMessages, (obj1,obj2) -> {return obj1.dateObject.compareTo(obj2.dateObject) ;});
            if(count==0){
                chatAdapter.notifyDataSetChanged();
            }else {
                //visible true ------------------------------------
                // change recyler View
                chatAdapter.notifyItemRangeInserted(chatMessages.size(),chatMessages.size());
                binding.recMessage.smoothScrollToPosition(chatMessages.size()-1);
            }
            binding.recMessage.setVisibility(View.VISIBLE);
        }
    };
    private Bitmap getBitMapFromEncodingString (String encodedImage){
        byte []  bytes = Base64.decode(encodedImage,Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes,0,bytes.length);
    }
    private void loadReceiversDetails (){
        receiverUser = (User) getIntent().getSerializableExtra(Constants.KEY_USER);
        binding.textName.setText(receiverUser.getFullName());
    }
    private void setListener(){
        binding.imageBack.setOnClickListener(v-> onBackPressed());
        binding.layoutSend.setOnClickListener(v-> sendMessage());
    }


}