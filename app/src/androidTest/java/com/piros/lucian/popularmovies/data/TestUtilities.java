package com.piros.lucian.popularmovies.data;

import android.content.ContentValues;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.test.AndroidTestCase;

import com.piros.lucian.popularmovies.utils.PollingCheck;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;

/**
 * Class copied from Sunshine demo project within Udactiy class
 *
 * @author
 * @version 1.1
 */
public class TestUtilities extends AndroidTestCase {

    static void validateCursor(String error, Cursor valueCursor, ContentValues expectedValues) {
        assertTrue("Empty cursor returned. " + error, valueCursor.moveToFirst());
        validateCurrentRecord(error, valueCursor, expectedValues);
        valueCursor.close();
    }

    static void validateCurrentRecord(String error, Cursor valueCursor, ContentValues expectedValues) {
        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();
        for (Map.Entry<String, Object> entry : valueSet) {
            String columnName = entry.getKey();
            int idx = valueCursor.getColumnIndex(columnName);
            assertFalse("Column '" + columnName + "' not found. " + error, idx == -1);

            if(columnName == MovieContract.MovieEntry.COLUMN_IMAGE_THUMBNAIL) {
                byte[] expectedValue = (byte[])entry.getValue();
                assertTrue("Value '" + entry.getValue().toString() +
                        "' did not match the expected value '" +
                        expectedValue + "'. " + error, Arrays.equals(expectedValue,valueCursor.getBlob(idx)));
            }
            else {
                String expectedValue = entry.getValue().toString();
                assertEquals("Value '" + entry.getValue().toString() +
                        "' did not match the expected value '" +
                        expectedValue + "'. " + error, expectedValue, valueCursor.getString(idx));
            }
        }
    }

    /*
        Default values for Movie.
     */
    static ContentValues createMovieValues() {
        // Create a new map of values, where column names are the keys
        ContentValues testValues = new ContentValues();
        testValues.put(MovieContract.MovieEntry.COLUMN_TITLE, "Title");
        testValues.put(MovieContract.MovieEntry.COLUMN_SYNOPSIS, "Synopsis");
        testValues.put(MovieContract.MovieEntry.COLUMN_FAVOURITE, "1");
        testValues.put(MovieContract.MovieEntry.COLUMN_IMAGE_THUMBNAIL_PATH, "/some_path");
        testValues.put(MovieContract.MovieEntry.COLUMN_IMAGE_THUMBNAIL, new byte[0]);
        testValues.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, "01/01/2010");
        testValues.put(MovieContract.MovieEntry.COLUMN_USER_RATING, "3.5");

        return testValues;
    }

    /*
        Default values for Sort (populat / top_rated) Movie.
     */
    static ContentValues createSortTypeMovieValues(long movie_id, String sortCriteria) {
        // Create a new map of values, where column names are the keys
        ContentValues testValues = new ContentValues();
        testValues.put(MovieContract.SortEntry.COLUMN_MOVIE_KEY, movie_id);
        testValues.put(MovieContract.SortEntry.COLUMN_SORT_CRITERIA, sortCriteria);
        testValues.put(MovieContract.SortEntry.COLUMN_INDEX, 1);

        return testValues;
    }

    /*
        Default values for Movie Trailer.
     */
    static ContentValues createMovieTrailerValues(long movie_id) {
        // Create a new map of values, where column names are the keys
        ContentValues testValues = new ContentValues();
        testValues.put(MovieContract.TrailerEntry.COLUMN_MOVIE_KEY, movie_id);
        testValues.put(MovieContract.TrailerEntry.COLUMN_YOUTUBE_KEY, "youtube_key");
        testValues.put(MovieContract.TrailerEntry.COLUMN_TRAILER_DESCRIPTION, "trailer");

        return testValues;
    }

    /*
        Default values for Movie Reviews.
     */
    static ContentValues createMovieReviewValues(long movie_id) {
        // Create a new map of values, where column names are the keys
        ContentValues testValues = new ContentValues();
        testValues.put(MovieContract.ReviewEntry.COLUMN_MOVIE_KEY, movie_id);
        testValues.put(MovieContract.ReviewEntry.COLUMN_AUTHOR, "author");
        testValues.put(MovieContract.ReviewEntry.COLUMN_CONTENT, "content");
        testValues.put(MovieContract.ReviewEntry.COLUMN_URL, "url");

        return testValues;
    }

    static ContentValues createMovieSortCriteriaValues(long movie_id, String sortCriteria) {
        // Create a new map of values, where column names are the keys
        ContentValues testValues = new ContentValues();
        testValues.put(MovieContract.SortEntry.COLUMN_MOVIE_KEY, movie_id);
        testValues.put(MovieContract.SortEntry.COLUMN_SORT_CRITERIA, sortCriteria);
        testValues.put(MovieContract.SortEntry.COLUMN_INDEX, 1);

        return testValues;
    }

    /*
        Function copied from Sunshine app on UDACITY
        The functions we provide inside of TestProvider use this utility class to test
        the ContentObserver callbacks using the PollingCheck class that we grabbed from the Android
        CTS tests.

        Note that this only tests that the onChange function is called; it does not test that the
        correct Uri is returned.
     */
    static class TestContentObserver extends ContentObserver {
        final HandlerThread mHT;
        boolean mContentChanged;

        static TestContentObserver getTestContentObserver() {
            HandlerThread ht = new HandlerThread("ContentObserverThread");
            ht.start();
            return new TestContentObserver(ht);
        }

        private TestContentObserver(HandlerThread ht) {
            super(new Handler(ht.getLooper()));
            mHT = ht;
        }

        // On earlier versions of Android, this onChange method is called
        @Override
        public void onChange(boolean selfChange) {
            onChange(selfChange, null);
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            mContentChanged = true;
        }

        public void waitForNotificationOrFail() {
            // Note: The PollingCheck class is taken from the Android CTS (Compatibility Test Suite).
            // It's useful to look at the Android CTS source for ideas on how to test your Android
            // applications.  The reason that PollingCheck works is that, by default, the JUnit
            // testing framework is not running on the main Android application thread.
            new PollingCheck(5000) {
                @Override
                protected boolean check() {
                    return mContentChanged;
                }
            }.run();
            mHT.quit();
        }
    }

    static TestContentObserver getTestContentObserver() {
        return TestContentObserver.getTestContentObserver();
    }
}
