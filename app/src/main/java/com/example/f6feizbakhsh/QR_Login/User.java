package com.example.f6feizbakhsh.QR_Login;

/**
 * Created by f6feizbakhsh on 2/11/2016.
 */
public class User {
    String username, name_;
    int id, check;

    public User(String username, int check, int id, String name_)
     {
         this.username = username;
         this.check    = check;
         this.id       = id;
         this.name_    = name_;
    }
    public User(String username)
    {
        this.username = username;
    }
}
