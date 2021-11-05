package com.example.authproject;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.example.authproject.utilities.PreferenceManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;


public class NavigatorActivity extends AppCompatActivity {
    private PreferenceManager preferenceManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigator);



        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavigationView);

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragmentContainerView);

        NavController navCo = navHostFragment.getNavController();

        NavigationUI.setupWithNavController(bottomNavigationView, navCo);
    }

}