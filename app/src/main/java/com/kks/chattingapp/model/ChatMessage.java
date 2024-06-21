package com.kks.chattingapp.model;

import com.google.firebase.Timestamp;

public class ChatMessage {

    private String userName;
    private String message;
    private String profileImageUrl;
    private Timestamp timestamp;

    public ChatMessage() {
        // Firestore의 문서 스냅샷이 인스턴스를 생성할 때 빈 생성자를 필요로 함
    }

    public ChatMessage(String userName, String message, String profileImageUrl) {
        this.userName = userName;
        this.message = message;
        this.profileImageUrl = profileImageUrl;
        this.timestamp = Timestamp.now();  // 기본값으로 현재 시간을 설정
    }

    public ChatMessage(String userName, String message, String profileImageUrl, Timestamp timestamp) {
        this.userName = userName;
        this.message = message;
        this.profileImageUrl = profileImageUrl;
        this.timestamp = timestamp;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
}
