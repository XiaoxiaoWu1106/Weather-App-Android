package com.brook.wu.weatherapp.storage;

import com.brook.wu.weatherapp.model.City;
import com.brook.wu.weatherapp.model.WeatherItem;

import java.util.List;

public interface IStorageProvider {
    void init();
    List<City> getWorldCities(String filter);
    List<City> getStoredCities();
    void saveCity(City city);
    void deleteCity(City city);
}
