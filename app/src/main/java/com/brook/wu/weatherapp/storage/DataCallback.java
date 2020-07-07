package com.brook.wu.weatherapp.storage;

public interface DataCallback<T> {
    void completed(T obj);
}
