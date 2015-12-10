package com.dg.movies;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

// Adapter to tie the movie data into the grid layout
public class MovieGridAdapter extends RecyclerView.Adapter<MovieGridAdapter.ViewHolder> {

    private Context context;
    private MovieSelectionActivity activity;
    private ArrayList<MovieDetailsDO> movies;
    private boolean twoPane;

    public MovieGridAdapter(ArrayList<MovieDetailsDO> movies, boolean twoPane, Activity activity) {
        this.movies = movies;
        this.twoPane = twoPane;
        this.activity = (MovieSelectionActivity) activity;
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

        ViewHolder vh = new ViewHolder(poster, new ViewHolder.MoviePosterOnClickListener() {
            public void posterClicked(int index) {
                if(activity != null && twoPane) {
                    activity.updateDetailFragment(movies.get(index));
                } else {
                    Intent myIntent = new Intent(context, MovieDetailActivity.class);
                    myIntent.putExtra(context.getString(R.string.movie_details_key), movies.get(index));
                    context.startActivity(myIntent);
                }
            }
        });
        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int i) {
        // Update the view holder index and then load the imageview with the poster
        viewHolder.index = i;
        Picasso.with(context).load(movies.get(i).getPosterPath()).placeholder(R.drawable.movie_poster_placeholder).error(R.drawable.movie_poster_not_available).into(viewHolder.imageView);
    }

    @Override
    public int getItemCount() {
        if(movies == null) {
            return 0;
        } else {
            return movies.size();
        }

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
