package com.piros.lucian.popularmovies.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Defines table and column names for the movies database.
 *
 * @author Lucian Piros
 * @version 1.1
 */
public class MovieContract {

    public static final String CONTENT_AUTHORITY = "com.piros.lucian.popularmovies";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_MOVIE = "movie";
    public static final String PATH_SORT = "sort";
    public static final String PATH_TRAILER = "trailer";
    public static final String PATH_REVIEW = "review";

    public static final String FILTER_FAVOURITE = "favourite";
    public static final String FILTER_TOP_RATED = "top_rated";
    public static final String FILTER_POPULAR = "popular";

    public static final int POPULAR = 1;
    public static final int TOP_RATED = 2;
    public static final int FAVORITE = 3;

    /* Inner class that defines the table contents of the movie table */
    public static final class MovieEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIE).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;

        // Table name
        public static final String TABLE_NAME = "movie";
        // Movie title
        public static final String COLUMN_TITLE = "title";
        // Movie id within MovieDB
        public static final String COLUMN_MOVIE_ID = "moviedb_id";
        // Movie image thumbnail path
        public static final String COLUMN_IMAGE_THUMBNAIL_PATH = "image_thumbnail_path";
        // Movie image thumbnail
        public static final String COLUMN_IMAGE_THUMBNAIL = "image";
        // Movie synopsis
        public static final String COLUMN_SYNOPSIS = "synopsis";
        // User rating
        public static final String COLUMN_USER_RATING = "user_rating";
        // Release date
        public static final String COLUMN_RELEASE_DATE = "release_date";
        // Favorite movie?
        public static final String COLUMN_FAVOURITE = "favourite";

        public static Uri buildMovieUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildFilteredMoviesUri(String filter) {
            return CONTENT_URI.buildUpon().appendPath(filter).build();
        }

        public static Uri buildMovieTrailersUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id).buildUpon().appendPath(PATH_TRAILER).build();
        }

        public static Uri buildMoviereviewsUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id).buildUpon().appendPath(PATH_REVIEW).build();
        }

        public static long getIDFromUri(Uri uri) {
            return Long.parseLong(uri.getPathSegments().get(1));
        }
    }

    /* Inner class that defines the table contents of the sort table
     * sort table keep the ordering position and category for each movie */
    public static final class SortEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_SORT).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_SORT;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_SORT;

        // Table name
        public static final String TABLE_NAME = "sort";
        // Column with the foreign key into the movie table.
        public static final String COLUMN_MOVIE_KEY = "movie_id";
        // Sort criteria
        public static final String COLUMN_SORT_CRITERIA = "sort_criteria";
        // Index within sorted list
        public static final String COLUMN_INDEX = "sort_index";

        public static Uri buildSortUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    /* Inner class that defines the table contents of the trailer table */
    public static final class TrailerEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_TRAILER).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TRAILER;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TRAILER;

        // Table name
        public static final String TABLE_NAME = "trailer";
        // Column with the foreign key into the movie table.
        public static final String COLUMN_MOVIE_KEY = "movie_id";
        // Youtube key for this specific trailer
        public static final String COLUMN_YOUTUBE_KEY = "youtube_key";
        // Trailer description
        public static final String COLUMN_TRAILER_DESCRIPTION = "trailer_description";

        public static Uri buildTrailerUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    /* Inner class that defines the table contents of the review table */
    public static final class ReviewEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_REVIEW).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_REVIEW;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_REVIEW;

        // Table name
        public static final String TABLE_NAME = "review";
        // Column with the foreign key into the movie table.
        public static final String COLUMN_MOVIE_KEY = "movie_id";
        // Review's author
        public static final String COLUMN_AUTHOR = "author";
        // Review's content
        public static final String COLUMN_CONTENT = "content";
        // Review's url
        public static final String COLUMN_URL = "url";

        public static Uri buildReviewUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }


    //movie._id = ?
    public static final String sMovieSelection =
            MovieContract.MovieEntry.TABLE_NAME +
                    "." + MovieContract.MovieEntry._ID + " = ? ";

    //movie.favourite = ?
    public static final String sFavouriteMoviesSelection =
            MovieContract.MovieEntry.TABLE_NAME +
                    "." + MovieContract.MovieEntry.COLUMN_FAVOURITE + " = ? ";

    //trailer.movie_id = ?
    public static final String sTrailersForMovieSelection =
            MovieContract.TrailerEntry.TABLE_NAME +
                    "." + MovieContract.TrailerEntry.COLUMN_MOVIE_KEY + " = ? ";

    //review.movie_id = ?
    public static final String sReviewsForMovieSelection =
            MovieContract.ReviewEntry.TABLE_NAME +
                    "." + MovieContract.ReviewEntry.COLUMN_MOVIE_KEY + " = ? ";

    //sort.movie_id = ?
    public static final String sSortForMovieSelection =
            MovieContract.SortEntry.TABLE_NAME +
                    "." + MovieContract.SortEntry.COLUMN_MOVIE_KEY + " = ? ";

    //sort.sort_criteria = ?
    public static final String sSortCriteriaMovieSelection =
            MovieContract.SortEntry.TABLE_NAME +
                    "." + MovieContract.SortEntry.COLUMN_SORT_CRITERIA + " = ? ";
}
