package com.brook.wu.weatherapp.model;

import org.json.JSONObject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class WeatherItem {

    private String city;
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
            item.city = json.getString("name");
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

    public String getCity() {
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
