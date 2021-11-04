package com.example.authproject.utilities;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.authproject.AddParticipantActivity;
import com.example.authproject.GroupInfoActivity;
import com.example.authproject.models.User;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class UserUtilities {

    public  List<User> getListUsers(String currentUserId, Task<QuerySnapshot> task) {
        List<User> users = new ArrayList<>();
        for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
            if (currentUserId.equals(queryDocumentSnapshot.getData().get(ProjectStorage.KEY_USER_ID))) {
                continue;
            }
            User user = new User();
            user.setFullName(queryDocumentSnapshot.getString(ProjectStorage.KEY_NAME));
            user.setEmail(queryDocumentSnapshot.getString(ProjectStorage.KEY_USER_EMAIL));
            user.setId(queryDocumentSnapshot.getString(ProjectStorage.KEY_USER_ID));
            user.setUri(queryDocumentSnapshot.getString(ProjectStorage.KEY_AVATAR));
            users.add(user);
        }
        return users;
    }

}
