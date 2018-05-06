/*
 * Copyright (C) 2018 Bill Combs
 */

package com.billcombsdevelopment.popularmovies;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.billcombsdevelopment.popularmovies.model.Movie;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DetailActivity extends AppCompatActivity {

    private ImageView backgroundImageView;
    private ImageView moviePosterImageView;
    private TextView movieTitleTextView;
    private TextView releaseDateTextView;
    private TextView synopsisTextView;
    private RatingBar ratingBar;

    private Movie mMovie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        // Found at https://stackoverflow.com/questions/4761686/how-to-set-background-color-of-an-activity-to-white-programmatically
        getWindow().getDecorView().setBackgroundColor(Color.BLACK);

        // Get the intent extras
        Intent intent = getIntent();

        backgroundImageView = findViewById(R.id.background_tv);
        moviePosterImageView = findViewById(R.id.movie_poster_tv);
        movieTitleTextView = findViewById(R.id.title_tv);
        releaseDateTextView = findViewById(R.id.release_date_tv);
        synopsisTextView = findViewById(R.id.synopsis_tv);
        ratingBar = findViewById(R.id.rating_bar);

        // Get the movie object from savedInstanceState or the intent
        if (savedInstanceState != null && savedInstanceState.getParcelable("movie") != null) {
            mMovie = savedInstanceState.getParcelable("movie");
        } else {
            mMovie = intent.getParcelableExtra("movie");
        }

        populateUi(mMovie);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("movie", mMovie);
    }

    private void populateUi(Movie movie) {

        String releaseYear = "";
        Date date = null;
        backgroundImageView.setImageDrawable(getResources().getDrawable(R.drawable.cinema));


        // Convert the release date string to a Date object
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            date = dateFormat.parse(movie.getReleaseDate());
        } catch (java.text.ParseException e) {
            Toast.makeText(this,
                    getResources().getString(R.string.date_parse_error), Toast.LENGTH_SHORT).show();
        }

        // Create a calendar object and set its date to the Date object
        Calendar calendar = Calendar.getInstance();
        if (date != null) {
            calendar.setTime(date);
            Integer year = calendar.get(Calendar.YEAR);
            releaseYear = year.toString();
        }

        // Download the poster image
        Picasso.with(this).load(movie.getImageUrl()).fit()
                .placeholder(R.drawable.film)
                .error(R.drawable.film)
                .noFade()
                .into(moviePosterImageView);

        // Populate the TextViews
        movieTitleTextView.setText(movie.getTitle());
        releaseDateTextView.setText(releaseYear);

        synopsisTextView.setText(movie.getSynopsis());

        // Convert userRating String to float and divide by 2 to get the rating out of 5
        float userRating = Float.parseFloat(movie.getUserRating()) / 2;
        ratingBar.setRating(userRating);

    }
}
