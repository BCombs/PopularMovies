/*
 * Copyright (C) 2018 Bill Combs
 */

package com.billcombsdevelopment.popularmovies;

import android.content.Context;
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

    private List<Movie> mMovieList;
    private OnItemClickListener mListener;
    private Context mContext;

    public MovieAdapter(Context context, List<Movie> movieList, OnItemClickListener listener) {
        this.mContext = context;
        this.mMovieList = movieList;
        this.mListener = listener;
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
        Movie movie = mMovieList.get(position);
        holder.bind(movie, mListener);


    }

    @Override
    public int getItemCount() {
        return mMovieList.size();
    }

    /**
     * Interface to handle Item clicks
     * Found in https://antonioleiva.com/recyclerview-listener/
     * and Udacity Classroom Lesson 4: RecyclerView 20. Responding to Item Clicks
     */
    public interface OnItemClickListener {
        void onItemClick(Movie movie);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView movieImageView;

        public ViewHolder(View itemView) {
            super(itemView);

            movieImageView = itemView.findViewById(R.id.movie_list_image_iv);
        }

        public void bind(final Movie movie, final OnItemClickListener listener) {

            // Load image into ImageView with Picasso
            Picasso.with(mContext).load(movie.getImageUrl())
                    .placeholder(R.drawable.film)
                    .error(R.drawable.film)
                    .fit()
                    .noFade()
                    .into(movieImageView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(movie);
                }
            });
        }
    }
}
