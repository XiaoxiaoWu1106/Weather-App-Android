package com.brook.wu.weatherapp.utils;

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
}
