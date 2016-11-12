package com.piros.lucian.popularmovies.sync.volley;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.piros.lucian.popularmovies.BuildConfig;
import com.piros.lucian.popularmovies.data.MovieContract;

import java.util.HashMap;
import java.util.Map;

/**
 * StringRequest class to be used with Volley. Handles requests for Popular and Top_Rated moviesß
 *
 * @author Lucian Piros
 * @version 1.0
 */
public class VolleyMovieDBStringRequest extends StringRequest {
    private final String LOG_TAG = VolleyMovieDBStringRequest.class.getSimpleName();

    public VolleyMovieDBStringRequest(int sortType, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(Request.Method.POST, BuildConfig.THEMOVIEDB_BASEURL + "/" + (sortType == MovieContract.POPULAR ? MovieContract.FILTER_POPULAR : MovieContract.FILTER_TOP_RATED), listener, errorListener);
    }

    @Override
    public Map<String, String> getParams() {
        Map<String, String> params = new HashMap<>();
        params.put("api_key", BuildConfig.THEMOVIEDB_APIKEY);
        return params;
    }
}
