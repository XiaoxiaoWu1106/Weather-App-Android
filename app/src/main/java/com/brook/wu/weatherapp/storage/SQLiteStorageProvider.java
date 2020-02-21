package com.brook.wu.weatherapp.storage;

import com.brook.wu.weatherapp.model.City;
import com.brook.wu.weatherapp.model.WeatherItem;

import java.util.List;

class SQLiteStorageProvider implements IStorageProvider{
    @Override
    public void init() {
        new Thread(()->SQLiteHelper.getInstance().getWritableDatabase()).start();
    }

    @Override
    public List<City> getWorldCities(String filter) {
        return SQLiteHelper.getInstance().getCities(filter);
    }

    @Override
    public List<City> getStoredCities() {
        return null;
    }


    @Override
    public void saveCity(City cityName) {
        SQLiteHelper.getInstance().insertCity(cityName);
    }

    @Override
    public void deleteCity(City cityName) {
        SQLiteHelper.getInstance().deleteCity(cityName);

    }
}
