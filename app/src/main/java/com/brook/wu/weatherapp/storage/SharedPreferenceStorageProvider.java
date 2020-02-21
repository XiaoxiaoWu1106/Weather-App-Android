package com.brook.wu.weatherapp.storage;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;

import com.brook.wu.weatherapp.MyApplication;
import com.brook.wu.weatherapp.model.City;
import com.brook.wu.weatherapp.model.WeatherItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import androidx.collection.ArraySet;

class SharedPreferenceStorageProvider implements IStorageProvider {
    private final String KEY_CITY_IDS_LIST = "shared_pref_city_ids_list";

    private static final String KEY_CITY_NAME = "shared_pref_%s_name";
    private static final String KEY_CITY_COUNTRY = "shared_pref_%s_country";
    private static final String KEY_CITY_LON = "shared_pref_%s_lon";
    private static final String KEY_CITY_LAT = "shared_pref_%s_lat";

    //TODO ; Add version control

    @Override
    public void init() {
        //do nothing
    }

    @Override
    public List<City> getWorldCities(String filter) {
        return new ArrayList<>(0);
    }

    @Override
    public List<City> getStoredCities() {
        SharedPreferences prefs = MyApplication.getInstance().getSharedPreferences();
        Set<String> cityIds = prefs.getStringSet(KEY_CITY_IDS_LIST, new ArraySet<>());

        List<City> cities = new ArrayList<>();
        for (String cityId : cityIds) {
            String name = prefs.getString(String.format(KEY_CITY_NAME, cityId), "");
            String country = prefs.getString(String.format(KEY_CITY_COUNTRY, cityId), "");
            double lat = Double.valueOf(prefs.getString(String.format(KEY_CITY_LAT, cityId), ""));
            double lon = Double.valueOf(prefs.getString(String.format(KEY_CITY_LON, cityId), ""));
            cities.add(new City(Integer.valueOf(cityId), name, country, lat, lon));
        }
        return cities;
    }

    @Override
    public void saveCity(City city) {
        updateCity(city, false);
    }

    @Override
    public void deleteCity(City city) {
        updateCity(city, true);
    }

    @SuppressLint("ApplySharedPref")
    private void updateCity(City city, boolean isDelete) {
        SharedPreferences prefs = MyApplication.getInstance().getSharedPreferences();
        List<City> cities = getStoredCities();
        Set<String> listOfCityIds = prefs.getStringSet("shared_pref_city_ids_list", new ArraySet<>());
        String cityId = String.valueOf(city.getId());
        if (isDelete && listOfCityIds.contains(cityId)) {
            listOfCityIds.remove(cityId);
        } else if (!isDelete && !listOfCityIds.contains(cityId)) {
            listOfCityIds.add(cityId);
        } else {
            return;
        }

        SharedPreferences.Editor edit = prefs.edit();
        edit.putStringSet(KEY_CITY_IDS_LIST, new ArraySet<>(listOfCityIds));
        if (isDelete) {
            edit.remove(String.format(KEY_CITY_NAME, cityId))
                    .remove(String.format(KEY_CITY_COUNTRY, cityId))
                    .remove(String.format(KEY_CITY_LAT, cityId))
                    .remove(String.format(KEY_CITY_LON, cityId));
        } else {
            edit.putString(String.format(KEY_CITY_NAME, cityId), city.getName())
                    .putString(String.format(KEY_CITY_COUNTRY, cityId), city.getCountry())
                    .putString(String.format(KEY_CITY_LAT, cityId), String.valueOf(city.getLat()))
                    .putString(String.format(KEY_CITY_LON, cityId), String.valueOf(city.getLon()));
        }
        edit.commit();


    }
}
