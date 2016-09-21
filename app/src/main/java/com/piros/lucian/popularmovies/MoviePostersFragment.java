package com.piros.lucian.popularmovies;


import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.piros.lucian.popularmovies.data.MovieDBRequest;
import com.piros.lucian.popularmovies.data.MovieDataProvider;

import java.util.ArrayList;
import java.util.List;

/**
 * A {@link Fragment} that presents a grid view of movie posters.
 *
 * @author Lucian Piros
 * @version 1.0
 */
public class MoviePostersFragment extends Fragment {

    private static final String LOG_TAG = MoviePostersFragment.class.getSimpleName();

    private GridView gridviewMoviePosters;
    private MovieAdapter movieAdapter;

    public MoviePostersFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_movieposters, container, false);

        gridviewMoviePosters = (GridView) rootView.findViewById(R.id.gridview_movieposters);

        List<Movie> movies = new ArrayList<Movie>();

        // Instantiate the custom MovieAdapte
        movieAdapter = new MovieAdapter(getActivity(), movies);
        gridviewMoviePosters.setAdapter(movieAdapter);

        // Set onItemClick listener. When item is clicked the MovieDetail page will be displayed
        gridviewMoviePosters.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Movie movie = movieAdapter.getItem(position);
                Intent intent = new Intent(getActivity().getApplicationContext(), MovieDetailsActivity.class);
                intent.putExtra(getResources().getString(R.string.activity_extra_param), movie);
                startActivity(intent);
            }
        });

        MovieDataProvider movieDataProvider = MovieDataProvider.getInstance(getContext());
        movieDataProvider.hookMovieAdapter(movieAdapter);

        return rootView;
    }

    @Override
    public void onResume() {
        fetchMovies();

        super.onResume();
    }

    /**
     * Fetch movies from movie data provider
     */
    private void fetchMovies() {
        MovieDataProvider movieDataProvider = MovieDataProvider.getInstance(getContext());
        String sortType = PreferenceManager
                .getDefaultSharedPreferences(getContext())
                .getString(getResources().getString(R.string.pref_sort_key), getResources().getString(R.string.pref_sort_mostpopular));
        if (sortType.equalsIgnoreCase(getResources().getString(R.string.pref_sort_mostpopular)))
            movieDataProvider.fetchMovies(MovieDBRequest.MOST_POPULAR);
        if (sortType.equalsIgnoreCase(getResources().getString(R.string.pref_sort_toprated)))
            movieDataProvider.fetchMovies(MovieDBRequest.TOP_RATED);
    }
}