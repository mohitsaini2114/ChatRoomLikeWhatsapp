package com.example.vaibhav.login_inclass6;

import android.net.Uri;

import com.google.android.gms.tasks.Task;

import org.ocpsoft.prettytime.PrettyTime;

import java.net.URI;
import java.net.URL;
import java.util.Date;

public class Message {

    String message;
    String firstName;
    String lastName;
    String email;
    String imageName;
    String scheduleTime;
    String messageKey;
    String imageUrl;

    public Message() {
    }

    public Message(String message,String userName,String lastName, String imageName, String scheduleTime,String email) {
        this.message = message;
        this.firstName = userName;
        this.lastName = lastName;
        this.imageName = imageName;
        this.scheduleTime = scheduleTime;
        this.email = email;

    }


    public String getPrettyTime()
    {
        PrettyTime prettyTime = new PrettyTime();
        return prettyTime.format(new Date(this.scheduleTime));
    }
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String userName) {
        this.firstName = userName;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public String getScheduleTime() {



        return scheduleTime;
    }

    public void setScheduleTime(String scheduleTime) {
        this.scheduleTime = scheduleTime;
    }

}
