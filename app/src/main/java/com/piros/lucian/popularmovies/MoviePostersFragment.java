package com.piros.lucian.popularmovies;


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

import junit.framework.Assert;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnItemClick;

/**
 * A {@link Fragment} that presents a grid view of movie posters.
 *
 * @author Lucian Piros
 * @version 1.0
 */
public class MoviePostersFragment extends Fragment {

    private static final String LOG_TAG = MoviePostersFragment.class.getSimpleName();

    @BindView(R.id.gridview_movieposters)
    GridView gridviewMoviePosters;
    private MovieAdapter movieAdapter;

    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of item
     * selections.
     */
    public interface Callback {
        /**
         * DetailFragmentCallback for when an item has been selected.
         */
        public void onItemSelected(Movie movie);
    }

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

        ButterKnife.bind(this, rootView);
        Assert.assertNotNull(gridviewMoviePosters);

        List<Movie> movies = new ArrayList<Movie>();

        // Instantiate the custom MovieAdapte
        movieAdapter = new MovieAdapter(getActivity(), movies);
        gridviewMoviePosters.setAdapter(movieAdapter);

        MovieDataProvider movieDataProvider = MovieDataProvider.getInstance(getContext());
        movieDataProvider.hookMovieAdapter(movieAdapter);

        return rootView;
    }

    @OnItemClick(R.id.gridview_movieposters)
    public void onItemClick(AdapterView<?> adapterView, int position) {
        Movie movie = movieAdapter.getItem(position);
        ((Callback) getActivity())
                .onItemSelected(movie);
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