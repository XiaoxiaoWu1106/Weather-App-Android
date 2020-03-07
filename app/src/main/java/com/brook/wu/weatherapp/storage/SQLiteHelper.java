package com.brook.wu.weatherapp.storage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.brook.wu.weatherapp.MyApplication;
import com.brook.wu.weatherapp.R;
import com.brook.wu.weatherapp.model.City;
import com.brook.wu.weatherapp.utils.FileUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;

//create database helper class by extending from SQLiteOpenHelper
public class SQLiteHelper extends SQLiteOpenHelper {
    private final static String DATABASE_NAME = "com.brook.wu.weatherapp.storage.SQL";
    private final static int SQL_VERSION = 1;

    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_COUNTRY = "country";
    private static final String COLUMN_LAT = "lat";
    private static final String COLUMN_LON = "lon";
    private static SQLiteHelper instance;

    static final String ALL_CITIES_QUERY = "select * from cities where name like ? AND LENGTH(name) > 0 order by name";
    static final String STORED_CITIES_QUERY = "select * from weather order by name";
    static final String TABLE_NAME_CITIES = "cities";
    static final String TABLE_NAME_WEATHER = "weather";

    static SQLiteHelper getInstance() {
        if (instance == null) {
            instance = new SQLiteHelper(MyApplication.getInstance());
        }
        return  instance;
    }

    SQLiteHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, SQL_VERSION);
    }

    //onCreate will only be called when the first time it is requested
    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d("DBLoading", "Load database start on create start");
        db.execSQL(
                "create table weather " +
                        "(id integer primary key, name text,country text,lat numeric, lon numeric)"
        );
        //create table
        db.execSQL(
                "create table cities " +
                        "(id integer primary key, name text,country text,lat numeric, lon numeric)"
        );

        //create index for column "name", this will speed up query by column
        db.execSQL("CREATE INDEX city_by_name " +
                "ON cities(name);");

        db.execSQL("CREATE INDEX weather_by_city_name " +
                "ON weather(name);");

        new Thread(()-> {
            try {
                fillDatabase(db);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }).start();
        Log.d("DBLoading", "Load database start on create finish");
    }

    //FileUtils will convert json to string, this function convert string to json and store the information
    //in to the table "cities"
    private void fillDatabase(SQLiteDatabase db) throws JSONException {
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
            Log.v("DBLoading", "inserting city");
            insertCityImpl(TABLE_NAME_CITIES, new City(cityId, cityName, cityCountry, cityLat, cityLon), db);
        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }


    private void insertCityImpl(String tableName, City city, SQLiteDatabase db) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_ID, city.getId());
        contentValues.put(COLUMN_NAME, city.getName());
        contentValues.put(COLUMN_COUNTRY, city.getCountry());
        contentValues.put(COLUMN_LAT, city.getLat());
        contentValues.put(COLUMN_LON, city.getLon());
        db.insert(tableName, null, contentValues);
    }


    List<City> getCities(String queryString, String queryFilter) {
        SQLiteDatabase db = this.getReadableDatabase();
        List<City> cities;
        String[] strings = queryFilter == null || queryFilter.isEmpty() ? null : new String[]{queryFilter + "%"};
        try (Cursor res = db.rawQuery(queryString, strings)) {
            res.moveToFirst();
            cities = new ArrayList<>();
            while (!res.isAfterLast()) {
                int cityId = res.getInt(res.getColumnIndex(COLUMN_ID));
                String cityName = res.getString(res.getColumnIndex(COLUMN_NAME));
                String cityCountry = res.getString(res.getColumnIndex(COLUMN_COUNTRY));
                double cityLat = res.getDouble(res.getColumnIndex(COLUMN_LAT));
                double cityLon = res.getDouble(res.getColumnIndex(COLUMN_LON));
                cities.add(new City(cityId, cityName, cityCountry, cityLat, cityLon));
                res.moveToNext();
            }
        }
        return cities;
    }

    void insertCity(String tableName, City city) {
        SQLiteDatabase db = getWritableDatabase();
        insertCityImpl(tableName, city, db);
    }

    void deleteCity(String tableName, City cityName) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(tableName,"name like ?",new String[]{cityName.getName()});
    }
}
