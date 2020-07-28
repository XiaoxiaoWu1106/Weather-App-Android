package com.brook.wu.weatherapp;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.brook.wu.weatherapp.model.WeatherItem;
import com.brook.wu.weatherapp.storage.CityManager;
import com.squareup.picasso.Picasso;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class WeatherListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<WeatherItem> weatherList;
    private TextView placeHolder;
    private OnClickListener mOnClickListener;

    public WeatherListAdapter(List<WeatherItem> list, @NonNull OnClickListener listener) {
        weatherList = list;
        mOnClickListener = listener;
        registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                boolean shouldShow = weatherList == null || weatherList.size() == 0;
                WeatherListAdapter.this.placeHolder.setVisibility(shouldShow ? View.VISIBLE : View.GONE);
            }
        });
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_weather_row, parent, false);
        return new WeatherViewHolder(view, mOnClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        WeatherViewHolder vHo = (WeatherViewHolder) holder;
        WeatherItem item = weatherList.get(position);
        CityManager.getInstance().getCityById(item.getCityId(), (city) ->
                vHo.city.setText(city != null ? city.getName() : "")
        );
        vHo.temp.setText(String.format("%s °C", item.getTemp()));
        Picasso.get()
                .load(item.getIconUrl())
                .fit()
                .into(vHo.icon);
    }

    @Override
    public int getItemCount() {
        return weatherList != null ? weatherList.size() : 0;
    }

    private static class WeatherViewHolder extends RecyclerView.ViewHolder {
        TextView city;
        TextView temp;
        ImageView icon;

        WeatherViewHolder(@NonNull View itemView, OnClickListener listener) {
            super(itemView);
            itemView.setOnClickListener((view) -> listener.onClick(getAdapterPosition()));
            itemView.setOnLongClickListener((view) -> {
                listener.onLongClick(getAdapterPosition());
                return true;
            });
            city = itemView.findViewById(R.id.city_name);
            temp = itemView.findViewById(R.id.city_temp);
            icon = itemView.findViewById(R.id.weather_icon);

        }
    }

    public void setEmptyView(TextView placeHolder) {
        this.placeHolder = placeHolder;
    }


    public interface OnClickListener {
        void onClick(int position);

        void onLongClick(int position);
    }
}
