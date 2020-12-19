package com.example.mychart.requestFriend;

public class RequestModel {
    private String UserName;
    private String UserId;
    private String PhotoName;

    public RequestModel(String userName, String userId, String photoName) {
        UserName = userName;
        UserId = userId;
        PhotoName = photoName;
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
}
