package com.piros.lucian.popularmovies.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.AlertDialog;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.piros.lucian.popularmovies.BuildConfig;
import com.piros.lucian.popularmovies.R;
import com.piros.lucian.popularmovies.data.MovieContract;
import com.piros.lucian.popularmovies.sync.volley.VolleyMovieDBStringRequest;
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
 * Sync adapter for popular movies
 *
 * @author Lucian Piros
 * @version 1.1
 */
public class MovieDBSyncAdapter extends AbstractThreadedSyncAdapter {
    public final String LOG_TAG = MovieDBSyncAdapter.class.getSimpleName();
    private final String VOLLEY_TAG = MovieDBSyncAdapter.class.getSimpleName();

    // Interval at which to sync with the weather, in seconds.
    // 60 seconds (1 minute) * 60 = 1 hour
    public static final int SYNC_INTERVAL = 60 * 60;
    public static final int SYNC_FLEXTIME = SYNC_INTERVAL / 3;

    // Picasso accepts only weak references make - so we save them to make sure application works correctly
    private Map<String, PicassoMovieTarget> picassoMovieTargets;

    // Volley request queue
    RequestQueue queue;


    public MovieDBSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);

        this.queue = Volley.newRequestQueue(context);
        this.picassoMovieTargets = new HashMap<>();
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        Log.d(LOG_TAG, "Starting sync");

        fetchMovies(MovieContract.POPULAR);
        fetchMovies(MovieContract.TOP_RATED);
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
    public void fetchMovies(final int sortType) {
        // Success response listener
        // on success return populate the ArrayAdapter with received data
        Response.Listener<String> responseListener = new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                try {
                    Vector<ContentValues> vContentValue = new Vector();

                    String filter = MovieContract.FILTER_POPULAR;
                    if (sortType == MovieContract.TOP_RATED)
                        filter = MovieContract.FILTER_TOP_RATED;

                    // first delete movies from database
                    int deleted = getContext().getContentResolver().delete(
                            MovieContract.MovieEntry.buildFilteredMoviesUri(filter),
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
                        contentValues.put(MovieContract.SortEntry.COLUMN_SORT_CRITERIA, filter);
                        vContentValue.add(contentValues);
                    }

                    // insert into database
                    ContentValues[] contentValuesArray = new ContentValues[vContentValue.size()];
                    vContentValue.toArray(contentValuesArray);
                    int noOfInsertedValues = getContext().getContentResolver().bulkInsert(buildFilteredMoviesUri(filter), contentValuesArray);

                    Log.d(LOG_TAG, "Insert  " + noOfInsertedValues + " items in local database");

                    // get movies back so we can download the thumbnails
                    // A cursor is your primary interface to the query results.
                    Cursor cursor = getContext().getContentResolver().query(
                            MovieContract.MovieEntry.buildFilteredMoviesUri(filter),
                            null, // leaving "columns" null just returns all the columns.
                            null, // cols for "where" clause
                            null, // values for "where" clause
                            null/*MovieContract.SortEntry.COLUMN_INDEX + " ASC" */ // sort order == by DATE ASCENDING
                    );

                    // and let's make sure they match the ones we created
                    cursor.moveToFirst();
                    int count = 1;
                    do {
                        String movieTitle = (String) cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_TITLE));
                        PicassoMovieTarget movieTarget = new PicassoMovieTarget(cursor.getLong(cursor.getColumnIndex(MovieContract.MovieEntry._ID)), movieTitle);
                        if (!picassoMovieTargets.containsKey(movieTitle)) {
                            picassoMovieTargets.put(movieTitle, movieTarget); // make sure we keep a refference to movie target as Picasso only works with weak refferences
                        }
                        // load bitmap into PicassoMovieTarget class
                        Picasso.with(getContext())
                                .load(BuildConfig.THEMOVIEDB_IMAGE_BASEURL + "/" + cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_IMAGE_THUMBNAIL_PATH)))
                                .placeholder(R.drawable.loading_image)
                                .error(R.drawable.connectionerror)
                                .into(movieTarget);
                    } while (cursor.moveToNext());
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
                new AlertDialog.Builder(getContext())
                        .setTitle(getContext().getResources().getString(R.string.network_error_alertdialog_title))
                        .setMessage(getContext().getResources().getString(R.string.network_error_alertdialog_body))
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        };

        VolleyMovieDBStringRequest moviewDBRequest = new VolleyMovieDBStringRequest(sortType, responseListener, errorListener);
        moviewDBRequest.setTag(VOLLEY_TAG);
        queue.add(moviewDBRequest);
    }


    /**
     * Copied from Sunshine project within Udacity Android Developer Nanodegree program
     *
     * Helper method to get the fake account to be used with SyncAdapter, or make a new one
     * if the fake account doesn't exist yet.  If we make a new account, we call the
     * onAccountCreated method so we can initialize things.
     *
     * @param context The context used to access the account service
     * @return a fake account.
     */
    public static Account getSyncAccount(Context context) {
        // Get an instance of the Android account manager
        AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        // Create the account type and default account
        Account newAccount = new Account(
                context.getString(R.string.app_name), context.getString(R.string.sync_account_type));

        // If the password doesn't exist, the account doesn't exist
        if ( null == accountManager.getPassword(newAccount) ) {

        /*
         * Add the account and account type, no password or user data
         * If successful, return the Account object, otherwise report an error.
         */
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }
            /*
             * If you don't set android:syncable="true" in
             * in your <provider> element in the manifest,
             * then call ContentResolver.setIsSyncable(account, AUTHORITY, 1)
             * here.
             */

            //onAccountCreated(newAccount, context);
            /*
         * Since we've created an account
         */
            MovieDBSyncAdapter.configurePeriodicSync(context, SYNC_INTERVAL, SYNC_FLEXTIME);

        /*
         * Without calling setSyncAutomatically, our periodic sync will not be enabled.
         */
            ContentResolver.setSyncAutomatically(newAccount, context.getString(R.string.content_authority), true);

        /*
         * Finally, let's do a sync to get things started
         */
            syncImmediately(context);
        }
        return newAccount;
    }

    /**
     * Copied from Sunshine project within Udacity Android Developer Nanodegree program
     *
     * Helper method to schedule the sync adapter periodic execution
     */
    public static void configurePeriodicSync(Context context, int syncInterval, int flexTime) {
        Account account = getSyncAccount(context);
        String authority = context.getString(R.string.content_authority);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // we can enable inexact timers in our periodic sync
            SyncRequest request = new SyncRequest.Builder().
                    syncPeriodic(syncInterval, flexTime).
                    setSyncAdapter(account, authority).
                    setExtras(new Bundle()).build();
            ContentResolver.requestSync(request);
        } else {
            ContentResolver.addPeriodicSync(account,
                    authority, new Bundle(), syncInterval);
        }
    }

    /**
     * Copied from Sunshine project within Udacity Android Developer Nanodegree program
     *
     * Helper method to have the sync adapter sync immediately
     * @param context The context used to access the account service
     */
    public static void syncImmediately(Context context) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(getSyncAccount(context),
                context.getString(R.string.content_authority), bundle);
    }


    private static void onAccountCreated(Account newAccount, Context context) {
        /*
         * Since we've created an account
         */
        MovieDBSyncAdapter.configurePeriodicSync(context, SYNC_INTERVAL, SYNC_FLEXTIME);

        /*
         * Without calling setSyncAutomatically, our periodic sync will not be enabled.
         */
        ContentResolver.setSyncAutomatically(newAccount, context.getString(R.string.content_authority), true);

        /*
         * Finally, let's do a sync to get things started
         */
        syncImmediately(context);
    }

    public static void initializeSyncAdapter(Context context) {
        getSyncAccount(context);
    }

    /**
     * Target class to be used with Picasso
     * On bitmap loaded - the provided bitmap will be saved within Movie class so we can use it in MovieDetails view
     * and ImageView
     */
    private class PicassoMovieTarget implements Target {
        private long _id;
        private String movieTitle;

        public PicassoMovieTarget(long _id, String movieTitle) {
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

            getContext().getContentResolver().update(
                    MovieContract.MovieEntry.CONTENT_URI, updateValues, MovieContract.MovieEntry._ID + "= ?",
                    new String[]{Long.toString(_id)});

            // no need to keep the refference any longer
            picassoMovieTargets.remove(movieTitle);
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {
            // load a Bitmap showing there is a network error
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            Bitmap bitmap = BitmapFactory.decodeResource(getContext().getResources(),
                    R.drawable.connectionerror);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            byte[] img = bos.toByteArray();

            ContentValues updateValues = new ContentValues();
            updateValues.put(MovieContract.MovieEntry.COLUMN_IMAGE_THUMBNAIL, img);

            getContext().getContentResolver().update(
                    MovieContract.MovieEntry.CONTENT_URI, updateValues, MovieContract.MovieEntry._ID + "= ?",
                    new String[]{Long.toString(_id)});
        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {

        }
    }
}
