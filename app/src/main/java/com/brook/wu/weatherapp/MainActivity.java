package com.brook.wu.weatherapp;

import android.os.Bundle;

import com.androidnetworking.AndroidNetworking;
import com.brook.wu.weatherapp.backend.NetworkProvider;
import com.brook.wu.weatherapp.model.WeatherItem;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    private final List<WeatherItem> mDataSet = new ArrayList<>();
    private WeatherListAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        final RecyclerView recyclerList = findViewById(R.id.city_list);
        recyclerList.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new WeatherListAdapter(mDataSet);
        recyclerList.setAdapter(mAdapter);

        loadCity("Barcelona");
    }

    private void loadCity(String cityName) {
        NetworkProvider.getWeather(cityName, new NetworkProvider.Callback() {
            @Override
            public void onComplete(WeatherItem item) {
                Log.d("Pass", item.toString());
                mDataSet.add(item);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onError(int errorCode) {
                Log.d("fail", errorCode + "");
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
