package com.example.authproject.listeners;

import android.net.Uri;

public interface UploadFileSuccessListener {
    void onUploadFileSuccess(Uri uri,Object [] params);
}
