package com.example.authproject;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Pair;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class StartScreenActivity extends AppCompatActivity {
    private static int SPLASH_SCREEN = 3500;
    Animation topAnim, bottomAnim;
    ImageView image;
    TextView name, from, team;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_start_screen);

        //Animations
        topAnim = AnimationUtils.loadAnimation(this,R.anim.top_animation);
        bottomAnim = AnimationUtils.loadAnimation(this,R.anim.bottom_animation);

        //Hooks
        image = findViewById(R.id.logoImage);
        name = findViewById(R.id.textAppName);
        from = findViewById(R.id.textAppFrom);
        team = findViewById(R.id.textAppTeam);

        image.setAnimation(topAnim);
        name.setAnimation(bottomAnim);
        from.setAnimation(bottomAnim);
        team.setAnimation(bottomAnim);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(StartScreenActivity.this, MainActivity.class);
                Pair[] pairs = new Pair[2];
                pairs[0] = new Pair<View, String>(image, "logo_name");
                pairs[1] = new Pair<View, String>(name, "logo_text");

                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(StartScreenActivity.this, pairs);
                startActivity(intent, options.toBundle());
                finish();
            }
        }, SPLASH_SCREEN);
    }
}