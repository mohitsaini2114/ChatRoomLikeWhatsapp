package com.example.vaibhav.login_inclass6;

import java.io.Serializable;

public class LoginDetails implements Serializable {
    String firstName;
    String lastName;
    String email;
    String password;
    String token;
    String user_id;
    String user_role;
    String msg;
    public LoginDetails(String firstName,
            String lastName,
            String email,
            String password){
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
    }
}
