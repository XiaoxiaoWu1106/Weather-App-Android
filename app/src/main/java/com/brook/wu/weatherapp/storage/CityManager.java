package com.brook.wu.weatherapp.storage;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.brook.wu.weatherapp.MyApplication;
import com.brook.wu.weatherapp.model.City;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.function.Function;

import androidx.room.Room;

public class CityManager {

    private final CityDao mCityDao = MyApplication.getInstance().database.cityDao();

    private static CityManager mInstance;

    private Handler mainHandler = new Handler(Looper.getMainLooper());


    public static CityManager getInstance() {
        if (mInstance == null) {
            synchronized (CityManager.class) {
                if (mInstance == null) {
                    mInstance = new CityManager();
                }
            }
        }
        return mInstance;
    }

    public void getUserSavedCities(DataCallback<List<City>> data) {
        new Thread(() -> {
            List<City> fullData = mCityDao.getStoredCities();
            mainHandler.post(() -> data.completed(fullData));
        }).start();

    }

    public void getWorldCities(String newText, DataCallback<List<City>> data) {
        new Thread(() -> {
            List<City> fullData = mCityDao.getWorldCities(newText);
            mainHandler.post(() -> data.completed(fullData));
        }).start();
    }

    public void deleteCity(int id, DataCallback<Void> data) {
        new Thread(() -> {
            mCityDao.deleteCity(id);
            mainHandler.post(() -> data.completed(null));
        }).start();
    }

    public void saveCity(City city, DataCallback<Void> data) {
        Log.d("TESTINGTAG","Saving a cityThreadStart!");
        new Thread(() -> {
            Log.d("TESTINGTAG","Saving a city!");
            mCityDao.saveCity(city);
            Log.d("TESTINGTAG","Save ready, calling callback....!");
            mainHandler.post(() -> data.completed(null));
        }).start();
    }
}

