package com.brook.wu.weatherapp;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.brook.wu.weatherapp.backend.NetworkProvider;
import com.brook.wu.weatherapp.storage.CityManager;
import com.brook.wu.weatherapp.storage.DataCallback;
import com.brook.wu.weatherapp.utils.FileUtils;
import com.brook.wu.weatherapp.utils.SearchAdapter;
import com.brook.wu.weatherapp.model.WeatherItem;
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

        initAppForFirstTime((completion) -> {
            hideSplashScreen();
            loadData();
        });
    }

    private void initAppForFirstTime(DataCallback<Void> completion) {
        if (FileUtils.isAppInit()) {
            MyApplication.getInstance().database.initWithCityData((success) -> {
                FileUtils.markAppFirstTimeInit();
                completion.completed(null);
            });
        } else {
            completion.completed(null);
        }
    }

    private void hideSplashScreen() {
        findViewById(R.id.splash_screen_ly).setVisibility(View.GONE);
    }

    private void loadData() {
        mPlaceholder.setText(R.string.loading);
        CityManager.getInstance().getUserSavedItems((items) -> {
            setData(items);
            if (items.size() == 0) {
                mPlaceholder.setText(R.string.no_cities_placeholder_text);
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    final List<String> cityQueries = new ArrayList<>();

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        SearchManager manager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        if (manager == null) return false;
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
                Cursor cursor = search.getSuggestionsAdapter().getCursor();
                cursor.moveToPosition(position);
                String suggestion = cursor.getString(1);//2 is the index of col containing suggestion name.
                search.setQuery(suggestion, true);//setting suggestion
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
                CityManager.getInstance().getCitiesByNameFilter(newText, (cities) -> {
                    cityQueries.clear();
                    List<String> worldCitiesFiltered = WeatherUtils.mapCitiesToCityNames(cities);
                    WeatherUtils.removeDuplicates(mDataSet, worldCitiesFiltered);
                    cityQueries.addAll(worldCitiesFiltered);
                    Cursor cursor = WeatherUtils.getCursor(cityQueries);
                    search.setSuggestionsAdapter(new SearchAdapter(MainActivity.this, cursor, cityQueries));
                });
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
        //TODO : Show some loading progress if needed.
        CityManager.getInstance().getCityByName(query, (city) -> {
            if (!WeatherUtils.containsCity(mDataSet, city.getId())) {
                loadCity(query);
            } else {
                Snackbar.make(mSwipeRefreshLayout, R.string.error_city_exist, Snackbar.LENGTH_LONG).show();
            }
            cityQueries.clear();
            invalidateOptionsMenu();
            InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
            View focus = getCurrentFocus();
            if (imm != null && focus != null) {
                imm.hideSoftInputFromWindow(focus.getWindowToken(), 0);
            }
        });
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
        Bundle extras = intent.getExtras();
        if (extras != null) {
            String city = extras.getString("user_query");
            if (city != null) handleAddCity(city);
        }
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
                .setPositiveButton("Yes", (dialog, which) -> {
                    WeatherItem weatherItem = mDataSet.remove(position);
                    mAdapter.notifyDataSetChanged();
                    CityManager.getInstance().deleteWeatherItem(weatherItem,
                            (completed) -> invalidatePlaceholder());
                }).setNegativeButton("Cancel", null)
                .show();
    }


//TODO:refactor the following functions to own class


    private void loadCity(String cityName) {
        NetworkProvider.getWeather(cityName, new NetworkProvider.Callback() {
            private void adapterSync(WeatherItem item) {
                addData(item);
            }

            @Override
            public void onComplete(WeatherItem item) {
                CityManager.getInstance().saveWeatherItem(item, (complted) -> adapterSync(item));
                adapterSync(item);
            }

            @Override
            public void onError(int errorCode) {
                Log.d("fail", errorCode + "");
                mPlaceholder.setText(R.string.error_loading_data);
                if (errorCode == 404) {
                    Snackbar.make(mSwipeRefreshLayout, "City not found.", Snackbar.LENGTH_LONG)
                            .show();
                }
            }
        });
    }

    private void setData(List<WeatherItem> items) {
        mSwipeRefreshLayout.setRefreshing(false);
        mDataSet.clear();
        mDataSet.addAll(items);
        mAdapter.notifyDataSetChanged();
    }

    private void addData(WeatherItem item) {
        mSwipeRefreshLayout.setRefreshing(false);
        mDataSet.add(item);
        mAdapter.notifyDataSetChanged();
    }

    private void invalidatePlaceholder() {
        if (mAdapter.getItemCount() == 0) {
            mPlaceholder.setText(R.string.no_cities_placeholder_text);
        }
    }
}
