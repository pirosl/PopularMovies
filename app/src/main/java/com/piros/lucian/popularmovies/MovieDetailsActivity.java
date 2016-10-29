package com.piros.lucian.popularmovies;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

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

        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.

            // Retrieve movie passed as parameter from Main Activity
            Bundle bundle = getIntent().getExtras();
            Movie movie = bundle.getParcelable(getResources()
                    .getString(R.string.activity_extra_param));

            Bundle arguments = new Bundle();
            arguments.putParcelable(MovieDetailsFragment.DETAIL_MOVIE, movie);

            MovieDetailsFragment fragment = new MovieDetailsFragment();
            fragment.setArguments(arguments);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_moviedetails, fragment)
                    .commit();
        }
    }
}
