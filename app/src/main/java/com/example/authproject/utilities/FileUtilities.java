package com.example.authproject.utilities;

import android.content.Context;
import android.net.Uri;
import android.transition.Slide;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.authproject.R;
import com.example.authproject.listeners.UploadFileSuccessListener;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


public class FileUtilities {

    public void uploadFile(Context context, UploadFileSuccessListener listener, Uri filePath) {
        if (filePath != null) {

            // Create a reference to "mountains.jpg"
            StorageReference mountainsRef = ProjectStorage.STORAGE_REFERENCE.child(new FunctionalUtilities().getRandomImageName());
            UploadTask uploadTask = mountainsRef.putFile(filePath);

            Task<Uri> urlTask = uploadTask
                    .continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()) {
                                throw task.getException();
                            }

                            // Continue with the task to get the download URL
                            return mountainsRef.getDownloadUrl();
                        }
                    })
                    .addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                Uri downloadUri = task.getResult();
                                listener.onUploadFileSuccess(downloadUri);
                                Toast.makeText(context, "Upload sucessfully !", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(context, "Upload failed !", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        }
    }
}
