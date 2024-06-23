package com.ksg.chattingapp.model;

import com.google.firebase.Timestamp;

public class ChatMessage {
    private String nickname;
    private String message;
    private Timestamp timestamp;
    private String imageUrl;
    private String profileImageUrl; // 프로필 이미지 URL 필드 추가

    public ChatMessage() {
    }

    public ChatMessage(String nickname, String message, Timestamp timestamp, String imageUrl, String profileImageUrl) {
        this.nickname = nickname;
        this.message = message;
        this.timestamp = timestamp;
        this.imageUrl = imageUrl;
        this.profileImageUrl = profileImageUrl; // 프로필 이미지 URL 초기화
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

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }
}
