package com.piros.lucian.popularmovies.data;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.piros.lucian.popularmovies.BuildConfig;
import com.piros.lucian.popularmovies.Movie;
import com.piros.lucian.popularmovies.R;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Data provider singleton used to fetch movie information from MovieDB database.
 * Movie list is fetched using Volley, Movie poster is fetched using Picasso
 *
 * @author Lucian Piros
 * @version 1.0
 */
public class MovieDataProvider {
    private final String LOG_TAG = MovieDataProvider.class.getSimpleName();
    private final String VOLLEY_TAG = MovieDataProvider.class.getSimpleName();

    private static MovieDataProvider movieDataProvider = null;

    // target custom adapter
    private ArrayAdapter<Movie> movieAdapter;
    private Context context;

    // Picasso accepts only weak references make - so we save them to make sure application works correctly
    private Map<String, MovieTarget> picassoMovieTargets;

    // Volley request queue
    RequestQueue queue;

    /**
     * Private constructor
     *
     * @param context - application context
     */
    private MovieDataProvider(Context context) {
        this.queue = Volley.newRequestQueue(context);
        this.picassoMovieTargets = new HashMap<>();
    }

    /**
     * Factory method - return singleton instance
     *
     * @param context - application context
     * @return Singleton instance
     */
    public static synchronized MovieDataProvider getInstance(Context context) {
        if (movieDataProvider == null) {
            movieDataProvider = new MovieDataProvider(context);
        }

        // make sure the right context is use every time
        movieDataProvider.context = context;

        return movieDataProvider;
    }

    /**
     * Setup the array adapter where results will be saved
     *
     * @param movieAdapter Target ArrayAdapter
     */
    public void hookMovieAdapter(ArrayAdapter<Movie> movieAdapter) {
        this.movieAdapter = movieAdapter;
    }

    /**
     * Fetch movies from TheMovieDB database.  Use Volley to handle and manage network requests.
     * Movies are fetched based on sort type.
     * <p/>
     * Note: Volley is caching network data for a period of time - hence if phone is loosing network connection
     * you will still see application displaying data.
     * Once the cache expires and network is down there will be an error dialog on the network connectivity
     *
     * @param sortType sort type (most popular / top rated)
     */
    public void fetchMovies(int sortType) {
        // first cancel any outstanding request
        queue.cancelAll(VOLLEY_TAG);

        // Success response listener
        // on success return populate the ArrayAdapter with received data
        Response.Listener<String> responseListener = new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                try {
                    movieAdapter.clear();
                    JSONObject jsonResponse = new JSONObject(response);
                    JSONArray movies = jsonResponse.getJSONArray("results");
                    for (int loop = 0; loop < movies.length(); ++loop) {
                        JSONObject movie = movies.optJSONObject(loop);
                        Movie m = new Movie(movie);
                        movieAdapter.add(m);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        };

        // Error response listener
        // Display an AlertDialog with an error message
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                new AlertDialog.Builder(context)
                        .setTitle(context.getResources().getString(R.string.network_error_alertdialog_title))
                        .setMessage(context.getResources().getString(R.string.network_error_alertdialog_body))
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        };

        MovieDBRequest moviewDBRequest = new MovieDBRequest(sortType, responseListener, errorListener);
        moviewDBRequest.setTag(VOLLEY_TAG);
        queue.add(moviewDBRequest);
    }

    /**
     * Fetch movie poster. Once movie poster is fetched, bitmap is loaded both on provided ImageView and Movie
     *
     * @param movie
     * @param imageView
     */
    public void fetchMoviePoster(Movie movie, ImageView imageView) {
        MovieTarget movieTarget = new MovieTarget(movie, imageView);
        // as Picasso accept weak refferences make sure this is saved
        // to avoid beeing garbage collected
        if (picassoMovieTargets.containsKey(movie.getOriginalTitle())) {
            picassoMovieTargets.remove(movie.getOriginalTitle());
        }
        picassoMovieTargets.put(movie.getOriginalTitle(), movieTarget);

        // load bitmap into MovieTarget class
        Picasso.with(context).load(BuildConfig.THEMOVIEDB_IMAGE_BASEURL + "/" + movie.getMoviePoster()).into(movieTarget);
    }

    /**
     * Target class to be used with Picasso
     * On bitmap loaded - the provided bitmap will be saved within Movie class so we can use it in MovieDetails view
     * and ImageView
     */
    private class MovieTarget implements Target {
        private Movie movie;
        private ImageView moviePosterImageView;

        public MovieTarget(Movie movie, ImageView moviePosterImageView) {
            this.movie = movie;
            this.moviePosterImageView = moviePosterImageView;
        }

        @Override
        public void onBitmapLoaded(Bitmap bitmap, com.squareup.picasso.Picasso.LoadedFrom from) {
            moviePosterImageView.setImageBitmap(bitmap);
            movie.setMovieBitmap(bitmap);
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {
            // load a Bitmap showing there is a network error
            moviePosterImageView.setImageResource(R.drawable.connectionerror);
        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {

        }
    }
}
