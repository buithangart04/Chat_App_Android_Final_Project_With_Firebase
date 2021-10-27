package com.example.authproject.utilities;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.authproject.listeners.UploadFileSuccessListener;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


public class FileUtilities {

    public void uploadFile(Context context, UploadFileSuccessListener listener, Uri filePath,Object ... params) {
        if (filePath != null) {
            String fileName = params.length==0 ?new FunctionalUtilities().getRandomFileName():
                    new FunctionalUtilities().getRandomFileName()+"."+params[0].toString().split("[.]")[1];
            // Create a reference to "mountains.jpg"
            StorageReference mountainsRef = ProjectStorage.STORAGE_REFERENCE.child(fileName);
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
                                listener.onUploadFileSuccess(downloadUri,params);
                                Toast.makeText(context, "Upload sucessfully !", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(context, "Upload failed !", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        }
    }
    public String getFileType(Context context,Uri uri){
        try {
            ContentResolver cR = context.getContentResolver();
            MimeTypeMap mime = MimeTypeMap.getSingleton();
            String type = mime.getExtensionFromMimeType(cR.getType(uri));
            switch(type){
                case "pdf":
                    return "file";
                case "doc":
                    return "file";
                case "docx":
                    return "file";
                case "xlsx":
                    return "file";
                case "txt":
                    return "file";
                case "jpg":
                    return "image";
                case "jpeg":
                    return "image";
                default:
                    Toast.makeText(context,"File extension not supported !",Toast.LENGTH_LONG).show();
                    return "undefined";
            }
        }catch (Exception e ){
            Toast.makeText(context,"File extension not supported !",Toast.LENGTH_LONG).show();
            return  "undefined" ;
        }
    }
    public void onDownloadFile(Context context,String uri ){
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setType("*/*");
        intent.setData(Uri.parse(uri));
        context.startActivity(intent);
    }
    @SuppressLint("Range")
    public String getFileNameByUri(Context context, Uri uri ){
        String name="";
        String scheme = uri.getScheme();

        if (scheme.equals("file")) {
            name = uri.getLastPathSegment();
        }
        else if (scheme.equals("content")) {
            Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                name = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
            }
        }
        return name;
    }
    public void loadVideoToView (Context ctx , PlayerView playerView, String uri){
        try {
            BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter.Builder(ctx).build();
            TrackSelector trackSelector = new DefaultTrackSelector(new AdaptiveTrackSelection.Factory(bandwidthMeter));
            SimpleExoPlayer exoPlayer = (SimpleExoPlayer) ExoPlayerFactory.newSimpleInstance(ctx);
            Uri video = Uri.parse(uri);
            DefaultHttpDataSourceFactory dataSourceFactory = new DefaultHttpDataSourceFactory("video");
            ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
            MediaSource mediaSource = new ExtractorMediaSource(video, dataSourceFactory, extractorsFactory, null, null);
            playerView.setPlayer(exoPlayer);
            exoPlayer.prepare(mediaSource);
            exoPlayer.setPlayWhenReady(false);
        }catch (Exception e ){
            Toast.makeText(ctx,"Fail to load video ", Toast.LENGTH_LONG).show();
        }
    }
}
