// RegisterActivity.java
package com.kks.chattingapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.kks.chattingapp.utility.FirebaseID;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "RegisterActivity";
    private static final int PICK_IMAGE_REQUEST = 1;

    EditText editPassword, editNickname;
    Button btnRegister;
    TextView txtLogin;
    ImageView profileImage;
    private FirebaseFirestore firestore;
    private FirebaseStorage firebaseStorage;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        firestore = FirebaseFirestore.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();

        txtLogin = findViewById(R.id.txtLogin);
        editPassword = findViewById(R.id.editPassword);
        editNickname = findViewById(R.id.editNickname);
        btnRegister = findViewById(R.id.btnRegister);
        profileImage = findViewById(R.id.profileImage);

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

        txtLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
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
            profileImage.setImageURI(imageUri);
        }
    }

    @Override
    public void onClick(View view) {
        final String password = editPassword.getText().toString().trim();
        final String nickname = editNickname.getText().toString().trim();

        if (!password.isEmpty() && !nickname.isEmpty() && imageUri != null) {
            StorageReference storageReference = firebaseStorage.getReference("profile_images/" + nickname + ".jpg");
            storageReference.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String imageUrl = uri.toString();

                                    Map<String, Object> userMap = new HashMap<>();
                                    userMap.put(FirebaseID.password, password);
                                    userMap.put(FirebaseID.nickname, nickname);
                                    userMap.put("imageUrl", imageUrl);

                                    firestore.collection(FirebaseID.user)
                                            .add(userMap)
                                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                @Override
                                                public void onSuccess(DocumentReference documentReference) {
                                                    // Firestore 저장 성공 시 로그인 화면으로 이동
                                                    Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                                    startActivity(intent);
                                                    finish();
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Log.e(TAG, "Firestore 오류: " + e);
                                                    Toast.makeText(RegisterActivity.this, "회원가입 실패: Firestore 오류", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.e(TAG, "이미지 URL 가져오기 실패: " + e);
                                    Toast.makeText(RegisterActivity.this, "이미지 업로드 실패", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e(TAG, "이미지 업로드 실패: " + e);
                            Toast.makeText(RegisterActivity.this, "이미지 업로드 실패", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(RegisterActivity.this, "모든 필드를 채워주세요.", Toast.LENGTH_SHORT).show();
        }
    }
}
