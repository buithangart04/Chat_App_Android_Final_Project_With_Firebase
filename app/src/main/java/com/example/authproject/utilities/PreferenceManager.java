package com.example.authproject.utilities;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferenceManager {
    private final SharedPreferences sharedPreferences;
    private static PreferenceManager preferenceManager;
    public static PreferenceManager getInstance(Context ... params){
        if(preferenceManager == null){
            preferenceManager = new PreferenceManager(params[0]);
        }
        return preferenceManager;
    }

    public PreferenceManager(Context context) {
        sharedPreferences = context.getSharedPreferences(ProjectStorage.KEY_PREFERENCE_NAME, Context.MODE_PRIVATE);

    }

    public void putBoolean(String key, Boolean value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public Boolean getBoolean(String key) {
        return sharedPreferences.getBoolean(key, false);
    }

    public void putString(String key, String value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }


    public String getString(String key) {
        return sharedPreferences.getString(key, null);
    }

    public void clear() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

    }

}
