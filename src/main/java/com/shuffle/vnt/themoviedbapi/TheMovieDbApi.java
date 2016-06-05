package com.shuffle.vnt.themoviedbapi;

import org.apache.commons.lang3.StringUtils;

import com.omertron.themoviedbapi.MovieDbException;
import com.omertron.themoviedbapi.model.config.Configuration;
import com.omertron.themoviedbapi.model.movie.MovieInfo;
import com.shuffle.vnt.configuration.PreferenceManager;
import com.shuffle.vnt.core.parser.bean.Movie;
import com.shuffle.vnt.core.parser.bean.Torrent;

public class TheMovieDbApi extends com.omertron.themoviedbapi.TheMovieDbApi {
    public TheMovieDbApi() throws MovieDbException {
	super(PreferenceManager.getInstance().getPreferences().getTmdbApiKey());
    }

    private static TheMovieDbApi instance;

    public static TheMovieDbApi getInstance() throws MovieDbException {
	if (instance == null) {
	    instance = new TheMovieDbApi();
	}
	return instance;
    }

    public static Movie getMovie(MovieInfo movieInfo, Torrent torrent) {
	Movie movie = torrent != null ? torrent.getMovie() : null;
	if (movie == null) {
	    movie = new Movie();
	    if (torrent != null) {
		torrent.setMovie(movie);
	    }
	}
	movie.setTitle(movieInfo.getTitle());
	movie.setOriginalTitle(movieInfo.getOriginalTitle());
	if (StringUtils.isNotBlank(movieInfo.getOverview())) {
	    movie.setPlot(movieInfo.getOverview());
	}
	try {
	    Configuration configuration = instance.getConfiguration();
	    String posterPath = configuration.createImageUrl(movieInfo.getPosterPath(), configuration.getPosterSizes().get(0)).toExternalForm();
	    if (StringUtils.isNotBlank(posterPath)) {
		movie.setPoster(posterPath);
	    }
	} catch (MovieDbException e) {
	    e.printStackTrace();
	}
	return movie;
    }
}
