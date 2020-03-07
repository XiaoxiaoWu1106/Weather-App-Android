package com.brook.wu.weatherapp;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.brook.wu.weatherapp.backend.NetworkProvider;
import com.brook.wu.weatherapp.model.City;
import com.brook.wu.weatherapp.model.WeatherItem;
import com.brook.wu.weatherapp.storage.IStorageProvider;
import com.brook.wu.weatherapp.storage.StorageProvider;
import com.brook.wu.weatherapp.utils.SearchAdapter;
import com.brook.wu.weatherapp.utils.WeatherUtils;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener, WeatherListAdapter.OnClickListener {


    private final List<WeatherItem> mDataSet = new ArrayList<>();
    private final IStorageProvider mStorageProvider = StorageProvider.getStorageProvider();
    private WeatherListAdapter mAdapter;
    private TextView mPlaceholder;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final RecyclerView recyclerList = findViewById(R.id.city_list);

        //set city list adapter
        recyclerList.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new WeatherListAdapter(mDataSet, this);

        //set the view for empty place holder
        mPlaceholder = findViewById(R.id.empty_view_place_holder);
        mAdapter.setEmptyView(mPlaceholder);
        recyclerList.setAdapter(mAdapter);

        //set view for swipe to refresh
        //TODO: do not swipe to refresh when list is empty?
        mSwipeRefreshLayout = findViewById(R.id.swipe_layout);
        //set listener before using the view
        mSwipeRefreshLayout.setOnRefreshListener(this);
        loadData();
    }

    private void loadData() {
        invalidatePlaceholder();
        List<City> storedCities = mStorageProvider.getStoredCities();
        for (City city : storedCities) {
            loadCity(city.getName());
        }
        if (storedCities.size() == 0) {
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    private void invalidatePlaceholder() {
        List<City> storedCities = mStorageProvider.getStoredCities();
        if (storedCities.size() == 0) {
            mPlaceholder.setText("No cities added. Press + to add one");
        }
    }


    final List<String> cityQueries = new ArrayList<>();
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        SearchManager manager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView search = (SearchView) searchItem.getActionView();

        search.setSearchableInfo(manager.getSearchableInfo(getComponentName()));
        search.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
            @Override
            public boolean onSuggestionSelect(int position) {
                return true;
            }

            @Override
            public boolean onSuggestionClick(int position) {
                Cursor cursor= search.getSuggestionsAdapter().getCursor();
                cursor.moveToPosition(position);
                String suggestion =cursor.getString(1);//2 is the index of col containing suggestion name.
                search.setQuery(suggestion,true);//setting suggestion
                return true;
            }
        });
        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                handleAddCity(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //query the cities matching user's input
                cityQueries.clear();
                List<String> worldCitiesFiltered = WeatherUtils.mapCitiesToCityNames(mStorageProvider.getWorldCities(newText));
                WeatherUtils.removeDuplicates(mDataSet,worldCitiesFiltered);
                cityQueries.addAll(worldCitiesFiltered);
                Cursor cursor = WeatherUtils.getCursor(cityQueries);
                search.setSuggestionsAdapter(new SearchAdapter(MainActivity.this, cursor, cityQueries));

                return false;
            }

        });
        return true;
    }

    private void handleAddCity(String query) {
        if (!cityQueries.contains(query.toLowerCase())) {
            Snackbar.make(mSwipeRefreshLayout, R.string.error_city_does_not_exist, Snackbar.LENGTH_LONG).show();
            return;
        }

        if(!WeatherUtils.containsCity(mDataSet, query)) {
            loadCity(query, true);
        } else {
            Snackbar.make(mSwipeRefreshLayout, R.string.error_city_exist, Snackbar.LENGTH_LONG).show();
        }
        cityQueries.clear();
        invalidateOptionsMenu();
        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
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

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        String city = intent.getExtras().getString("user_query");
        handleAddCity(city);

    }

    @Override
    public void onRefresh() {
        //clear the list before reloading
        mDataSet.clear();
        mAdapter.notifyDataSetChanged();
        loadData();
    }

    @Override
    public void onClick(int position) {
        // TODO Show Details from the clicked weather row
    }

    @Override
    public void onLongClick(int position) {
        new AlertDialog.Builder(this)
                .setTitle("Confirm Delete")
                .setMessage("Are you sure you want to delete that item?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        WeatherItem weatherItem = mDataSet.remove(position);
                        mAdapter.notifyDataSetChanged();
                        mStorageProvider.deleteCity(weatherItem.getCity());
                        invalidatePlaceholder();
                    }
                }).setNegativeButton("Cancel", null)
                .show();
    }






//TODO:refactor the following functions to own class


    private void loadCity(String cityName, boolean save) {
        NetworkProvider.getWeather(cityName, new NetworkProvider.Callback() {
            @Override
            public void onComplete(WeatherItem item) {
                Log.d("Pass", item.toString());
                //reset layout
                if (save) mStorageProvider.saveCity(item.getCity());
                mSwipeRefreshLayout.setRefreshing(false);
                mDataSet.add(item);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onError(int errorCode) {
                Log.d("fail", errorCode + "");
                mPlaceholder.setText("Error loading data");
                if (errorCode == 404) {
                    Snackbar.make(mSwipeRefreshLayout, "City not found.", Snackbar.LENGTH_LONG)
                            .show();
                }
            }
        });
    }

    private void loadCity(String cityName) {
        loadCity(cityName, false);
    }


}
