package com.dg.movies;

import android.app.Activity;
import android.app.Fragment;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import org.json.JSONArray;

import java.util.ArrayList;

// Main Movie Activity that will show a grid of movie posters that the user can select
public class MovieSelectionFragment extends Fragment {

    private ProgressBar progressBar;
    private RecyclerView grid;
    private MovieSelectionCallback callBackListener;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        RelativeLayout movieSelectionLayout = (RelativeLayout) inflater.inflate(R.layout.fragment_movie_selection, null);

        progressBar = (ProgressBar) movieSelectionLayout.findViewById(R.id.progress_bar);
        grid = (RecyclerView) movieSelectionLayout.findViewById(R.id.poster_grid);
        grid.setHasFixedSize(true);

        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            grid.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        } else {
            grid.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        }

        grid.addItemDecoration(new MovieGridItemDecoration(1));

        retrieveMovies();

        return movieSelectionLayout;
    }


    // Call to retrieve the list of movies from the moviedb API
    public void retrieveMovies() {
        grid.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);

        callBackListener.retrieveMovies();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // Activities containing this fragment must implement its callbacks.
        if (!(activity instanceof MovieSelectionCallback)) {
            throw new IllegalStateException(activity.getString(R.string.wrong_activity_attached));
        }

        callBackListener = (MovieSelectionCallback) activity;
    }

    //  Call to update the state of the recycler view
    public void updateRecyclerView(JSONArray movies, boolean twoPane) {
        grid.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
        grid.setAdapter(new MovieGridAdapter(movies, twoPane, getActivity()));
    }

    //  Call to update the state of the recycler view
    public void updateRecyclerView(ArrayList<MovieDetailsDO> movies, boolean twoPane) {
        grid.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
        grid.setAdapter(new MovieGridAdapter(movies, twoPane, getActivity()));
    }

    public interface MovieSelectionCallback {
        public void retrieveMovies();
    }
}
