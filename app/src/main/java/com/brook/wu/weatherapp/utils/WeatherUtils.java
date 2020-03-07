package com.brook.wu.weatherapp.utils;

import android.database.Cursor;
import android.database.MatrixCursor;

import com.brook.wu.weatherapp.model.City;
import com.brook.wu.weatherapp.model.WeatherItem;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import androidx.annotation.NonNull;

public class WeatherUtils {
    /**
     * this method is for checking if the given city name is in the weather list already
     * @param weatherList
     * @param cityName
     * @return
     */
    public static boolean containsCity(List<WeatherItem> weatherList, String cityName) {
        if (cityName == null || cityName.isEmpty()) return false;
        for (WeatherItem weatherItem : weatherList) {
            City city = weatherItem.getCity();
            if (city != null && city.getName().equalsIgnoreCase(cityName)) return true;
        }
        return false;
    }

    public static List<String> mapCitiesToCityNames(@NonNull List<City> cities) {
        List<String> cityNames = new ArrayList<>();
        for (City city:cities) {
            if (!cityNames.contains(city.getName().toLowerCase())) {
                cityNames.add(city.getName().toLowerCase());
            }
        }
        return cityNames;
    }

    public static Cursor getCursor(List<String> cityQueries) {
        String[] columns = new String[] { "_id", "text" };
        Object[] temp = new Object[] { 0, "default" };
        MatrixCursor cursor = new MatrixCursor(columns);
        for(int i = 0; i < cityQueries.size(); i++) {
            temp[0] = i;
            temp[1] = cityQueries.get(i);
            cursor.addRow(temp);
        }
        return cursor;
    }

    public static void removeDuplicates(List<WeatherItem> weatherItems, List<String> suggested) {
        List<String> userList = new ArrayList<>();
        for(WeatherItem item : weatherItems) {
            userList.add(item.getCity().getName().toLowerCase());
        }
        Iterator<String> iter = suggested.iterator();
        while(iter.hasNext()){
            if(userList.contains(iter.next().toLowerCase())){
                iter.remove();
            }
        }
    }
}
