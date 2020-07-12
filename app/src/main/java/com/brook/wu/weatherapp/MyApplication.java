package com.brook.wu.weatherapp;

import android.app.Application;
import android.content.SharedPreferences;

import com.androidnetworking.AndroidNetworking;
import com.brook.wu.weatherapp.storage.WeatherDatabase;

//only called when app is created
public class MyApplication extends Application {

    private static MyApplication instance;
    public WeatherDatabase database;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        AndroidNetworking.initialize(this);
        database = WeatherDatabase.getInstance();
    }

    public static MyApplication getInstance() {
        return instance;
    }
    public SharedPreferences getSharedPreferences() {
        return getSharedPreferences("com.brook.wu.weatherapp.shared_prefs",MODE_PRIVATE);
    }
}
