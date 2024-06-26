package com.ksg.chattingapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.ksg.chattingapp.utility.FirebaseID;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    EditText editNickname, editPassword;
    Button btnLogin;
    TextView txtRegister;
    private FirebaseFirestore firestore;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().setTitle("Please log in");

        firestore = FirebaseFirestore.getInstance();

        editNickname = findViewById(R.id.editNickname);
        editPassword = findViewById(R.id.editPassword);
        btnLogin = findViewById(R.id.btnLogin);
        txtRegister = findViewById(R.id.txtRegister);
        context = this; // 현재 액티비티의 context를 가져옴

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
                                        QueryDocumentSnapshot document = (QueryDocumentSnapshot) task.getResult().getDocuments().get(0);
                                        String imageUrl = document.getString("imageUrl");

                                        // 로그인 성공 후 프로필 선택 화면으로 이동
                                        Intent intent = new Intent(MainActivity.this, FirstActivity.class);
                                        intent.putExtra("nickname", nickname); // nickname 전달
                                        intent.putExtra("imageUrl", imageUrl); // 이미지 URL 전달
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
