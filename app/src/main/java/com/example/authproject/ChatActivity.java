package com.example.authproject;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.transition.Slide;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.authproject.adapters.ChatAdapter;
import com.example.authproject.adapters.FileChooserAdapter;
import com.example.authproject.databinding.ActivityChatBinding;
import com.example.authproject.listeners.UploadFileSuccessListener;
import com.example.authproject.models.ChatMessage;
import com.example.authproject.models.User;
import com.example.authproject.utilities.FileUtilities;
import com.example.authproject.utilities.FunctionalUtilities;
import com.example.authproject.utilities.PreferenceManager;
import com.example.authproject.utilities.ProjectStorage;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
;

public class ChatActivity extends AppCompatActivity implements UploadFileSuccessListener {
    private ActivityChatBinding binding;
    private User receiverUser;
    List<ChatMessage> chatMessages;
    private ChatAdapter chatAdapter;
    private FileChooserAdapter fileChooserAdapter ;
    private PreferenceManager preferenceManager;
    boolean showOptionsFile= false;
    List<Uri> listFileSelected;
    private static final int PICK_IMAGE_REQUEST = 3241;
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
        Log.d("Tag", "init");
        preferenceManager =new PreferenceManager(getApplicationContext());
        chatMessages = new ArrayList<>();
        listFileSelected = new ArrayList<>();
        chatAdapter= new ChatAdapter(chatMessages ,preferenceManager.getString(ProjectStorage.KEY_USER_EMAIL));
        binding.recMessage.setLayoutManager(new LinearLayoutManager(ChatActivity.this));
        binding.recMessage.setAdapter(chatAdapter);
        // set adapter for message
        fileChooserAdapter = new FileChooserAdapter(listFileSelected);
        binding.recFileAdd.setLayoutManager(new LinearLayoutManager(binding.sendBox.getContext(),LinearLayoutManager.HORIZONTAL,false));
        binding.recFileAdd.setAdapter(fileChooserAdapter);
    }
    //send file or image
    private void showFileOptions(){
        Log.d("Tag", "showFileOptions");
        Transition transition = new Slide(Gravity.BOTTOM);
        transition.setDuration(400);
        transition.addTarget(binding.layoutChooseFile);

        TransitionManager.beginDelayedTransition(binding.viewBackground, transition);
        binding.layoutChooseFile.setVisibility(showOptionsFile ? View.GONE: View.VISIBLE );
        binding.imageviewAdd.setImageResource(showOptionsFile?R.drawable.ic_add:R.drawable.ic_clear);
        showOptionsFile=!showOptionsFile;

    }
    private void sendMessage (){
        if(!binding.inputMessage.getText().toString().trim().isEmpty()&&binding.inputMessage.getText()!= null){
            Log.d("Tag", "sendMessage");
            HashMap<String,Object> message = new HashMap<>();
            message.put(ProjectStorage.KEY_SENDER_EMAIL, preferenceManager.getString(ProjectStorage.KEY_USER_EMAIL));
            message.put(ProjectStorage.KEY_RECEIVER_EMAIL , receiverUser.getEmail());
            message.put(ProjectStorage.KEY_MESSAGE,binding.inputMessage.getText().toString());
            message.put(ProjectStorage.KEY_TIMESTAMP,new Date() );
            message.put(ProjectStorage.KEY_MESSAGE_TYPE,"text");
            ProjectStorage.DATABASE_REFERENCE.collection(ProjectStorage.KEY_COLLECTION_CHAT).add(message);
            binding.inputMessage.setText(null);
        }
        if(listFileSelected.size()!=0){
            Log.d("Tag", "sendMessage File");
            for(Uri uri: listFileSelected){
               new FileUtilities().uploadFile(this,this,uri);
            }
            listFileSelected.clear();
            fileChooserAdapter.notifyDataSetChanged();
        }

    }
    public void listenMessage(){
        Log.d("Tag", "listenMessage");
        ProjectStorage.DATABASE_REFERENCE.collection(ProjectStorage.KEY_COLLECTION_CHAT)
                .whereEqualTo(ProjectStorage.KEY_SENDER_EMAIL, preferenceManager.getString(ProjectStorage.KEY_USER_EMAIL))
                .whereEqualTo(ProjectStorage.KEY_RECEIVER_EMAIL, receiverUser.getEmail())
                .addSnapshotListener(eventListener);
        ProjectStorage.DATABASE_REFERENCE.collection(ProjectStorage.KEY_COLLECTION_CHAT)
                .whereEqualTo(ProjectStorage.KEY_SENDER_EMAIL, receiverUser.getEmail())
                .whereEqualTo(ProjectStorage.KEY_RECEIVER_EMAIL,preferenceManager.getString(ProjectStorage.KEY_USER_EMAIL) )
                .addSnapshotListener(eventListener);
    }
    private final EventListener<QuerySnapshot> eventListener = (value, error)->{
        if(error!=null) return;
        if(value!=null ){
            int count = chatMessages.size();
            for(DocumentChange docs : value.getDocumentChanges()){
                if(docs.getType()==DocumentChange.Type.ADDED){
                    ChatMessage chatMessage = new ChatMessage();
                    chatMessage.senderEmail  = docs.getDocument().getString(ProjectStorage.KEY_SENDER_EMAIL);
                    chatMessage.receiverEmail  = docs.getDocument().getString(ProjectStorage.KEY_RECEIVER_EMAIL);
                    chatMessage.message  = docs.getDocument().getString(ProjectStorage.KEY_MESSAGE);
                    chatMessage.dateObject  = docs.getDocument().getDate(ProjectStorage.KEY_TIMESTAMP);
                    chatMessage.dateTime= FunctionalUtilities.getDateFormat(chatMessage.dateObject);
                    chatMessage.type= docs.getDocument().getString(ProjectStorage.KEY_MESSAGE_TYPE);
                    chatMessages.add(chatMessage);
                }

            }
            Collections.sort(chatMessages, (obj1,obj2) -> {return obj1.dateObject.compareTo(obj2.dateObject) ;});
            if(count==0){
                Log.d("Tag", "eventListener = 0");
                chatAdapter.notifyDataSetChanged();
            }else {
                Log.d("Tag", "eventListener");
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
        Log.d("Tag", "loadReceiversDetails");
        receiverUser = (User) getIntent().getSerializableExtra(ProjectStorage.KEY_USER);
        binding.textName.setText(receiverUser.getFullName());
    }
    private void setListener(){
        Log.d("Tag", "setListener");
        binding.imageBack.setOnClickListener(v-> onBackPressed());
        binding.layoutSend.setOnClickListener(v-> sendMessage());
        binding.layoutOptionSendFile.setOnClickListener(v->showFileOptions());
    }

    public void onChoseOptionSend(View view) {
        Log.d("Tag", "onChoseOptionSend");
        switch(view.getId()){
            case R.id.optionImage:
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Select an image"), PICK_IMAGE_REQUEST);
                break;
            case R.id.optionFile:
                Toast.makeText(ChatActivity.this, "file choose", Toast.LENGTH_LONG).show();
                break;
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==PICK_IMAGE_REQUEST&& resultCode==RESULT_OK&& data!=null && data.getData()!=null){
            Uri filePath = data.getData();
            listFileSelected.add(filePath);
            fileChooserAdapter.notifyDataSetChanged();
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        chatAdapter.notifyDataSetChanged();
        binding.recMessage.setAdapter(chatAdapter);
    }
    @Override
    public void onUploadFileSuccess(Uri uri) {
        Log.d("Tag", "onUploadFileSuccess");
        HashMap<String,Object> message = new HashMap<>();
        message.put(ProjectStorage.KEY_SENDER_EMAIL, preferenceManager.getString(ProjectStorage.KEY_USER_EMAIL));
        message.put(ProjectStorage.KEY_RECEIVER_EMAIL , receiverUser.getEmail());
        message.put(ProjectStorage.KEY_MESSAGE,uri.toString());
        message.put(ProjectStorage.KEY_TIMESTAMP,new Date() );
        message.put(ProjectStorage.KEY_MESSAGE_TYPE,"image");
        ProjectStorage.DATABASE_REFERENCE.collection(ProjectStorage.KEY_COLLECTION_CHAT).add(message);
    }
}