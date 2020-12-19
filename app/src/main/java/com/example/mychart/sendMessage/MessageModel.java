package com.example.mychart.sendMessage;

public class MessageModel {
    private String FROM;
    private String message;
    private long message_TIME;
    private String message_id;
    private String message_type;

    public MessageModel() {
    }

    public MessageModel(String FROM, String message, long message_TIME, String message_id, String message_type) {
        this.FROM = FROM;
        this.message = message;
        this.message_TIME = message_TIME;
        this.message_id = message_id;
        this.message_type = message_type;
    }

    public String getFROM() {
        return FROM;
    }

    public void setFROM(String FROM) {
        this.FROM = FROM;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getMessage_TIME() {
        return message_TIME;
    }

    public void setMessage_TIME(long message_TIME) {
        this.message_TIME = message_TIME;
    }

    public String getMessage_id() {
        return message_id;
    }

    public void setMessage_id(String message_id) {
        this.message_id = message_id;
    }

    public String getMessage_type() {
        return message_type;
    }

    public void setMessage_type(String message_type) {
        this.message_type = message_type;
    }
}
