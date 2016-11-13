package com.piros.lucian.popularmovies;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.piros.lucian.popularmovies.data.MovieContract;

import junit.framework.Assert;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.StringTokenizer;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Movie details Fragment
 *
 * @author Lucian Piros
 * @version 1.1
 */
public class MovieDetailsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private final String LOG_TAG = MovieDetailsFragment.class.getSimpleName();

    private static final int MOVIE_DETAILS_LOADER_ID = 0;
    private static final int MOVIE_TRAILERS_LOADER_ID = 1;
    private static final int MOVIE_REVIEWS_LOADER_ID = 2;

    static final String DETAIL_MOVIE = "MOVIE";

    @BindView(R.id.movietitle)
    TextView movieTitle;
    @BindView(R.id.movieposter)
    ImageView moviePoster;
    @BindView(R.id.releasedate)
    TextView releaseDate;
    @BindView(R.id.userrating)
    RatingBar userRating;
    @BindView(R.id.moviesynopsis)
    TextView movieSynopsis;
    @BindView(R.id.gridview_movietrailers)
    GridView movieTrailersGridView;
    private MovieTrailerAdapter mMovieTrailerAdapter;
    @BindView(R.id.listview_moviereviews)
    ListView movieReviewsListView;
    private MovieReviewAdapter mMovieReviewAdapter;

    private Uri mMovieUri;
    private static final String[] MOVIE_COLUMNS = {
            MovieContract.MovieEntry.TABLE_NAME + "." + MovieContract.MovieEntry._ID,
            MovieContract.MovieEntry.TABLE_NAME + "." + MovieContract.MovieEntry.COLUMN_TITLE,
            MovieContract.MovieEntry.TABLE_NAME + "." + MovieContract.MovieEntry.COLUMN_SYNOPSIS,
            MovieContract.MovieEntry.TABLE_NAME + "." + MovieContract.MovieEntry.COLUMN_RELEASE_DATE,
            MovieContract.MovieEntry.TABLE_NAME + "." + MovieContract.MovieEntry.COLUMN_USER_RATING,
            MovieContract.MovieEntry.TABLE_NAME + "." + MovieContract.MovieEntry.COLUMN_FAVOURITE,
            MovieContract.MovieEntry.TABLE_NAME + "." + MovieContract.MovieEntry.COLUMN_IMAGE_THUMBNAIL
    };

    static final int COL_MOVIE_MOVIE_ID = 0;
    static final int COL_MOVIE_TITLE = 1;
    static final int COL_MOVIE_SYNOPSIS = 2;
    static final int COL_MOVIE_RELEASE_DATE = 3;
    static final int COL_MOVIE_USER_RATING = 4;
    static final int COL_MOVIE_FAVOURITE = 5;
    static final int COL_MOVIE_IMAGE_THUMBNAIL = 6;

    private static final String[] TRAILER_COLUMNS = {
            MovieContract.TrailerEntry.TABLE_NAME + "." + MovieContract.TrailerEntry._ID,
            MovieContract.TrailerEntry.TABLE_NAME + "." + MovieContract.TrailerEntry.COLUMN_TRAILER_DESCRIPTION,
            MovieContract.TrailerEntry.TABLE_NAME + "." + MovieContract.TrailerEntry.COLUMN_YOUTUBE_KEY
    };

    static final int COL_TRAILER_TRAILER_ID = 0;
    static final int COL_TRAILER_DESCRIPTION = 1;
    static final int COL_TRAILER_YOUTUBE_KEY = 2;

    private static final String[] REVIEW_COLUMNS = {
            MovieContract.ReviewEntry.TABLE_NAME + "." + MovieContract.ReviewEntry._ID,
            MovieContract.ReviewEntry.TABLE_NAME + "." + MovieContract.ReviewEntry.COLUMN_AUTHOR,
            MovieContract.ReviewEntry.TABLE_NAME + "." + MovieContract.ReviewEntry.COLUMN_CONTENT,
            MovieContract.ReviewEntry.TABLE_NAME + "." + MovieContract.ReviewEntry.COLUMN_URL
    };

    static final int COL_REVIEW_REVIEW_ID = 0;
    static final int COL_REVIEW_AUTHOR = 1;
    static final int COL_REVIEW_CONTENT = 2;
    static final int COL_REVIEW_URL = 3;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Bundle arguments = getArguments();
        if (arguments != null) {
            mMovieUri = arguments.getParcelable(MovieDetailsFragment.DETAIL_MOVIE);
        }

        View detailsView = inflater.inflate(R.layout.fragment_moviedetails, container, false);

        ButterKnife.bind(this, detailsView);
        Assert.assertNotNull(movieTitle);
        Assert.assertNotNull(moviePoster);
        Assert.assertNotNull(releaseDate);
        Assert.assertNotNull(userRating);
        Assert.assertNotNull(movieSynopsis);
        Assert.assertNotNull(movieTrailersGridView);
        Assert.assertNotNull(movieReviewsListView);

        return detailsView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(MOVIE_DETAILS_LOADER_ID, null, this);
        super.onActivityCreated(savedInstanceState);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Loader<Cursor> cursor = null;

        switch (id) {
            case MOVIE_DETAILS_LOADER_ID:
                if (null != mMovieUri) {
                    cursor = new CursorLoader(
                            getActivity(),
                            mMovieUri,
                            MOVIE_COLUMNS,
                            null,
                            null,
                            null
                    );
                }
                break;
            case MOVIE_TRAILERS_LOADER_ID: {
                long _movieId = MovieContract.MovieEntry.getIDFromUri(mMovieUri);
                cursor = new CursorLoader(
                        getActivity(),
                        MovieContract.TrailerEntry.CONTENT_URI,
                        TRAILER_COLUMNS,
                        MovieContract.sTrailersForMovieSelection,
                        new String[]{new Long(_movieId).toString()},
                        null
                );
                break;
            }
            case MOVIE_REVIEWS_LOADER_ID: {
                long _movieId = MovieContract.MovieEntry.getIDFromUri(mMovieUri);
                cursor = new CursorLoader(
                        getActivity(),
                        MovieContract.ReviewEntry.CONTENT_URI,
                        REVIEW_COLUMNS,
                        MovieContract.sReviewsForMovieSelection,
                        new String[]{new Long(_movieId).toString()},
                        null
                );
                break;
            }
        }

        return cursor;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        int loaderId = loader.getId();
        if (MOVIE_DETAILS_LOADER_ID == loaderId) {
            if (data != null && data.moveToFirst()) {

                // Set Movie Title
                movieTitle.setText(data.getString(COL_MOVIE_TITLE));

                // Load the bitmap. Leave this empty if bitmap was not fetched already
                byte[] imageThumbnail = data.getBlob(COL_MOVIE_IMAGE_THUMBNAIL);

                if (imageThumbnail == null) {
                    moviePoster.setImageResource(R.drawable.loading_image);
                } else {
                    moviePoster.setImageBitmap(BitmapFactory.decodeByteArray(imageThumbnail, 0, imageThumbnail.length));
                }

                // Set release date - format release date as [<month name> <year>]
                StringTokenizer st = new StringTokenizer(data.getString(COL_MOVIE_RELEASE_DATE), "-");
                String year = st.nextToken();
                int month = Integer.parseInt(st.nextToken());
                Calendar calendar = Calendar.getInstance();
                SimpleDateFormat month_date = new SimpleDateFormat("MMMM");
                calendar.set(Calendar.MONTH, month);
                String month_name = month_date.format(calendar.getTime());
                releaseDate.setText(month_name + " " + year);

                // Set user rating - as we only use 5 stars half the value received from database
                userRating.setRating((float) data.getFloat(COL_MOVIE_USER_RATING) / 2.0f);

                // Set movie synopsis
                movieSynopsis.setText(data.getString(COL_MOVIE_SYNOPSIS));

                // Instantiate the custom MovieTrailerAdapter
                mMovieTrailerAdapter = new MovieTrailerAdapter(getActivity(), null, 0);
                movieTrailersGridView.setAdapter(mMovieTrailerAdapter);

                // Instantiate the custom MovieReviewAdapter
                mMovieReviewAdapter = new MovieReviewAdapter(getActivity(), null, 0);
                movieReviewsListView.setAdapter(mMovieReviewAdapter);

                // init loader manager for trailers
                getLoaderManager().initLoader(MOVIE_TRAILERS_LOADER_ID, null, this);
            }
        }
        if (MOVIE_TRAILERS_LOADER_ID == loaderId) {
            mMovieTrailerAdapter.swapCursor(data);

            movieTrailersGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                                             @Override
                                                             public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                                                 Cursor cursor = (Cursor) parent.getItemAtPosition(position);
                                                                 if (cursor != null) {
                                                                     startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(BuildConfig.YOUTUBE_LINK + cursor.getString(cursor.getColumnIndex(MovieContract.TrailerEntry.COLUMN_YOUTUBE_KEY)))));
                                                                 }
                                                             }
                                                         }
            );

            // init loader manager for reviews
            getLoaderManager().initLoader(MOVIE_REVIEWS_LOADER_ID, null, this);
        }

        if (MOVIE_REVIEWS_LOADER_ID == loaderId) {
            mMovieReviewAdapter.swapCursor(data);

            movieReviewsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                                             @Override
                                                             public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                                                 Cursor cursor = (Cursor) parent.getItemAtPosition(position);
                                                                 if (cursor != null) {
                                                                     startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(cursor.getString(cursor.getColumnIndex(MovieContract.ReviewEntry.COLUMN_URL)))));
                                                                 }
                                                             }
                                                         }
            );

        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }
}