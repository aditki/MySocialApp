package com.example.aditya.mysocialapp;



public class RequestObj {
    private String type;
    private Request request;

    public RequestObj(String type, Request request) {
        this.type = type;
        this.request = request;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Request getRequest() {
        return request;
    }

    public void setRequest(Request request) {
        this.request = request;
    }
}
