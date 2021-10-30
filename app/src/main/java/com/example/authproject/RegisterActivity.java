package com.example.authproject;

import static com.example.authproject.utilities.ProjectStorage.PICK_IMAGE_REQUEST;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Pair;
import android.util.Patterns;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.authproject.listeners.UploadFileSuccessListener;
import com.example.authproject.models.User;
import com.example.authproject.utilities.PreferenceManager;
import com.example.authproject.utilities.ProjectStorage;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener, UploadFileSuccessListener {

    private TextView btnRegister, txtBackToLogin, loginText, logoName;
    private TextInputLayout editTextFullName, editTextEmail, editTextPassword;
    private ImageView logoImage, img_avatar;

    private FirebaseAuth mAuth;
    private PreferenceManager preferenceManager;
    private Uri imgData;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        preferenceManager = new PreferenceManager(getApplicationContext());
        mAuth = FirebaseAuth.getInstance();

        btnRegister = findViewById(R.id.btnRegister);
        btnRegister.setOnClickListener(this);

        txtBackToLogin = findViewById(R.id.txtBackToLogin);
        txtBackToLogin.setOnClickListener(this);

        editTextFullName = findViewById(R.id.fullName);
        editTextEmail = findViewById(R.id.email);
        editTextPassword = findViewById(R.id.password);
        loginText = findViewById(R.id.loginText);
        logoName = findViewById(R.id.logoName);
        logoImage = findViewById(R.id.logoImage);
//        img_avatar = (ImageView) findViewById(R.id.img_avatar);
//        img_avatar.setOnClickListener(this);

    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnRegister:
                register();
                break;
            case R.id.txtBackToLogin:
                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                Pair[] pairs = new Pair[7];
                pairs[0] = new Pair<View, String>(logoImage, "logo_name");
                pairs[1] = new Pair<View, String>(logoName, "logo_text");
                pairs[2] = new Pair<View, String>(loginText, "login_text");
                pairs[3] = new Pair<View, String>(btnRegister, "button_tran");
                pairs[4] = new Pair<View, String>(txtBackToLogin, "singup_tran");
                pairs[5] = new Pair<View, String>(editTextEmail, "email_text");
                pairs[6] = new Pair<View, String>(editTextPassword, "password_text");

                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(RegisterActivity.this, pairs);
                startActivity(intent, options.toBundle());
                break;
//            case R.id.img_avatar:
//                selectImage();
//                break;
        }
    }

    private void selectImage() {
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            imgData = data.getData();
            img_avatar.setImageURI(imgData);
        }
    }

    private void register() {
        String email = editTextEmail.getEditText().getText().toString().trim();
        String password = editTextPassword.getEditText().getText().toString().trim();
        String fullName = editTextFullName.getEditText().getText().toString().trim();

        if (!validateInputFullname(fullName) | !validateInputEmail(email) | !validateInputPassword(password)) {
            return;
        }
        createUser(fullName, email, password);
    }

    private boolean validateInputFullname(String fullName) {
        if (fullName.isEmpty()) {
            editTextFullName.setError("Full name is required!");
            return false;
        }
        return true;
    }

    private boolean validateInputEmail(String email) {
        if (email.isEmpty()) {
            editTextEmail.setError("Email is required!");
            return false;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail.setError("Pls provide valid email!");
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

    private void createUser(String fullName, String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            user = new User(fullName, email);
//                            new FileUtilities()
//                                    .uploadFile(RegisterActivity.this, RegisterActivity.this, imgData);

                        } else {
                            Toast.makeText(RegisterActivity.this, "Failed to register! Try again!", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    @Override
    public void onUploadFileSuccess(Uri uri,Object [] params) {
        user.setUri(uri.toString());

        CollectionReference dbUsers = ProjectStorage.DATABASE_REFERENCE.collection("users");
        dbUsers
                .add(user)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(RegisterActivity.this, "User has been registered successfully!", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(RegisterActivity.this, "Failed to register! Try again!", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}