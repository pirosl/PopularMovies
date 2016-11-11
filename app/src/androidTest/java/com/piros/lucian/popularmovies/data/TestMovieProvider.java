package com.piros.lucian.popularmovies.data;

import android.content.ComponentName;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.test.AndroidTestCase;
import android.util.Log;

import com.piros.lucian.popularmovies.data.MovieContract.MovieEntry;
import com.piros.lucian.popularmovies.data.MovieContract.ReviewEntry;
import com.piros.lucian.popularmovies.data.MovieContract.SortEntry;
import com.piros.lucian.popularmovies.data.MovieContract.TrailerEntry;

/**
 * Few unit tests for MovieProvider class.
 *
 * @author Lucian Piros
 * @version 1.1
 */
public class TestMovieProvider extends AndroidTestCase {

    public static final String LOG_TAG = TestMovieProvider.class.getSimpleName();

    /*
       This helper function deletes all records from both database tables using the ContentProvider.
       It also queries the ContentProvider to make sure that the database has been successfully
       deleted, so it cannot be used until the Query and Delete functions have been written
       in the ContentProvider.
     */
    public void deleteAllRecordsFromProvider() {
        mContext.getContentResolver().delete(
                MovieEntry.CONTENT_URI,
                null,
                null
        );
        mContext.getContentResolver().delete(
                SortEntry.CONTENT_URI,
                null,
                null
        );
        mContext.getContentResolver().delete(
                TrailerEntry.CONTENT_URI,
                null,
                null
        );
        mContext.getContentResolver().delete(
                ReviewEntry.CONTENT_URI,
                null,
                null
        );

        Cursor cursor = mContext.getContentResolver().query(
                MovieEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        assertEquals("Error: Records not deleted from movie table during delete", 0, cursor.getCount());
        cursor.close();

        cursor = mContext.getContentResolver().query(
                SortEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        assertEquals("Error: Records not deleted from sort table during delete", 0, cursor.getCount());
        cursor.close();

        cursor = mContext.getContentResolver().query(
                TrailerEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        assertEquals("Error: Records not deleted from trailer table during delete", 0, cursor.getCount());
        cursor.close();

        cursor = mContext.getContentResolver().query(
                ReviewEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        assertEquals("Error: Records not deleted from review table during delete", 0, cursor.getCount());
        cursor.close();
    }

    public void deleteAllRecords() {
        deleteAllRecordsFromProvider();
    }

    // Since we want each test to start with a clean slate, run deleteAllRecords
    // in setUp (called by the test runner before each test).
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        deleteAllRecords();
    }

    /*
        This test checks to make sure that the content provider is registered correctly.
     */
    public void testProviderRegistry() {
        PackageManager pm = mContext.getPackageManager();

        // We define the component name based on the package name from the context and the
        // WeatherProvider class.
        ComponentName componentName = new ComponentName(mContext.getPackageName(),
                MovieProvider.class.getName());
        try {
            // Fetch the provider info using the component name from the PackageManager
            // This throws an exception if the provider isn't registered.
            ProviderInfo providerInfo = pm.getProviderInfo(componentName, 0);

            // Make sure that the registered authority matches the authority from the Contract.
            assertEquals("Error: MovieProvider registered with authority: " + providerInfo.authority +
                            " instead of authority: " + MovieContract.CONTENT_AUTHORITY,
                    providerInfo.authority, MovieContract.CONTENT_AUTHORITY);
        } catch (PackageManager.NameNotFoundException e) {
            // I guess the provider isn't registered correctly.
            assertTrue("Error: MovieProvider not registered at " + mContext.getPackageName(),
                    false);
        }
    }

    /*
            This test doesn't touch the database.  It verifies that the ContentProvider returns
            the correct type for each type of URI that it can handle.
         */
    public void testGetType() {
        // content://com.piros.lucian.popularmovies/movie/
        String type = mContext.getContentResolver().getType(MovieEntry.CONTENT_URI);
        // vnd.android.cursor.dir/com.piros.lucian.popularmovies/movie/
        assertEquals("Error: the MovieEntry CONTENT_URI should return MovieEntry.CONTENT_TYPE",
                MovieEntry.CONTENT_TYPE, type);

        long id = 3L;
        // content://com.piros.lucian.popularmovies/movie/
        type = mContext.getContentResolver().getType(MovieEntry.buildMovieUri(id));
        // vnd.android.cursor.item/com.piros.lucian.popularmovies/movie/
        assertEquals("Error: the MovieEntry with ID CONTENT_URI should return MovieEntry.CONTENT_ITEM_TYPE",
                MovieEntry.CONTENT_ITEM_TYPE, type);

        // content://com.piros.lucian.popularmovies/movie/favourite
        type = mContext.getContentResolver().getType(MovieEntry.buildFilteredMoviesUri(MovieContract.FILTER_FAVOURITE));
        // vnd.android.cursor.dir/com.piros.lucian.popularmovies/movie/favourite
        assertEquals("Error: the FavouriteMovieEntry CONTENT_URI should return MovieEntry.CONTENT_TYPE",
                MovieEntry.CONTENT_TYPE, type);

        // content://com.piros.lucian.popularmovies/movie/top_rated
        type = mContext.getContentResolver().getType(MovieEntry.buildFilteredMoviesUri(MovieContract.FILTER_TOP_RATED));
        // vnd.android.cursor.dir/com.piros.lucian.popularmovies/movie/top_rated
        assertEquals("Error: the TopRatedMovieEntry with ID CONTENT_URI should return MovieEntry.CONTENT_TYPE",
                MovieEntry.CONTENT_TYPE, type);

        // content://com.piros.lucian.popularmovies/movie/popular
        type = mContext.getContentResolver().getType(MovieEntry.buildFilteredMoviesUri(MovieContract.FILTER_POPULAR));
        // vnd.android.cursor.dir/com.piros.lucian.popularmovies/movie/popular
        assertEquals("Error: the PopularMovieEntry with ID CONTENT_URI should return MovieEntry.CONTENT_TYPE",
                MovieEntry.CONTENT_TYPE, type);

        // content://com.piros.lucian.popularmovies/movie/3/trailers
        type = mContext.getContentResolver().getType(MovieEntry.buildMovieTrailersUri(id));
        // vnd.android.cursor.dir/com.piros.lucian.popularmovies/movie/3/trailers
        assertEquals("Error: the MovieTrailersEntry with ID CONTENT_URI should return MovieEntry.CONTENT_TYPE",
                MovieEntry.CONTENT_TYPE, type);

        // content://com.piros.lucian.popularmovies/movie/3/reviews
        type = mContext.getContentResolver().getType(MovieEntry.buildMoviereviewsUri(id));
        // vnd.android.cursor.dir/com.piros.lucian.popularmovies/movie/3/reviews
        assertEquals("Error: the MovieReviewsEntry with ID CONTENT_URI should return MovieEntry.CONTENT_TYPE",
                MovieEntry.CONTENT_TYPE, type);

        // content://com.piros.lucian.popularmovies/sort/
        type = mContext.getContentResolver().getType(SortEntry.CONTENT_URI);
        // vnd.android.cursor.dir/com.piros.lucian.popularmovies/sort/
        assertEquals("Error: the SortEntry CONTENT_URI should return SortEntry.CONTENT_TYPE",
                SortEntry.CONTENT_TYPE, type);
        // content://com.piros.lucian.popularmovies/trailer/
        type = mContext.getContentResolver().getType(TrailerEntry.CONTENT_URI);
        // vnd.android.cursor.dir/com.piros.lucian.popularmovies/trailer/
        assertEquals("Error: the TrailerEntry CONTENT_URI should return TrailerEntry.CONTENT_TYPE",
                TrailerEntry.CONTENT_TYPE, type);
        // content://com.piros.lucian.popularmovies/review/
        type = mContext.getContentResolver().getType(ReviewEntry.CONTENT_URI);
        // vnd.android.cursor.dir/com.piros.lucian.popularmovies/review/
        assertEquals("Error: the ReviewEntry CONTENT_URI should return ReviewEntry.CONTENT_TYPE",
                ReviewEntry.CONTENT_TYPE, type);
    }


    /*
            This test uses the database directly to insert and then uses the ContentProvider to
            read out the data.
    */
    public void testBasicTopRatedMoviesQuery() {
        // insert our test records into the database
        MovieDBHelper dbHelper = new MovieDBHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues testMovieValues = TestUtilities.createMovieValues();
        long movieRowId = db.insert(MovieEntry.TABLE_NAME, null, testMovieValues);
        assertTrue("Unable to Insert MovieEntry into the Database", movieRowId != -1);

        ContentValues movieSortCriteria = TestUtilities.createMovieSortCriteriaValues(movieRowId, MovieContract.FILTER_TOP_RATED);

        long movieSortRowId = db.insert(SortEntry.TABLE_NAME, null, movieSortCriteria);
        assertTrue("Unable to Insert WeatherEntry into the Database", movieRowId != -1);

        db.close();

        // Test the basic content provider query
        Cursor topRatedMovies = mContext.getContentResolver().query(
                MovieEntry.buildFilteredMoviesUri(MovieContract.FILTER_TOP_RATED),
                null,
                null,
                null,
                null
        );

        // Make sure we get the correct cursor out of the database
        TestUtilities.validateCursor("testBasicWeatherQuery", topRatedMovies, testMovieValues);
    }

    /*
            This test uses the database directly to insert and then uses the ContentProvider to
            read out the data.
    */
    public void testBasicPopularMoviesQuery() {
        // insert our test records into the database
        MovieDBHelper dbHelper = new MovieDBHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues testMovieValues = TestUtilities.createMovieValues();
        long movieRowId = db.insert(MovieEntry.TABLE_NAME, null, testMovieValues);
        assertTrue("Unable to Insert MovieEntry into the Database", movieRowId != -1);

        ContentValues movieSortCriteria = TestUtilities.createMovieSortCriteriaValues(movieRowId, MovieContract.FILTER_POPULAR);

        long movieSortRowId = db.insert(SortEntry.TABLE_NAME, null, movieSortCriteria);
        assertTrue("Unable to Insert WeatherEntry into the Database", movieRowId != -1);

        db.close();

        // Test the basic content provider query
        Cursor topRatedMovies = mContext.getContentResolver().query(
                MovieEntry.buildFilteredMoviesUri(MovieContract.FILTER_POPULAR),
                null,
                null,
                null,
                null
        );

        // Make sure we get the correct cursor out of the database
        TestUtilities.validateCursor("testBasicWeatherQuery", topRatedMovies, testMovieValues);
    }

    /*
        This test uses the provider to insert and then update the data. Uncomment this test to
        see if your update location is functioning correctly.
     */
    public void testUpdateMOVIE() {
        // Create a new map of values, where column names are the keys
        ContentValues values = TestUtilities.createMovieValues();

        Uri movieUri = mContext.getContentResolver().
                insert(MovieEntry.CONTENT_URI, values);
        long movieRowId = ContentUris.parseId(movieUri);

        // Verify we got a row back.
        assertTrue(movieRowId != -1);
        Log.d(LOG_TAG, "New row id: " + movieRowId);

        ContentValues updatedValues = new ContentValues(values);
        updatedValues.put(MovieEntry._ID, movieRowId);
        updatedValues.put(MovieEntry.COLUMN_FAVOURITE, 1);

        // Create a cursor with observer to make sure that the content provider is notifying
        // the observers as expected
        Cursor movieCursor = mContext.getContentResolver().query(MovieEntry.CONTENT_URI, null, null, null, null);

        TestUtilities.TestContentObserver tco = TestUtilities.getTestContentObserver();
        movieCursor.registerContentObserver(tco);

        int count = mContext.getContentResolver().update(
                MovieEntry.CONTENT_URI, updatedValues, MovieEntry._ID + "= ?",
                new String[]{Long.toString(movieRowId)});
        assertEquals(count, 1);

        // Test to make sure our observer is called.  If not, we throw an assertion.
        tco.waitForNotificationOrFail();

        movieCursor.unregisterContentObserver(tco);
        movieCursor.close();

        // A cursor is your primary interface to the query results.
        Cursor cursor = mContext.getContentResolver().query(
                MovieEntry.CONTENT_URI,
                null,   // projection
                MovieEntry._ID + " = " + movieRowId,
                null,   // Values for the "where" clause
                null    // sort order
        );

        TestUtilities.validateCursor("testUpdateMovie.  Error validating movie entry update.",
                cursor, updatedValues);

        cursor.close();
    }


    public void testInsertReadProvider() {
        ContentValues testValues = TestUtilities.createMovieValues();

        // Register a content observer for our insert.  This time, directly with the content resolver
        TestUtilities.TestContentObserver tco = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(MovieEntry.CONTENT_URI, true, tco);
        Uri locationUri = mContext.getContentResolver().insert(MovieEntry.CONTENT_URI, testValues);

        tco.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(tco);

        long movieRowId = ContentUris.parseId(locationUri);

        // Verify we got a row back.
        assertTrue(movieRowId != -1);

        // Data's inserted.  IN THEORY.  Now pull some out to stare at it and verify it made
        // the round trip.
        // A cursor is your primary interface to the query results.
        Cursor cursor = mContext.getContentResolver().query(
                MovieEntry.CONTENT_URI,
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order
        );

        TestUtilities.validateCursor("testInsertReadProvider. Error validating MovieEntry.",
                cursor, testValues);

        // Excelent.  Now that we have a movie, add sort criteria!
        ContentValues sortPopularValues = TestUtilities.createMovieSortCriteriaValues(movieRowId, MovieContract.FILTER_POPULAR);
        // The TestContentObserver is a one-shot class
        tco = TestUtilities.getTestContentObserver();

        mContext.getContentResolver().registerContentObserver(SortEntry.CONTENT_URI, true, tco);

        Uri popularInsertUri = mContext.getContentResolver()
                .insert(SortEntry.CONTENT_URI, sortPopularValues);
        long sortPopulatRowId = ContentUris.parseId(popularInsertUri);
        assertTrue(sortPopulatRowId != -1);

        // Check data is inserted
        Cursor cursorPopularMovies = mContext.getContentResolver().query(
                SortEntry.CONTENT_URI,
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order
        );

        TestUtilities.validateCursor("testInsertReadProvider. Error validating SortEntry.",
                cursorPopularMovies, sortPopularValues);

        // delete data and repeat test for top_rated
        mContext.getContentResolver().delete(
                SortEntry.CONTENT_URI,
                null,
                null
        );

        ContentValues sortTopRatedValues = TestUtilities.createMovieSortCriteriaValues(movieRowId, MovieContract.FILTER_TOP_RATED);
        // The TestContentObserver is a one-shot class
        tco = TestUtilities.getTestContentObserver();

        mContext.getContentResolver().registerContentObserver(SortEntry.CONTENT_URI, true, tco);

        Uri topRatedInsertUri = mContext.getContentResolver()
                .insert(SortEntry.CONTENT_URI, sortTopRatedValues);
        long sortTopRatedRowId = ContentUris.parseId(topRatedInsertUri);
        assertTrue(sortTopRatedRowId != -1);

        // Check data is inserted
        Cursor cursorTopRatedMovies = mContext.getContentResolver().query(
                SortEntry.CONTENT_URI,
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order
        );

        TestUtilities.validateCursor("testInsertReadProvider. Error validating SortEntry.",
                cursorTopRatedMovies, sortTopRatedValues);

        // insert trailer
        ContentValues trailerValues = TestUtilities.createMovieTrailerValues(movieRowId);
        // The TestContentObserver is a one-shot class
        tco = TestUtilities.getTestContentObserver();

        mContext.getContentResolver().registerContentObserver(TrailerEntry.CONTENT_URI, true, tco);

        Uri trailerInsertUri = mContext.getContentResolver()
                .insert(TrailerEntry.CONTENT_URI, trailerValues);
        long trailerRowId = ContentUris.parseId(trailerInsertUri);
        assertTrue(trailerRowId != -1);

        // Check data is inserted
        Cursor cursorTrailer = mContext.getContentResolver().query(
                TrailerEntry.CONTENT_URI,
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order
        );

        TestUtilities.validateCursor("testInsertReadProvider. Error validating TrailerEntry.",
                cursorTrailer, trailerValues);

        // insert review
        ContentValues reviewValues = TestUtilities.createMovieReviewValues(movieRowId);
        // The TestContentObserver is a one-shot class
        tco = TestUtilities.getTestContentObserver();

        mContext.getContentResolver().registerContentObserver(ReviewEntry.CONTENT_URI, true, tco);

        Uri reviewsInsertUri = mContext.getContentResolver()
                .insert(ReviewEntry.CONTENT_URI, reviewValues);
        long reviewRowId = ContentUris.parseId(trailerInsertUri);
        assertTrue(reviewRowId != -1);

        // Check data is inserted
        Cursor cursorReview = mContext.getContentResolver().query(
                ReviewEntry.CONTENT_URI,
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order
        );

        TestUtilities.validateCursor("testInsertReadProvider. Error validating ReviewEntry.",
                cursorReview, reviewValues);
    }

    // Make sure we can still delete after adding/updating stuff
    //
    public void testDeleteRecords() {
        testInsertReadProvider();

        // Register a content observer for our MOVIE delete.
        TestUtilities.TestContentObserver movieObserver = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(MovieEntry.CONTENT_URI, true, movieObserver);

        // Register a content observer for our SORT delete.
        TestUtilities.TestContentObserver sortObserver = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(SortEntry.CONTENT_URI, true, sortObserver);

        // Register a content observer for our TRAILER delete.
        TestUtilities.TestContentObserver trailerObserver = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(TrailerEntry.CONTENT_URI, true, trailerObserver);

        // Register a content observer for our REVIEW delete.
        TestUtilities.TestContentObserver reviewObserver = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(ReviewEntry.CONTENT_URI, true, reviewObserver);

        deleteAllRecordsFromProvider();

        movieObserver.waitForNotificationOrFail();
        sortObserver.waitForNotificationOrFail();
        trailerObserver.waitForNotificationOrFail();
        reviewObserver.waitForNotificationOrFail();

        mContext.getContentResolver().unregisterContentObserver(movieObserver);
        mContext.getContentResolver().unregisterContentObserver(sortObserver);
        mContext.getContentResolver().unregisterContentObserver(trailerObserver);
        mContext.getContentResolver().unregisterContentObserver(reviewObserver);
    }

    static private final int BULK_INSERT_RECORDS_TO_INSERT = 10;

    static ContentValues[] createBulkInsertTopRatedMoviesValues() {
        ContentValues[] returnContentValues = new ContentValues[BULK_INSERT_RECORDS_TO_INSERT];

        for (int i = 0; i < BULK_INSERT_RECORDS_TO_INSERT; i++) {
            ContentValues topRatedMovieValues = new ContentValues();
            topRatedMovieValues.put(MovieEntry.COLUMN_TITLE, "title");
            topRatedMovieValues.put(MovieEntry.COLUMN_MOVIE_ID, i);
            topRatedMovieValues.put(MovieEntry.COLUMN_SYNOPSIS, "synopsis");
            topRatedMovieValues.put(MovieEntry.COLUMN_IMAGE_THUMBNAIL_PATH, "thumbnail_path");
            topRatedMovieValues.put(MovieEntry.COLUMN_USER_RATING, 3.5);
            topRatedMovieValues.put(MovieEntry.COLUMN_RELEASE_DATE, "01/01/2016");
            topRatedMovieValues.put(MovieEntry.COLUMN_FAVOURITE, 0);
            topRatedMovieValues.put(SortEntry.COLUMN_SORT_CRITERIA, MovieContract.FILTER_TOP_RATED);
            returnContentValues[i] = topRatedMovieValues;
        }
        return returnContentValues;
    }

    public void testBulkInsertTopRatedMovies() {
        // first clear database
        deleteAllRecords();

        // bulkInsert some top rated movies.
        ContentValues[] bulkInsertContentValues = createBulkInsertTopRatedMoviesValues();

        // Register a content observer for our bulk insert.
        TestUtilities.TestContentObserver topRatedMoviesObserver = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(MovieEntry.buildFilteredMoviesUri(MovieContract.FILTER_TOP_RATED), true, topRatedMoviesObserver);

        int insertCount = mContext.getContentResolver().bulkInsert(MovieEntry.buildFilteredMoviesUri(MovieContract.FILTER_TOP_RATED), bulkInsertContentValues);

        topRatedMoviesObserver.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(topRatedMoviesObserver);

        assertEquals(insertCount, BULK_INSERT_RECORDS_TO_INSERT);

        // A cursor is your primary interface to the query results.
        Cursor cursor = mContext.getContentResolver().query(
                MovieEntry.buildFilteredMoviesUri(MovieContract.FILTER_TOP_RATED),
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                SortEntry.COLUMN_INDEX + " ASC"  // sort order == by DATE ASCENDING
        );

        // we should have as many records in the database as we've inserted
        assertEquals(cursor.getCount(), BULK_INSERT_RECORDS_TO_INSERT);

        // and let's make sure they match the ones we created
        cursor.moveToFirst();
        for (int i = 0; i < BULK_INSERT_RECORDS_TO_INSERT; i++, cursor.moveToNext()) {
            TestUtilities.validateCurrentRecord("testBulkInsert.  Error validating top rated entry " + i,
                    cursor, bulkInsertContentValues[i]);
        }
        cursor.close();
    }

    static ContentValues[] createBulkInsertPopularMoviesValues() {
        ContentValues[] returnContentValues = new ContentValues[BULK_INSERT_RECORDS_TO_INSERT];

        for (int i = 0; i < BULK_INSERT_RECORDS_TO_INSERT; i++) {
            ContentValues popularMovieValues = new ContentValues();
            popularMovieValues.put(MovieEntry.COLUMN_TITLE, "title");
            popularMovieValues.put(MovieEntry.COLUMN_MOVIE_ID, i);
            popularMovieValues.put(MovieEntry.COLUMN_SYNOPSIS, "synopsis");
            popularMovieValues.put(MovieEntry.COLUMN_IMAGE_THUMBNAIL_PATH, "thumbnail_path");
            popularMovieValues.put(MovieEntry.COLUMN_USER_RATING, 3.5);
            popularMovieValues.put(MovieEntry.COLUMN_RELEASE_DATE, "01/01/2016");
            popularMovieValues.put(MovieEntry.COLUMN_FAVOURITE, 0);
            popularMovieValues.put(SortEntry.COLUMN_SORT_CRITERIA, MovieContract.FILTER_POPULAR);
            returnContentValues[i] = popularMovieValues;
        }
        return returnContentValues;
    }

    public void testBulkInsertPopularMovies() {
        // first clear database
        deleteAllRecords();

        // bulkInsert some top rated movies.
        ContentValues[] bulkInsertContentValues = createBulkInsertPopularMoviesValues();

        // Register a content observer for our bulk insert.
        TestUtilities.TestContentObserver topRatedMoviesObserver = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(MovieEntry.buildFilteredMoviesUri(MovieContract.FILTER_POPULAR), true, topRatedMoviesObserver);

        int insertCount = mContext.getContentResolver().bulkInsert(MovieEntry.buildFilteredMoviesUri(MovieContract.FILTER_POPULAR), bulkInsertContentValues);

        topRatedMoviesObserver.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(topRatedMoviesObserver);

        assertEquals(insertCount, BULK_INSERT_RECORDS_TO_INSERT);

        // A cursor is your primary interface to the query results.
        Cursor cursor = mContext.getContentResolver().query(
                MovieEntry.buildFilteredMoviesUri(MovieContract.FILTER_POPULAR),
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                SortEntry.COLUMN_INDEX + " ASC"  // sort order == by DATE ASCENDING
        );

        // we should have as many records in the database as we've inserted
        assertEquals(cursor.getCount(), BULK_INSERT_RECORDS_TO_INSERT);

        // and let's make sure they match the ones we created
        cursor.moveToFirst();
        for (int i = 0; i < BULK_INSERT_RECORDS_TO_INSERT; i++, cursor.moveToNext()) {
            TestUtilities.validateCurrentRecord("testBulkInsert.  Error validating top rated entry " + i,
                    cursor, bulkInsertContentValues[i]);
        }
        cursor.close();
    }

    static ContentValues[] createBulkInsertMoviesTrailerValues() {
        ContentValues[] returnContentValues = new ContentValues[BULK_INSERT_RECORDS_TO_INSERT];

        for (int i = 0; i < BULK_INSERT_RECORDS_TO_INSERT; i++) {
            ContentValues movieTrailerValues = new ContentValues();
            movieTrailerValues.put(TrailerEntry.COLUMN_YOUTUBE_KEY, "youtube_key");
            movieTrailerValues.put(TrailerEntry.COLUMN_TRAILER_DESCRIPTION, "trailer");
            returnContentValues[i] = movieTrailerValues;
        }
        return returnContentValues;
    }

    public void testBulkInsertMovieTrailer() {
        // first clear database
        deleteAllRecords();

        // first insert a movie
        ContentValues testValues = TestUtilities.createMovieValues();

        // Register a content observer for our insert.  This time, directly with the content resolver
        TestUtilities.TestContentObserver tco = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(MovieEntry.CONTENT_URI, true, tco);
        Uri locationUri = mContext.getContentResolver().insert(MovieEntry.CONTENT_URI, testValues);

        tco.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(tco);

        long movieRowId = ContentUris.parseId(locationUri);
        assertTrue(movieRowId != -1);

        // bulkInsert some top rated movies.
        ContentValues[] bulkInsertContentValues = createBulkInsertMoviesTrailerValues();

        // now change the value of movie_id for inner join
        for(int i = 0; i < bulkInsertContentValues.length; ++i)
            bulkInsertContentValues[i].put(TrailerEntry.COLUMN_MOVIE_KEY, movieRowId);

        // Register a content observer for our bulk insert.
        TestUtilities.TestContentObserver movieTrailersObserver = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(TrailerEntry.CONTENT_URI, true, movieTrailersObserver);

        int insertCount = mContext.getContentResolver().bulkInsert(TrailerEntry.CONTENT_URI, bulkInsertContentValues);

        movieTrailersObserver.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(movieTrailersObserver);

        assertEquals(insertCount, BULK_INSERT_RECORDS_TO_INSERT);

        // A cursor is your primary interface to the query results.
        Cursor cursor = mContext.getContentResolver().query(
                TrailerEntry.CONTENT_URI,
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null
        );

        // we should have as many records in the database as we've inserted
        assertEquals(cursor.getCount(), BULK_INSERT_RECORDS_TO_INSERT);

        // and let's make sure they match the ones we created
        cursor.moveToFirst();
        for (int i = 0; i < BULK_INSERT_RECORDS_TO_INSERT; i++, cursor.moveToNext()) {
            TestUtilities.validateCurrentRecord("testBulkInsert.  Error validating trailer entry " + i,
                    cursor, bulkInsertContentValues[i]);
        }
        cursor.close();
    }

    static ContentValues[] createBulkInsertMoviesReviewValues() {
        ContentValues[] returnContentValues = new ContentValues[BULK_INSERT_RECORDS_TO_INSERT];

        for (int i = 0; i < BULK_INSERT_RECORDS_TO_INSERT; i++) {
            ContentValues movieReviewValues = new ContentValues();
            movieReviewValues.put(ReviewEntry.COLUMN_AUTHOR, "author");
            movieReviewValues.put(ReviewEntry.COLUMN_CONTENT, "content");
            movieReviewValues.put(ReviewEntry.COLUMN_URL, "url");
            returnContentValues[i] = movieReviewValues;
        }
        return returnContentValues;
    }

    public void testBulkInsertMovieReview() {
        // first clear database
        deleteAllRecords();

        // first insert a movie
        ContentValues testValues = TestUtilities.createMovieValues();

        // Register a content observer for our insert.  This time, directly with the content resolver
        TestUtilities.TestContentObserver tco = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(MovieEntry.CONTENT_URI, true, tco);
        Uri locationUri = mContext.getContentResolver().insert(MovieEntry.CONTENT_URI, testValues);

        tco.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(tco);

        long movieRowId = ContentUris.parseId(locationUri);
        assertTrue(movieRowId != -1);

        // bulkInsert some top rated movies.
        ContentValues[] bulkInsertContentValues = createBulkInsertMoviesReviewValues();

        // now change the value of movie_id for inner join
        for(int i = 0; i < bulkInsertContentValues.length; ++i)
            bulkInsertContentValues[i].put(ReviewEntry.COLUMN_MOVIE_KEY, movieRowId);

        // Register a content observer for our bulk insert.
        TestUtilities.TestContentObserver movieTrailersObserver = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(ReviewEntry.CONTENT_URI, true, movieTrailersObserver);

        int insertCount = mContext.getContentResolver().bulkInsert(ReviewEntry.CONTENT_URI, bulkInsertContentValues);

        movieTrailersObserver.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(movieTrailersObserver);

        assertEquals(insertCount, BULK_INSERT_RECORDS_TO_INSERT);

        // A cursor is your primary interface to the query results.
        Cursor cursor = mContext.getContentResolver().query(
                ReviewEntry.CONTENT_URI,
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null
        );

        // we should have as many records in the database as we've inserted
        assertEquals(cursor.getCount(), BULK_INSERT_RECORDS_TO_INSERT);

        // and let's make sure they match the ones we created
        cursor.moveToFirst();
        for (int i = 0; i < BULK_INSERT_RECORDS_TO_INSERT; i++, cursor.moveToNext()) {
            TestUtilities.validateCurrentRecord("testBulkInsert.  Error validating review entry " + i,
                    cursor, bulkInsertContentValues[i]);
        }
        cursor.close();
    }
}
