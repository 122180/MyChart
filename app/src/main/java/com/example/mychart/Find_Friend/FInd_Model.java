package com.example.mychart.Find_Friend;

public class FInd_Model {
    private String UserName;
    private String PhotoName;
    private String UserId;
    private boolean Request;

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getPhotoName() {
        return PhotoName;
    }

    public void setPhotoName(String photoName) {
        PhotoName = photoName;
    }

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String userId) {
        UserId = userId;
    }

    public boolean isRequest() {
        return Request;
    }

    public void setRequest(boolean request) {
        Request = request;
    }

    public FInd_Model(String userName, String photoName, String userId, boolean request) {
        UserName = userName;
        PhotoName = photoName;
        UserId = userId;
        Request = request;
    }
}
