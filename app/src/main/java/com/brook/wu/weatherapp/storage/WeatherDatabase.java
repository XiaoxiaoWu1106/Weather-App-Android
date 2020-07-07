package com.brook.wu.weatherapp.storage;

import android.content.Context;

import com.brook.wu.weatherapp.model.City;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {City.class}, version = 1)
public abstract class WeatherDatabase extends RoomDatabase {
    private static volatile WeatherDatabase INSTANCE;

    public abstract CityDao cityDao();

    public static WeatherDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (WeatherDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            WeatherDatabase.class, "WeatherAppDatabase.db")
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
