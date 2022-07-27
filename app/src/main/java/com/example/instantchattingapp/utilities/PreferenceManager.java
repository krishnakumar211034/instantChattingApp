package com.example.instantchattingapp.utilities;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferenceManager {
    private final SharedPreferences sharedPreferences;
    public PreferenceManager(Context context) {
        sharedPreferences=context.getSharedPreferences(Constants.KEY_PREFERENCE_NAME,Context.MODE_PRIVATE);
    }

    // set the boolean and string data
    public void putBoolean(String key,Boolean value){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key,value);
        editor.apply();
    }
    public void putString(String key,String value){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key,value);
        editor.apply();
    }
    // get the boolean and string data
    public boolean getBoolean(String key){
        return sharedPreferences.getBoolean(key,false);
    }
    public String getString(String key){
        return sharedPreferences.getString(key,null);
    }
    // clear the prefereces
    public void clear(){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }
}
