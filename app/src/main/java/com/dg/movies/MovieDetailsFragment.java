package com.dg.movies;

import android.app.Activity;
import android.app.Fragment;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;

/**
 * Created by DG on 11/16/15.
 */
public class MovieDetailsFragment extends Fragment implements HttpConnectionManager.HttpConnectionDelegate {

    private LinearLayout detailLayout;
    private ProgressBar progressBar;
    private MovieDetailsDO movie;
    private JSONArray trailers;
    private JSONArray reviews;
    private LinearLayout trailersList;
    private LinearLayout reviewsList;
    private int numberOfCallsCompleted = 0;
    private String trailerKeys = null;
    private String reviewerNames = null;
    private String reviewTextData = null;
    private ScrollView movieDetailLayout;
    private HttpConnectionManager httpConnectionManager = new HttpConnectionManager();
    private static final String MOVIE_BUNDLE_KEY = "movie";
    private MovieDetailCallback callBackListener;

    public static MovieDetailsFragment newInstance(MovieDetailsDO movie) {

        MovieDetailsFragment movieDetailsFragment = new MovieDetailsFragment();

        Bundle args = new Bundle();
        args.putParcelable(MOVIE_BUNDLE_KEY, movie);
        movieDetailsFragment.setArguments(args);

        return movieDetailsFragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        httpConnectionManager.setDelegate(this);

        if(getArguments() != null) {
            movie = getArguments().getParcelable(MOVIE_BUNDLE_KEY);
        }
        if(savedInstanceState != null) {
            movie = savedInstanceState.getParcelable(MOVIE_BUNDLE_KEY);
        }
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        // Build the detail page view
        movieDetailLayout = (ScrollView) inflater.inflate(R.layout.fragment_movie_detail, null);

        detailLayout = (LinearLayout) movieDetailLayout.findViewById(R.id.detail_main);
        progressBar = (ProgressBar) movieDetailLayout.findViewById(R.id.progress_bar);

        trailersList = (LinearLayout) movieDetailLayout.findViewById(R.id.trailers_list);
        reviewsList = (LinearLayout) movieDetailLayout.findViewById(R.id.reviews_list);

        movieDetailLayout.setVisibility(View.INVISIBLE);

        if(movie != null) {
            showDetails(movie);
        }

        return movieDetailLayout;
    }

    public void showDetails(MovieDetailsDO movie) {
        if(movie == null) {
            movieDetailLayout.setVisibility(View.INVISIBLE);
        } else {

            // Reset state from previous movie
            trailersList.removeAllViews();
            reviewsList.removeAllViews();


            this.movie = movie;
            final MovieDetailsDO movieDetails = movie;

            movieDetailLayout.setVisibility(View.VISIBLE);

            final ImageButton favoritesButton = (ImageButton) movieDetailLayout.findViewById(R.id.favorites_button);
            TextView title = (TextView) movieDetailLayout.findViewById(R.id.title);
            ImageView poster = (ImageView) movieDetailLayout.findViewById(R.id.poster);
            TextView voteAverage = (TextView) movieDetailLayout.findViewById(R.id.vote_average);
            TextView releaseDate = (TextView) movieDetailLayout.findViewById(R.id.release_date);
            TextView synopsis = (TextView) movieDetailLayout.findViewById(R.id.synopsis);

            if (FavoritesDBHelper.isFavorite(this.movie)) {
                favoritesButton.setSelected(true);
            } else {
                favoritesButton.setSelected(false);
            }

            // Load the detail data into the views
            title.setText(this.movie.getTitle());
            Picasso.with(getActivity()).load(this.movie.getPosterPath()).placeholder(R.drawable.movie_poster_placeholder).error(R.drawable.movie_poster_not_available).into(poster);
            voteAverage.setText(this.movie.getVoteAverage() + getString(R.string.out_of_ten));
            releaseDate.setText(this.movie.getReleaseDate());
            synopsis.setText(this.movie.getSynopsis());

            // Listener to favoite/unfavorite
            favoritesButton.setOnClickListener(new Button.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (FavoritesDBHelper.isFavorite(movieDetails)) {
                        FavoritesDBHelper.removeFavorite(movieDetails);
                        favoritesButton.setSelected(false);
                    } else {
                        FavoritesDBHelper.addFavorite(movieDetails);
                        favoritesButton.setSelected(true);
                    }
                }
            });

