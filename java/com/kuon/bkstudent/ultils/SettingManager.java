package com.kuon.bkstudent.ultils;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Cuong on 2/4/2018.
 */

public class SettingManager {
    private Context context;
    private SharedPreferences settings;
    private static class SettingMangagerHolder {
        static SettingManager instance = new SettingManager();
    }

    private SettingManager() {

    }

    public static SettingManager getInstance(Context context) {
        SettingMangagerHolder.instance.context = context;
        return SettingMangagerHolder.instance;
    }

    public String getToken() {
        settings = context.getSharedPreferences("token", Context.MODE_MULTI_PROCESS);
        String token = settings.getString(Constant.TOKEN_TAG,"" );
        return token;

    }

    public String getUser() {
        settings = context.getSharedPreferences("user", Context.MODE_MULTI_PROCESS);
        String user = settings.getString(Constant.USERS_TAG, "");
        return user;

    }

    public void setToken(String token) {
        settings = context.getSharedPreferences("token", Context.MODE_MULTI_PROCESS);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(Constant.TOKEN_TAG, token);
        editor.commit();
    }

    public void setUser(String users) {
        settings = context.getSharedPreferences("user", Context.MODE_MULTI_PROCESS);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(Constant.USERS_TAG, users);
        editor.commit();
    }
}
