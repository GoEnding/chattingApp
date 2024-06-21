package com.kks.chattingapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.kks.chattingapp.adapter.ChatAdapter;
import com.kks.chattingapp.model.ChatMessage;
import com.kks.chattingapp.utility.FirebaseID;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {

    private EditText editChat;
    private Button btnSend;
    private RecyclerView recyclerView;
    private ChatAdapter chatAdapter;
    private List<ChatMessage> chatMessages;

    private FirebaseFirestore firestore;
    private String userProfileImageUrl;
    private String nickname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        firestore = FirebaseFirestore.getInstance();

        editChat = findViewById(R.id.editChat);
        btnSend = findViewById(R.id.btnSend);
        recyclerView = findViewById(R.id.recyclerView);

        chatMessages = new ArrayList<>();
        nickname = getIntent().getStringExtra("nickname");
        userProfileImageUrl = getIntent().getStringExtra("profileImageUrl");
        chatAdapter = new ChatAdapter(chatMessages, nickname);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(chatAdapter);

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });

        loadMessages();
    }

    private void sendMessage() {
        String messageText = editChat.getText().toString().trim();
        if (!messageText.isEmpty()) {
            Map<String, Object> message = new HashMap<>();
            message.put(FirebaseID.nickname, nickname);
            message.put("message", messageText);
            message.put("profileImageUrl", userProfileImageUrl);
            message.put("timestamp", Timestamp.now());

            firestore.collection("chats").add(message)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            chatMessages.add(new ChatMessage(nickname, messageText, userProfileImageUrl, Timestamp.now()));
                            chatAdapter.notifyDataSetChanged();
                            editChat.setText("");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(ChatActivity.this, "메시지 전송 실패", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void loadMessages() {
        firestore.collection("chats").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            chatMessages.clear();
                            for (DocumentSnapshot document : task.getResult()) {
                                String nickname = document.getString(FirebaseID.nickname);
                                String message = document.getString("message");
                                String profileImageUrl = document.getString("profileImageUrl");
                                Timestamp timestamp = document.getTimestamp("timestamp");
                                chatMessages.add(new ChatMessage(nickname, message, profileImageUrl, timestamp));
                            }
                            chatAdapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(ChatActivity.this, "메시지 로드 실패", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
