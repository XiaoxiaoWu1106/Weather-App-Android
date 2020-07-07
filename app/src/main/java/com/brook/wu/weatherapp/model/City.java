package com.brook.wu.weatherapp.model;

import java.io.Serializable;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class City implements Serializable {

    @NonNull
    @PrimaryKey
    private int id;
    private String name;
    private String country;
    private double lat;
    private double lon;

    public City(int id, String name, String country, double lat, double lon) {
        this.id = id;
        this.name = name;
        this.country = country;
        this.lat = lat;
        this.lon = lon;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCountry() {
        return country;
    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }
}
