package com.piros.lucian.popularmovies;

import android.content.Context;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;

import com.piros.lucian.popularmovies.data.MovieContract;

/**
 * Custom array adapter. Provides a list of movies and display each of them as a single thumbnail
 *
 * Changed MovieAdapter during Popular Movies, Stage 2.
 * Changed Adapter from being an ArrayAdapter to being a CursorAdapter
 *
 * @author Lucian Piros
 * @version 1.0
 */
public class MovieAdapter extends CursorAdapter {

    private static final String LOG_TAG = MovieAdapter.class.getSimpleName();

    /**
     * ViewHolder pattern - cache the children views.
     */
    public static class ViewHolder {
        public final ImageView moviePoster;

        public ViewHolder(View view) {
            moviePoster = (ImageView) view.findViewById(R.id.movieposter);
        }
    }

    /**
     *
     * @param context - Application context
     * @param c - Cursor used to provide data
     * @param flags - Flags
     */
    public MovieAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }


    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(
                R.layout.movieposter_layout, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        ViewHolder viewHolder = (ViewHolder) view.getTag();

        byte[] imageThumbnail = cursor.getBlob(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_IMAGE_THUMBNAIL));

        if(imageThumbnail == null) {
            viewHolder.moviePoster.setImageResource(R.drawable.loading_image);
        }
        else {
            viewHolder.moviePoster.setImageBitmap(BitmapFactory.decodeByteArray(imageThumbnail, 0, imageThumbnail.length));
        }

    }
}
