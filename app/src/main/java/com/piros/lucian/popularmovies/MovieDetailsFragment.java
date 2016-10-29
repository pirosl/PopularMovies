package com.piros.lucian.popularmovies;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

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
public class MovieDetailsFragment extends Fragment {

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Movie movie = null;
        Bundle arguments = getArguments();
        if (arguments != null) {
            movie = arguments.getParcelable(MovieDetailsFragment.DETAIL_MOVIE);
        }

        View detailsView = inflater.inflate(R.layout.fragment_moviedetails, container, false);

        ButterKnife.bind(this, detailsView);
        Assert.assertNotNull(movieTitle);
        Assert.assertNotNull(moviePoster);
        Assert.assertNotNull(releaseDate);
        Assert.assertNotNull(userRating);
        Assert.assertNotNull(movieSynopsis);

        // Set Movie Title
        movieTitle.setText(movie.getOriginalTitle());

        // Load the bitmap. Leave this empty if bitmap was not fetched already
        Bitmap movieBitmap = movie.getMovieBitmap();
        if (movieBitmap != null) {
            moviePoster.setImageBitmap(movieBitmap);
        }

        // Set release date - format release date as [<month name> <year>]
        StringTokenizer st = new StringTokenizer(movie.getReleaseDate(), "-");
        String year = st.nextToken();
        int month = Integer.parseInt(st.nextToken());
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat month_date = new SimpleDateFormat("MMMM");
        calendar.set(Calendar.MONTH, month);
        String month_name = month_date.format(calendar.getTime());
        releaseDate.setText(month_name + " " + year);

        // Set user rating - as we only use 5 stars half the value received from database
        userRating.setRating((float) movie.getUserRating() / 2.0f);

        // Set movie synopsis
        movieSynopsis.setText(movie.getPlotSynopsis());

        return detailsView;
    }
}
