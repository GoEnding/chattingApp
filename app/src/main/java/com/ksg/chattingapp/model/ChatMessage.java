package com.ksg.chattingapp.model;

import com.google.firebase.Timestamp;

public class ChatMessage {
    private String nickname;
    private String message;
    private Timestamp timestamp;
    private String imageUrl;

    public ChatMessage() {
        // Default constructor required for calls to DataSnapshot.getValue(ChatMessage.class)
    }

    public ChatMessage(String nickname, String message, Timestamp timestamp, String imageUrl) {
        this.nickname = nickname;
        this.message = message;
        this.timestamp = timestamp;
        this.imageUrl = imageUrl;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
