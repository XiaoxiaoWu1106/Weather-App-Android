package com.brook.wu.weatherapp.utils;

import com.brook.wu.weatherapp.model.City;

import org.json.JSONObject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class WeatherItem {

    private City city;
    private double temp;
    private double minTemp;
    private double maxTemp;
    private String iconCode;

    private WeatherItem() {
        //private constructor
    }

    @Nullable
    public static WeatherItem fromJSON(JSONObject json) {
        try {
            JSONObject jsonObject = json.getJSONObject("main");
            WeatherItem item = new WeatherItem();
            int cityId = json.getInt("id");
            String cityName = json.getString("name");
            String cityCountry = json.getJSONObject("sys").getString("country");
            double lat = json.getJSONObject("coord").getDouble("lat");
            double lon = json.getJSONObject("coord").getDouble("lon");
            item.city = new City(cityId,cityName,cityCountry,lat,lon);
            item.temp = jsonObject.getDouble("temp");
            item.minTemp = jsonObject.getDouble("temp_min");
            item.maxTemp = jsonObject.getDouble("temp_max");
            item.iconCode = json.getJSONArray("weather")
                    .getJSONObject(0)
                    .getString("icon");
            return item;
        } catch (Exception e) {
            return null;
        }

    }

    public City getCity() {
        return city;
    }

    public double getTemp() {
        return temp;
    }

    public double getMinTemp() {
        return minTemp;
    }

    public double getMaxTemp() {
        return maxTemp;
    }

    public String getIconCode() {
        return iconCode;
    }

    @NonNull
    @Override
    public String toString() {
        return city + " , temp " + temp;
    }

    public String getIconUrl() {
       return "https://openweathermap.org/img/wn/" + getIconCode() + "@2x.png";
    }
}
