package com.brook.wu.weatherapp.utils;

import android.database.Cursor;
import android.database.MatrixCursor;

import com.brook.wu.weatherapp.model.City;
import com.brook.wu.weatherapp.model.WeatherItem;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import androidx.annotation.NonNull;

public class WeatherUtils {
    /**
     * this method is for checking if the given city name is in the weather list already
     * @param weatherList
     * @param cityId
     * @return
     */
    public static boolean containsCity(List<WeatherItem> weatherList, int cityId) {
        for (WeatherItem weatherItem : weatherList) {
            int weatherItemCityId = weatherItem.getCityId();
            if (weatherItemCityId == cityId) return true;
        }
        return false;
    }

    public static List<String> mapCitiesToCityNames(@NonNull List<City> cities) {
        List<String> cityNames = new ArrayList<>();
        for (City city:cities) {
            String cityNameWithCountry = city.getName().toLowerCase() + " " + city.getCountry().toLowerCase();
            if (!cityNames.contains(cityNameWithCountry)) {
                cityNames.add(cityNameWithCountry);
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
        //TODO : THis might need to be fixed...
        /*List<String> userList = new ArrayList<>();
        for(WeatherItem item : weatherItems) {
            userList.add(item.getCity().getName().toLowerCase());
        }
        Iterator<String> iter = suggested.iterator();
        while(iter.hasNext()){
            if(userList.contains(iter.next().toLowerCase())){
                iter.remove();
            }
        }*/
    }
}
