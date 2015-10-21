package com.dg.movies;

import android.content.res.Configuration;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

// Main Movie Activity that will show a grid of movie posters that the user can select
public class MainActivity extends ActionBarActivity implements HttpConnectionManager.HttpConnectionDelegate {

    private boolean isCallInProgress = false;
    private JSONArray movies;
    private ProgressBar progressBar;
    private RecyclerView grid;
    private String sortType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // By default, sort by most popular
        sortType = getString(R.string.sort_popular_desc);

        // Restore previous state if it exists
        if(savedInstanceState != null) {
            isCallInProgress = savedInstanceState.getBoolean(getString(R.string.call_in_progress_key));
            sortType = savedInstanceState.getString(getString(R.string.sort_type_key));
        }

        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        grid = (RecyclerView) findViewById(R.id.poster_grid);
        grid.setHasFixedSize(true);

        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            grid.setLayoutManager(new GridLayoutManager(this, 3));
        } else {
            grid.setLayoutManager(new GridLayoutManager(this, 2));
        }

        grid.addItemDecoration(new MovieGridItemDecoration(1));

        retrieveMovies();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Add action bar items
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Action bar sorting items.  Kick off a new retrieve call when selected
        int id = item.getItemId();

        if (id == R.id.sort_popular) {

            sortType = getString(R.string.sort_popular_desc);
            retrieveMovies();
            return true;
        } else if (id == R.id.sort_ratings) {

            sortType = getString(R.string.sort_ratings_desc);
            retrieveMovies();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // Call to retrieve the list of movies from the moviedb API
    private void retrieveMovies() {
        HttpConnectionManager.setDelegate(this);

        if(!isCallInProgress) {
            grid.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
            HttpConnectionManager.performGet(getString(R.string.movies_api_url) + getString(R.string.sort_by_param) + sortType + getString(R.string.vote_count_param) + getString(R.string.api_key_param) + getString(R.string.movies_api_key));
            isCallInProgress = true;
        }
    }

    // Parse the response from the moviedb API
    private void parseMovieResponseJson(String responseJson) {

        try {
            JSONObject parser = new JSONObject(responseJson);
            JSONArray movies = parser.getJSONArray(getString(R.string.results_key));

            if(movies != null && movies.length() > 0) {
                this.movies = movies;
            }

        } catch(JSONException e) {
            Log.e(MainActivity.class.getName(), getString(R.string.moviedb_parse_error));
        }

        updateRecyclerView();
    }

    private void updateRecyclerView() {
        grid.setAdapter(new MovieGridAdapter(movies));
    }

    // Call back on the http request
    @Override
    public void httpGetResponse(String responseJson) {
        if(responseJson != null) {
            parseMovieResponseJson(responseJson);
        }
        isCallInProgress = false;
        progressBar.setVisibility(View.GONE);
        grid.setVisibility(View.VISIBLE);
    }


    // Save state
    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(getString(R.string.call_in_progress_key), isCallInProgress);
        outState.putString(getString(R.string.sort_type_key), sortType);

        HttpConnectionManager.setDelegate(null);

        super.onSaveInstanceState(outState);
    }
}
