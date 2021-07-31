package com.rajdroid.wave.Class;

public class Messages {

    String message;
    String senderId;
    long timeStamp;
    String currentTime;


    public Messages() {

    }

    public Messages(String message, String senderId, long timeStamp, String currentTime) {
        this.message = message;
        this.senderId = senderId;
        this.timeStamp = timeStamp;
        this.currentTime = currentTime;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public void setCurrentTime(String currentTime) {
        this.currentTime = currentTime;
    }

    public String getMessage() {
        return message;
    }

    public String getSenderId() {
        return senderId;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public String getCurrentTime() {
        return currentTime;
    }
}
