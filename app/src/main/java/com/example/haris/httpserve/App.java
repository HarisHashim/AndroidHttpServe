package com.example.haris.httpserve;

import android.app.Application;
import android.content.SharedPreferences;

public class App extends Application {

    private static App instance;

    private static SharedPreferences prefs;

    public static final String ITEM_IDS = "NAMES";
    public static final String DEFAULT_IDS = "[]";


    public static final String PORT = "PORT";
    public static final int DEFAULT_PORT = 8080;

    public static App get(){
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        instance.init();
    }

    private void init() {
        if(prefs == null)
            prefs = instance.getSharedPreferences(instance.getPackageName(), App.MODE_PRIVATE);
    }

    public String prefsRead(String key, String defValue) {
        return prefs.getString(key, defValue);
    }

    public void prefsWrite(String key, String value) {
        SharedPreferences.Editor prefsEditor = prefs.edit();
        prefsEditor.putString(key, value);
        prefsEditor.commit();
    }

    public boolean prefsRead(String key, boolean defValue) {
        return prefs.getBoolean(key, defValue);
    }

    public void prefsWrite(String key, boolean value) {
        SharedPreferences.Editor prefsEditor = prefs.edit();
        prefsEditor.putBoolean(key, value);
        prefsEditor.commit();
    }

    public Integer prefsRead(String key, int defValue) {
        return prefs.getInt(key, defValue);
    }

    public  void prefsWrite(String key, Integer value) {
        SharedPreferences.Editor prefsEditor = prefs.edit();
        prefsEditor.putInt(key, value).commit();
    }
}
