package com.example.aditya.mysocialapp;

import java.io.Serializable;
import java.util.Date;



public class Post implements Serializable
{
    private String post_id, postedBy, contents;
    private Date timestamp;

    public Post(String post_id, String postedBy, String contents, Date timestamp) {
        this.post_id = post_id;
        this.postedBy = postedBy;
        this.contents = contents;
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "Post{" +
                "post_id='" + post_id + '\'' +
                ", postedBy='" + postedBy + '\'' +
                ", contents='" + contents + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }

    public Post() {
    }

    public String getPost_id() {
        return post_id;
    }

    public void setPost_id(String post_id) {
        this.post_id = post_id;
    }

    public String getPostedBy() {
        return postedBy;
    }

    public void setPostedBy(String postedBy) {
        this.postedBy = postedBy;
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}
