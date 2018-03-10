package com.example.hp.hidi2;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import java.util.HashMap;

/**
 * Created by HP on 10-Mar-18.
 */

public class SessionManager
{
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    Context context;
    private static final String IS_LOGIN="isLoggedIn";
    private static final String PREF_NAME="Hidi_Session";
    public static final String KEY_MOBILE="mobileno";
    public static final String KEY_UID="uid";
    public static final String KEY_NAME="username";
    public static final String KEY_SECNAME="secretName";
    public static final String KEY_ADMIRE="admire";
    public static final String KEY_LOVE="love";
    public static final String KEY_POPULARITY="popularity";
    public static final String KEY_VISITORS="visitors";
    public static final String KEY_HIDIES="my_hidies";
    public static final String KEY_BLOCKS="blocks";
    int PRIVATE_MODE=0;
    public SessionManager(Context context)
    {
        this.context=context;
        sharedPreferences=context.getSharedPreferences(PREF_NAME,PRIVATE_MODE);
        editor=sharedPreferences.edit();
    }
    public void createLoginSession(int uid,String mobileno,String username,String secname)
    {
        editor.putBoolean(IS_LOGIN,true);
        editor.putInt(KEY_UID,uid);
        editor.putString(KEY_MOBILE,mobileno);
        editor.putString(KEY_NAME,username);
        editor.putString(KEY_SECNAME,secname);
        editor.commit();
    }
    public boolean isLoggedIn()
    {
        return sharedPreferences.getBoolean(IS_LOGIN, false);
    }
    public void checkLogin()
    {
        if(!this.isLoggedIn())
        {
            Intent i=new Intent(context,MainActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        }
    }
    void logoutUser()
    {
        editor.clear();
        editor.commit();
        Intent i=new Intent(context,MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
    }
    public HashMap<String,String> getUserDetails()
    {
        HashMap<String,String> user=new HashMap<>();
        user.put(KEY_MOBILE,sharedPreferences.getString(KEY_MOBILE,null));
        user.put(KEY_NAME,sharedPreferences.getString(KEY_NAME,null));
        user.put(KEY_SECNAME,sharedPreferences.getString(KEY_SECNAME,null));
        user.put(KEY_ADMIRE,sharedPreferences.getString(KEY_ADMIRE,null));
        user.put(KEY_LOVE,sharedPreferences.getString(KEY_LOVE,null));
        user.put(KEY_POPULARITY,sharedPreferences.getString(KEY_POPULARITY,null));
        user.put(KEY_VISITORS,sharedPreferences.getString(KEY_VISITORS,null));
        user.put(KEY_HIDIES,sharedPreferences.getString(KEY_HIDIES,null));
        user.put(KEY_BLOCKS,sharedPreferences.getString(KEY_BLOCKS,null));
        return user;
    }
    public int getUID()
    {
        return sharedPreferences.getInt(KEY_UID,0);
    }
}