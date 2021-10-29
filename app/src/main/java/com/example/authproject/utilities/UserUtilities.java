package com.example.authproject.utilities;


import com.example.authproject.models.User;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class UserUtilities {

    public static List<User> getListUsers(String currentUserId, Task<QuerySnapshot> task) {
        List<User> users = new ArrayList<>();
        for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
            if (currentUserId.equals(queryDocumentSnapshot.getData().get(ProjectStorage.KEY_USER_EMAIL))) {
                continue;
            }
            User user = new User();
            user.setFullName(queryDocumentSnapshot.getString(ProjectStorage.KEY_NAME));
            user.setEmail(queryDocumentSnapshot.getString(ProjectStorage.KEY_USER_EMAIL));
            user.setUri(queryDocumentSnapshot.getString(ProjectStorage.KEY_USER_URI));
            users.add(user);
        }
        return users;
    }
}
