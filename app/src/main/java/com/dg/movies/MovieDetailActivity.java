package com.dg.movies;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

// Activity to display the movie details to the user
public class MovieDetailActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Build the detail page view
        setContentView(R.layout.activity_movie_detail);

        // Retrieve the detail data from the intent
        MovieDetailsDO movieDetails = getIntent().getParcelableExtra(getString(R.string.movie_details_key));

        TextView title = (TextView) findViewById(R.id.title);
        ImageView poster = (ImageView) findViewById(R.id.poster);
        TextView voteAverage = (TextView) findViewById(R.id.vote_average);
        TextView releaseDate = (TextView) findViewById(R.id.release_date);
        TextView synopsis = (TextView) findViewById(R.id.synopsis);

        // Load the detail data into the views
        title.setText(movieDetails.getTitle());
        Picasso.with(this).load(movieDetails.getPosterPath()).placeholder(R.drawable.movie_poster_placeholder).error(R.drawable.movie_poster_not_available).into(poster);
        voteAverage.setText(movieDetails.getVoteAverage() + getString(R.string.out_of_ten));
        releaseDate.setText(movieDetails.getReleaseDate());
        synopsis.setText(movieDetails.getSynopsis());
    }
}
