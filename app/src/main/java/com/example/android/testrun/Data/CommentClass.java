package com.example.android.testrun.Data;

/**
 * Created by Codetribe on 2017/02/03.
 */

public class CommentClass {

    String user;
    String message;
    String timestamp;
    String date;


    public CommentClass(String u , String m ,String t, String d)
    {
        user = u;
        message = m;
        timestamp = t;
        date = d;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public CommentClass()
    {

    }


}
