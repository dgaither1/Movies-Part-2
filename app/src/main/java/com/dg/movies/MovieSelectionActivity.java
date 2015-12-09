package com.dg.movies;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by DG on 11/15/15.
 *
 * The MovieSelectionActivity is the main activity of the app.  This will setup the movie grid and set up master/detail views if needed
 */
public class MovieSelectionActivity extends ActionBarActivity implements HttpConnectionManager.HttpConnectionDelegate, MovieSelectionFragment.MovieSelectionCallback, MovieDetailsFragment.MovieDetailCallback {

    private boolean twoPane = false;
    private String sortType;
    private MovieSelectionFragment movieSelectionFragment;
    private MovieDetailsFragment movieDetailsFragment;
    private boolean favoritesMode = false;
    private HttpConnectionManager httpConnectionManager = new HttpConnectionManager();
    private MenuItem shareItem;
    private Intent sharingIntent;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Setup our delegate callbacks for the Favorites database and the http connection manager
        FavoritesDBHelper.setContext(this);
        httpConnectionManager.setDelegate(this);

        // Default the sort type to sort by popularity
        sortType = getString(R.string.sort_popular_desc);

        // Add the first fragment that contains the grid of movies to select from
        movieSelectionFragment = new MovieSelectionFragment();
        FragmentTransaction addMovieSelectionFragment = getFragmentManager().beginTransaction();
        addMovieSelectionFragment.add(R.id.movie_selection_container, movieSelectionFragment).commit();

        //  If we are on a tablet, enter two pane mode
        if (findViewById(R.id.movie_detail_container) != null) {
            twoPane = true;

            // Load the details fragment on the right side of the layout
            movieDetailsFragment = new MovieDetailsFragment();
            getFragmentManager().beginTransaction().add(R.id.movie_detail_container, movieDetailsFragment).commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Add action bar items
        getMenuInflater().inflate(R.menu.menu_main, menu);

        shareItem = menu.getItem(0);
        disableShare();

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Action bar sorting items.  Kick off a new retrieve call when selected.  updateDetailFragment(null) is called to reset the detail page if we are on tablet
        int id = item.getItemId();

        if (id == R.id.sort_popular) {
            sortType = getString(R.string.sort_popular_desc);
            movieSelectionFragment.retrieveMovies();
            favoritesMode = false;
            updateDetailFragment(null);
            return true;
        } else if (id == R.id.sort_ratings) {
            sortType = getString(R.string.sort_ratings_desc);
            movieSelectionFragment.retrieveMovies();
            favoritesMode = false;
            updateDetailFragment(null);
            return true;
        } else if (id == R.id.show_favorites) {
            movieSelectionFragment.updateRecyclerView((ArrayList<MovieDetailsDO>)FavoritesDBHelper.getAllFavorites(), twoPane);
            favoritesMode = true;
            updateDetailFragment(null);
            return true;
        } else if(id == R.id.share_action) {
            launchShare();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(favoritesMode && movieSelectionFragment != null && movieSelectionFragment.isVisible()) {
            movieSelectionFragment.updateRecyclerView((ArrayList<MovieDetailsDO>)FavoritesDBHelper.getAllFavorites(), twoPane);
        }
    }

    @Override
    // Retrieve the movies by making a call to the Http Connection Manager
    public void retrieveMovies() {
        httpConnectionManager.performGet(getString(R.string.movies_api_url) + getString(R.string.sort_by_param) + sortType + getString(R.string.vote_count_param) + getString(R.string.api_key_param) + getString(R.string.movies_api_key));
    }

    @Override
    // Handle the response from the Http Connection Manager
    public void httpGetResponse(String responseJson) {
        if(responseJson != null) {

            parseMovieResponse(responseJson);
        } else {
            Toast.makeText(this, getString(R.string.showing_favorites), Toast.LENGTH_SHORT).show();
            movieSelectionFragment.updateRecyclerView((ArrayList<MovieDetailsDO>)FavoritesDBHelper.getAllFavorites(), twoPane);
        }
    }

    // Parse the response from the moviedb API
    private void parseMovieResponse(String responseJson) {

        JSONArray movies = null;

        try {
            JSONObject parser = new JSONObject(responseJson);
            movies = parser.getJSONArray(getString(R.string.results_key));

        } catch(JSONException e) {
            Log.e(MovieSelectionFragment.class.getName(), getString(R.string.moviedb_parse_error));
        }

        movieSelectionFragment.updateRecyclerView(movies, twoPane);
    }

    public void updateDetailFragment(MovieDetailsDO movie) {
        if(twoPane && movieDetailsFragment != null && movieDetailsFragment.isAdded()) {
            if(movie == null) {
                disableShare();
            } else {
                enableShare();
            }
            movieDetailsFragment.showDetails(movie);
        }
    }

    private void disableShare() {
        shareItem.setVisible(false);
        shareItem.setEnabled(false);
    }

    private void enableShare() {
        shareItem.setVisible(true);
        shareItem.setEnabled(true);
    }

    private void launchShare() {
        startActivity(Intent.createChooser(sharingIntent, getString(R.string.share_chooser)));
    }

    @Override
    public void updateShareIntent(String youtubeURL) {
        sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType(getString(R.string.share_type));
        String shareBody = getString(R.string.share_body) + youtubeURL;
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, getString(R.string.share_subject));
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
    }
}
