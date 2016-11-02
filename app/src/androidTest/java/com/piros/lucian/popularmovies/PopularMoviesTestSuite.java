package com.piros.lucian.popularmovies;

import android.test.suitebuilder.TestSuiteBuilder;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * PopularMovies JUnit TestSuite.
 *
 * @author Lucian Piros
 * @version 1.1
 */
public class PopularMoviesTestSuite extends TestSuite {
    public static Test suite() {
        return new TestSuiteBuilder(PopularMoviesTestSuite.class)
                .includeAllPackagesUnderHere().build();
    }

    public PopularMoviesTestSuite() {
        super();
    }
}