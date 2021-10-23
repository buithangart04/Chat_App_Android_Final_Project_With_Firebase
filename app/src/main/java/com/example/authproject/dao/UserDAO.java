package com.example.authproject.dao;

import android.util.Log;

import com.example.authproject.utilities.ProjectStorage;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;


public class UserDAO {
    private DocumentReference documentReference;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    public void setUserStatus(String status) {
        documentReference = FirebaseFirestore.getInstance()
                .document("users/" + user.getUid());
        Map<String, Object> map = new HashMap<>();

        map.put(ProjectStorage.KEY_USER_STATUS, status);
        documentReference.update(map)
                .addOnSuccessListener(unused -> Log.d("TAG", "User Online"))
                .addOnFailureListener(e -> Log.e("TAG", "onFailure", e));
    }
}
