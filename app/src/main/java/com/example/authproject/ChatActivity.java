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
import android.util.Pair;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.authproject.adapters.ChatAdapter;
import com.example.authproject.adapters.FileChooserAdapter;
import com.example.authproject.databinding.ActivityChatBinding;
import com.example.authproject.listeners.UploadFileSuccessListener;
import com.example.authproject.models.ChatMessage;
import com.example.authproject.models.Group;
import com.example.authproject.models.User;
import com.example.authproject.utilities.FileUtilities;
import com.example.authproject.utilities.FunctionalUtilities;
import com.example.authproject.utilities.PreferenceManager;
import com.example.authproject.utilities.ProjectStorage;
import com.example.authproject.utilities.RemoveFcmToken;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;


public class ChatActivity extends AppCompatActivity implements UploadFileSuccessListener {
    private ActivityChatBinding binding;
    private User receiverUser;
    private Group receiverGroup ;
    List<ChatMessage> chatMessages;
    private ChatAdapter chatAdapter;
    private FileChooserAdapter fileChooserAdapter ;
    private String senderId;
    boolean showOptionsFile= false;
    List<Pair<Uri,String>> listFileSelected;
    List<User> listReceiverUser;
    private static final int PICK_IMAGE_REQUEST = 3241,PICK_FILE_REQUEST=3251,PICK_VIDEO_REQUEST=3161;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setListener();
        loadReceiversDetails();
        init();
        listenMessage();
        setCallListener(listReceiverUser);
    }
    private void init (){
        senderId =PreferenceManager.getInstance().getString(ProjectStorage.KEY_USER_ID);
        chatMessages = new ArrayList<>();
        listFileSelected = new ArrayList<>();
        chatAdapter= new ChatAdapter(chatMessages ,senderId);
        binding.recMessage.setLayoutManager(new LinearLayoutManager(ChatActivity.this));
        binding.recMessage.setAdapter(chatAdapter);
        // set adapter for message
        fileChooserAdapter = new FileChooserAdapter(listFileSelected);
        binding.recFileAdd.setLayoutManager(new LinearLayoutManager(binding.sendBox.getContext(),LinearLayoutManager.HORIZONTAL,false));
        binding.recFileAdd.setAdapter(fileChooserAdapter);
    }
    //send file or image
    private void showFileOptions(){
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
            HashMap<String,Object> message = new HashMap<>();
            message.put(ProjectStorage.KEY_SENDER_ID,senderId);
            if(receiverUser!=null) message.put(ProjectStorage.KEY_RECEIVER_ID , receiverUser.getId());
            else  message.put(ProjectStorage.KEY_RECEIVER_ID , receiverGroup.groupId);
            message.put(ProjectStorage.KEY_MESSAGE,binding.inputMessage.getText().toString());
            message.put(ProjectStorage.KEY_TIMESTAMP,new Date() );
            message.put(ProjectStorage.KEY_MESSAGE_TYPE,"text");
            message.put(ProjectStorage.KEY_FILE_NAME,"");
            if(receiverUser!=null)  ProjectStorage.DATABASE_REFERENCE.collection(ProjectStorage.KEY_COLLECTION_CHAT).add(message);
            else ProjectStorage.DATABASE_REFERENCE.collection(ProjectStorage.KEY_COLLECTION_GROUP_CHAT).add(message);
            binding.inputMessage.setText(null);
        }
        if(listFileSelected.size()!=0){
            for(Pair<Uri,String> pair : listFileSelected){
               new FileUtilities().uploadFile(this,this,pair.first,
                       (Object)pair.second.split("[|]")[0],(Object)pair.second.split("[|]")[1]);
            }
            listFileSelected.clear();
            fileChooserAdapter.notifyDataSetChanged();
        }

    }
    public void listenMessage(){
        if(receiverUser!=null) {
            ProjectStorage.DATABASE_REFERENCE.collection(ProjectStorage.KEY_COLLECTION_CHAT)
                    .whereEqualTo(ProjectStorage.KEY_SENDER_ID, senderId)
                    .whereEqualTo(ProjectStorage.KEY_RECEIVER_ID, receiverUser.getId())
                    .addSnapshotListener(eventListener);
            ProjectStorage.DATABASE_REFERENCE.collection(ProjectStorage.KEY_COLLECTION_CHAT)
                    .whereEqualTo(ProjectStorage.KEY_SENDER_ID, receiverUser.getId())
                    .whereEqualTo(ProjectStorage.KEY_RECEIVER_ID, senderId)
                    .addSnapshotListener(eventListener);
        }else {
            ProjectStorage.DATABASE_REFERENCE.collection(ProjectStorage.KEY_COLLECTION_GROUP_CHAT)
                    .whereEqualTo(ProjectStorage.KEY_RECEIVER_ID, receiverGroup.groupId)
                    .addSnapshotListener(eventListener);
        }
    }
    private final EventListener<QuerySnapshot> eventListener = (value, error)->{
        if(error!=null) return;
        if(value!=null ){
            int count = chatMessages.size();
            for(DocumentChange docs : value.getDocumentChanges()){
                if(docs.getType()==DocumentChange.Type.ADDED){
                    ChatMessage chatMessage = new ChatMessage();
                    chatMessage.senderId  = docs.getDocument().getString(ProjectStorage.KEY_SENDER_ID);
                    chatMessage.receiverId  = docs.getDocument().getString(ProjectStorage.KEY_RECEIVER_ID);
                    chatMessage.message  = docs.getDocument().getString(ProjectStorage.KEY_MESSAGE);
                    chatMessage.dateObject  = docs.getDocument().getDate(ProjectStorage.KEY_TIMESTAMP);
                    chatMessage.dateTime= FunctionalUtilities.getDateFormat(chatMessage.dateObject);
                    chatMessage.type= docs.getDocument().getString(ProjectStorage.KEY_MESSAGE_TYPE);
                    chatMessage.fileName= docs.getDocument().getString(ProjectStorage.KEY_FILE_NAME);
                    List<ChatMessage> duplicateMessByTime = chatMessages.stream()
                            .filter(c-> c.senderId.equals(chatMessage.senderId)&&c.dateTime.equals(chatMessage.dateTime))
                            .collect(Collectors.toList());
                    if(duplicateMessByTime.size()>0) continue;
                    chatMessages.add(chatMessage);
                }

            }
            Collections.sort(chatMessages, (obj1,obj2) -> {return obj1.dateObject.compareTo(obj2.dateObject);});
            if(count==0){
                chatAdapter.notifyDataSetChanged();
            }else {
                chatAdapter.notifyItemRangeInserted(chatMessages.size(),chatMessages.size());
            }
            if(chatMessages.size()!=0) binding.recMessage.smoothScrollToPosition(chatMessages.size()-1);
            binding.recMessage.setVisibility(View.VISIBLE);
        }
    };
    private Bitmap getBitMapFromEncodingString (String encodedImage){
        byte []  bytes = Base64.decode(encodedImage,Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes,0,bytes.length);
    }
    private void loadReceiversDetails (){
        listReceiverUser = new ArrayList<>();
        try {
            receiverUser = (User) getIntent().getSerializableExtra(ProjectStorage.KEY_USER);
            if(receiverUser == null) throw new Exception();
            binding.textName.setText(receiverUser.getFullName());
            binding.imageInfo.setVisibility(View.GONE);
            if (receiverUser != null){
                listReceiverUser.add(receiverUser);
            }
        }catch (Exception e){
            receiverGroup= (Group) getIntent().getSerializableExtra(ProjectStorage.KEY_COLLECTION_GROUP);
            binding.textName.setText(receiverGroup.groupName);
            binding.imageInfo.setOnClickListener(v->{
                Intent intent= new Intent(this, GroupInfoActivity.class);
                intent.putExtra(ProjectStorage.KEY_GROUP_ID, receiverGroup.groupId);
                startActivity(intent);
            });
            List<String> lístIdReceiverParticipant = receiverGroup.participant;
            lístIdReceiverParticipant.remove(PreferenceManager.getInstance().getString(ProjectStorage.KEY_USER_ID));
            ProjectStorage.DATABASE_REFERENCE.collection(ProjectStorage.KEY_COLLECTION_USERS)
                    .whereIn(FieldPath.documentId(), lístIdReceiverParticipant)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()){
                                for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()){
                                    User user = new User();
                                    user.setId(queryDocumentSnapshot.getData().get(ProjectStorage.KEY_USER_ID).toString());
                                    user.setFullName(queryDocumentSnapshot.getData().get(ProjectStorage.KEY_NAME).toString());
                                    user.setEmail(queryDocumentSnapshot.getData().get(ProjectStorage.KEY_USER_EMAIL).toString());
                                    user.setUri(queryDocumentSnapshot.getString(ProjectStorage.KEY_AVATAR));
                                    user.setToken(queryDocumentSnapshot.getString(ProjectStorage.KEY_FCM_TOKEN));
                                    listReceiverUser.add(user);
                                }
                            }
                        }
                    });
        }

    }
    private void setListener(){
        binding.imageBack.setOnClickListener(v-> onBackPressed());
        binding.layoutSend.setOnClickListener(v-> sendMessage());
        binding.layoutOptionSendFile.setOnClickListener(v->showFileOptions());
    }

    public void onChoseOptionSend(View view) {
        Intent intent = new Intent();
        switch(view.getId()){
            case R.id.optionImage:
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Select an image"), PICK_IMAGE_REQUEST);
                break;
            case R.id.optionFile:
                intent.setType("*/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Select file"), PICK_FILE_REQUEST);
                break;
            case R.id.optionVideo:
                intent.setType("video/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Select Video"), PICK_VIDEO_REQUEST);

        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK&& data!=null && data.getData()!=null){
            String type= requestCode==PICK_IMAGE_REQUEST ? "image":
                    requestCode== PICK_FILE_REQUEST? new FileUtilities().getFileType(this,data.getData()):
                    requestCode==PICK_VIDEO_REQUEST?"video":"undefined" ;
            if(!type.equals("undefined")){
                Uri filePath = data.getData();
                listFileSelected.add(new Pair<>(filePath,new FileUtilities().getFileNameByUri(this,filePath)+"|"+type));
                fileChooserAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        chatAdapter.notifyDataSetChanged();
        binding.recMessage.setAdapter(chatAdapter);
    }
    @Override
    public void onUploadFileSuccess(Uri uri, Object [] params) {
        String type ="image";
        String fileName="";
        if(params.length!=0){
            fileName= params[0].toString();
            type=params[1].toString();
        }
        HashMap<String,Object> message = new HashMap<>();
        message.put(ProjectStorage.KEY_SENDER_ID, senderId);
        if(receiverUser!=null) message.put(ProjectStorage.KEY_RECEIVER_ID, receiverUser.getId());
        else message.put(ProjectStorage.KEY_RECEIVER_ID, receiverGroup.groupId);
        message.put(ProjectStorage.KEY_MESSAGE,uri.toString());
        message.put(ProjectStorage.KEY_TIMESTAMP,new Date() );
        message.put(ProjectStorage.KEY_MESSAGE_TYPE,type);
        message.put(ProjectStorage.KEY_FILE_NAME,fileName);
        if(receiverUser!=null) ProjectStorage.DATABASE_REFERENCE.collection(ProjectStorage.KEY_COLLECTION_CHAT).add(message);
        else ProjectStorage.DATABASE_REFERENCE.collection(ProjectStorage.KEY_COLLECTION_GROUP_CHAT).add(message);
    }
    private void setCallListener(List<User> listReceiverUser) {
        binding.imageCall.setOnClickListener(v -> initiateAudioMeeting(listReceiverUser));
        binding.imageVideo.setOnClickListener(v -> initiateVideoMeeting(listReceiverUser));
    }

    private void initiateVideoMeeting(List<User> listReceiverUser) {
        if (listReceiverUser.size() == 1) {
            User user = listReceiverUser.get(0);
            if (user.getToken() == null || user.getToken().trim().isEmpty()) {
                Toast.makeText(this, user.getFullName() + " is not avaiable for video call", Toast.LENGTH_SHORT).show();
            } else {
                Intent intent = new Intent(getApplicationContext(), OutgoingInvitationActivity.class);
                intent.putExtra("user", user);
                intent.putExtra("type", "video");
                intent.putExtra("isGroup", false);
                startActivity(intent);
            }
        } else {
            Intent intent = new Intent(getApplicationContext(), OutgoingInvitationActivity.class);
            intent.putExtra("groupUser", new Gson().toJson(listReceiverUser));
            intent.putExtra("group", receiverGroup);
            intent.putExtra("type", "video");
            intent.putExtra("isGroup", true);
            startActivity(intent);
        }
    }

    private void initiateAudioMeeting(List<User> listReceiverUser) {
        if (listReceiverUser.size() == 1) {
            User user = listReceiverUser.get(0);
            if (user.getToken() == null || user.getToken().trim().isEmpty()) {
                Toast.makeText(this, user.getFullName() + " is not avaiable for audio call", Toast.LENGTH_SHORT).show();
            } else {
                Intent intent = new Intent(getApplicationContext(), OutgoingInvitationActivity.class);
                intent.putExtra("user", user);
                intent.putExtra("type", "audio");
                intent.putExtra("isGroup", false);
                startActivity(intent);
            }
        } else {
                Intent intent = new Intent(getApplicationContext(), OutgoingInvitationActivity.class);
                intent.putExtra("groupUser", new Gson().toJson(listReceiverUser));
                intent.putExtra("group", receiverGroup);
                intent.putExtra("type", "audio");
                intent.putExtra("isGroup", true);
                startActivity(intent);
        }
    }
}