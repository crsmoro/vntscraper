package com.shuffle.vnt.api.omdb;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.shuffle.vnt.api.bean.Movie;
import com.shuffle.vnt.core.configuration.PreferenceManager;

public class OmdbAPI {

	private static final Log log = LogFactory.getLog(OmdbAPI.class);
	
	private static PoolingHttpClientConnectionManager poolingHttpClientConnectionManager = new PoolingHttpClientConnectionManager();
	static {
		poolingHttpClientConnectionManager.setMaxTotal(500);
		poolingHttpClientConnectionManager.setDefaultMaxPerRoute(150);
	}
	
	private static final CloseableHttpClient httpClient = HttpClientBuilder.create().setRedirectStrategy(new LaxRedirectStrategy()).setDefaultRequestConfig(RequestConfig.custom().setConnectTimeout(30000).setConnectionRequestTimeout(10000).setSocketTimeout(40000).build()).setConnectionManager(poolingHttpClientConnectionManager).build();

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

			List<NameValuePair> nameValuePairs = buildNameValuePair();
			nameValuePairs.add(new BasicNameValuePair("apikey", PreferenceManager.getPreferences().getOmdbApiKey()));

			String url = "http://www.omdbapi.com/?" + URLEncodedUtils.format(nameValuePairs, "UTF-8");
			log.debug("URL : " + url);
			HttpGet httpGet = new HttpGet(url);
			httpGet.setHeader("User-Agent", "Firefox/60.0.1");
			httpGet.addHeader("Accept-Encoding", "gzip");
			httpGet.addHeader("Accept-Charset", "utf-8");
			log.debug("Trying to get info  from OMDb " + nameValuePairs.get(0));
			omdbResponse = CompletableFuture.supplyAsync(() -> {
				int maxRetries =3, retries = 0;
				boolean ok = false;
				while (!ok && retries < maxRetries) {
					log.debug("Try " + (retries + 1) + " | "  + nameValuePairs.get(0));
					try (CloseableHttpResponse imdbResponse = httpClient.execute(httpGet)) {
						if (imdbResponse.getStatusLine().getStatusCode() == 200) {
							ok = true;
							HttpEntity httpEntity = imdbResponse.getEntity();
							
							String content = EntityUtils.toString(httpEntity, StandardCharsets.UTF_8);
							log.debug("content : " + content);
							// FIXME to use jackson objectmapper
							Gson gson = new GsonBuilder().serializeNulls().create();
							return gson.fromJson(content, OmdbResponse.class);
						}
					}
					catch (ConnectTimeoutException e) {
						
					}
					catch (Exception e) {
						e.printStackTrace();
					}
					finally {
						httpGet.reset();
						log.debug("Try " + (retries + 1) + " finished " + " | "  + nameValuePairs.get(0));
						log.debug("OK " + ok  + " | "  + nameValuePairs.get(0));
						retries++;
						try {
							if (!ok) {
								TimeUnit.SECONDS.sleep(5);
							}
						} catch (InterruptedException e1) {
							
						}
					}
				}
				return null;
			}).get(3, TimeUnit.MINUTES);
		} catch (Exception e) {
			
		}
		return omdbResponse;
	}

	public static Movie getMovie(OmdbResponse omdbResponse) {
		if (omdbResponse == null || !omdbResponse.isResponse()) {
			return null;
		}
		Movie movie = new Movie();
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
