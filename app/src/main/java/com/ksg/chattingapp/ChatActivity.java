package com.ksg.chattingapp;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.ksg.chattingapp.adapter.ChatAdapter;
import com.ksg.chattingapp.model.ChatMessage;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private EditText editChat;
    private Button btnSend;
    private ImageView imgSendImage;
    private ChatAdapter chatAdapter;
    private List<ChatMessage> chatMessages;
    private FirebaseFirestore db;
    private String nickname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        recyclerView = findViewById(R.id.recyclerView);
        editChat = findViewById(R.id.editChat);
        btnSend = findViewById(R.id.btnSend);
        imgSendImage = findViewById(R.id.imgSendImage);

        db = FirebaseFirestore.getInstance();
        nickname = getIntent().getStringExtra("nickname");

        // 로그 추가
        Log.d("ChatActivity", "nickname: " + nickname);

        if (nickname == null) {
            Toast.makeText(this, "Missing nickname", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        chatMessages = new ArrayList<>();
        chatAdapter = new ChatAdapter(chatMessages, nickname);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);  // Fixed size 추가
        recyclerView.setAdapter(chatAdapter);

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });

        imgSendImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 이미지 전송 코드 추가
            }
        });

        loadMessages();
    }

    private void loadMessages() {
        db.collection("chats")
                .orderBy("timestamp", Query.Direction.ASCENDING) // 메시지를 시간순으로 정렬
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            return;
                        }

                        if (snapshots != null) {
                            chatMessages.clear();
                            for (DocumentSnapshot document : snapshots.getDocuments()) {
                                ChatMessage message = document.toObject(ChatMessage.class);
                                chatMessages.add(message);
                            }
                            chatAdapter.notifyDataSetChanged();
                            recyclerView.scrollToPosition(chatMessages.size() - 1); // 최신 메시지로 스크롤
                        }
                    }
                });
    }

    private void sendMessage() {
        String message = editChat.getText().toString();
        if (!TextUtils.isEmpty(message)) {
            ChatMessage chatMessage = new ChatMessage(nickname, message, Timestamp.now(), null);
            db.collection("chats").add(chatMessage);
            editChat.setText("");
        }
    }
}
