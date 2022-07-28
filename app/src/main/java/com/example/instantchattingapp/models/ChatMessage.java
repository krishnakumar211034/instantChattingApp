package com.example.instantchattingapp.models;

import java.util.Date;

public class ChatMessage {
    private String senderId,receiverId,message,dateTime;
    public Date dateObject;
    public ChatMessage(String senderId, String recieverId, String message, String dateTime) {
        this.senderId = senderId;
        this.receiverId = recieverId;
        this.message = message;
        this.dateTime = dateTime;
    }
    public ChatMessage(){
    }

    public String getSenderId() {
        return senderId;
    }
    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getReceiverId() {
        return receiverId;
    }
    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }

    public String getDateTime() {
        return dateTime;
    }
    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }
}
