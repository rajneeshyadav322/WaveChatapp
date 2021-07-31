package com.rajdroid.wave.Class;

public class firebaseModel {

    String Name;
    String Uid;
    String Image;
    String Status;


    public firebaseModel(String name, String uid, String image, String status) {
        Name = name;
        Uid = uid;
        Image = image;
        Status = status;
    }

    public firebaseModel() {

    }


    public String getName() {
        return Name;
    }

    public String getUid() {
        return Uid;
    }

    public String getImage() {
        return Image;
    }

    public String getStatus() {
        return Status;
    }

    public void setName(String name) {
        Name = name;
    }

    public void setUid(String uid) {
        Uid = uid;
    }

    public void setImage(String image) {
        Image = image;
    }

    public void setStatus(String status) {
        Status = status;
    }
}
