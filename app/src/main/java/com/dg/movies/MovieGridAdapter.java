package com.dg.movies;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import com.squareup.picasso.Picasso;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

// Adapter to tie the movie data into the grid layout
public class MovieGridAdapter extends RecyclerView.Adapter<MovieGridAdapter.ViewHolder> {

    private JSONArray movies;
    private Context context;

    public MovieGridAdapter(JSONArray movies) {
        this.movies = movies;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int i) {

        context = parent.getContext();

        // Create a new ImageView
        ImageView poster = new ImageView(context);
        // Set the poster's size
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        poster.setLayoutParams(params);
        poster.setScaleType(ImageView.ScaleType.CENTER_CROP);

        ViewHolder vh = new ViewHolder(poster, new MovieGridAdapter.ViewHolder.MoviePosterOnClickListener() {
            public void posterClicked(int index) {
                Intent myIntent = new Intent(context, MovieDetailActivity.class);
                myIntent.putExtra(context.getString(R.string.movie_details_key), buildDetailData(index));
                context.startActivity(myIntent);
            }
        });
        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int i) {
        // Update the view holder index and then load the imageview with the poster
        viewHolder.index = i;
        Picasso.with(context).load(constructImageUrl(i)).placeholder(R.drawable.movie_poster_placeholder).error(R.drawable.movie_poster_not_available).into(viewHolder.imageView);
    }

    @Override
    public int getItemCount() {
        if(movies != null) {
            return movies.length();
        } else {
            return 0;
        }

    }

    // Find the poster URL at index
    private String constructImageUrl(int index) {
        String posterUrl = null;

        try {
            JSONObject individualMovie = movies.getJSONObject(index);

            posterUrl = context.getString(R.string.base_image_url) + individualMovie.getString(context.getString(R.string.poster_key));

        } catch(JSONException e) {
            Log.e(MovieGridAdapter.class.getName(), context.getString(R.string.poster_parse_error));
        }

        return posterUrl;
    }

    // Build the detail data object to launch the detail activity
    private MovieDetailsDO buildDetailData(int index) {

        MovieDetailsDO movieDetails = new MovieDetailsDO();

        try {
            JSONObject individualMovie = movies.getJSONObject(index);

            movieDetails.setTitle(individualMovie.getString(context.getString(R.string.title_key)));
            movieDetails.setPosterPath(context.getString(R.string.base_image_url) + individualMovie.getString(context.getString(R.string.poster_key)));
            movieDetails.setReleaseDate(individualMovie.getString(context.getString(R.string.release_date_key)));
            movieDetails.setSynopsis(individualMovie.getString(context.getString(R.string.overview_key)));
            movieDetails.setVoteAverage(individualMovie.getDouble(context.getString(R.string.vote_average_key)));

        } catch(JSONException e) {
            Log.e(MovieGridAdapter.class.getName(), context.getString(R.string.detail_parse_error));
        }

        return movieDetails;
    }

    // Viewholder class representing a poster view object
    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public ImageView imageView;
        public MoviePosterOnClickListener listener;
        public int index;

        public ViewHolder(ImageView v, MoviePosterOnClickListener clickListener) {
            super(v);
            listener = clickListener;
            imageView = v;
            imageView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            listener.posterClicked(index);
        }

        public static interface MoviePosterOnClickListener {
            public void posterClicked(int index);
        }
    }
}
