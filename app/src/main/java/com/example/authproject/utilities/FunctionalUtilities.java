package com.example.authproject.utilities;

import android.view.View;

import androidx.annotation.NonNull;

import com.example.authproject.adapters.UsersAdapter;
import com.example.authproject.listeners.GetUserSuccessListener;
import com.example.authproject.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class FunctionalUtilities {
    public static String getDateFormat(Date date) {
        return new SimpleDateFormat("dd-MM-yyyy hh:mm:ss").format(date);
    }

    private String getUUID() {
        return UUID.randomUUID().toString();
    }

    public String getRandomImageName() {
        return "images/" + UUID.randomUUID();
    }

    public String generateId(String type) {
        switch (type) {
            case "user":
                return "us" + getUUID();
            case "group":
                return "gr" + getUUID();
            default:
                return null;
        }
    }
    public void getUserByEmail(String email, GetUserSuccessListener listener){

        User user = new User();
        ProjectStorage.DATABASE_REFERENCE.collection(ProjectStorage.KEY_COLLECTION_USERS).whereEqualTo(ProjectStorage.KEY_USER_EMAIL,email)
                .get()
                .addOnCompleteListener(task ->{
                    if(task.isSuccessful() && task.getResult()!=null){
                        for(QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()){

                                user.setFullName(queryDocumentSnapshot.getData().get(ProjectStorage.KEY_NAME).toString());
                                user.setEmail(queryDocumentSnapshot.getData().get(ProjectStorage.KEY_USER_EMAIL).toString());
                                listener.onGetUserSuccess(user);
                        }

                    }
                });

    }
}
