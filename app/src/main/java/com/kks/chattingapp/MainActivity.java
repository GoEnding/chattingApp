package com.kks.chattingapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.kks.chattingapp.utility.FirebaseID;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    EditText editNickname, editPassword;
    Button btnLogin;
    TextView txtRegister;
    ImageView profileImage;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firestore = FirebaseFirestore.getInstance();

        editNickname = findViewById(R.id.editNickname);
        editPassword = findViewById(R.id.editPassword);
        btnLogin = findViewById(R.id.btnLogin);
        txtRegister = findViewById(R.id.txtRegister);
        profileImage = findViewById(R.id.profileImage);

        txtRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, RegisterActivity.class));
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nickname = editNickname.getText().toString().trim();
                String password = editPassword.getText().toString().trim();

                if (!nickname.isEmpty() && !password.isEmpty()) {
                    firestore.collection(FirebaseID.user)
                            .whereEqualTo(FirebaseID.nickname, nickname)
                            .whereEqualTo(FirebaseID.password, password)
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful() && task.getResult() != null && !task.getResult().isEmpty()) {
                                        DocumentSnapshot document = task.getResult().getDocuments().get(0);
                                        String imageUrl = document.getString("imageUrl");
                                        if (imageUrl != null) {
                                            Glide.with(MainActivity.this).load(imageUrl).into(profileImage);
                                        }
                                        String userNickname = document.getString(FirebaseID.nickname);

                                        // 로그인 성공 후 채팅 화면으로 이동
                                        Intent intent = new Intent(MainActivity.this, ChatActivity.class);
                                        intent.putExtra("profileImageUrl", imageUrl);
                                        intent.putExtra("nickname", userNickname);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        Toast.makeText(MainActivity.this, "로그인 실패: 사용자 데이터 없음", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                } else {
                    Toast.makeText(MainActivity.this, "모든 필드를 채워주세요.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