            // Retrieve trailers and reviews
            if (movie.getTrailerKeys() == null) {
                retrieveTrailers();
            } else {
                populateTrailerViewsFromDB();

                if (movie.getReviewerNames() != null) {
                    populateReviewsFromDB();
                }
            }
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // Activities containing this fragment must implement its callbacks.
        if (!(activity instanceof MovieDetailCallback)) {
            throw new IllegalStateException(activity.getString(R.string.wrong_activity_attached));
        }

        callBackListener = (MovieDetailCallback) activity;
    }

    // Call to retrieve the list of trailers
    private void retrieveTrailers() {

        detailLayout.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        httpConnectionManager.performGet(getString(R.string.nondiscover_api_url) + this.movie.getMovieID() + getString(R.string.videos_url) + "?api_key=" + getString(R.string.movies_api_key));

    }

    // Call to retrieve the list of reviews
    private void retrieveReviews() {
        httpConnectionManager.performGet(getString(R.string.nondiscover_api_url) + this.movie.getMovieID() + getString(R.string.reviews_url) + "?api_key=" + getString(R.string.movies_api_key));
    }

    @Override
    public void httpGetResponse(String responseJson) {
        numberOfCallsCompleted++;

        if(numberOfCallsCompleted == 1) {
            if (responseJson != null) {
                parseTrailersResponse(responseJson);
            }

            retrieveReviews();
        } else if (numberOfCallsCompleted == 2) {
            if(responseJson != null) {
                parseReviewsResponse(responseJson);
            }

            progressBar.setVisibility(View.GONE);
            detailLayout.setVisibility(View.VISIBLE);
            numberOfCallsCompleted = 0;
        }
    }

    private void parseTrailersResponse(String responseJson) {

        try {
            JSONObject parser = new JSONObject(responseJson);
            JSONArray trailers = parser.getJSONArray(getActivity().getString(R.string.results_json_key));

            if(trailers != null && trailers.length() > 0) {
                this.trailers = trailers;

            }

        } catch(JSONException e) {
            Log.e(MovieDetailActivity.class.getName(), getString(R.string.moviedb_parse_error));
        }

        if(this.trailers != null) {
            populateTrailerViews();
        }
    }

    private void populateTrailerViews() {
        trailerKeys = null;

        for(int i = 0; i < trailers.length(); i++) {

            final int index = i;
            RelativeLayout trailerView = (RelativeLayout) getActivity().getLayoutInflater().inflate(R.layout.trailer_view, null);

            TextView trailerNumberField = (TextView) trailerView.findViewById(R.id.trailer_number_field);
            trailerNumberField.setText("Trailer " + Integer.toString(index + 1));

            try {
                String videoKey = trailers.getJSONObject(index).getString(getActivity().getString(R.string.trailer_key_json_key));

                if(i == 0) {
                    callBackListener.updateShareIntent(getActivity().getString(R.string.youtube_prefix) + videoKey);
                }

                if(trailerKeys == null) {
                    trailerKeys = videoKey;
                } else {
                    trailerKeys = trailerKeys + "|" + videoKey;
                }
            } catch(JSONException e) {
                Log.e(MovieDetailActivity.class.getName(), getString(R.string.moviedb_parse_error));
            }

            trailerView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String videoID = null;
                    try {
                        videoID = trailers.getJSONObject(index).getString(getActivity().getString(R.string.trailer_key_json_key));

                    } catch (JSONException e) {
                        Log.e(MovieDetailActivity.class.getName(), getString(R.string.moviedb_parse_error));
                    }

                    try {

                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(getActivity().getString(R.string.youtube_uri_prefix) + videoID));
                        intent.putExtra(getActivity().getString(R.string.video_id_key), videoID);
                        startActivity(intent);
                    } catch (ActivityNotFoundException e) {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        Uri.parse(getActivity().getString(R.string.youtube_prefix) + videoID);
                        startActivity(intent);
                    }
                }
            });

            trailersList.addView(trailerView);
        }

        this.movie.setTrailerKeys(trailerKeys);
    }

    private void populateTrailerViewsFromDB() {
        final List<String> trailerKeys = Arrays.asList(this.movie.getTrailerKeys().split("\\|"));

        for(int i = 0; i < trailerKeys.size(); i++) {

            final int index = i;

            RelativeLayout trailerView = (RelativeLayout) getActivity().getLayoutInflater().inflate(R.layout.trailer_view, null);

            TextView trailerNumberField = (TextView) trailerView.findViewById(R.id.trailer_number_field);
            trailerNumberField.setText("Trailer " + Integer.toString(i + 1));

            trailerView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String videoID = trailerKeys.get(index);

                    try {

                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(getActivity().getString(R.string.youtube_uri_prefix) + videoID));
                        intent.putExtra(getActivity().getString(R.string.video_id_key), videoID);
                        startActivity(intent);
                    } catch (ActivityNotFoundException e) {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        Uri.parse(getActivity().getString(R.string.youtube_prefix) + videoID);
                        startActivity(intent);
                    }
                }
            });

            trailersList.addView(trailerView);
        }

    }

    private void parseReviewsResponse(String responseJson) {
        try {
            JSONObject parser = new JSONObject(responseJson);
            JSONArray reviews = parser.getJSONArray(getActivity().getString(R.string.results_json_key));

            if(reviews != null && reviews.length() > 0) {
                this.reviews = reviews;

            }

        } catch(JSONException e) {
            Log.e(MovieDetailActivity.class.getName(), getString(R.string.moviedb_parse_error));
        }

        if(this.reviews != null) {
            populateReviewViews();
        }
    }

    private void populateReviewViews() {
        reviewerNames = null;
        reviewTextData = null;

        for(int i = 0; i < reviews.length(); i++) {
            RelativeLayout reviewView = (RelativeLayout) getActivity().getLayoutInflater().inflate(R.layout.review_view, null);

            TextView reviewerNameView = (TextView) reviewView.findViewById(R.id.reviewer_name);
            TextView reviewTextView = (TextView) reviewView.findViewById(R.id.review_text);
            try {
                String reviewAuthor = reviews.getJSONObject(i).getString(getActivity().getString(R.string.author_json_key));
                String reviewTextItem = reviews.getJSONObject(i).getString(getActivity().getString(R.string.content_json_key));

                reviewerNameView.setText(reviewAuthor);
                reviewTextView.setText(reviewTextItem);

                if(reviewerNames == null) {
                    reviewerNames = reviewAuthor;
                } else {
                    reviewerNames = reviewerNames + "|" + reviewAuthor;
                }

                if(reviewTextData == null) {
                    reviewTextData = reviewTextItem;
                } else {
                    reviewTextData = reviewTextData + "|" + reviewTextItem;
                }

            } catch(JSONException e) {
                Log.e(MovieDetailActivity.class.getName(), getString(R.string.moviedb_parse_error));
            }

            reviewsList.addView(reviewView);
        }

        this.movie.setReviewerNames(reviewerNames);
        this.movie.setReviewText(reviewTextData);
    }

    private void populateReviewsFromDB() {
        List<String> reviewerNames = Arrays.asList(this.movie.getReviewerNames().split("\\|"));
        List<String> reviewContent = Arrays.asList(this.movie.getReviewText().split("\\|"));

        for(int i = 0; i < reviewerNames.size(); i++) {
            RelativeLayout reviewView = (RelativeLayout) getActivity().getLayoutInflater().inflate(R.layout.review_view, null);

            TextView reviewerNameView = (TextView) reviewView.findViewById(R.id.reviewer_name);
            TextView reviewTextView = (TextView) reviewView.findViewById(R.id.review_text);

            reviewerNameView.setText(reviewerNames.get(i));
            reviewTextView.setText(reviewContent.get(i));

            reviewsList.addView(reviewView);
        }
    }

    public interface MovieDetailCallback {
        public void updateShareIntent(String youtubeURL);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if(movie != null) {
            outState.putParcelable(MOVIE_BUNDLE_KEY, movie);
        }

    }
}
