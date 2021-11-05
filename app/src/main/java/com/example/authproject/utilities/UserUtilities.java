package com.example.authproject.utilities;


import android.util.Pair;

import com.example.authproject.listeners.GetUserSuccessListener;
import com.example.authproject.models.User;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

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
    public void getUserByCondition(GetUserSuccessListener listener, Pair<String,String>condition) {

        User user = new User();
        ProjectStorage.DATABASE_REFERENCE.collection(ProjectStorage.KEY_COLLECTION_USERS)
                .whereEqualTo(condition.first, condition.second)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                            user.setId(queryDocumentSnapshot.getData().get(ProjectStorage.KEY_USER_ID).toString());
                            user.setFullName(queryDocumentSnapshot.getData().get(ProjectStorage.KEY_NAME).toString());
                            user.setEmail(queryDocumentSnapshot.getData().get(ProjectStorage.KEY_USER_EMAIL).toString());
                            user.setUri(queryDocumentSnapshot.getString(ProjectStorage.KEY_AVATAR));
                            listener.onGetUserSuccess(user);
                        }

                    }
                });

    }
}
