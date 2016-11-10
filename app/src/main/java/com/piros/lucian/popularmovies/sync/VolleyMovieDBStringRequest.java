package com.piros.lucian.popularmovies.sync;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.piros.lucian.popularmovies.BuildConfig;

import java.util.HashMap;
import java.util.Map;

/**
 * StringRequest class to be used with Volley
 *
 * @author Lucian Piros
 * @version 1.0
 */
public class VolleyMovieDBStringRequest extends StringRequest {
    private final String LOG_TAG = VolleyMovieDBStringRequest.class.getSimpleName();

    public final static int MOST_POPULAR = 1;
    public final static int TOP_RATED = 2;

    private int sortType;

    public VolleyMovieDBStringRequest(int sortType, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(Request.Method.POST, BuildConfig.THEMOVIEDB_BASEURL + (sortType == MOST_POPULAR ? "/popular" : "/top_rated"), listener, errorListener);

        this.sortType = sortType;
    }

    @Override
    public Map<String, String> getParams() {
        Map<String, String> params = new HashMap<>();
        params.put("api_key", BuildConfig.THEMOVIEDB_APIKEY);
        return params;
    }
}
