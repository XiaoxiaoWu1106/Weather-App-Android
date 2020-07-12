package com.brook.wu.weatherapp.model;

import org.json.JSONObject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class WeatherItem {

    @PrimaryKey
    private int cityId;
    private double temp;
    private double minTemp;
    private double maxTemp;
    private String iconCode;

    public WeatherItem() {

    }

    public WeatherItem(int cityId, double temp, double minTemp, double maxTemp, String iconCode) {
        this.cityId = cityId;
        this.temp = temp;
        this.minTemp = minTemp;
        this.maxTemp = maxTemp;
        this.iconCode = iconCode;
    }

    @Nullable
    public static WeatherItem fromJSON(JSONObject json) {
        try {
            JSONObject jsonObject = json.getJSONObject("main");
            WeatherItem item = new WeatherItem();
            item.cityId = json.getInt("id");
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

    public int getCityId() {
        return cityId;
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
        return cityId + " , temp " + temp;
    }

    public String getIconUrl() {
        return "https://openweathermap.org/img/wn/" + getIconCode() + "@2x.png";
    }

    public void setCityId(int cityId) {
        this.cityId = cityId;
    }

    public void setTemp(double temp) {
        this.temp = temp;
    }

    public void setMinTemp(double minTemp) {
        this.minTemp = minTemp;
    }

    public void setMaxTemp(double maxTemp) {
        this.maxTemp = maxTemp;
    }

    public void setIconCode(String iconCode) {
        this.iconCode = iconCode;
    }
}
