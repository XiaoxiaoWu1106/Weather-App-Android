package com.brook.wu.weatherapp.storage;

import com.brook.wu.weatherapp.model.City;

import java.util.List;

class SQLiteStorageProvider implements IStorageProvider{
    @Override
    public void init() {
        SQLiteHelper.getInstance().getWritableDatabase();
    }

    @Override
    public List<City> getWorldCities(String filter) {
        return SQLiteHelper.getInstance().getCities(SQLiteHelper.ALL_CITIES_QUERY,filter);
    }

    @Override
    public List<City> getStoredCities() {
        return SQLiteHelper.getInstance().getCities(SQLiteHelper.STORED_CITIES_QUERY,"");
    }


    @Override
    public void saveCity(City cityName) {
        SQLiteHelper.getInstance().insertCity(SQLiteHelper.TABLE_NAME_WEATHER,cityName);
    }

    @Override
    public void deleteCity(City cityName) {
        SQLiteHelper.getInstance().deleteCity(SQLiteHelper.TABLE_NAME_WEATHER,cityName);

    }
}
