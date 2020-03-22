package com.example.f6feizbakhsh.QR_Login;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by f6feizbakhsh on 2/11/2016.
 */

public class UserLocalStore {
    public static final String SP_NAME="userDetails";
    SharedPreferences userLocalDatabase;

    public UserLocalStore(Context context){
        userLocalDatabase = context.getSharedPreferences(SP_NAME,0);
    }

    public void storeUserData(User user) {
        SharedPreferences.Editor spEditor = userLocalDatabase.edit();
        spEditor.putString("username",user.username);
        spEditor.putInt("check"      ,user.check );
        spEditor.putInt("id"         ,user.id);
        spEditor.commit();
    }

    public User getLoggedInUser() {
        int check=0;
        String username = userLocalDatabase.getString( "username", "" );
        //
        //put a try here because earlier it was text saved inside check and when its retrieved, it causes error.
        //
        try {
            check       = userLocalDatabase.getInt("check",-1);
        } catch (Exception e) { e.printStackTrace(); }
        int id = (int) userLocalDatabase.getInt( "id", 0 );
        User storedUser = new User( username, check, id,"");
        return storedUser;

    }

    public void setUserLoggedIn(boolean loggedIn) {
        SharedPreferences.Editor spEditor = userLocalDatabase.edit();
        spEditor.putBoolean("loggedIn",loggedIn);
        spEditor.commit();
    }

    public boolean getUserLoggedIn(){
        return userLocalDatabase.getBoolean("loggedIn", false) == true;
    }
    public void clearUserData() {
        SharedPreferences.Editor spEditor = userLocalDatabase.edit();
        spEditor.clear();
        spEditor.commit();
    }
}
