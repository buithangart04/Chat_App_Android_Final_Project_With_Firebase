package com.example.authproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.example.authproject.databinding.ActivityNavigatorBinding;
import com.example.authproject.models.ChatMessage;
import com.example.authproject.utilities.FunctionalUtilities;
import com.example.authproject.utilities.PreferenceManager;
import com.example.authproject.utilities.ProjectStorage;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Collections;


public class NavigatorActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigator);

        ProjectStorage.DATABASE_REFERENCE.collection(ProjectStorage.KEY_FRIEND)
                .whereEqualTo(ProjectStorage.KEY_RECEIVER_EMAIL,PreferenceManager.getInstance().getString(ProjectStorage.KEY_USER_EMAIL))
                .whereEqualTo(ProjectStorage.KEY_STATUS,"sent")
                .addSnapshotListener(eventListener);

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavigationView);

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragmentContainerView);

        NavController navCo = navHostFragment.getNavController();

        NavigationUI.setupWithNavController(bottomNavigationView, navCo);
    }
    private final EventListener<QuerySnapshot> eventListener = (value, error)->{
        if(error!=null) return;
        if(value!=null ){
            int size = value.getDocuments().size();
           if(size==0) ((TextView)findViewById(R.id.txt_noti)).setVisibility(View.GONE);
           else {
               ((TextView)findViewById(R.id.txt_noti)).setVisibility(View.VISIBLE);
               ((TextView)findViewById(R.id.txt_noti)).setText(String.valueOf(size));
           }
        }
    };

    @Override
    public void onBackPressed() {
        finish();
        startActivity(getIntent());
        super.onBackPressed();
    }
}