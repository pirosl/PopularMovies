package com.piros.lucian.popularmovies.data;

import android.content.UriMatcher;
import android.net.Uri;
import android.test.AndroidTestCase;

/**
 * Test Case for URIMatcher within MovieProvider.
 *
 * @author Lucian Piros
 * @version 1.1
 */
public class TestURIMatcher extends AndroidTestCase {
    private static final long TEST_MOVIE_ID = 3L;

    // content://com.example.android.sunshine.app/weather"
    private static final Uri TEST_MOVIE_DIR = MovieContract.MovieEntry.CONTENT_URI;
    private static final Uri TEST_MOVIE_WITH_ID = MovieContract.MovieEntry.buildMovieUri(TEST_MOVIE_ID);
    private static final Uri TEST_FAVOURITE_MOVIE_DIR = MovieContract.MovieEntry.buildFilteredMoviesUri(MovieContract.FILTER_FAVOURITE);
    private static final Uri TEST_TOP_RATED_MOVIE_DIR = MovieContract.MovieEntry.buildFilteredMoviesUri(MovieContract.FILTER_TOP_RATED);
    private static final Uri TEST_POPULAR_MOVIE_DIR = MovieContract.MovieEntry.buildFilteredMoviesUri(MovieContract.FILTER_POPULAR);
    private static final Uri TEST_MOVIE_TRAILERS_DIR = MovieContract.MovieEntry.buildMovieTrailersUri(TEST_MOVIE_ID);
    private static final Uri TEST_MOVIE_REVIEWS_DIR = MovieContract.MovieEntry.buildMoviereviewsUri(TEST_MOVIE_ID);

    private static final Uri TEST_SORT_DIR = MovieContract.SortEntry.CONTENT_URI;
    private static final Uri TEST_TRAILER_DIR = MovieContract.TrailerEntry.CONTENT_URI;
    private static final Uri TEST_REVIEW_DIR = MovieContract.ReviewEntry.CONTENT_URI;
    /*
        Tests that UriMatcher returns the correct integer value
        for each of the Uri types that our ContentProvider can handle.
     */
    public void testUriMatcher() {
        UriMatcher testMatcher = MovieProvider.buildUriMatcher();

        assertEquals("Error: The MOVIE URI was matched incorrectly.",
                testMatcher.match(TEST_MOVIE_DIR), MovieProvider.MOVIE);
        assertEquals("Error: The MOVIE WITH ID URI was matched incorrectly.",
                testMatcher.match(TEST_MOVIE_WITH_ID), MovieProvider.MOVIE_WITH_ID);
        assertEquals("Error: The FAVOURITE MOVIE URI was matched incorrectly.",
                testMatcher.match(TEST_FAVOURITE_MOVIE_DIR), MovieProvider.FAVOURITE_MOVIES);
        assertEquals("Error: The TOP RATED MOVIE URI was matched incorrectly.",
                testMatcher.match(TEST_TOP_RATED_MOVIE_DIR), MovieProvider.TOP_RATED_MOVIES);
        assertEquals("Error: The POPULAR MOVIE URI was matched incorrectly.",
                testMatcher.match(TEST_POPULAR_MOVIE_DIR), MovieProvider.POPULAR_MOVIES);
        assertEquals("Error: The MOVIE TRAILERS URI was matched incorrectly.",
                testMatcher.match(TEST_MOVIE_TRAILERS_DIR), MovieProvider.MOVIE_TRAILERS);
        assertEquals("Error: The MOVIE REVIEWS URI was matched incorrectly.",
                testMatcher.match(TEST_MOVIE_REVIEWS_DIR), MovieProvider.MOVIE_REVIEWS);

        assertEquals("Error: The SORT URI was matched incorrectly.",
                testMatcher.match(TEST_SORT_DIR), MovieProvider.SORT);
        assertEquals("Error: The TRAILER URI was matched incorrectly.",
                testMatcher.match(TEST_TRAILER_DIR), MovieProvider.TRAILER);
        assertEquals("Error: The REVIEW URI was matched incorrectly.",
                testMatcher.match(TEST_REVIEW_DIR), MovieProvider.REVIEW);
    }
}

