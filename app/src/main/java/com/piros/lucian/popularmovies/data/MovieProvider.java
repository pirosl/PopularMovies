package com.piros.lucian.popularmovies.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

/**
 * Movie content provider. Manages access to persisted data within movies.db database
 *
 * @author Lucian Piros
 * @version 1.1
 */
public class MovieProvider extends ContentProvider {

    // The URI Matcher used by this content provider.
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private MovieDBHelper mOpenHelper;

    static final int MOVIE = 100;
    static final int FAVOURITE_MOVIES = 101;
    static final int POPULAR_MOVIES = 102;
    static final int TOP_RATED_MOVIES = 103;

    static final int MOVIE_WITH_ID = 201;

    static final int MOVIE_TRAILERS = 301;
    static final int MOVIE_REVIEWS = 302;

    static final int SORT = 401;
    static final int TRAILER = 501;
    static final int REVIEW = 601;

    private static final SQLiteQueryBuilder sSortedMoviesListQueryBuilder;

    static {
        sSortedMoviesListQueryBuilder = new SQLiteQueryBuilder();

        //This is an inner join which looks like
        //sort INNER JOIN movie ON sort.movie_id = movie._id
        sSortedMoviesListQueryBuilder.setTables(
                MovieContract.SortEntry.TABLE_NAME + " INNER JOIN " +
                        MovieContract.MovieEntry.TABLE_NAME +
                        " ON " + MovieContract.SortEntry.TABLE_NAME +
                        "." + MovieContract.SortEntry.COLUMN_MOVIE_KEY +
                        " = " + MovieContract.MovieEntry.TABLE_NAME +
                        "." + MovieContract.MovieEntry._ID);
    }

    //movie._id = ?
    private static final String sMovieSelection =
            MovieContract.MovieEntry.TABLE_NAME +
                    "." + MovieContract.MovieEntry._ID + " = ? ";

    //movie.favourite = ?
    private static final String sFavouriteMoviesSelection =
            MovieContract.MovieEntry.TABLE_NAME +
                    "." + MovieContract.MovieEntry.COLUMN_FAVOURITE + " = ? ";

    //trailer.movie_id = ?
    private static final String sTrailersForMovieSelection =
            MovieContract.TrailerEntry.TABLE_NAME +
                    "." + MovieContract.TrailerEntry.COLUMN_MOVIE_KEY + " = ? ";

    //review.movie_id = ?
    private static final String sReviewsForMovieSelection =
            MovieContract.ReviewEntry.TABLE_NAME +
                    "." + MovieContract.ReviewEntry.COLUMN_MOVIE_KEY + " = ? ";

    //sort.movie_id = ?
    private static final String sSortForMovieSelection =
            MovieContract.SortEntry.TABLE_NAME +
                    "." + MovieContract.SortEntry.COLUMN_MOVIE_KEY + " = ? ";

    //sort.sort_criteria = ?
    private static final String sSortCriteriaMovieSelection =
            MovieContract.SortEntry.TABLE_NAME +
                    "." + MovieContract.SortEntry.COLUMN_SORT_CRITERIA + " = ? ";

    /*
        URI matcher for this content providers.
     */
    static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MovieContract.CONTENT_AUTHORITY;

        // For each type of URI create a corresponding code.
        matcher.addURI(authority, MovieContract.PATH_MOVIE, MOVIE);
        matcher.addURI(authority, MovieContract.PATH_MOVIE + "/#", MOVIE_WITH_ID);

        matcher.addURI(authority, MovieContract.PATH_MOVIE + "/" + MovieContract.FILTER_FAVOURITE, FAVOURITE_MOVIES);
        matcher.addURI(authority, MovieContract.PATH_MOVIE + "/" + MovieContract.FILTER_TOP_RATED, TOP_RATED_MOVIES);
        matcher.addURI(authority, MovieContract.PATH_MOVIE + "/" + MovieContract.FILTER_POPULAR, POPULAR_MOVIES);

        matcher.addURI(authority, MovieContract.PATH_MOVIE + "/#/" + MovieContract.PATH_TRAILER, MOVIE_TRAILERS);
        matcher.addURI(authority, MovieContract.PATH_MOVIE + "/#/" + MovieContract.PATH_REVIEW, MOVIE_REVIEWS);

