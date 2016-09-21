package com.piros.lucian.popularmovies;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.StringTokenizer;

/**
 * Movie details activity
 *
 * @author Lucian Piros
 * @version 1.0
 */
public class MovieDetailsActivity extends AppCompatActivity {
    private final String LOG_TAG = MovieDetailsActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        // Retrieve movie passed as parameter from Main Activity
        Bundle bundle = getIntent().getExtras();
        Movie movie = bundle.getParcelable(getResources()
                .getString(R.string.activity_extra_param));

        // Set Movie Title
        TextView movieTitle = (TextView) findViewById(R.id.movietitle);
        movieTitle.setText(movie.getOriginalTitle());

        // Load the bitmap. Leave this empty if bitmap was not fetched already
        ImageView moviePoster = (ImageView) findViewById(R.id.movieposter);
        Bitmap movieBitmap = movie.getMovieBitmap();
        if (movieBitmap != null) {
            moviePoster.setImageBitmap(movieBitmap);
        }

        // Set release date - format release date as [<month name> <year>]
        TextView releaseDate = (TextView) findViewById(R.id.releasedate);
        StringTokenizer st = new StringTokenizer(movie.getReleaseDate(), "-");
        String year = st.nextToken();
        int month = Integer.parseInt(st.nextToken());
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat month_date = new SimpleDateFormat("MMMM");
        calendar.set(Calendar.MONTH, month);
        String month_name = month_date.format(calendar.getTime());
        releaseDate.setText(month_name + " " + year);

        // Set user rating - as we only use 5 stars half the value received from database
        RatingBar userRating = (RatingBar) findViewById((R.id.userrating));
        userRating.setRating((float) movie.getUserRating() / 2.0f);

        // Set movie synopsis
        TextView movieSynopsis = (TextView) findViewById(R.id.moviesynopsis);
        movieSynopsis.setText(movie.getPlotSynopsis());
    }
}
