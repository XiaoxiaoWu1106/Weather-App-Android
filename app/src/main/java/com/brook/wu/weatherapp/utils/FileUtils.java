package com.brook.wu.weatherapp.utils;

import android.content.SharedPreferences;

import com.brook.wu.weatherapp.MyApplication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;

import androidx.annotation.RawRes;

public class FileUtils {
    public static String readRawFileAsString(@RawRes int rawId) {
        InputStream is = MyApplication.getInstance().getResources().openRawResource(rawId);
        Writer writer = new StringWriter();
        char[] buffer = new char[1024];

        try (Reader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            int n;
            while ((n = reader.read(buffer)) != -1) {
                writer.write(buffer, 0, n);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

       return writer.toString();
    }

    public static void markAppFirstTimeInit() {
        SharedPreferences sharedPref = MyApplication.getInstance().getSharedPreferences();
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean("weather_app_first_time_init", true);
        editor.apply();
    }

    public static boolean isAppInit() {
        SharedPreferences sharedPref = MyApplication.getInstance().getSharedPreferences();
        return sharedPref.getBoolean("weather_app_first_time_init", false);
    }
}
