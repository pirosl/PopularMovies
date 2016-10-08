package com.piros.lucian.popularmovies;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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
 * Movie details activity
 *
 * @author Lucian Piros
 * @version 1.0
 */
public class MovieDetailsActivity extends AppCompatActivity {
    private final String LOG_TAG = MovieDetailsActivity.class.getSimpleName();

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        // Retrieve movie passed as parameter from Main Activity
        Bundle bundle = getIntent().getExtras();
        Movie movie = bundle.getParcelable(getResources()
                .getString(R.string.activity_extra_param));

        ButterKnife.bind(this);
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
    }
}
