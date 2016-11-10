package com.piros.lucian.popularmovies.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

/**
 * Sync service - download movies from Movie DB and stores them in local db
 *
 * @author Lucian Piros
 * @version 1.1
 */
public class MovieDBSyncService extends Service {
    public final String LOG_TAG = MovieDBSyncService.class.getSimpleName();

    private static final Object sSyncAdapterLock = new Object();
    private static MovieDBSyncAdapter sMovieDBSyncAdapter = null;

    @Override
    public void onCreate() {
        Log.d(LOG_TAG, "onCreate - MovieDBSyncService");
        synchronized (sSyncAdapterLock) {
            if (sMovieDBSyncAdapter == null) {
                sMovieDBSyncAdapter = new MovieDBSyncAdapter(getApplicationContext(), true);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return sMovieDBSyncAdapter.getSyncAdapterBinder();
    }
}
