package com.ksg.chattingapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.ksg.chattingapp.adapter.ChatAdapter;
import com.ksg.chattingapp.model.ChatMessage;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private RecyclerView recyclerView;
    private EditText editChat;
    private Button btnSend;
    private ImageView imgSendImage;
    private ChatAdapter chatAdapter;
    private List<ChatMessage> chatMessages;
    private FirebaseFirestore db;
    private FirebaseStorage storage;
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
        storage = FirebaseStorage.getInstance();
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
        recyclerView.setHasFixedSize(true);
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
                openFileChooser();
            }
        });

        loadMessages();
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
            Uri imageUri = data.getData();
            uploadImage(imageUri);
        }
    }

    private void uploadImage(Uri imageUri) {
        if (imageUri != null) {
            StorageReference fileReference = storage.getReference().child("chat_images/" + System.currentTimeMillis() + ".jpg");
            fileReference.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String imageUrl = uri.toString();
                                    sendMessageWithImage(imageUrl);
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(ChatActivity.this, "Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void sendMessage() {
        String message = editChat.getText().toString();
        if (!TextUtils.isEmpty(message)) {
            ChatMessage chatMessage = new ChatMessage(nickname, message, Timestamp.now(), null);
            db.collection("chats").add(chatMessage);
            editChat.setText("");
        }
    }

    private void sendMessageWithImage(String imageUrl) {
        ChatMessage chatMessage = new ChatMessage(nickname, null, Timestamp.now(), imageUrl);
        db.collection("chats").add(chatMessage);
    }

    private void loadMessages() {
        db.collection("chats")
                .orderBy("timestamp", Query.Direction.ASCENDING)
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
                            recyclerView.scrollToPosition(chatMessages.size() - 1);
                        }
                    }
                });
    }
}
