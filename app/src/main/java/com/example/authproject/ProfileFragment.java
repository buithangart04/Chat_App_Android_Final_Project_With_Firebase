package com.example.authproject;

import static com.example.authproject.utilities.ProjectStorage.PICK_IMAGE_REQUEST;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.authproject.listeners.UploadFileSuccessListener;
import com.example.authproject.models.User;
import com.example.authproject.utilities.FileUtilities;
import com.example.authproject.utilities.PreferenceManager;
import com.example.authproject.utilities.ProjectStorage;
import com.example.authproject.utilities.RemoveFcmToken;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment implements View.OnClickListener, UploadFileSuccessListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private ImageView imgAvatar;
    private TextView textUsername, textUserEmail, textSignOut;
    private TextInputLayout textInputUsername, textInputPassword, textInputRePassword;
    private Button btnSave;
    private PreferenceManager preferenceManager;
    private FirebaseFirestore database;
    private DocumentReference documentReference;
    private Uri imgData;
    private User userInfo;

    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        textUsername = view.findViewById(R.id.textUsername);
        textUserEmail = view.findViewById(R.id.textUserEmail);
        textInputUsername = view.findViewById(R.id.fullName);
        textInputPassword = view.findViewById(R.id.password);
        textInputRePassword = view.findViewById(R.id.repassword);
        btnSave = view.findViewById(R.id.btnSaveProfile);
        btnSave.setOnClickListener(this);
        imgAvatar = view.findViewById(R.id.img_avatar);
        imgAvatar.setOnClickListener(this);
        textSignOut = view.findViewById(R.id.txtSignOut);
        textSignOut.setOnClickListener(this);
        preferenceManager = new PreferenceManager(requireContext());
        database = FirebaseFirestore.getInstance();
        documentReference = database.collection(ProjectStorage.KEY_COLLECTION_USERS).document(
                PreferenceManager.getInstance().getString(ProjectStorage.KEY_USER_ID)
        );
        getProfile();
        return view;
    }

    public void getProfile() {
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    userInfo = documentSnapshot.toObject(User.class);
                    textUserEmail.setText(userInfo.getEmail());
                    textUsername.setText(userInfo.getFullName());
                    textInputUsername.getEditText().setText(userInfo.getFullName());
                    textInputUsername.getEditText().setSelection(userInfo.getFullName().length());
                    Picasso.get()
                            .load(userInfo.getUri())
                            .fit()
                            .into(imgAvatar);
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnSaveProfile:
                saveProfile();
                break;
            case R.id.img_avatar:
                selectImage();
                break;
            case R.id.txtSignOut:
                RemoveFcmToken removeFcmToken = new RemoveFcmToken();
                removeFcmToken.removeToken(preferenceManager, getActivity());
                Intent intent = new Intent(getActivity(),MainActivity.class);
                Toast.makeText(getActivity(), "Sign out successfully!", Toast.LENGTH_SHORT).show();
                startActivity(intent);
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
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == -1 && data != null) {
            imgData = data.getData();
            imgAvatar.setImageURI(imgData);
        }
    }

    private void saveProfile() {
        String newFullname = textInputUsername.getEditText().getText().toString().trim();
        String newPassword = textInputPassword.getEditText().getText().toString().trim();
        String rePassword = textInputRePassword.getEditText().getText().toString().trim();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (!newFullname.equals(userInfo.getFullName()) && validateInputFullname(newFullname)) {
            documentReference
                    .update(ProjectStorage.KEY_NAME, newFullname)
                    .addOnSuccessListener(runnable -> {
                        Toast.makeText(getActivity(), "Update fullname successfully!", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(runnable -> {
                        Toast.makeText(getActivity(), "Update fullname fail!: " + runnable.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
        if (!newPassword.isEmpty() || !rePassword.isEmpty()) {
            if (!validateInputPassword(newPassword) | !validateReInputPassword(newPassword, rePassword)) {
                return;
            }
            user.updatePassword(newPassword)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(getActivity(), "Update password successfully!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
        if (imgData != null) {
            new FileUtilities().uploadFile(getActivity(), this, imgData);
            imgData = null;
        }
        getProfile();
    }

    private boolean validateInputFullname(String fullName) {
        if (fullName.isEmpty()) {
            textInputUsername.setError("Full name is not empty!");
            return false;
        }
        return true;
    }

    private boolean validateInputPassword(String password) {
        if (password.length() < 6) {
            textInputPassword.setError("Password must be > 6");
            return false;
        }
        return true;
    }

    private boolean validateReInputPassword(String password, String repassword) {
        if (repassword.isEmpty()) {
            textInputRePassword.setError("Re-Password is required!");
            return false;
        }

        if (!password.equals(repassword)) {
            textInputRePassword.setError("Re-Password must same Password");
            return false;
        }

        if (repassword.length() < 6) {
            textInputRePassword.setError("Re-Password must be > 6");
            return false;
        }
        return true;
    }

    @Override
    public void onUploadFileSuccess(Uri uri, Object[] params) {
        documentReference.update(ProjectStorage.KEY_AVATAR, uri.toString());
        getProfile();
    }
}