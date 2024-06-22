package com.ksg.chattingapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;

public class FirstActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    Button btnEnter;
    ImageView profileImage;
    TextView txtLogout;
    FirebaseFirestore db;
    FirebaseStorage storage;
    Uri imageUri;
    String imageUrl;
    String nickname;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.first, menu);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);

        getSupportActionBar().setTitle("Welcome to Chatting App!");

        // 액션바에 화살표 백버튼을 표시하는 코드
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        db = FirebaseFirestore.getInstance(); // Firestore 인스턴스 초기화
        storage = FirebaseStorage.getInstance(); // Firebase Storage 인스턴스 초기화

        btnEnter = findViewById(R.id.btnEnter);
        profileImage = findViewById(R.id.profileImage);
        txtLogout = findViewById(R.id.txtLogout);

        // MainActivity에서 전달받은 nickname과 imageUrl
        nickname = getIntent().getStringExtra("nickname");
        imageUrl = getIntent().getStringExtra("imageUrl");

        if (imageUrl != null) {
            // Glide를 사용하여 이미지 표시
            Glide.with(this)
                    .load(imageUrl)
                    .placeholder(R.drawable.user3) // 이미지 로딩 중에 표시할 임시 이미지
                    .error(R.drawable.default_profile_image) // 이미지 로딩 실패 시 표시할 이미지
                    .into(profileImage);
        } else {
            // imageUrl이 null일 경우 기본 이미지 설정
            Glide.with(this)
                    .load(R.drawable.default_profile_image)
                    .into(profileImage);
        }

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

        btnEnter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImage();
            }
        });

        txtLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLogoutDialog();
            }
        });
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

    private void uploadImage() {
        if (imageUri != null) {
            StorageReference fileReference = storage.getReference().child("profile_images/" + System.currentTimeMillis() + ".jpg");
            fileReference.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    imageUrl = uri.toString();
                                    Intent intent = new Intent(FirstActivity.this, ChatActivity.class);
                                    intent.putExtra("imageUrl", imageUrl); // imageUrl 전달
                                    intent.putExtra("nickname", nickname); // nickname 전달
                                    startActivity(intent);
                                    finish();
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(FirstActivity.this, "Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Intent intent = new Intent(FirstActivity.this, ChatActivity.class);
            intent.putExtra("nickname", nickname); // nickname 전달
            startActivity(intent);
            finish();
        }
    }

    private void showLogoutDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("로그아웃");
        builder.setMessage("로그아웃 하시겠습니까?");
        builder.setPositiveButton("로그아웃", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(FirstActivity.this, "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(FirstActivity.this, MainActivity.class)); // 로그인 화면으로 이동
                finish();
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // 사용자가 취소 버튼을 클릭한 경우 처리 (아무것도 하지 않음)
            }
        });
        builder.show(); // AlertDialog 표시
    }

    // 액션바의 화살표를 누르면, 이 액티비티를 종료하도록 하는 함수
    @Override
    public boolean onSupportNavigateUp() {
        startActivity(new Intent(FirstActivity.this, MainActivity.class));
        finish();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menuLogout) {
            showLogoutDialog();
            return true; // 이벤트 처리 완료
        }
        return super.onOptionsItemSelected(item); // 기본 처리 (이 경우는 없겠지만 안전하게 기본 핸들링)
    }
}
