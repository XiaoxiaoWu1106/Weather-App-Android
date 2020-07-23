package com.brook.wu.weatherapp.storage;

import android.os.Handler;
import android.os.Looper;

import com.brook.wu.weatherapp.MyApplication;
import com.brook.wu.weatherapp.model.City;
import com.brook.wu.weatherapp.model.WeatherItem;

import java.util.List;

public class CityManager {

    private final CityDao mCityDao = MyApplication.getInstance().database.cityDao();
    private final WeatherItemDao mItemDao = MyApplication.getInstance().database.weatherItemDao();
    private static CityManager mInstance;

    private Handler mainHandler = new Handler(Looper.getMainLooper());
    private List<City> mCachedCities;

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

    public void getUserSavedItems(DataCallback<List<WeatherItem>> data) {
        new Thread(() -> {
            List<WeatherItem> fullData = mItemDao.getAllWeatherItems();
            mainHandler.post(() -> data.completed(fullData));
        }).start();

    }

    public void saveWeatherItem(WeatherItem item, DataCallback<Void> data) {
        new Thread(() -> {
            mItemDao.saveWeatherItem(item);
            mainHandler.post(() -> data.completed(null));
        }).start();

    }

    public void getCityById(int cityId, DataCallback<City> data) {
        new Thread(() -> {
            City city = mCityDao.getCityById(cityId);
            mainHandler.post(() -> data.completed(city));
        }).start();
    }


    public void getCityByName(String cityName, DataCallback<City> data) {
        getCitiesByNameFilter(cityName,(cities) ->
                data.completed(cities.size() > 0 ? cities.get(0) : null)
        );
    }


    public void deleteWeatherItem(WeatherItem item,DataCallback<Void> data) {
        new Thread(() -> {
            mItemDao.deleteWeatherItem(item.getCityId());
            mainHandler.post(() -> data.completed(null));
        }).start();

    }

    public void getCitiesByNameFilter(String newText, DataCallback<List<City>> data) {

            new Thread(() -> {
                mCachedCities = mCityDao.getCitiesFilteredByName(newText);
                mainHandler.post(() -> data.completed(mCachedCities));
            }).start();
    }
}

