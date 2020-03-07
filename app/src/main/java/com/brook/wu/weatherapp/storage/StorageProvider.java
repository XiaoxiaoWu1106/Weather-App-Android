package com.brook.wu.weatherapp.storage;

public class StorageProvider {
    //Determine which storage provider will be used
    public static IStorageProvider getStorageProvider() {
        //Object o return either SharedPreference or SQLite storage provider depends on class name
        //this should never be null
        String className = "com.brook.wu.weatherapp.storage.SQLiteStorageProvider";
        try {
            Object o = Class.forName(className).newInstance();
            return (IStorageProvider) o;
        } catch (IllegalAccessException | ClassNotFoundException | InstantiationException e) {
            e.printStackTrace();
        }
        throw new RuntimeException("There is something wrong with the Class Name");
    }
}
