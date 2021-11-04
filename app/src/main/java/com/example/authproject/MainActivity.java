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

import com.example.authproject.utilities.PreferenceManager;
import com.example.authproject.utilities.ProjectStorage;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView txtRegister, txtForgotPassword, loginText, logoName;
    private ImageView logoImage;
    private TextInputLayout editTextEmail, editTextPassword;
    private Button btnLogin;
    private PreferenceManager preferenceManager;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtRegister = (TextView) findViewById(R.id.txtRegister);
        txtRegister.setOnClickListener(this);

        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(this);

        editTextEmail = findViewById(R.id.email);
        editTextPassword = findViewById(R.id.password);

        mAuth = FirebaseAuth.getInstance();

        txtForgotPassword = (TextView) findViewById(R.id.txtForgotPassword);
        txtForgotPassword.setOnClickListener(this);

        loginText = findViewById(R.id.loginText);
        logoName = findViewById(R.id.logoName);
        logoImage = findViewById(R.id.logoImage);

        preferenceManager = new PreferenceManager(getApplicationContext());
    }

    @Override
    public void onClick(View view) {
        Intent intent;
        Pair[] pairs;
        ActivityOptions options;
        switch (view.getId()) {
            case R.id.txtRegister:
                intent = new Intent(MainActivity.this, RegisterActivity.class);
                pairs = new Pair[5];
                pairs[0] = new Pair<View, String>(loginText, "login_text");
                pairs[1] = new Pair<View, String>(btnLogin, "button_tran");
                pairs[2] = new Pair<View, String>(txtRegister, "singup_tran");
                pairs[3] = new Pair<View, String>(editTextEmail, "email_text");
                pairs[4] = new Pair<View, String>(editTextPassword, "password_text");

                options = ActivityOptions.makeSceneTransitionAnimation(MainActivity.this, pairs);
                startActivity(intent, options.toBundle());
                break;
            case R.id.btnLogin:
                Login();
                break;
            case R.id.txtForgotPassword:
                intent = new Intent(MainActivity.this, ForgotPasswordActivity.class);
                pairs = new Pair[6];
                pairs[0] = new Pair<View, String>(logoImage, "logo_name");
                pairs[1] = new Pair<View, String>(logoName, "logo_text");
                pairs[2] = new Pair<View, String>(loginText, "login_text");
                pairs[3] = new Pair<View, String>(btnLogin, "button_tran");
                pairs[4] = new Pair<View, String>(txtRegister, "singup_tran");
                pairs[5] = new Pair<View, String>(editTextEmail, "email_text");

                options = ActivityOptions.makeSceneTransitionAnimation(MainActivity.this, pairs);
                startActivity(intent, options.toBundle());
                break;
        }
    }

    @Override
    protected void onDestroy() {
        FirebaseFirestore database = FirebaseFirestore.getInstance();

        DocumentReference documentReference =
                database.collection(ProjectStorage.KEY_COLLECTION_USERS).document(
                        preferenceManager.getString(ProjectStorage.KEY_USER_ID)
                );
        HashMap<String, Object> updates = new HashMap<>();
        updates.put(ProjectStorage.KEY_FCM_TOKEN, FieldValue.delete());
        documentReference.update(updates)
                .addOnSuccessListener(runnable -> {
                    preferenceManager.clear();
                    finish();
                })
                .addOnFailureListener(runnable -> {
                    Toast.makeText(MainActivity.this,"Unable to remove token: "+runnable.getMessage(), Toast.LENGTH_SHORT).show();
                });
        super.onDestroy();
    }

    private void Login() {
        String email = editTextEmail.getEditText().getText().toString().trim();
        String password = editTextPassword.getEditText().getText().toString().trim();

        if (!validateInputPassword(password) | !validateInputEmail(email)) {
            return;
        }
        signInUser(email, password);
    }

    private boolean validateInputEmail(String email) {
        if (email.isEmpty()) {
            editTextEmail.setError("Email is required!");
            return false;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail.setError("Please provide valid email!");
            return false;
        }
        return true;
    }

    private boolean validateInputPassword(String password) {
        if (password.isEmpty()) {
            editTextPassword.setError("Password is required!");
            return false;
        }

        if (password.length() < 6) {
            editTextPassword.setError("Password must be > 6");
            return false;
        }
        return true;
    }

    private void signInUser(String email, String password) {
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection(ProjectStorage.KEY_COLLECTION_USERS)
                .whereEqualTo(ProjectStorage.KEY_USER_EMAIL, email)
                .get()
                .addOnSuccessListener(task1 -> {
                    DocumentSnapshot documentSnapshot = task1.getDocuments().get(0);
                    preferenceManager.putString(ProjectStorage.KEY_USER_ID, documentSnapshot.getId());
                });
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful() && preferenceManager.getString(ProjectStorage.KEY_USER_ID) != null) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user.isEmailVerified()) {
                    FirebaseMessaging.getInstance().getToken()
                            .addOnCompleteListener(new OnCompleteListener<String>() {
                                @Override
                                public void onComplete(@NonNull Task<String> task) {
                                    if (task.isSuccessful() && task.getResult() != null && preferenceManager.getString(ProjectStorage.KEY_USER_ID) != null) {
                                        sendFCMTokenToDatabase(task.getResult());
                                    }
                                }
                            });
                    Intent intent = new Intent(MainActivity.this, GroupInfoActivity.class);
                    intent.putExtra("email",email);
                    // redirect to user profile
                    startActivity(intent);
                } else {
                    user.sendEmailVerification();
                    Toast.makeText(MainActivity.this, "Check your email to verify your account", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(MainActivity.this, "Failed to login! Try again!", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void sendFCMTokenToDatabase(String token){
        FirebaseFirestore database = FirebaseFirestore.getInstance();

        DocumentReference documentReference =
                database.collection(ProjectStorage.KEY_COLLECTION_USERS).document(
                    preferenceManager.getString(ProjectStorage.KEY_USER_ID)
                );

        documentReference.update(ProjectStorage.KEY_FCM_TOKEN, token)
                .addOnSuccessListener(runnable -> {
                    Toast.makeText(MainActivity.this,"token updated successfully", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(runnable -> {
                    Toast.makeText(MainActivity.this,"Unable to send token: "+runnable.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}