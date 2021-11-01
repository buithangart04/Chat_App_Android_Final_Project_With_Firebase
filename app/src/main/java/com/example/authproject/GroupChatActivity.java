package com.example.authproject;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.transition.Slide;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import com.example.authproject.adapters.ChatAdapter;
import com.example.authproject.adapters.FileChooserAdapter;
import com.example.authproject.databinding.ActivityGroupChatBinding;
import com.example.authproject.listeners.UploadFileSuccessListener;
import com.example.authproject.models.ChatMessage;
import com.example.authproject.utilities.FileUtilities;
import com.example.authproject.utilities.PreferenceManager;
import com.example.authproject.utilities.ProjectStorage;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class GroupChatActivity extends AppCompatActivity {
    private ActivityGroupChatBinding binding;
    List<ChatMessage> chatMessages;
    private ChatAdapter chatAdapter;
    private FileChooserAdapter fileChooserAdapter;
    private PreferenceManager preferenceManager;
    boolean showOptionsFile = false;
    private List<Uri> listFileSelected;
    private static final int PICK_IMAGE_REQUEST = 3241;
    private String groupId;
    private String groupName;
    private String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);
        binding = ActivityGroupChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setListener();
        init();
    }

    private void init() {
        Log.d("Tag", "init");

        //get info from previous intent
        Intent intent = getIntent();
        groupId = intent.getStringExtra(ProjectStorage.KEY_GROUP_ID);
        groupName = intent.getStringExtra(ProjectStorage.KEY_GROUP_NAME);
        currentUserId = intent.getStringExtra(ProjectStorage.KEY_USER_EMAIL);

        binding.textName.setText(groupName);
        preferenceManager = new PreferenceManager(getApplicationContext());
        chatMessages = new ArrayList<>();
        listFileSelected = new ArrayList<>();
        chatAdapter = new ChatAdapter(chatMessages, preferenceManager.getString(ProjectStorage.KEY_USER_EMAIL));
        binding.recMessage.setLayoutManager(new LinearLayoutManager(GroupChatActivity.this));
        binding.recMessage.setAdapter(chatAdapter);
        // set adapter for message

        binding.recFileAdd.setLayoutManager(new LinearLayoutManager(binding.sendBox.getContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.recFileAdd.setAdapter(fileChooserAdapter);
    }

    private void showFileOptions() {
        Log.d("Tag", "showFileOptions");
        Transition transition = new Slide(Gravity.BOTTOM);
        transition.setDuration(400);
        transition.addTarget(binding.layoutChooseFile);
        TransitionManager.beginDelayedTransition(binding.viewBackground, transition);
        binding.layoutChooseFile.setVisibility(showOptionsFile ? View.GONE : View.VISIBLE);
        binding.imageviewAdd.setImageResource(showOptionsFile ? R.drawable.ic_add : R.drawable.ic_clear);
        showOptionsFile = !showOptionsFile;
    }

    private void sendMessage() {
        if (!binding.inputMessage.getText().toString().trim().isEmpty() && binding.inputMessage.getText() != null) {

            HashMap<String,Object> message = new HashMap<>();
            message.put(ProjectStorage.KEY_SENDER_EMAIL, currentUserId);
            message.put(ProjectStorage.KEY_RECEIVER_EMAIL , groupId);
            message.put(ProjectStorage.KEY_MESSAGE,binding.inputMessage.getText().toString());
            message.put(ProjectStorage.KEY_TIMESTAMP,new Date() );
            message.put(ProjectStorage.KEY_MESSAGE_TYPE,"text");
            ProjectStorage.DATABASE_REFERENCE.collection(ProjectStorage.KEY_COLLECTION_GROUP
                    + "/" + groupId
                    + "/" + ProjectStorage.KEY_COLLECTION_CHAT)
                    .add(message);

            binding.inputMessage.setText(null);
        }
//        if (listFileSelected.size() != 0) {
//            for (Uri uri : listFileSelected) {
//                new FileUtilities().uploadFile(this, this, uri);
//            }
//            listFileSelected.clear();
//            fileChooserAdapter.notifyDataSetChanged();
//        }
    }

    private void setListener() {
        Log.d("Tag", "setListener");
        binding.imageBack.setOnClickListener(v -> onBackPressed());
        binding.layoutInfo.setOnClickListener(v -> onClickGroupInfo());
        binding.layoutGroupSend.setOnClickListener(v -> sendMessage());
        binding.layoutOptionSendFile.setOnClickListener(v -> showFileOptions());
    }

    private void onClickGroupInfo() {
        Intent intent = new Intent(getApplicationContext(), GroupInfoActivity.class);
        groupId = intent.getStringExtra(ProjectStorage.KEY_GROUP_ID);
        groupName = intent.getStringExtra(ProjectStorage.KEY_GROUP_NAME);
        currentUserId = intent.getStringExtra(ProjectStorage.KEY_USER_EMAIL);
        intent.putExtra(ProjectStorage.KEY_GROUP_ID, groupId);
        intent.putExtra(ProjectStorage.KEY_GROUP_ID, groupName);
        intent.putExtra(ProjectStorage.KEY_USER_EMAIL, currentUserId);
        startActivity(intent);
    }

    public void onChoseOptionSend(View view) {
        Log.d("Tag", "onChoseOptionSend");
        switch (view.getId()) {
            case R.id.optionImage:
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select an image"), PICK_IMAGE_REQUEST);
                break;
            case R.id.optionFile:
                Toast.makeText(GroupChatActivity.this, "file choose", Toast.LENGTH_LONG).show();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
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


}