        matcher.addURI(authority, MovieContract.PATH_SORT, SORT);
        matcher.addURI(authority, MovieContract.PATH_REVIEW, REVIEW);
        matcher.addURI(authority, MovieContract.PATH_TRAILER, TRAILER);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new MovieDBHelper(getContext());
        return true;
    }


    @Override
    public String getType(Uri uri) {

        // Use the Uri Matcher to determine what kind of URI this is.
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case MOVIE:
                return MovieContract.MovieEntry.CONTENT_TYPE;
            case MOVIE_WITH_ID:
                return MovieContract.MovieEntry.CONTENT_ITEM_TYPE;
            case FAVOURITE_MOVIES:
                return MovieContract.MovieEntry.CONTENT_TYPE;
            case TOP_RATED_MOVIES:
                return MovieContract.MovieEntry.CONTENT_TYPE;
            case POPULAR_MOVIES:
                return MovieContract.MovieEntry.CONTENT_TYPE;
            case MOVIE_TRAILERS:
                return MovieContract.MovieEntry.CONTENT_TYPE;
            case MOVIE_REVIEWS:
                return MovieContract.MovieEntry.CONTENT_TYPE;
            case SORT:
                return MovieContract.SortEntry.CONTENT_TYPE;
            case TRAILER:
                return MovieContract.TrailerEntry.CONTENT_TYPE;
            case REVIEW:
                return MovieContract.ReviewEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        // Given a URI, will determine what kind of request it is, and query the database accordingly.
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            // "movie"
            case MOVIE: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        MovieContract.MovieEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            // "movie/#"
            case MOVIE_WITH_ID: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        MovieContract.MovieEntry.TABLE_NAME,
                        projection,
                        sMovieSelection,
                        new String[]{Long.toString(MovieContract.MovieEntry.getDateFromUri(uri))},
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            // "movie/top_rated"
            case TOP_RATED_MOVIES: {
                retCursor = sSortedMoviesListQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                        projection,
                        sSortCriteriaMovieSelection,
                        new String[]{MovieContract.FILTER_TOP_RATED},
                        null,
                        null,
                        MovieContract.SortEntry.COLUMN_INDEX + " ASC"  // sort order == by sorting index ASCENDING
                );
                break;
            }
            // "movie/popular"
            case POPULAR_MOVIES: {
                retCursor = sSortedMoviesListQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                        projection,
                        sSortCriteriaMovieSelection,
                        new String[]{MovieContract.FILTER_POPULAR},
                        null,
                        null,
                        MovieContract.SortEntry.COLUMN_INDEX + " ASC"  // sort order == by sorting index ASCENDING
                );
                int cc = retCursor.getCount();
                break;
            }
            // "sort"
            case SORT: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        MovieContract.SortEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            // "movie"
            case TRAILER: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        MovieContract.TrailerEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            // "movie"
            case REVIEW: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        MovieContract.ReviewEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    /*
        Student: Add the ability to insert Locations to the implementation of this function.
     */
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case MOVIE: {
                long _id = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = MovieContract.MovieEntry.buildMovieUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case SORT: {
                long _id = db.insert(MovieContract.SortEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = MovieContract.SortEntry.buildSortUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case TRAILER: {
                long _id = db.insert(MovieContract.TrailerEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = MovieContract.TrailerEntry.buildTrailerUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case REVIEW: {
                long _id = db.insert(MovieContract.ReviewEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = MovieContract.ReviewEntry.buildReviewUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;
        // this makes delete all rows return the number of rows deleted
        if (null == selection) selection = "1";
        switch (match) {
            case MOVIE:
                rowsDeleted = db.delete(
                        MovieContract.MovieEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case TOP_RATED_MOVIES: {
                Cursor cursor = sSortedMoviesListQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                        null,
                        sSortCriteriaMovieSelection,
                        new String[]{MovieContract.FILTER_TOP_RATED},
                        null,
                        null,
                        null
                );
                cursor.moveToFirst();
                rowsDeleted = 0;
                while (!cursor.isLast()) {
                    int _id = cursor.getInt(cursor.getColumnIndex(MovieContract.MovieEntry._ID));
                    int movieDeleted = db.delete(MovieContract.MovieEntry.TABLE_NAME, sMovieSelection, new String[]{new Integer(_id).toString()});
                    int sortDeleted = db.delete(MovieContract.SortEntry.TABLE_NAME, sSortForMovieSelection, new String[]{new Integer(_id).toString()});

                    rowsDeleted += (movieDeleted == 1 && sortDeleted == 1) ? 1 : 0;
                    cursor.moveToNext();
                }
                break;
            }
            case POPULAR_MOVIES: {
                Cursor cursor = sSortedMoviesListQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                        null,
                        sSortCriteriaMovieSelection,
                        new String[]{MovieContract.FILTER_POPULAR},
                        null,
                        null,
                        null
                );
                cursor.moveToFirst();
                rowsDeleted = 0;
                while (!cursor.isLast()) {
                    int _id = cursor.getInt(cursor.getColumnIndex(MovieContract.MovieEntry._ID));
                    int movieDeleted = db.delete(MovieContract.MovieEntry.TABLE_NAME, sMovieSelection, new String[]{new Integer(_id).toString()});
                    int sortDeleted = db.delete(MovieContract.SortEntry.TABLE_NAME, sSortForMovieSelection, new String[]{new Integer(_id).toString()});

                    rowsDeleted += (movieDeleted == 1 && sortDeleted == 1) ? 1 : 0;
                    cursor.moveToNext();
                }
                break;
            }
            case SORT:
                rowsDeleted = db.delete(
                        MovieContract.SortEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case TRAILER:
                rowsDeleted = db.delete(
                        MovieContract.TrailerEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case REVIEW:
                rowsDeleted = db.delete(
                        MovieContract.ReviewEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        // Because a null deletes all rows
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(
            Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;

        switch (match) {
            case MOVIE:
                rowsUpdated = db.update(MovieContract.MovieEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);

        String tableName = null; // default table name to insert into
        switch (match) {
            case TRAILER:  // inserting into trailer & review has the same logic
                tableName = MovieContract.TrailerEntry.TABLE_NAME;
                break;
            case REVIEW:
                tableName = MovieContract.ReviewEntry.TABLE_NAME;
                break;
        }

        switch (match) {
            case TRAILER:  // inserting into trailer & review has the same logic
            case REVIEW: {
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(tableName, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            }
            case TOP_RATED_MOVIES: // logic for top_rated and popular movies is exactly the same
            case POPULAR_MOVIES: {
                // we have to insert within two tables: movie and sort
                db.beginTransaction();
                int returnCount = 0;
                int sortIndex = 0;
                try {
                    for (ContentValues value : values) {
                        ContentValues movieValue = new ContentValues();
                        movieValue.put(MovieContract.MovieEntry.COLUMN_FAVOURITE, value.getAsInteger(MovieContract.MovieEntry.COLUMN_FAVOURITE));
                        movieValue.put(MovieContract.MovieEntry.COLUMN_USER_RATING, value.getAsDouble(MovieContract.MovieEntry.COLUMN_USER_RATING));
                        movieValue.put(MovieContract.MovieEntry.COLUMN_TITLE, value.getAsString(MovieContract.MovieEntry.COLUMN_TITLE));
                        movieValue.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, value.getAsString(MovieContract.MovieEntry.COLUMN_RELEASE_DATE));
                        movieValue.put(MovieContract.MovieEntry.COLUMN_IMAGE_THUMBNAIL_PATH, value.getAsString(MovieContract.MovieEntry.COLUMN_IMAGE_THUMBNAIL_PATH));
                        movieValue.put(MovieContract.MovieEntry.COLUMN_SYNOPSIS, value.getAsString(MovieContract.MovieEntry.COLUMN_SYNOPSIS));

                        long _movieId = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, movieValue);
                        if (_movieId != -1) {
                            ContentValues sortValue = new ContentValues();
                            sortValue.put(MovieContract.SortEntry.COLUMN_MOVIE_KEY, _movieId);
                            sortValue.put(MovieContract.SortEntry.COLUMN_INDEX, sortIndex++);
                            sortValue.put(MovieContract.SortEntry.COLUMN_SORT_CRITERIA, value.getAsString(MovieContract.SortEntry.COLUMN_SORT_CRITERIA));

                            long _sortId = db.insert(MovieContract.SortEntry.TABLE_NAME, null, sortValue);
                            if (_sortId != -1) {
                                returnCount++;
                            }
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            }

            default:
                return super.bulkInsert(uri, values);
        }
    }
}
