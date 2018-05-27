package com.shuffle.vnt.api.themoviedb;

import org.apache.commons.lang3.StringUtils;

import com.omertron.themoviedbapi.MovieDbException;
import com.omertron.themoviedbapi.model.config.Configuration;
import com.omertron.themoviedbapi.model.movie.MovieBasic;
import com.shuffle.vnt.api.bean.Movie;
import com.shuffle.vnt.core.configuration.PreferenceManager;

public class TheMovieDbApi extends com.omertron.themoviedbapi.TheMovieDbApi {
	public TheMovieDbApi() throws MovieDbException {
		super(PreferenceManager.getPreferences().getTmdbApiKey());
	}

	private static TheMovieDbApi instance;

	public static TheMovieDbApi getInstance() throws MovieDbException {
		if (instance == null) {
			instance = new TheMovieDbApi();
		}
		return instance;
	}

	public static Movie getMovie(MovieBasic movieBasic) {
		return getMovie(movieBasic, null);
	}

	public static Movie getMovie(MovieBasic movieBasic, Movie movie) {
		if (movie == null) {
			movie = new Movie();
		}
		movie.setTitle(movieBasic.getTitle());
		movie.setOriginalTitle(movieBasic.getOriginalTitle());
		if (StringUtils.isNotBlank(movieBasic.getOverview())) {
			movie.setPlot(movieBasic.getOverview());
		}
		if (movie.getImdbRating() == 0D) {
			movie.setImdbRating(movieBasic.getVoteAverage());
		}
		if (movie.getImdbVotes() == 0D) {
			movie.setImdbVotes(movieBasic.getVoteCount());
		}
		try {
			Configuration configuration = instance.getConfiguration();
			String posterPath = configuration.createImageUrl(movieBasic.getPosterPath(), configuration.getPosterSizes().get(0)).toExternalForm();
			if (StringUtils.isNotBlank(posterPath)) {
				movie.setPoster(posterPath);
			}
		} catch (MovieDbException e) {
			e.printStackTrace();
		}
		return movie;
	}
}
