package com.piros.lucian.popularmovies;


import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.piros.lucian.popularmovies.data.MovieContract;
import com.piros.lucian.popularmovies.sync.VolleyMovieDBStringRequest;

import junit.framework.Assert;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnItemClick;

/**
 * A {@link Fragment} that presents a grid view of movie posters.
 *
 * @author Lucian Piros
 * @version 1.0
 */
public class MoviePostersFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = MoviePostersFragment.class.getSimpleName();

    private static final String[] MOVIE_COLUMNS = {
            MovieContract.MovieEntry.TABLE_NAME + "." + MovieContract.MovieEntry._ID,
            MovieContract.MovieEntry.TABLE_NAME + "." + MovieContract.MovieEntry.COLUMN_IMAGE_THUMBNAIL
    };

    static final int COL_MOVIE_ID = 0;
    static final int COL_MOVIE_IMAGE_THUMBNAIL = 1;

    @BindView(R.id.gridview_movieposters)
    GridView gridviewMoviePosters;
    private MovieAdapter mMovieAdapter;

    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of item
     * selections.
     */
    public interface Callback {
        /**
         * DetailFragmentCallback for when an item has been selected.
         */
        public void onItemSelected(Uri movieUri);
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

        // Instantiate the custom MovieAdapte
        mMovieAdapter = new MovieAdapter(getActivity(), null, 0);
        gridviewMoviePosters.setAdapter(mMovieAdapter);

        return rootView;
    }

    @OnItemClick(R.id.gridview_movieposters)
    public void onItemClick(AdapterView<?> adapterView, int position) {
        Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
        if (cursor != null) {
            ((Callback) getActivity())
                    .onItemSelected(MovieContract.MovieEntry.buildMovieUri(
                            cursor.getLong(cursor.getColumnIndex(MovieContract.MovieEntry._ID))
                    ));
        }

    }

    @Override
    public void onResume() {
        String sortType = PreferenceManager
                .getDefaultSharedPreferences(getContext())
                .getString(getResources().getString(R.string.pref_sort_key), getResources().getString(R.string.pref_sort_mostpopular));
        if (sortType.equalsIgnoreCase(getResources().getString(R.string.pref_sort_mostpopular))) {
            getLoaderManager().restartLoader(VolleyMovieDBStringRequest.MOST_POPULAR, null, this);
        }
        if (sortType.equalsIgnoreCase(getResources().getString(R.string.pref_sort_toprated))) {
            getLoaderManager().restartLoader(VolleyMovieDBStringRequest.TOP_RATED, null, this);
        }

        super.onResume();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
      //  getLoaderManager().initLoader(0, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
        // This is called when a new Loader needs to be created.  This
        // fragment only uses one loader, so we don't care about checking the id.

        // To only show current and future dates, filter the query to return weather only for
        // dates after or including today.

        // Sort order:  Ascending, by date.
        String sortOrder = MovieContract.SortEntry.COLUMN_INDEX + " ASC";

        String filter = "";
        if (id == VolleyMovieDBStringRequest.MOST_POPULAR)
            filter = MovieContract.FILTER_POPULAR;
        if (id == VolleyMovieDBStringRequest.TOP_RATED)
            filter = MovieContract.FILTER_TOP_RATED;

        Uri filteredMoviesUri = MovieContract.MovieEntry.buildFilteredMoviesUri(filter);

        return new CursorLoader(getActivity(),
                filteredMoviesUri,
                MOVIE_COLUMNS,
                null,
                null,
                sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mMovieAdapter.swapCursor(data);
//        if (mPosition != ListView.INVALID_POSITION) {
//            // If we don't need to restart the loader, and there's a desired position to restore
//            // to, do so now.
//            mListView.smoothScrollToPosition(mPosition);
//        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mMovieAdapter.swapCursor(null);
    }
}