package com.example.authproject;

import static com.example.authproject.utilities.ProjectStorage.PICK_IMAGE_REQUEST;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.authproject.listeners.UploadFileSuccessListener;
import com.example.authproject.models.User;
import com.example.authproject.utilities.PreferenceManager;
import com.example.authproject.utilities.FileUtilities;
import com.example.authproject.utilities.ProjectStorage;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener, UploadFileSuccessListener {

    private TextView banner, btnRegister;
    private EditText editTextFullName, editTextAge, editTextEmail, editTextPassword;
    private ImageView img_avatar;

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

        banner = (TextView) findViewById(R.id.banner);
        banner.setOnClickListener(this);

        btnRegister = (Button) findViewById(R.id.btnRegister);
        btnRegister.setOnClickListener(this);

        editTextFullName = (EditText) findViewById(R.id.fullName);
        editTextAge = (EditText) findViewById(R.id.age);
        editTextEmail = (EditText) findViewById(R.id.email);
        editTextPassword = (EditText) findViewById(R.id.password);

        img_avatar = (ImageView) findViewById(R.id.img_avatar);
        img_avatar.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.banner:
                startActivity(new Intent(this, MainActivity.class));
                break;
            case R.id.btnRegister:
                register();
                break;
            case R.id.img_avatar:
                selectImage();
                break;
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
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String fullName = editTextFullName.getText().toString().trim();
        String age = editTextAge.getText().toString().trim();

        if (validateInput(fullName, age, email, password)) {
            createUser(fullName, age, email, password);
        }

    }

    private boolean validateInput(String fullName, String age, String email, String password) {
        if (fullName.isEmpty()) {
            editTextFullName.setError("Full name is required!");
            editTextFullName.requestFocus();
            return false;
        }

        if (age.isEmpty()) {
            editTextAge.setError("Age is required!");
            editTextAge.requestFocus();
            return false;
        }

        if (email.isEmpty()) {
            editTextEmail.setError("Email is required!");
            editTextEmail.requestFocus();
            return false;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail.setError("Pls provide valid email!");
            editTextEmail.requestFocus();
            return false;
        }

        if (password.isEmpty()) {
            editTextPassword.setError("Password is required!");
            editTextPassword.requestFocus();
            return false;
        }

        if (password.length() < 6) {
            editTextPassword.setError("Password must be > 6");
            editTextPassword.requestFocus();
            return false;
        }

        if (imgData == null) {
            Toast.makeText(RegisterActivity.this, "Pls select image", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void createUser(String fullName, String age, String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            user = new User(fullName, age, email);
                            new FileUtilities()
                                    .uploadFile(RegisterActivity.this, RegisterActivity.this, imgData);

                        } else {
                            Toast.makeText(RegisterActivity.this, "Failed to register! Try again!", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    @Override
    public void onUploadFileSuccess(Uri uri) {
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