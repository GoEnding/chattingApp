package com.kks.chattingapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kks.chattingapp.utility.FirebaseID;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "RegisterActivity";

    EditText editPassword, editNickname;
    Button btnRegister;
    TextView txtLogin;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        firestore = FirebaseFirestore.getInstance();

        txtLogin = findViewById(R.id.txtLogin);
        editPassword = findViewById(R.id.editPassword);
        editNickname = findViewById(R.id.editNickname);
        btnRegister = findViewById(R.id.btnRegister);

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

    @Override
    public void onClick(View view) {
        final String password = editPassword.getText().toString().trim();
        final String nickname = editNickname.getText().toString().trim();

        if (!password.isEmpty() && !nickname.isEmpty()) {
            Map<String, Object> userMap = new HashMap<>();
            userMap.put(FirebaseID.password, password);
            userMap.put(FirebaseID.nickname, nickname);

            firestore.collection(FirebaseID.user)
                    .add(userMap)
                    .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentReference> task) {
                            if (task.isSuccessful()) {
                                // Firestore 저장 성공 시 로그인 화면으로 이동
                                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                Log.e(TAG, "Firestore 오류: " + task.getException());
                                Toast.makeText(RegisterActivity.this, "회원가입 실패: Firestore 오류", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        } else {
            Toast.makeText(RegisterActivity.this, "모든 필드를 채워주세요.", Toast.LENGTH_SHORT).show();
        }
    }
}
