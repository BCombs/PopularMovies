/*
 * Copyright (C) 2018 Bill Combs
 */

package com.billcombsdevelopment.popularmovies;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.billcombsdevelopment.popularmovies.model.Movie;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private static Retrofit sRetrofit = null;
    private RecyclerView movieListRecyclerView;
    private TextView networkConnTextView;
    private String mFilterSelected;
    private String mLastFilterSelected;
    private int mScrollPosition = 0;
    private ArrayList<Movie> mMovieList = new ArrayList<>();
    private String BASE_URL = "http://api.themoviedb.org/3/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        networkConnTextView = findViewById(R.id.no_conn_tv);

        if (savedInstanceState != null) {
            mMovieList = savedInstanceState.getParcelableArrayList("movieList");
            mFilterSelected = savedInstanceState.getString("filterSelected");
            mLastFilterSelected = savedInstanceState.getString("lastSelectedFilter");
            mScrollPosition = savedInstanceState.getInt("scrollPosition");

            initRecyclerView();
        } else {
            // Get JSON data with "Most Popular" as the default filter
            mFilterSelected = getResources().getString(R.string.most_popular);

            getJson(mFilterSelected);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelableArrayList("movieList", mMovieList);

        outState.putString("filterSelected", mFilterSelected);

        // Store the previous selected filter
        outState.putString("lastSelectedFilter", mLastFilterSelected);

        // Information found at https://stackoverflow.com/questions/29463560/findfirstvisibleitempositions-doesnt-work-for-recycleview-android/29529952
        mScrollPosition = ((GridLayoutManager) movieListRecyclerView.getLayoutManager()).findFirstVisibleItemPosition();
        outState.putInt("scrollPosition", mScrollPosition);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);

        // Spinner object
        MenuItem item = menu.findItem(R.id.spinner);
        Spinner spinner = (Spinner) item.getActionView();

        // Array to populate the spinner options
        String[] filterOptions = getResources().getStringArray(R.array.filter_options);

        // Adapter for spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                R.layout.spinner, filterOptions);
        spinner.setAdapter(adapter);
        spinner.setSelection(adapter.getPosition(mFilterSelected));

        // Listener for spinner item selection
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                /*
                 * This sets the color of the currently selected filter option
                 * Details were found at
                 * http://tech.chitgoks.com/2015/12/05/how-to-change-color-of-spinner-selected-item-in-android/
                 */
                ((TextView) parent.getChildAt(0))
                        .setTextColor(getResources().getColor(R.color.colorAccent));

                mFilterSelected = parent.getItemAtPosition(position).toString();

                // If they are not equal, the user selected a new option. Reset scroll position
                if (!mLastFilterSelected.equals(mFilterSelected)) {
                    mScrollPosition = 0;
                }
                // Call getJson with the selection that was made
                getJson(mFilterSelected);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        return true;
    }

    /**
     * Checks if there is a network connection
     * Discussed in article at
     * https://stackoverflow.com/questions/1560788/how-to-check-internet-access-on-android-inetaddress-never-times-out
     *
     * @return
     */
    private boolean checkNetworkConnectivity() {
        ConnectivityManager checkConn = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = checkConn.getActiveNetworkInfo();

        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }

    /**
     * Uses Retrofit library to make an HTTP GET request
     */
    private void getJson(String filterOption) {

        if (sRetrofit == null) {
            sRetrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

        MovieServices movieServices = sRetrofit.create(MovieServices.class);

        Call<JsonObject> call;

        // Which filter option does the user have selected
        if (filterOption.equals(getResources().getString(R.string.most_popular))) {
            // This is now the last selected filter option
            mLastFilterSelected = getResources().getString(R.string.most_popular);
            call = movieServices.getPopularMovies();
        } else {
            // This is now the last selected filter option
            mLastFilterSelected = getResources().getString(R.string.top_rated);
            call = movieServices.getTopRatedMovies();
        }

        if (checkNetworkConnectivity()) {
            // If we regained network connectivity
            if (movieListRecyclerView != null &&
                    movieListRecyclerView.getVisibility() == View.GONE) {
                movieListRecyclerView.setVisibility(View.VISIBLE);
            }
            if (networkConnTextView.getVisibility() == View.VISIBLE) {
                networkConnTextView.setVisibility(View.GONE);
            }

            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                    JsonObject jsonObject = response.body();

                    mMovieList = parseJsonData(jsonObject);

                    initRecyclerView();
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    // Display a toast showing the error message
                    Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        } else {
            if (movieListRecyclerView != null) {
                movieListRecyclerView.setVisibility(View.INVISIBLE);
                Log.d("No Connection", "Setting RecyclerView to GONE");
            }
            networkConnTextView.setVisibility(View.VISIBLE);
        }

    }

    /**
     * Parses JSON data to a Movie object and adds to movieList.
     *
     * @param jsonObject
     */
    private ArrayList<Movie> parseJsonData(JsonObject jsonObject) {

        JsonArray results = jsonObject.getAsJsonArray("results");
        ArrayList<Movie> movieList = new ArrayList<>();

        /*
         * Iterate over each element of the results array creating a Movie object of
         * each element. Then add the new Movie object to the movieList
         */
        int movieListLength = results.size();
        for (int i = 0; i < movieListLength; i++) {

            // Get each JsonObject in the array and parse the needed data
            JsonObject movieObject = results.get(i).getAsJsonObject();
            String name = movieObject.get("title").getAsString();
            String imagePath = movieObject.get("poster_path").getAsString();
            String synopsis = movieObject.get("overview").getAsString();
            String releaseDate = movieObject.get("release_date").getAsString();
            String userRating = movieObject.get("vote_average").getAsString();

            // Create the complete image URL
            String imageUrl = "http://image.tmdb.org/t/p/w185/" + imagePath;

            // Create a new Movie
            Movie movie = new Movie(name, imageUrl, synopsis, releaseDate, userRating);

            // Add the movie to the list
            movieList.add(movie);
        }
        return movieList;
    }

    /**
     * Initializes the RecyclerView after the proper data has been received
     */
    private void initRecyclerView() {
        movieListRecyclerView = findViewById(R.id.movie_recyclerview);
        movieListRecyclerView.setLayoutManager(new GridLayoutManager(MainActivity.this, 2));

        // Set the adapter to the RecyclerView and pass in the movies list
        MovieAdapter movieAdapter = new MovieAdapter(getApplicationContext(), mMovieList);
        movieListRecyclerView.setAdapter(movieAdapter);
        movieListRecyclerView.scrollToPosition(mScrollPosition);

        // Check if RecyclerView is visible
        if (movieListRecyclerView.getVisibility() == View.GONE) {
            movieListRecyclerView.setVisibility(View.VISIBLE);
        }
    }
}
