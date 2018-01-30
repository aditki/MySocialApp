package com.example.aditya.mysocialapp;


public class Request {
    private String id, uid;

    public Request(String id, String uid) {
        this.id = id;
        this.uid = uid;
    }

    public Request() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
