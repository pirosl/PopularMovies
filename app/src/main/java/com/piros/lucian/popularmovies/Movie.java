package com.piros.lucian.popularmovies;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Movie placeholder class
 *
 * @author Lucian Piros
 * @version 1.0
 */
public class Movie implements Parcelable {
    private final String LOG_TAG = Movie.class.getSimpleName();

    String moviePoster;
    Bitmap movieBitmap;
    String originalTitle;
    String plotSynopsis;
    double userRating;
    String releaseDate;

    /**
     * Class constructor
     *
     * @param moviePoster   The movie poster name
     * @param originalTitle Movie title
     * @param plotSynopsis  Movie synopsis
     * @param userRating    User rating
     * @param releaseDate   Release date
     */
    public Movie(String moviePoster, String originalTitle, String plotSynopsis, double userRating, String releaseDate) {
        this.moviePoster = moviePoster;
        this.movieBitmap = null;
        this.originalTitle = originalTitle;
        this.plotSynopsis = plotSynopsis;
        this.userRating = userRating;
        this.releaseDate = releaseDate;
    }

    /**
     * Construct a Movie object from a JSONObject
     * Used to construct Movie objects based on stream received from themoviedb webservice
     *
     * @param movieJSONObject - the movie jsonobject
     */
    public Movie(JSONObject movieJSONObject) {
        try {
            this.moviePoster = movieJSONObject.getString("poster_path");
        } catch (JSONException e) {
            this.moviePoster = "";
        }
        this.movieBitmap = null;
        try {
            this.originalTitle = movieJSONObject.getString("original_title");
        } catch (JSONException e) {
            this.originalTitle = "";
        }
        try {
            this.plotSynopsis = movieJSONObject.getString("overview");
        } catch (JSONException e) {
            this.plotSynopsis = "";
        }
        try {
            this.userRating = movieJSONObject.getDouble("vote_average");
        } catch (JSONException e) {
            this.userRating = 0.0;
        }
        try {
            this.releaseDate = movieJSONObject.getString("release_date");
        } catch (JSONException e) {
            this.releaseDate = "";
        }
    }

    /**
     * Parcelable constructor
     *
     * @param in Parcel where this object was saved to
     */
    public Movie(Parcel in) {
        this.moviePoster = in.readString();
        this.movieBitmap = in.readParcelable(getClass().getClassLoader());
        this.originalTitle = in.readString();
        this.plotSynopsis = in.readString();
        this.userRating = in.readDouble();
        this.releaseDate = in.readString();
    }


    public String getMoviePoster() {
        return this.moviePoster;
    }

    public void setMovieBitmap(Bitmap movieBitmap) {
        this.movieBitmap = movieBitmap.copy(movieBitmap.getConfig(), true);
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public Bitmap getMovieBitmap() {
        return this.movieBitmap;
    }

    public String getPlotSynopsis() {
        return plotSynopsis;
    }

    public double getUserRating() {
        return userRating;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(moviePoster);
        parcel.writeParcelable(movieBitmap, i);
        parcel.writeString(originalTitle);
        parcel.writeString(plotSynopsis);
        parcel.writeDouble(userRating);
        parcel.writeString(releaseDate);
    }

    public final static Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel parcel) {
            return new Movie(parcel);
        }

        @Override
        public Movie[] newArray(int i) {
            return new Movie[i];
        }
    };
}
