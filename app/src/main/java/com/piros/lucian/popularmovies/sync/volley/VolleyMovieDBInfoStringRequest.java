package com.piros.lucian.popularmovies.sync.volley;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.piros.lucian.popularmovies.BuildConfig;

/**
 * StringRequest class to be used with Volley. Handles requests for videos and reviews for movies
 *
 * @author Lucian Piros
 * @version 1.1
 */
public class VolleyMovieDBInfoStringRequest extends StringRequest {
    private final String LOG_TAG = VolleyMovieDBInfoStringRequest.class.getSimpleName();

    public VolleyMovieDBInfoStringRequest(long movieDBId, String info, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(Method.GET, BuildConfig.THEMOVIEDB_BASEURL + "/" + new Long(movieDBId).toString() + "/" + info + "?api_key="+BuildConfig.THEMOVIEDB_APIKEY, listener, errorListener);
    }
}
