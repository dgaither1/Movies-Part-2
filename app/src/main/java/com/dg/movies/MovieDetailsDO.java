package com.dg.movies;

import android.os.Parcel;
import android.os.Parcelable;

import ckm.simple.sql_provider.annotation.SimpleSQLColumn;
import ckm.simple.sql_provider.annotation.SimpleSQLTable;

// Parcelable data object to hold movie details data
@SimpleSQLTable(
        table = "favorites",
        provider = "FavoritesProvider")
public class MovieDetailsDO implements Parcelable {

    @SimpleSQLColumn(value = "movieID", primary = true)
    private int movieID;

    @SimpleSQLColumn("title")
    private String title;

    @SimpleSQLColumn("releaseDate")
    private String releaseDate;

    @SimpleSQLColumn("posterPath")
    private String posterPath;

    @SimpleSQLColumn("voteAverage")
    private double voteAverage;

    @SimpleSQLColumn("synopsis")
    private String synopsis;

    @SimpleSQLColumn("trailerKeys")
    private String trailerKeys;

    @SimpleSQLColumn("reviewerNames")
    private String reviewerNames;

    @SimpleSQLColumn("reviewText")
    private String reviewText;


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public double getVoteAverage() {
        return voteAverage;
    }

    public void setVoteAverage(double voteAverage) {
        this.voteAverage = voteAverage;
    }

    public String getSynopsis() {
        return synopsis;
    }

    public void setSynopsis(String synopsis) {
        this.synopsis = synopsis;
    }

    public int getMovieID() {
        return movieID;
    }

    public void setMovieID(int movieID) {
        this.movieID = movieID;
    }

    public String getTrailerKeys() {
        return trailerKeys;
    }

    public void setTrailerKeys(String trailerKeys) {
        this.trailerKeys = trailerKeys;
    }

    public String getReviewerNames() {
        return reviewerNames;
    }

    public void setReviewerNames(String reviewerNames) {
        this.reviewerNames = reviewerNames;
    }

    public String getReviewText() {
        return reviewText;
    }

    public void setReviewText(String reviewText) {
        this.reviewText = reviewText;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(movieID);
        dest.writeString(title);
        dest.writeString(releaseDate);
        dest.writeString(posterPath);
        dest.writeDouble(voteAverage);
        dest.writeString(synopsis);
        dest.writeString(trailerKeys);
        dest.writeString(reviewerNames);
        dest.writeString(reviewText);

    }

    public MovieDetailsDO() {

    }

    public MovieDetailsDO(Parcel in) {
        movieID = in.readInt();
        title = in.readString();
        releaseDate = in.readString();
        posterPath = in.readString();
        voteAverage = in.readDouble();
        synopsis = in.readString();
        trailerKeys = in.readString();
        reviewerNames = in.readString();
        reviewText = in.readString();
    }

    public static final Parcelable.Creator<MovieDetailsDO> CREATOR = new Parcelable.Creator<MovieDetailsDO>() {

        @Override
        public MovieDetailsDO createFromParcel(Parcel source) {
            return new MovieDetailsDO(source);
        }

        @Override
        public MovieDetailsDO[] newArray(int size) {
            return new MovieDetailsDO[size];
        }
    };



}
