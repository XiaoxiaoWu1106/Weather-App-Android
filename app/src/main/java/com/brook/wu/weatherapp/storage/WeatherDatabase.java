package com.brook.wu.weatherapp.storage;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.brook.wu.weatherapp.MyApplication;
import com.brook.wu.weatherapp.R;
import com.brook.wu.weatherapp.model.City;
import com.brook.wu.weatherapp.model.WeatherItem;
import com.brook.wu.weatherapp.utils.FileUtils;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

@Database(entities = {City.class, WeatherItem.class}, version = 2)
public abstract class WeatherDatabase extends RoomDatabase {

    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_COUNTRY = "country";
    private static final String COLUMN_LAT = "lat";
    private static final String COLUMN_LON = "lon";
    private Handler mainHandler = new Handler(Looper.getMainLooper());

    private static volatile WeatherDatabase INSTANCE;

    public abstract CityDao cityDao();

    public abstract WeatherItemDao weatherItemDao();

    public static WeatherDatabase getInstance() {
        if (INSTANCE == null) {
            synchronized (WeatherDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(MyApplication.getInstance().getApplicationContext(),
                            WeatherDatabase.class, "WeatherAppDatabase.db")
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    public void initWithCityData(DataCallback<Boolean> completion) {
        new Thread(()-> {
            try {
                String jsonString = FileUtils.readRawFileAsString(R.raw.city_list);
                JSONArray jsonArray = new JSONArray(jsonString);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    int cityId = jsonObject.getInt(COLUMN_ID);
                    String cityName = jsonObject.getString(COLUMN_NAME);
                    String cityCountry = jsonObject.getString(COLUMN_COUNTRY);
                    JSONObject coord = jsonObject.getJSONObject("coord");
                    double cityLat = coord.getDouble(COLUMN_LAT);
                    double cityLon = coord.getDouble(COLUMN_LON);
                    Log.v("DBLoading", "inserting city " + cityName);
                    cityDao().saveCity(new City(cityId, cityName, cityCountry, cityLat, cityLon));
                }
                mainHandler.post(()->completion.completed(true));
            } catch (Exception e) {
                e.printStackTrace();
                mainHandler.post(() ->completion.completed(false));
            }
        }).start();

    }
}
