package com.brook.wu.weatherapp.backend;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.brook.wu.weatherapp.model.WeatherItem;

import org.json.JSONObject;

public class NetworkProvider {

    private final static String APIKEY= "4a7d8cd523ca070116a6afa4b84aeb02";
    private final static String URL = "https://api.openweathermap.org/data/2.5/weather";//?q=${CITY}&APPID=${APIKEY}"

    public static void getWeather(String city, final Callback callback){
        AndroidNetworking.get(URL)
                .addQueryParameter("q", city)
                .addQueryParameter("APPID", APIKEY)
                .addQueryParameter("units", "metric")
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        int cod = response.optInt("cod", 500);
                        if (cod != 200) {
                            callback.onError(cod);
                        } else {
                            WeatherItem item = WeatherItem.fromJSON(response);
                            if (item != null){
                                callback.onComplete(item);
                            } else {
                                callback.onError(-10);
                            }
                        }
                    }
                    @Override
                    public void onError(ANError error) {
                        // handle error
                        callback.onError(error.getErrorCode());
                    }
                });
    }

    public interface Callback {
        void onComplete(WeatherItem item);
        void onError(int errorCode);
    }

}
