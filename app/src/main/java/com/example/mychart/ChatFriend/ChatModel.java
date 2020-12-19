package com.example.mychart.ChatFriend;

public class ChatModel {
    private String UserName;
    private String UserId;
    private String PhotoName;
    private String UnReadCountMessage;
    private String lastMessage;
    private String lastMessageTime;

    public ChatModel(String userName, String userId, String photoName, String unReadCountMessage, String lastMessage, String lastMessageTime) {
        UserName = userName;
        UserId = userId;
        PhotoName = photoName;
        UnReadCountMessage = unReadCountMessage;
        this.lastMessage = lastMessage;
        this.lastMessageTime = lastMessageTime;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String userId) {
        UserId = userId;
    }

    public String getPhotoName() {
        return PhotoName;
    }

    public void setPhotoName(String photoName) {
        PhotoName = photoName;
    }

    public String getUnReadCountMessage() {
        return UnReadCountMessage;
    }

    public void setUnReadCountMessage(String unReadCountMessage) {
        UnReadCountMessage = unReadCountMessage;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public String getLastMessageTime() {
        return lastMessageTime;
    }

    public void setLastMessageTime(String lastMessageTime) {
        this.lastMessageTime = lastMessageTime;
    }
}
