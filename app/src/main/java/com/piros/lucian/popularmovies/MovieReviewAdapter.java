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
 * Cursor adapter. Provides a list of movies reviews
 *
 * @author Lucian Piros
 * @version 1.1
 */
public class MovieReviewAdapter extends CursorAdapter {

    private static final String LOG_TAG = MovieAdapter.class.getSimpleName();

    /**
     * ViewHolder pattern - cache the children views.
     */
    public static class ViewHolder {
        public final TextView reviewAuthor;
        public final TextView reviewContent;

        public ViewHolder(View view) {
            reviewAuthor = (TextView) view.findViewById(R.id.moviereviewauthor);
            reviewContent = (TextView) view.findViewById(R.id.moviereviewcontent);
        }
    }

    /**
     *
     * @param context - Application context
     * @param c - Cursor used to provide data
     * @param flags - Flags
     */
    public MovieReviewAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(
                R.layout.moviereview_layout, parent, false);
        MovieReviewAdapter.ViewHolder viewHolder = new MovieReviewAdapter.ViewHolder(view);
        view.setTag(viewHolder);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        MovieReviewAdapter.ViewHolder viewHolder = (MovieReviewAdapter.ViewHolder) view.getTag();
        String reviewAuthor = cursor.getString(cursor.getColumnIndex(MovieContract.ReviewEntry.COLUMN_AUTHOR));
        String reviewContent = cursor.getString(cursor.getColumnIndex(MovieContract.ReviewEntry.COLUMN_CONTENT));
        viewHolder.reviewAuthor.setText(reviewAuthor);
        viewHolder.reviewContent.setText(reviewContent);
    }
}
