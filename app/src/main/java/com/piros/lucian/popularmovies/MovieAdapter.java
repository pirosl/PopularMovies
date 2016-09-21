package com.piros.lucian.popularmovies;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.piros.lucian.popularmovies.data.MovieDataProvider;

import java.util.List;

/**
 * Custom array adapter. Provides a list of movies and display each of them as a single thumbnail
 *
 * @author Lucian Piros
 * @version 1.0
 */
public class MovieAdapter extends ArrayAdapter<Movie> {

    private static final String LOG_TAG = MovieAdapter.class.getSimpleName();

    /**
     * Own custom constructor (it doesn't mirror a superclass constructor).
     * The context is used to inflate the layout file, and the List is the data we want
     * to populate into the lists
     *
     * @param context The current context. Used to inflate the layout file.
     * @param movies  A List of Movies objects to display in a list
     */

    public MovieAdapter(Activity context, List<Movie> movies) {
        super(context, 0, movies);
    }

    /**
     * Provides a view for Movie Posters GridView
     *
     * @param position    The AdapterView position that is requesting a view
     * @param convertView The recycled view to populate.
     * @param parent      The parent ViewGroup that is used for inflation.
     * @return The View for the position in the AdapterView.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Movie movie = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.movieposter_layout, parent, false);
        }

        ImageView moviePoster = (ImageView) convertView.findViewById(R.id.movieposter);

        // Fetch movie poster
        MovieDataProvider.getInstance(getContext()).fetchMoviePoster(movie, moviePoster);

        return convertView;
    }
}
