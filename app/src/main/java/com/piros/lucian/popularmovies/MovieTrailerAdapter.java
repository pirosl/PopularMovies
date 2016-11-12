package com.piros.lucian.popularmovies;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.piros.lucian.popularmovies.data.MovieContract;

/**
 * Cursor adapter. Provides a list of movies trailers
 *
 * Changed MovieAdapter during Popular Movies, Stage 2.
 * Changed Adapter from being an ArrayAdapter to being a CursorAdapter
 *
 * @author Lucian Piros
 * @version 1.0
 */
public class MovieTrailerAdapter extends CursorAdapter {

    private static final String LOG_TAG = MovieAdapter.class.getSimpleName();

    /**
     * ViewHolder pattern - cache the children views.
     */
    public static class ViewHolder {
        public final TextView trailerDescription;

        public ViewHolder(View view) {
            trailerDescription = (TextView) view.findViewById(R.id.movietrailerdescription);
        }
    }

    /**
     *
     * @param context - Application context
     * @param c - Cursor used to provide data
     * @param flags - Flags
     */
    public MovieTrailerAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }


    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(
                R.layout.movietrailer_layout, parent, false);
        MovieTrailerAdapter.ViewHolder viewHolder = new MovieTrailerAdapter.ViewHolder(view);
        view.setTag(viewHolder);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        MovieTrailerAdapter.ViewHolder viewHolder = (MovieTrailerAdapter.ViewHolder) view.getTag();
        String trailerDescription = cursor.getString(cursor.getColumnIndex(MovieContract.TrailerEntry.COLUMN_TRAILER_DESCRIPTION));
        viewHolder.trailerDescription.setText(trailerDescription);
    }
}
