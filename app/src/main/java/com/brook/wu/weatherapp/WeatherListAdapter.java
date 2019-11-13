package com.brook.wu.weatherapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.brook.wu.weatherapp.model.WeatherItem;
import com.squareup.picasso.Picasso;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class WeatherListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<WeatherItem> weatherList;

    public WeatherListAdapter (List<WeatherItem> list){
        weatherList = list;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_weather_row, parent,false);
        return new WeatherViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        WeatherViewHolder vHo = (WeatherViewHolder) holder;
        WeatherItem item = weatherList.get(position);
        vHo.city.setText(item.getCity());
        vHo.temp.setText(item.getTemp()+" °C");
        Picasso.get()
                .load(item.getIconUrl())
                .fit()
                .into(vHo.icon);
    }

    @Override
    public int getItemCount() {
        return weatherList != null ? weatherList.size() : 0;
    }

    private static class WeatherViewHolder extends RecyclerView.ViewHolder{
        TextView city;
        TextView temp;
        ImageView icon;

        WeatherViewHolder(@NonNull View itemView) {

            super(itemView);
            city = itemView.findViewById(R.id.city_name);
            temp = itemView.findViewById(R.id.city_temp);
            icon = itemView.findViewById(R.id.weather_icon);

        }
    }
}
