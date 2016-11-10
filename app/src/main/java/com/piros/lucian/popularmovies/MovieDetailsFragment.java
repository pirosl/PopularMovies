package com.piros.lucian.popularmovies;

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
import android.widget.ImageView;
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

    static final int COL_MOVIE_ID = 0;
    static final int COL_TITLE = 1;
    static final int COL_SYNOPSIS = 2;
    static final int COL_RELEASE_DATE = 3;
    static final int COL_USER_RATING = 4;
    static final int COL_FAVOURITE = 5;
    static final int COL_IMAGE_THUMBNAIL = 6;

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

        return detailsView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(0, null, this);
        super.onActivityCreated(savedInstanceState);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (null != mMovieUri) {
            // Now create and return a CursorLoader that will take care of
            // creating a Cursor for the data being displayed.
            return new CursorLoader(
                    getActivity(),
                    mMovieUri,
                    MOVIE_COLUMNS,
                    null,
                    null,
                    null
            );
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null && data.moveToFirst()) {

            // Set Movie Title
            movieTitle.setText(data.getString(COL_TITLE));

            // Load the bitmap. Leave this empty if bitmap was not fetched already
            byte[] imageThumbnail = data.getBlob(COL_IMAGE_THUMBNAIL);

            if (imageThumbnail == null) {
                moviePoster.setImageResource(R.drawable.loading_image);
            } else {
                moviePoster.setImageBitmap(BitmapFactory.decodeByteArray(imageThumbnail, 0, imageThumbnail.length));
            }

            // Set release date - format release date as [<month name> <year>]
            StringTokenizer st = new StringTokenizer(data.getString(COL_RELEASE_DATE), "-");
            String year = st.nextToken();
            int month = Integer.parseInt(st.nextToken());
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat month_date = new SimpleDateFormat("MMMM");
            calendar.set(Calendar.MONTH, month);
            String month_name = month_date.format(calendar.getTime());
            releaseDate.setText(month_name + " " + year);

            // Set user rating - as we only use 5 stars half the value received from database
            userRating.setRating((float) data.getFloat(COL_USER_RATING) / 2.0f);

            // Set movie synopsis
            movieSynopsis.setText(data.getString(COL_SYNOPSIS));

        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }
}
