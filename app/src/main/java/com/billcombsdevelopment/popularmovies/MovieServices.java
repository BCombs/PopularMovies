/*
 * Copyright (C) 2018 Bill Combs
 */

package com.billcombsdevelopment.popularmovies;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.GET;

public interface MovieServices {

    String popularMovies = "movie/popular?api_key=" + BuildConfig.API_KEY;
    String topRatedMovies = "movie/top_rated?api_key=" + BuildConfig.API_KEY;

    @GET(popularMovies)
    Call<JsonObject> getPopularMovies();

    @GET(topRatedMovies)
    Call<JsonObject> getTopRatedMovies();
}
