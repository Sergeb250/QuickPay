package com.auca.quickypay.Model;

import com.google.firebase.database.PropertyName;
import java.io.Serializable;

public class User implements Serializable {
    private String Username;
    private String Email;
    private String Password;

    public User() {
    }

    public User(String username, String email, String password) {
        Username = username;
        Email = email;
        Password = password;
    }

    @PropertyName("username")
    public String getUsername() {
        return Username;
    }

    @PropertyName("username")
    public void setUsername(String username) {
        Username = username;
    }

    @PropertyName("email")
    public String getEmail() {
        return Email;
    }

    @PropertyName("email")
    public void setEmail(String email) {
        Email = email;
    }

    @PropertyName("password")
    public String getPassword() {
        return Password;
    }

    @PropertyName("password")
    public void setPassword(String password) {
        Password = password;
    }

    @Override
    public String toString() {
        return "User{" +
                "Username='" + Username + '\'' +
                ", Email='" + Email + '\'' +
                '}';
    }
}