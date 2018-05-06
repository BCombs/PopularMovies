/*
 * Copyright (C) 2018 Bill Combs
 */

package com.billcombsdevelopment.popularmovies.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Movie object that stores information about individual movie titles
 */

public class Movie implements Parcelable {

    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {

        @Override
        public Movie createFromParcel(Parcel source) {
            return new Movie(source);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    private String mTitle;
    private String mImageUrl;
    private String mSynopsis;
    private String mReleaseDate;
    private String mUserRating;

    public Movie(String name, String imageUrl, String synopsis, String releaseDate, String userRating) {
        this.mTitle = name;
        this.mImageUrl = imageUrl;
        this.mSynopsis = synopsis;
        this.mReleaseDate = releaseDate;
        this.mUserRating = userRating;
    }

    // Private constructor for Parcel
    private Movie(Parcel source) {
        mTitle = source.readString();
        mImageUrl = source.readString();
        mSynopsis = source.readString();
        mReleaseDate = source.readString();
        mUserRating = source.readString();
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        this.mTitle = title;
    }

    public String getImageUrl() {
        return mImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.mImageUrl = imageUrl;
    }

    public String getSynopsis() {
        return mSynopsis;
    }

    public void setSynopsis(String mSynopsis) {
        this.mSynopsis = mSynopsis;
    }

    public String getReleaseDate() {
        return mReleaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.mReleaseDate = releaseDate;
    }

    public String getUserRating() {
        return mUserRating;
    }

    public void setUserRating(String userRating) {
        this.mUserRating = userRating;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mTitle);
        dest.writeString(mImageUrl);
        dest.writeString(mSynopsis);
        dest.writeString(mReleaseDate);
        dest.writeString(mUserRating);
    }
}
