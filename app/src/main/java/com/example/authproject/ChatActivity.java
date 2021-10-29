package com.example.authproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.transition.Slide;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.util.Base64;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import com.example.authproject.adapters.ChatAdapter;
import com.example.authproject.databinding.ActivityChatBinding;
import com.example.authproject.models.ChatMessage;
import com.example.authproject.models.User;
import com.example.authproject.utilities.ProjectStorage;
import com.example.authproject.utilities.PreferenceManager;
import com.example.authproject.utilities.FunctionalUtilities;
import com.google.firebase.firestore.DocumentChange;;
import com.google.firebase.firestore.EventListener;
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
    boolean showOptionsFile= false;

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
        setCallListener(receiverUser);
    }

    private void init() {
        preferenceManager = new PreferenceManager(getApplicationContext());
        chatMessages = new ArrayList<>();
        chatAdapter = new ChatAdapter(chatMessages, preferenceManager.getString(ProjectStorage.KEY_USER_EMAIL));
        binding.recMessage.setLayoutManager(new LinearLayoutManager(ChatActivity.this));
        binding.recMessage.setAdapter(chatAdapter);
    }

    //send file or image
    private void sendAttachment(){
        Transition transition = new Slide(Gravity.BOTTOM);
        transition.setDuration(600);
        transition.addTarget(binding.layoutChooseFile);

        TransitionManager.beginDelayedTransition(binding.viewBackground, transition);
        binding.layoutChooseFile.setVisibility(showOptionsFile ? View.GONE: View.VISIBLE );
        binding.imageviewAdd.setImageResource(showOptionsFile?R.drawable.ic_add:R.drawable.ic_clear);
        showOptionsFile=!showOptionsFile;

//        CharSequence [] options = new CharSequence[]{
//                "Images",
//                "PDF files",
//                "Word file"
//        };
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setTitle("Select the files");
//        builder.setItems(options, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
//                switch(i){
//                    case 0:
//                        break;
//                    case 1:
//                        break;
//                    case 2 :
//                        break;
//
//                }
//            }
//        });
    }

    private void sendMessage() {
        HashMap<String, Object> message = new HashMap<>();
        message.put(ProjectStorage.KEY_SENDER_EMAIL, preferenceManager.getString(ProjectStorage.KEY_USER_EMAIL));
        message.put(ProjectStorage.KEY_RECEIVER_EMAIL, receiverUser.getEmail());
        message.put(ProjectStorage.KEY_MESSAGE, binding.inputMessage.getText().toString());
        message.put(ProjectStorage.KEY_TIMESTAMP, new Date());
        ProjectStorage.DATABASE_REFERENCE.collection(ProjectStorage.KEY_COLLECTION_CHAT).add(message);
        binding.inputMessage.setText(null);
    }

    public void listenMessage() {
        ProjectStorage.DATABASE_REFERENCE.collection(ProjectStorage.KEY_COLLECTION_CHAT)
                .whereEqualTo(ProjectStorage.KEY_SENDER_EMAIL, preferenceManager.getString(ProjectStorage.KEY_USER_EMAIL))
                .whereEqualTo(ProjectStorage.KEY_RECEIVER_EMAIL, receiverUser.getEmail())
                .addSnapshotListener(eventListener);
        ProjectStorage.DATABASE_REFERENCE.collection(ProjectStorage.KEY_COLLECTION_CHAT)
                .whereEqualTo(ProjectStorage.KEY_SENDER_EMAIL, receiverUser.getEmail())
                .whereEqualTo(ProjectStorage.KEY_RECEIVER_EMAIL, preferenceManager.getString(ProjectStorage.KEY_USER_EMAIL))
                .addSnapshotListener(eventListener);
    }

    private final EventListener<QuerySnapshot> eventListener = (value, error) -> {
        if (error != null) return;
        if (value != null) {
            int count = chatMessages.size();
            for (DocumentChange docs : value.getDocumentChanges()) {
                if (docs.getType() == DocumentChange.Type.ADDED) {
                    ChatMessage chatMessage = new ChatMessage();
                    chatMessage.senderEmail  = docs.getDocument().getString(ProjectStorage.KEY_SENDER_EMAIL);
                    chatMessage.receiverEmail  = docs.getDocument().getString(ProjectStorage.KEY_RECEIVER_EMAIL);
                    chatMessage.message  = docs.getDocument().getString(ProjectStorage.KEY_MESSAGE);
                    chatMessage.dateObject  = docs.getDocument().getDate(ProjectStorage.KEY_TIMESTAMP);
                    chatMessage.dateTime= FunctionalUtilities.getDateFormat(chatMessage.dateObject);
                    chatMessages.add(chatMessage);
                }

            }
            Collections.sort(chatMessages, (obj1, obj2) -> {
                return obj1.dateObject.compareTo(obj2.dateObject);
            });
            if (count == 0) {
                chatAdapter.notifyDataSetChanged();
            } else {
                //visible true ------------------------------------
                // change recyler View
                chatAdapter.notifyItemRangeInserted(chatMessages.size(), chatMessages.size());
                binding.recMessage.smoothScrollToPosition(chatMessages.size() - 1);
            }
            binding.recMessage.setVisibility(View.VISIBLE);
        }
    };

    private Bitmap getBitMapFromEncodingString(String encodedImage) {
        byte[] bytes = Base64.decode(encodedImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    private void loadReceiversDetails() {
        receiverUser = (User) getIntent().getSerializableExtra(ProjectStorage.KEY_USER);
        binding.textName.setText(receiverUser.getFullName());
    }

    private void setListener() {
        binding.imageBack.setOnClickListener(v -> onBackPressed());
        binding.layoutSend.setOnClickListener(v -> sendMessage());
        binding.layoutOptionSendFile.setOnClickListener(v -> sendAttachment());
    }

    private void setCallListener(User user) {
        binding.imageCall.setOnClickListener(v -> initiateAudioMeeting(user));
        binding.imageVideo.setOnClickListener(v -> initiateVideoMeeting(user));
    }

    private void initiateVideoMeeting(User user) {
        if (user.token == null || user.token.trim().isEmpty()) {
            Toast.makeText(this, user.getFullName() + " is not avaiable for video call", Toast.LENGTH_SHORT).show();
        } else {
            Intent intent = new Intent(getApplicationContext(), OutgoingInvitationActivity.class);
            intent.putExtra("user", user);
            intent.putExtra("type", "video");
            startActivity(intent);
        }
    }

    private void initiateAudioMeeting(User user) {
        if (user.token == null || user.token.trim().isEmpty()) {
            Toast.makeText(this, user.getFullName() + " is not avaiable for audio call", Toast.LENGTH_SHORT).show();
        } else {
            Intent intent = new Intent(getApplicationContext(), OutgoingInvitationActivity.class);
            intent.putExtra("user", user);
            intent.putExtra("type", "audio");
            startActivity(intent);
        }
    }
}