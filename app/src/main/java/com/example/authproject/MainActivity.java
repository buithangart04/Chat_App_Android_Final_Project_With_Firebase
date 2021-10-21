package com.example.authproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.authproject.utilities.PreferenceManager;
import com.example.authproject.utilities.ProjectStorage;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView txtRegister, txtForgotPassword;
    private EditText editTextEmail, editTextPassword;
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

        editTextEmail = (EditText) findViewById(R.id.email);
        editTextPassword = (EditText) findViewById(R.id.password);

        mAuth = FirebaseAuth.getInstance();

        txtForgotPassword = (TextView) findViewById(R.id.txtForgotPassword);
        txtForgotPassword.setOnClickListener(this);
        preferenceManager = new PreferenceManager(getApplicationContext());
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w("TAG", "Fetching FCM registration token failed", task.getException());
                            return;
                        }

                        // Get new FCM registration token
                        String token = task.getResult();
                        Log.d("token",token);
                        // Log and toast
                    }
                });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.txtRegister:
                startActivity(new Intent(this, RegisterActivity.class));
                break;
            case R.id.btnLogin:
                Login();
                break;
            case R.id.txtForgotPassword:
                startActivity(new Intent(this,ForgotPasswordActivity.class));
                break;
        }
    }

    private void Login() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        validateInput(email, password);

        signInUser(email, password);
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection(ProjectStorage.KEY_COLLECTION_USERS)
                .whereEqualTo(ProjectStorage.KEY_USER_EMAIL, email)
                .get()
                .addOnCompleteListener(task1 -> {
                    if (task1.isSuccessful() && task1.getResult() != null) {
                        DocumentSnapshot documentSnapshot = task1.getResult().getDocuments().get(0);
                        preferenceManager.putString(ProjectStorage.KEY_USER_ID, documentSnapshot.getId());
                        Log.d("check",documentSnapshot.getId());
                    }
                });
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            sendFCMTokenToDatabase(task.getResult());
                        }
                    }
                });
    }

    private void validateInput(String email, String password) {
        if (email.isEmpty()) {
            editTextEmail.setError("Email is required!");
            editTextEmail.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail.setError("Pls provide valid email!");
            editTextEmail.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            editTextPassword.setError("Password is required!");
            editTextPassword.requestFocus();
            return;
        }

        if (password.length() < 6) {
            editTextPassword.setError("Password must be > 6");
            editTextPassword.requestFocus();
            return;
        }
    }

    private void signInUser(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user.isEmailVerified()) {
                    Intent intent = new Intent(MainActivity.this, UsersActivity.class);
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