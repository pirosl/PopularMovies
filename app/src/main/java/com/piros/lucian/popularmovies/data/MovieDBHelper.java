package com.piros.lucian.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.piros.lucian.popularmovies.data.MovieContract.MovieEntry;
import com.piros.lucian.popularmovies.data.MovieContract.SortEntry;
import com.piros.lucian.popularmovies.data.MovieContract.ReviewEntry;
import com.piros.lucian.popularmovies.data.MovieContract.TrailerEntry;

/**
 * Manages a local database for movie data.
 *
 * @author Lucian Piros
 * @version 1.1
 */
public class MovieDBHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;

    static final String DATABASE_NAME = "movies.db";

    public MovieDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_MOVIE_TABLE = "CREATE TABLE " + MovieEntry.TABLE_NAME + " (" +
                MovieEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                MovieEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL, " +
                MovieEntry.COLUMN_IMAGE_THUMBNAIL_PATH + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_IMAGE_THUMBNAIL + " BLOB, " +
                MovieEntry.COLUMN_SYNOPSIS + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_USER_RATING + " REAL NOT NULL, " +
                MovieEntry.COLUMN_RELEASE_DATE + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_FAVOURITE + " INTEGER NOT NULL" +
                " );";

        final String SQL_CREATE_SORT_TABLE = "CREATE TABLE " + SortEntry.TABLE_NAME + " (" +
                SortEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                SortEntry.COLUMN_MOVIE_KEY + " INTEGER NOT NULL, " +
                SortEntry.COLUMN_SORT_CRITERIA + " TEXT NOT NULL, " +
                SortEntry.COLUMN_INDEX + " INTEGER NOT NULL, " +

                // Set up the foreign key to movie table.
                " FOREIGN KEY (" + SortEntry.COLUMN_MOVIE_KEY + ") REFERENCES " +
                MovieEntry.TABLE_NAME + " (" + MovieEntry._ID +
                "));";

        final String SQL_CREATE_TRAILER_TABLE = "CREATE TABLE " + TrailerEntry.TABLE_NAME + " (" +
                TrailerEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                TrailerEntry.COLUMN_MOVIE_KEY + " INTEGER NOT NULL, " +
                TrailerEntry.COLUMN_YOUTUBE_KEY + " TEXT NOT NULL, " +
                TrailerEntry.COLUMN_TRAILER_DESCRIPTION + " TEXT NOT NULL, " +

                // Set up the foreign key to movie table.
                " FOREIGN KEY (" + TrailerEntry.COLUMN_MOVIE_KEY + ") REFERENCES " +
                MovieEntry.TABLE_NAME + " (" + MovieEntry._ID +
                "));";

        final String SQL_CREATE_REVIEW_TABLE = "CREATE TABLE " + ReviewEntry.TABLE_NAME + " (" +
                ReviewEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                ReviewEntry.COLUMN_MOVIE_KEY + " INTEGER NOT NULL, " +
                ReviewEntry.COLUMN_AUTHOR + " TEXT NOT NULL, " +
                ReviewEntry.COLUMN_CONTENT + " TEXT NOT NULL, " +
                ReviewEntry.COLUMN_URL + " TEXT NOT NULL, " +

                // Set up the foreign key to movie table.
                " FOREIGN KEY (" + ReviewEntry.COLUMN_MOVIE_KEY + ") REFERENCES " +
                MovieEntry.TABLE_NAME + " (" + MovieEntry._ID +
                "));";

        sqLiteDatabase.execSQL(SQL_CREATE_MOVIE_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_SORT_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_TRAILER_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_REVIEW_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        // We'll have to double check the logic here. As user saves some favourites movies, any db schema update
        // will erase them if we drop and re-create the tables
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + SortEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TrailerEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + ReviewEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
