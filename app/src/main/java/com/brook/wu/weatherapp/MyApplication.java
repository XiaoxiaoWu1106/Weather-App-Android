package com.brook.wu.weatherapp;

import android.app.Application;

import com.androidnetworking.AndroidNetworking;

//only called when app is created
public class MyApplication extends Application {

    private static MyApplication instance;
    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        AndroidNetworking.initialize(this);
    }

    public static MyApplication getInstance() {
        return instance;
    }
}
