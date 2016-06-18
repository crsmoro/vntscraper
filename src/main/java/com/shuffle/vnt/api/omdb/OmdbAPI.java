package com.shuffle.vnt.api.omdb;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.shuffle.vnt.core.parser.bean.Movie;
import com.shuffle.vnt.core.parser.bean.Torrent;

public class OmdbAPI {

	private static final Log log = LogFactory.getLog(OmdbAPI.class);

	private String imdbId;

	private String title;

	public enum Type {
		MOVIE, SERIES, EPISODE
	}

	private Type type;

	private long yearRelease;

	private boolean tomatoes;

	public static OmdbResponse getById(String imdbId) {
		OmdbAPI omdbAPI = new OmdbAPI(imdbId);
		return omdbAPI.fetchResult();
	}

	public static OmdbResponse getByTitle(String title) {
		OmdbAPI omdbAPI = new OmdbAPI();
		omdbAPI.setTitle(title);
		return omdbAPI.fetchResult();
	}

	public OmdbAPI() {
		super();
	}

	public OmdbAPI(String imdbId) {
		super();
		this.imdbId = imdbId;
	}

	public String getImdbId() {
		return imdbId;
	}

	/**
	 * A valid IMDb ID (e.g. tt1285016)
	 * 
	 * @param imdbId
	 */
	public void setImdbId(String imdbId) {
		this.imdbId = imdbId;
	}

	public String getTitle() {
		return title;
	}

	/**
	 * Movie title to search for.
	 * 
	 * @param title
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	public Type getType() {
		return type;
	}

	/**
	 * Type of result to return.
	 * 
	 * @param type
	 */
	public void setType(Type type) {
		this.type = type;
	}

	public long getYearRelease() {
		return yearRelease;
	}

	/**
	 * Year of release.
	 * 
	 * @param yearRelease
	 */
	public void setYearRelease(long yearRelease) {
		this.yearRelease = yearRelease;
	}

	public boolean isTomatoes() {
		return tomatoes;
	}

	/**
	 * Include Rotten Tomatoes ratings.
	 * 
	 * @param tomatoes
	 */
	public void setTomatoes(boolean tomatoes) {
		this.tomatoes = tomatoes;
	}

	private List<NameValuePair> buildNameValuePair() {
		List<NameValuePair> nameValuePairs = new ArrayList<>();
		if (StringUtils.isNotBlank(getImdbId())) {
			nameValuePairs.add(new BasicNameValuePair("i", "tt" + StringUtils.leftPad(getImdbId().substring(2), 7, "0")));
		}
		if (StringUtils.isNotBlank(getTitle())) {
			nameValuePairs.add(new BasicNameValuePair("t", getTitle()));
		}
		if (getType() != null) {
			nameValuePairs.add(new BasicNameValuePair("type", getType().toString()));
		}
		if (yearRelease != 0l) {
			nameValuePairs.add(new BasicNameValuePair("y", String.valueOf(getYearRelease())));
		}
		if (isTomatoes()) {
			nameValuePairs.add(new BasicNameValuePair("tomatoes", String.valueOf(isTomatoes())));
		}
		return nameValuePairs;
	}

	public OmdbResponse fetchResult() {
		OmdbResponse omdbResponse = null;
		try {
			CloseableHttpClient httpClient = HttpClientBuilder.create().setRedirectStrategy(new LaxRedirectStrategy()).build();

			List<NameValuePair> nameValuePairs = buildNameValuePair();

			String url = "http://www.omdbapi.com/?" + URLEncodedUtils.format(nameValuePairs, "UTF-8");
			log.debug("URL : " + url);
			HttpGet httpGet = new HttpGet(url);
			httpGet.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:43.0) Gecko/20100101 Firefox/43.0.3");
			httpGet.addHeader("Accept-Encoding", "gzip");
			CloseableHttpResponse imdbResponse = httpClient.execute(httpGet);

			HttpEntity httpEntity = imdbResponse.getEntity();

			String content = EntityUtils.toString(httpEntity);
			log.debug("content : " + content);
			//FIXME to use jackson objectmapper
			Gson gson = new GsonBuilder().serializeNulls().create();
			omdbResponse = gson.fromJson(content, OmdbResponse.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return omdbResponse;
	}

	public static Movie getMovie(OmdbResponse omdbResponse, Torrent torrent) {
		Movie movie = torrent != null ? torrent.getMovie() : null;
		if (movie == null) {
			movie = new Movie();
			if (torrent != null) {
				torrent.setMovie(movie);
			}
		}
		movie.setTitle(omdbResponse.getTitle());
		movie.setOriginalTitle(omdbResponse.getTitle());
		movie.setYear(Long.valueOf(omdbResponse.getYear().replaceAll("[^0-9&&[^\\.]]", "")));
		movie.setPlot(omdbResponse.getPlot());
		omdbResponse.setRuntime(omdbResponse.getRuntime().replaceAll("[^0-9&&[^\\.]]", ""));
		movie.setRuntime(StringUtils.isNotBlank(omdbResponse.getRuntime()) ? Long.valueOf(omdbResponse.getRuntime()) : 0l);
		omdbResponse.setImdbRating(omdbResponse.getImdbRating().replaceAll("[^0-9&&[^\\.]]", ""));
		movie.setImdbRating(StringUtils.isNotBlank(omdbResponse.getImdbRating()) ? Double.valueOf(omdbResponse.getImdbRating()) : 0d);
		omdbResponse.setImdbVotes(omdbResponse.getImdbVotes().replaceAll("[^0-9&&[^\\.]]", ""));
		movie.setImdbVotes(StringUtils.isNotBlank(omdbResponse.getImdbVotes()) ? Long.valueOf(omdbResponse.getImdbVotes()) : 0l);
		movie.setPoster(omdbResponse.getPoster());
		return movie;
	}
}
