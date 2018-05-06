/*
 * Copyright (C) 2018 Bill Combs
 */

package com.billcombsdevelopment.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.billcombsdevelopment.popularmovies.model.Movie;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.ViewHolder> {

    private Context mContext;
    private List<Movie> mMovieList;

    public MovieAdapter(Context context, List<Movie> movieList) {
        this.mContext = context;
        this.mMovieList = movieList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.movie_list_item, parent, false);

        view.setBackgroundColor(Color.BLACK);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Movie movie = mMovieList.get(position);

        // Load image into ImageView with Picasso
        Picasso.with(mContext).load(movie.getImageUrl())
                .placeholder(R.drawable.film)
                .error(R.drawable.film)
                .fit()
                .noFade()
                .into(holder.movieImageView);
    }

    @Override
    public int getItemCount() {
        return mMovieList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView movieImageView;

        public ViewHolder(View itemView) {
            super(itemView);

            movieImageView = itemView.findViewById(R.id.movie_list_image_iv);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int itemPosition = getAdapterPosition();

            Movie movie = mMovieList.get(itemPosition);

            Intent intent = new Intent(mContext, DetailActivity.class);
            intent.putExtra("movie", movie);

            mContext.startActivity(intent);
        }
    }
}
