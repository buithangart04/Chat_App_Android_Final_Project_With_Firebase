package com.example.authproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.authproject.databinding.ActivityCreateGroupChatBinding;
import com.example.authproject.databinding.ActivityCreateGroupChatNameAcitivityBinding;
import com.example.authproject.models.User;
import com.example.authproject.utilities.ProjectStorage;

import java.util.ArrayList;
import java.util.List;

public class CreateGroupChatNameActivity extends AppCompatActivity {
    private ActivityCreateGroupChatNameAcitivityBinding binding;
    private List<User> userGroup = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group_chat_name_acitivity);
        binding = ActivityCreateGroupChatNameAcitivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        init();
        getUserGroup();
        setListener();
    }

    private void init() {
        LinearLayoutManager linearLayoutManagerUser = new LinearLayoutManager(
                getApplicationContext());
        linearLayoutManagerUser.setReverseLayout(true);
        binding.recyclerUser.setLayoutManager(linearLayoutManagerUser);
    }

    private void getUserGroup() {
        loading(true);
    }

    private void setListener() {
        binding.textGroupName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().equals("")) {
                    binding.textCreateGroup.setClickable(false);
                    binding.textCreateGroup.setTextColor(Color.parseColor(ProjectStorage.KEY_COLOR_DISABLED));
                    return;
                }
                binding.textCreateGroup.setTextColor(Color.parseColor(ProjectStorage.KEY_COLOR_NAVIGATE));
                binding.textCreateGroup.setClickable(true);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        binding.textCreateGroup.setOnClickListener(view -> createGroup());
    }

    private void loading(Boolean isLoading) {
        if (isLoading) {
            binding.progressBarGroup3.setVisibility(View.VISIBLE);
        } else {
            binding.progressBarGroup3.setVisibility(View.INVISIBLE);
        }
    }

    private void createGroup() {

    }
}