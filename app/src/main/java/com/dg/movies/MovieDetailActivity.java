package com.dg.movies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

// Activity to display the movie details to the user
public class MovieDetailActivity extends ActionBarActivity implements MovieDetailsFragment.MovieDetailCallback {

    private MovieDetailsDO movieDetails;
    private Intent sharingIntent;
    private MovieDetailsFragment movieDetailsFragment;

    // Load the detail fragment and send in the movie details
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Build the detail page view
        setContentView(R.layout.activity_movie_detail);

        // Retrieve the detail data from the intent
        movieDetails = getIntent().getParcelableExtra(getString(R.string.movie_details_key));

        if(savedInstanceState != null) {
            movieDetailsFragment = (MovieDetailsFragment) getFragmentManager().getFragment(savedInstanceState, "details");
        } else {
            movieDetailsFragment = MovieDetailsFragment.newInstance(movieDetails);

            getFragmentManager().beginTransaction().add(R.id.movie_details_container, movieDetailsFragment).commit();
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Add action bar items
        getMenuInflater().inflate(R.menu.menu_detail, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Action bar sorting items.  Kick off a new retrieve call when selected.  updateDetailFragment(null) is called to reset the detail page if we are on tablet
        int id = item.getItemId();

        if(id == R.id.share_action) {
            launchShare();
            return true;
        }

        return super.onOptionsItemSelected(item);
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


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if(movieDetailsFragment != null && movieDetailsFragment.isAdded()) {
            getFragmentManager().putFragment(outState, "details", movieDetailsFragment);
        }
    }
}
