package com.brook.wu.weatherapp.storage;

import com.brook.wu.weatherapp.model.City;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public interface CityDao {

    @Query("SELECT * FROM City WHERE name LIKE  '%' || :filter || '%'")
    List<City> getWorldCities(String filter);

    @Query("SELECT * FROM City")

    List<City> getStoredCities();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void saveCity(City city);

    @Query("DELETE FROM City WHERE id = :cityId")
    void deleteCity(int cityId);
}
