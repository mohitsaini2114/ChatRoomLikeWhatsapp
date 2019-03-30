package com.example.vaibhav.login_inclass6;

import java.io.Serializable;

public class Thread implements Serializable {
    String title;
    String id;
    String userId;
    String fName;
    String lName;
    String token;
    String cacheToken;
    String cacheUser_id;
    //String createdDate;

    public Thread() {
    }

    public Thread(String title, String id, String userId, String fName, String lName) {
        this.title = title;
        this.id = id;
        this.userId = userId;
        this.fName = fName;

        this.lName = lName;
    }

    @Override
    public String toString() {
        return title;
    }
}
