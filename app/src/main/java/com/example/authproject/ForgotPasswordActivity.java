package com.example.authproject;


import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity {

    private TextView txtBackToLogin, loginText, logoName;
    private TextInputLayout emailEditText;
    private Button btnResetPassword;
    private ImageView logoImage;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        emailEditText = findViewById(R.id.email);
        btnResetPassword = (Button) findViewById(R.id.btnResetPassword);

        mAuth = FirebaseAuth.getInstance();

        btnResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetPassword();
            }
        });
        loginText = findViewById(R.id.loginText);
        logoName = findViewById(R.id.logoName);
        logoImage = findViewById(R.id.logoImage);
        txtBackToLogin = findViewById(R.id.txtBackToLogin);
        txtBackToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ForgotPasswordActivity.this, MainActivity.class);
                Pair[] pairs = new Pair[6];
                pairs[0] = new Pair<View, String>(logoImage, "logo_name");
                pairs[1] = new Pair<View, String>(logoName, "logo_text");
                pairs[2] = new Pair<View, String>(loginText, "login_text");
                pairs[3] = new Pair<View, String>(btnResetPassword, "button_tran");
                pairs[4] = new Pair<View, String>(txtBackToLogin, "singup_tran");
                pairs[5] = new Pair<View, String>(emailEditText, "email_text");

                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(ForgotPasswordActivity.this, pairs);
                startActivity(intent, options.toBundle());
            }
        });
    }

    private void resetPassword() {
        String email = emailEditText.getEditText().getText().toString().trim();

        if (!validateInput(email)) {
            return;
        }
        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(ForgotPasswordActivity.this, "Check your email to reset your password", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(ForgotPasswordActivity.this, "Try again! Some thing wrong happened!", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private boolean validateInput(String email) {
        if (email.isEmpty()) {
            emailEditText.setError("Email is required!");
            return false;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.setError("Please provide valid email!");
            return false;
        }
        return true;
    }
}