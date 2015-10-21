package com.dg.movies;

import android.os.Parcel;
import android.os.Parcelable;

// Parcelable data object to hold movie details data
public class MovieDetailsDO implements Parcelable {

    private String title;
    private String releaseDate;
    private String posterPath;
    private double voteAverage;
    private String synopsis;


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


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(releaseDate);
        dest.writeString(posterPath);
        dest.writeDouble(voteAverage);
        dest.writeString(synopsis);
    }

    public MovieDetailsDO() {

    }

    public MovieDetailsDO(Parcel in) {
        title = in.readString();
        releaseDate = in.readString();
        posterPath = in.readString();
        voteAverage = in.readDouble();
        synopsis = in.readString();
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
