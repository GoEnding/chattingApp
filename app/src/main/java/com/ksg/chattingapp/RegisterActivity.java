package com.ksg.chattingapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.ksg.chattingapp.utility.FirebaseID;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "RegisterActivity";
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final String DEFAULT_IMAGE_URL = "https://example.com/default_profile_image.jpg"; // 기본 이미지 URL (Firebase Storage에 업로드된 기본 이미지 URL)

    EditText editPassword, editNickname;
    Button btnRegister;
    ImageView profileImage;
    TextView txtLogin;
    private FirebaseFirestore firestore;
    private FirebaseStorage firebaseStorage;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        getSupportActionBar().setTitle("Please sign up");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        firestore = FirebaseFirestore.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        txtLogin = findViewById(R.id.txtLogout);
        editPassword = findViewById(R.id.editPassword);
        editNickname = findViewById(R.id.editNickname);
        btnRegister = findViewById(R.id.btnRegister);
        profileImage = findViewById(R.id.profileImage);

        txtLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

        btnRegister.setOnClickListener(this);
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                profileImage.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onClick(View view) {
        final String password = editPassword.getText().toString().trim();
        final String nickname = editNickname.getText().toString().trim();

        if (!password.isEmpty() && !nickname.isEmpty()) {
            checkNicknameAndRegister(nickname, password);
        } else {
            Toast.makeText(RegisterActivity.this, "모든 필드를 채워주세요.", Toast.LENGTH_SHORT).show();
        }
    }

    private void checkNicknameAndRegister(final String nickname, final String password) {
        firestore.collection(FirebaseID.user)
                .whereEqualTo(FirebaseID.nickname, nickname)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if (task.getResult().isEmpty()) {
                                if (imageUri != null) {
                                    uploadImageAndRegisterUser(nickname, password);
                                } else {
                                    registerUser(nickname, password, DEFAULT_IMAGE_URL);
                                }
                            } else {
                                Toast.makeText(RegisterActivity.this, "닉네임이 이미 존재합니다.", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(RegisterActivity.this, "Error checking nickname: " + task.getException(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void uploadImageAndRegisterUser(final String nickname, final String password) {
        final StorageReference storageReference = firebaseStorage.getReference("profileImages/" + System.currentTimeMillis() + ".jpg");
        storageReference.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {
                    storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String imageUrl = uri.toString();
                            registerUser(nickname, password, imageUrl);
                        }
                    });
                } else {
                    Toast.makeText(RegisterActivity.this, "이미지 업로드 실패: " + task.getException(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void registerUser(final String nickname, final String password, String imageUrl) {
        Map<String, Object> userMap = new HashMap<>();
        userMap.put(FirebaseID.password, password);
        userMap.put(FirebaseID.nickname, nickname);
        userMap.put(FirebaseID.imageUrl, imageUrl);

        firestore.collection(FirebaseID.user)
                .add(userMap)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if (task.isSuccessful()) {
                            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Log.e(TAG, "Firestore 오류: " + task.getException());
                            Toast.makeText(RegisterActivity.this, "회원가입 실패: Firestore 오류", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    public boolean onSupportNavigateUp() {
        Intent intent = new Intent(RegisterActivity.this, FirstActivity.class);
        startActivity(intent);
        finish();
        return true;
    }
}
