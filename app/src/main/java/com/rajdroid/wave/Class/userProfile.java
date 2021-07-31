package com.rajdroid.wave.Class;

public class userProfile {

    public String userName;
    public String userUID;

    public userProfile() {

    }

    public userProfile(String userName, String userUID) {
        this.userName = userName;
        this.userUID = userUID;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setUserUID(String userUID) {
        this.userUID = userUID;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserUID() {
        return userUID;
    }
}
