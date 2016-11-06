package com.piros.lucian.popularmovies.data;


import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.util.Log;
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

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import static com.piros.lucian.popularmovies.data.MovieContract.MovieEntry.buildFilteredMoviesUri;

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
                    Vector<ContentValues> vContentValue = new Vector();

                    // first delete movies from database
                    int deleted = context.getContentResolver().delete(
                        MovieContract.MovieEntry.buildFilteredMoviesUri(MovieContract.FILTER_POPULAR),
                            null, // leaving "columns" null just returns all the columns.
                            null  // cols for "where" clause
                    );

                    JSONObject jsonResponse = new JSONObject(response);
                    JSONArray movies = jsonResponse.getJSONArray("results");
                    for (int loop = 0; loop < movies.length(); ++loop) {
                        JSONObject movie = movies.optJSONObject(loop);
                        ContentValues contentValues = new ContentValues();
                        contentValues.put(MovieContract.MovieEntry.COLUMN_TITLE, movie.getString("original_title"));
                        contentValues.put(MovieContract.MovieEntry.COLUMN_SYNOPSIS, movie.getString("overview"));
                        contentValues.put(MovieContract.MovieEntry.COLUMN_IMAGE_THUMBNAIL_PATH, movie.getString("poster_path"));
                        contentValues.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, movie.getString("release_date"));
                        contentValues.put(MovieContract.MovieEntry.COLUMN_USER_RATING, movie.getString("vote_average"));
                        contentValues.put(MovieContract.MovieEntry.COLUMN_FAVOURITE, 0);
                        contentValues.put(MovieContract.SortEntry.COLUMN_SORT_CRITERIA, MovieContract.FILTER_POPULAR);
                        // Movie m = new Movie(movie);
                        //movieAdapter.add(m);
                        vContentValue.add(contentValues);
                    }

                    // insert into database
                    ContentValues[] contentValuesArray = new ContentValues[vContentValue.size()];
                    vContentValue.toArray(contentValuesArray);
                    int noOfInsertedValues = context.getContentResolver().bulkInsert(buildFilteredMoviesUri(MovieContract.FILTER_POPULAR), contentValuesArray);

                    Log.d(LOG_TAG, "Number of inserted values: " + noOfInsertedValues);

                    // get movies back so we can download the thumbnails
                    // A cursor is your primary interface to the query results.
                    Cursor cursor = context.getContentResolver().query(
                            MovieContract.MovieEntry.CONTENT_URI/*buildFilteredMoviesUri(MovieContract.FILTER_POPULAR)*/,
                            null, // leaving "columns" null just returns all the columns.
                            null, // cols for "where" clause
                            null, // values for "where" clause
                            null/*MovieContract.SortEntry.COLUMN_INDEX + " ASC" */ // sort order == by DATE ASCENDING
                    );

                    int cc = cursor.getCount();

                    // and let's make sure they match the ones we created
                    cursor.moveToFirst();
                    int count = 1;
                    while (!cursor.isLast()) {
                        String movieTitle = (String)cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_TITLE));
                        MovieTarget movieTarget = new MovieTarget(cursor.getLong(cursor.getColumnIndex(MovieContract.MovieEntry._ID)), movieTitle);
                        if(!picassoMovieTargets.containsKey(movieTitle)) {
                            picassoMovieTargets.put(movieTitle, movieTarget); // make sure we keep a refference to movie target as Picasso only works with weak refferences
                        }
                        // load bitmap into MovieTarget class
                        Picasso.with(context)
                                .load(BuildConfig.THEMOVIEDB_IMAGE_BASEURL + "/" + cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_IMAGE_THUMBNAIL_PATH)))
                                .placeholder(R.drawable.loading_image)
                                .error(R.drawable.connectionerror)
                                .into(movieTarget);
                        cursor.moveToNext();
                        Log.d(LOG_TAG, "Ask image for line: " + "(" + count++ + ") " + cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_TITLE)));
                        Log.d(LOG_TAG, "Further info: " + BuildConfig.THEMOVIEDB_IMAGE_BASEURL + "/" + cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_IMAGE_THUMBNAIL_PATH)));
                    }
                    cursor.close();
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
     * @param movie Movie for which image should be fetched
     * @param imageView ImageView where fetched imaged will be pushed into
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
        Picasso.with(context)
                .load(BuildConfig.THEMOVIEDB_IMAGE_BASEURL + "/" + movie.getMoviePoster())
                .placeholder(R.drawable.loading_image)
                .error(R.drawable.connectionerror)
                .into(movieTarget);
    }

    /**
     * Target class to be used with Picasso
     * On bitmap loaded - the provided bitmap will be saved within Movie class so we can use it in MovieDetails view
     * and ImageView
     */
    private class MovieTarget implements Target {
        private Movie movie;
        private ImageView moviePosterImageView;
        private long _id;
        private String movieTitle;

        public MovieTarget(Movie movie, ImageView moviePosterImageView) {
            this.movie = movie;
            this.moviePosterImageView = moviePosterImageView;
        }

        public MovieTarget(long _id, String movieTitle) {
            this._id = _id;
            this.movieTitle = movieTitle;
        }

        @Override
        public void onBitmapLoaded(Bitmap bitmap, com.squareup.picasso.Picasso.LoadedFrom from) {
            // moviePosterImageView.setImageBitmap(bitmap);
            //movie.setMovieBitmap(bitmap);

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            byte[] img = bos.toByteArray();

            ContentValues updateValues = new ContentValues();
            updateValues.put(MovieContract.MovieEntry.COLUMN_IMAGE_THUMBNAIL, img);

            context.getContentResolver().update(
                    MovieContract.MovieEntry.CONTENT_URI, updateValues, MovieContract.MovieEntry._ID + "= ?",
                    new String[]{Long.toString(_id)});

            // no need to keep the refference any longer
            picassoMovieTargets.remove(movieTitle);
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {
            // load a Bitmap showing there is a network error
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            Bitmap bitmap  = BitmapFactory.decodeResource(context.getResources(),
                    R.drawable.connectionerror);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            byte[] img = bos.toByteArray();

            ContentValues updateValues = new ContentValues();
            updateValues.put(MovieContract.MovieEntry.COLUMN_IMAGE_THUMBNAIL, img);

            context.getContentResolver().update(
                    MovieContract.MovieEntry.CONTENT_URI, updateValues, MovieContract.MovieEntry._ID + "= ?",
                    new String[]{Long.toString(_id)});
        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {

        }
    }
}
