package com.dg.movies;

import android.app.Activity;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.sqlite.SQLiteConstraintException;

import java.util.List;

//
// This helper class will perform database operations so they don't have to live in the application code
//
public class FavoritesDBHelper {

    private static Activity context;

    public static void setContext(Activity activity) {
        context = activity;
    }

    public static void deleteAllFavorites() {
        if(context != null) {
            context.getContentResolver().delete(FavoritesTable.CONTENT_URI, null, null);
        }
    }

    public static void removeFavorite(MovieDetailsDO movie) {
        if(context != null) {
            context.getContentResolver().delete(FavoritesTable.CONTENT_URI, "movieID=" + movie.getMovieID(), null);
        }
    }

    public static List<MovieDetailsDO> getAllFavorites() {
        if(context != null) {
            Cursor cursor = context.getContentResolver().query(FavoritesTable.CONTENT_URI, null, null, null, null);

            return FavoritesTable.getRows(cursor, true);
        }
        return null;
    }

    public static int numberOfFavorites() {
        if(context != null) {
            Cursor cursor = context.getContentResolver().query(FavoritesTable.CONTENT_URI, null, null, null, null);
            List<MovieDetailsDO> favoritesList = FavoritesTable.getRows(cursor, true);

            return favoritesList.size();
        }
        return 0;
    }

    public static void addFavorite(MovieDetailsDO movie) {
        if(context != null) {

            try {
                context.getContentResolver().insert(FavoritesTable.CONTENT_URI, FavoritesTable.getContentValues(movie, true));
            } catch(SQLiteConstraintException e) {

            }
        }
    }

    public static boolean isFavorite(MovieDetailsDO movie) {
        if(context != null) {
            MovieDetailsDO result = null;
            Cursor cursor = context.getContentResolver().query(FavoritesTable.CONTENT_URI, null, "movieID=" + movie.getMovieID(), null, null);
            try {
                result = FavoritesTable.getRow(cursor, true);
            } catch (CursorIndexOutOfBoundsException e) {
                return false;
            }

            if(result != null) {
                return true;
            }
        }
        return false;
    }
}
