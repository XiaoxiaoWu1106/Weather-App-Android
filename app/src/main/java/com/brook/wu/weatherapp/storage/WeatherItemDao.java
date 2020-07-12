package com.brook.wu.weatherapp.storage;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.brook.wu.weatherapp.model.City;
import com.brook.wu.weatherapp.model.WeatherItem;

import java.util.List;

@Dao
public interface WeatherItemDao {

    @Query("SELECT * FROM WeatherItem")
    List<WeatherItem> getAllWeatherItems();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void saveWeatherItem(WeatherItem item);

    @Query("DELETE FROM WeatherItem WHERE cityid = :cityId")
    void deleteWeatherItem(int cityId);

}
