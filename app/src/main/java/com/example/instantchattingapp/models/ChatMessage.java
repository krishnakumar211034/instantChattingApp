package com.example.instantchattingapp.models;

import java.util.Date;

public class ChatMessage {
    private String senderId,receiverId,message,dateTime;
    public Date dateObject;
    private String conversionId, conversionName, conversionImage;

    public ChatMessage(String senderId, String receiverId, String message, String dateTime) {
        this.senderId = senderId;
        this.receiverId = receiverId;
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

    public String getConversionId() {
        return conversionId;
    }
    public void setConversionId(String conversionId) {
        this.conversionId = conversionId;
    }

    public String getConversionName() {
        return conversionName;
    }
    public void setConversionName(String conversionName) {
        this.conversionName = conversionName;
    }

    public String getConversionImage() {
        return conversionImage;
    }
    public void setConversionImage(String conversionImage) {
        this.conversionImage = conversionImage;
    }

}
