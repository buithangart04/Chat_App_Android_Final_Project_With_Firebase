package com.example.authproject.utilities;

import android.content.Context;
import android.widget.Toast;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class RemoveFcmToken {
    public void removeToken(PreferenceManager preferenceManager, Context context) {
        FirebaseFirestore database = FirebaseFirestore.getInstance();

        DocumentReference documentReference =
                database.collection(ProjectStorage.KEY_COLLECTION_USERS).document(
                        preferenceManager.getString(ProjectStorage.KEY_USER_ID)
                );
        HashMap<String, Object> updates = new HashMap<>();
        updates.put(ProjectStorage.KEY_FCM_TOKEN, FieldValue.delete());
        documentReference.update(updates)
                .addOnSuccessListener(runnable -> {
                   // preferenceManager.clear();
                })
                .addOnFailureListener(runnable -> {
                    Toast.makeText(context,"Unable to remove token: "+runnable.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
