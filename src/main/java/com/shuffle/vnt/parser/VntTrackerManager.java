package com.shuffle.vnt.parser;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.annotation.concurrent.ThreadSafe;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.StatusLine;

import com.omertron.themoviedbapi.MovieDbException;
import com.omertron.themoviedbapi.model.movie.MovieInfo;
import com.shuffle.vnt.api.omdb.OmdbAPI;
import com.shuffle.vnt.api.themoviedb.TheMovieDbApi;
import com.shuffle.vnt.core.configuration.PreferenceManager;
import com.shuffle.vnt.core.exception.AuthenticationException;
import com.shuffle.vnt.core.exception.TimeoutException;
import com.shuffle.vnt.core.exception.VntException;
import com.shuffle.vnt.core.parser.Tracker;
import com.shuffle.vnt.core.parser.Tracker.ParameterType;
import com.shuffle.vnt.core.parser.bean.Body;
import com.shuffle.vnt.core.parser.bean.QueryParameters;
import com.shuffle.vnt.core.parser.bean.Row;
import com.shuffle.vnt.core.parser.bean.Torrent;
import com.shuffle.vnt.core.parser.bean.TorrentFilter;
import com.shuffle.vnt.core.parser.bean.TorrentFilter.FilterOperation;
import com.shuffle.vnt.core.parser.bean.TrackerCategory;
import com.shuffle.vnt.core.service.TrackerManager;
import com.shuffle.vnt.httprequest.HttpRequestBuilder;
import com.shuffle.vnt.util.VntUtil;

@ThreadSafe
public class VntTrackerManager implements TrackerManager {

	private static final Log log = LogFactory.getLog(VntTrackerManager.class);

	private boolean authenticated;

	private Tracker trackerConfig;

	private String username;

	private String password;

	private QueryParameters queryParameters;

	private long page;

	private int maxAttempts = 2;

	private HttpRequestBuilder httpRequest = new HttpRequestBuilder();

	private Lock lock = new ReentrantLock();

	private Map<ParameterType, Map<String, String>> urlPatterns = new HashMap<>();

	{
		Map<String, String> urlPatternsDefault = new HashMap<>();
		urlPatternsDefault.put("initial-separator", "?");
		urlPatternsDefault.put("separator", "&");
		urlPatternsDefault.put("assigner", "=");
		urlPatterns.put(ParameterType.DEFAULT, urlPatternsDefault);

		Map<String, String> urlPatternsPath = new HashMap<>();
		urlPatternsPath.put("initial-separator", "/");
		urlPatternsPath.put("separator", "/");
		urlPatternsPath.put("assigner", "/");
		urlPatterns.put(ParameterType.PATH, urlPatternsPath);
	}

	@Override
	public Tracker getTracker() {
		return trackerConfig;
	}

	@Override
	public void setTracker(Tracker trackerConfig) {
		lock.lock();
		this.trackerConfig = trackerConfig;
		lock.unlock();
	}

	@Override
	public String getUsername() {
		return this.username;
	}

	@Override
	public void setUsername(String username) {
		lock.lock();
		this.username = username;
		lock.unlock();
	}

	@Override
	public String getPassword() {
		return this.password;
	}

	@Override
	public void setPassword(String password) {
		lock.lock();
		this.password = password;
		lock.unlock();
	}

	@Override
	public void setUser(String username, String password) {
		lock.lock();
		this.username = username;
		this.password = password;
		lock.unlock();
	}

	@Override
	public QueryParameters getQueryParameters() {
		return queryParameters;
	}

	@Override
	public void setQueryParameters(QueryParameters queryParameters) {
		lock.lock();
		this.queryParameters = queryParameters;
		lock.unlock();
	}

	@Override
	public void setPage(long page) {
		lock.lock();
		this.page = page;
		lock.unlock();
	}

	@Override
	public long getPage() {
		return page;
	}

	private List<Torrent> buildResults(Body body) {
		log.debug("QueryParameters : " + getQueryParameters());
		List<Torrent> torrents = new ArrayList<Torrent>();
		for (Row row : getTracker().getTorrentParser().getRows(body)) {
			Torrent torrent = new Torrent();
			torrent.setTracker(getTracker());
			torrent.setUsername(getUsername());
			torrent.setPassword(getPassword());
			torrent.setId(getTracker().getTorrentParser().getId(row));
			torrent.setName(getTracker().getTorrentParser().getNome(row));
			torrent.setCategory(getTracker().getTorrentParser().getCategory(row));
			torrent.setAdded(getTracker().getTorrentParser().getAdded(row));
			torrent.setSize(getTracker().getTorrentParser().getSize(row));
			torrent.setLink(getTracker().getTorrentParser().getLink(row));
			torrent.setDownloadLink(getTracker().getTorrentParser().getDownlodLink(row));

			//TODO make as parameter
			boolean add = getQueryParameters().getTorrentFilters().isEmpty();
			if (!add) {
				log.debug("getting torrent details");
				getDetails(torrent);
			}
			log.trace(torrent);
			int totalFilters = getQueryParameters().getTorrentFilters().size();
			int totalPass = 0;
			for (TorrentFilter torrentFilter : getQueryParameters().getTorrentFilters()) {
				try {
					Field field = Torrent.class.getDeclaredField(torrentFilter.getField());
					field.setAccessible(true);
					Object torrentValue = field.get(torrent);
					if (torrentValue instanceof Long) {
						Long longTorrentValue = Long.valueOf(torrentValue.toString());
						Long longFilterValue = Long.valueOf(torrentFilter.getValue().toString());
						if (torrentFilter.getOperation().equals(FilterOperation.EQ) && longTorrentValue.equals(longFilterValue)) {
							totalPass++;
							continue;
						} else if (torrentFilter.getOperation().equals(FilterOperation.NE) && !longTorrentValue.equals(longFilterValue)) {
							totalPass++;
							continue;
						} else if (torrentFilter.getOperation().equals(FilterOperation.GT) && longTorrentValue.compareTo(longFilterValue) > 0) {
							totalPass++;
							continue;
						} else if (torrentFilter.getOperation().equals(FilterOperation.LT) && longTorrentValue.compareTo(longFilterValue) < 0) {
							totalPass++;
							continue;
						} else if (torrentFilter.getOperation().equals(FilterOperation.GE) && longTorrentValue.compareTo(longFilterValue) >= 0) {
							totalPass++;
							continue;
						} else if (torrentFilter.getOperation().equals(FilterOperation.LE) && longTorrentValue.compareTo(longFilterValue) <= 0) {
							totalPass++;
							continue;
						}
					} else if (torrentValue instanceof Double) {
						Double doubleTorrentValue = Double.valueOf(torrentValue.toString());
						Double doubleFilterValue = Double.valueOf(torrentFilter.getValue().toString());
						if (torrentFilter.getOperation().equals(FilterOperation.EQ) && doubleTorrentValue.equals(doubleFilterValue)) {
							totalPass++;
							continue;
						} else if (torrentFilter.getOperation().equals(FilterOperation.NE) && !doubleTorrentValue.equals(doubleFilterValue)) {
							totalPass++;
							continue;
						} else if (torrentFilter.getOperation().equals(FilterOperation.GT) && doubleTorrentValue.compareTo(doubleFilterValue) > 0) {
							totalPass++;
							continue;
						} else if (torrentFilter.getOperation().equals(FilterOperation.LT) && doubleTorrentValue.compareTo(doubleFilterValue) < 0) {
							totalPass++;
							continue;
						} else if (torrentFilter.getOperation().equals(FilterOperation.GE) && doubleTorrentValue.compareTo(doubleFilterValue) >= 0) {
							totalPass++;
							continue;
						} else if (torrentFilter.getOperation().equals(FilterOperation.LE) && doubleTorrentValue.compareTo(doubleFilterValue) <= 0) {
							totalPass++;
							continue;
						}
					} else if (torrentValue instanceof String) {
						String stringTorrentValue = torrentValue.toString();
						String stringFilterValue = torrentFilter.getValue().toString();
						if (torrentFilter.getOperation().equals(FilterOperation.EQ) && stringTorrentValue.equals(stringFilterValue)) {
							totalPass++;
							continue;
						} else if (torrentFilter.getOperation().equals(FilterOperation.NE) && !stringTorrentValue.equals(stringFilterValue)) {
							totalPass++;
							continue;
						} else if (torrentFilter.getOperation().equals(FilterOperation.GT) && stringTorrentValue.compareTo(stringFilterValue) > 0) {
							totalPass++;
							continue;
						} else if (torrentFilter.getOperation().equals(FilterOperation.LT) && stringTorrentValue.compareTo(stringFilterValue) < 0) {
							totalPass++;
							continue;
						} else if (torrentFilter.getOperation().equals(FilterOperation.GE) && stringTorrentValue.compareTo(stringFilterValue) >= 0) {
							totalPass++;
							continue;
						} else if (torrentFilter.getOperation().equals(FilterOperation.LE) && stringTorrentValue.compareTo(stringFilterValue) <= 0) {
							totalPass++;
							continue;
						} else if (torrentFilter.getOperation().equals(FilterOperation.LIKE) && stringTorrentValue.contains(stringFilterValue)) {
							totalPass++;
							continue;
						} else if (torrentFilter.getOperation().equals(FilterOperation.NLIKE) && !stringTorrentValue.contains(stringFilterValue)) {
							totalPass++;
							continue;
						} else if (torrentFilter.getOperation().equals(FilterOperation.REGEX) && stringTorrentValue.matches(stringFilterValue)) {
							totalPass++;
							continue;
						}
					}

				} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
					e.printStackTrace();
				}
			}
			add = totalFilters == totalPass;
			if (add) {
				log.trace("added on return list!");
				torrents.add(torrent);
			}
		}
		return torrents;
	}

	@Override
	public List<Torrent> fetchTorrents() {
		lock.lock();
		try {
			StringBuilder url = new StringBuilder();
			url.append(getTracker().getUrl() + (getTracker().getUrl().contains(urlPatterns.get(getTracker().getParameterType()).get("initial-separator"))
					? (getTracker().getParameterType().equals(ParameterType.DEFAULT) ? urlPatterns.get(getTracker().getParameterType()).get("separator") : "")
					: urlPatterns.get(getTracker().getParameterType()).get("initial-separator")));

			StringBuilder urlCategory = new StringBuilder();
			for (TrackerCategory trackerCategory : getQueryParameters().getTrackerCategories()) {
				if (getTracker().getCategories().contains(trackerCategory)) {
					if (getTracker().getParameterType().equals(ParameterType.DEFAULT)) {
						if (urlCategory.length() > 0) {
							urlCategory.append(urlPatterns.get(getTracker().getParameterType()).get("separator"));
						}
						if (StringUtils.isNotBlank(trackerCategory.getProperty())) {
							urlCategory.append(trackerCategory.getProperty());
						} else if (StringUtils.isNotBlank(getTracker().getCategoryField())) {
							urlCategory.append(getTracker().getCategoryField());
						}
						if (StringUtils.isNotBlank(trackerCategory.getCode())) {
							urlCategory.append(urlPatterns.get(getTracker().getParameterType()).get("assigner") + trackerCategory.getCode());
						}
					} else if (getTracker().getParameterType().equals(ParameterType.PATH)) {
						if (urlCategory.length() > 0) {
							urlCategory.append(",");
						}
						if (StringUtils.isNotBlank(trackerCategory.getCode())) {
							urlCategory.append(trackerCategory.getCode());
						}
					}
				}
			}
			if (getTracker().getParameterType().equals(ParameterType.PATH) && StringUtils.isNotBlank(getTracker().getCategoryField()) && !getQueryParameters().getTrackerCategories().isEmpty()) {
				url.append(getTracker().getCategoryField() + urlPatterns.get(getTracker().getParameterType()).get("separator"));
			}
			if (urlCategory.length() > 0) {
				url.append(urlCategory.toString());
			}
			if (!url.substring(url.length() - 1, url.length()).equals(urlPatterns.get(getTracker().getParameterType()).get("separator"))) {
				url.append(urlPatterns.get(getTracker().getParameterType()).get("separator"));
			}
			url.append(getTracker().getSearchField() + urlPatterns.get(getTracker().getParameterType()).get("assigner") + getQueryParameters().getSearch());
			url.append(urlPatterns.get(getTracker().getParameterType()).get("separator") + getTracker().getPageField() + urlPatterns.get(getTracker().getParameterType()).get("assigner") + getTracker().getPageValue(getPage()));
			log.info("URL : " + url.toString());

			List<Torrent> torrents = new ArrayList<>();
			if (authenticate()) {
				httpRequest.getParameters().clear();
				httpRequest.setHttpMethod("GET");
				httpRequest.setUrl(url.toString());
				getLoggedContent(httpRequest, new VntAttemptListener() {

					@Override
					public void loadFailed(StatusLine statusLine, String content) {
						throw new TimeoutException("Timeout trying to fetch torrents");
					}

					@Override
					public void contentLoaded(String content) {
						torrents.addAll(buildResults(new Body(content)));
					}
				});
			} else {
				throw new AuthenticationException(getTracker().getName() +  " Invalid Username and/or password");
			}
			return torrents;
		} finally {
			lock.unlock();
		}
	}

	@Override
	public Torrent getDetails(Torrent torrent) {
		lock.lock();
		try {

			if (getTracker().getTorrentDetailedParser() == null) {
				throw new IllegalArgumentException("Torrent Detailed Parser not set");
			}
			if (authenticate()) {
				httpRequest.getParameters().clear();
				httpRequest.setHttpMethod("GET");
				httpRequest.setUrl(torrent.getLink());

				getLoggedContent(httpRequest, new VntAttemptListener() {

					@Override
					public void loadFailed(StatusLine statusLine, String content) {
						throw new TimeoutException("Timeout trying to fetch torrents");
					}

					@Override
					public void contentLoaded(String content) {
						Body body = new Body(content);
						torrent.setDetailed(true);
						torrent.setYear(getTracker().getTorrentDetailedParser().getAno(body));
						torrent.setYoutubeLink(getTracker().getTorrentDetailedParser().getYoutubeLink(body));
						torrent.setImdbLink(getTracker().getTorrentDetailedParser().getImdbLink(body));
						torrent.setContent(getTracker().getTorrentDetailedParser().getContent(body));
						if (StringUtils.isNotBlank(torrent.getImdbLink())) {
							if (PreferenceManager.getPreferences().isImdbActive()) {
								OmdbAPI.getMovie(OmdbAPI.getById(VntUtil.getImdbId(torrent.getImdbLink())), torrent);
							}
							if (PreferenceManager.getPreferences().isTmdbActive()) {
								try {
									MovieInfo movieInfo = TheMovieDbApi.getInstance().getMovieInfoImdb("tt" + StringUtils.leftPad(VntUtil.getImdbId(torrent.getImdbLink()).replace("tt", ""), 7, "0"),
											PreferenceManager.getPreferences().getTmdbLanguage());
									TheMovieDbApi.getMovie(movieInfo, torrent);
								} catch (MovieDbException e) {
									e.printStackTrace();
								}
							}
						}
					}
				});
			} else {
				throw new AuthenticationException(getTracker().getName() + " Invalid Username and/or password");
			}

			return torrent;
		} finally {
			lock.unlock();
		}
	}

	@Override
	public boolean authenticate() {
		lock.lock();
		try {
			int attemptLogin = 0;
			HttpRequestBuilder httpRequestBuilder = new HttpRequestBuilder();
			httpRequestBuilder.setUrl(getTracker().getAuthenticationUrl()).setHttpMethod(getTracker().getAuthenticationMethod());
			httpRequestBuilder.addParameter(getTracker().getUsernameField(), getUsername());
			httpRequestBuilder.addParameter(getTracker().getPasswordField(), getPassword());
			for (String name : getTracker().getAuthenticationAdditionalParameters().keySet()) {
				httpRequestBuilder.addParameter(name, getTracker().getAuthenticationAdditionalParameters().get(name));
			}
			while (!authenticated && attemptLogin < maxAttempts) {
				log.debug("Login Attempt " + attemptLogin);
				attempt(httpRequestBuilder, new VntAttemptListener() {

					@Override
					public void loadFailed(StatusLine statusLine, String content) {
						if (statusLine.getStatusCode() != 200 || StringUtils.isBlank(content)) {
							throw new TimeoutException("Timeout trying to login");
						}
					}

					@Override
					public void contentLoaded(String content) {
						authenticated = getTracker().isAuthenticated(new Body(content));
						if (authenticated) {
							httpRequest.getCookieStore().clear();
							httpRequest.addCookies(httpRequestBuilder.getCookies());
						}
					}
				});
				attemptLogin++;
				if (!authenticated) {
					try {
						Thread.sleep(TrackerManager.DELAY_BETWEEN_REQUESTS);
					} catch (InterruptedException dontcare) {

					}
				}
			}
			return authenticated;
		} finally {
			lock.unlock();
		}
	}

	private void getLoggedContent(HttpRequestBuilder httpRequest, VntAttemptListener listener) {
		int loggedAttempt = 0;
		boolean reqOk = false;
		HttpRequestBuilder httpRequestBuilder = null;
		try {
			httpRequestBuilder = httpRequest.clone();
		} catch (CloneNotSupportedException e1) {
			VntException vntException = new VntException("Generic Error");
			vntException.addSuppressed(e1);
			throw vntException;
		}

		while (!reqOk && loggedAttempt < maxAttempts) {
			log.debug("Logged attempt " + loggedAttempt);
			try {
				httpRequestBuilder.request();
			} catch (TimeoutException e) {
				log.info("Logged attempt " + loggedAttempt + " timeout, trying again", e);
			}
			String response = httpRequestBuilder.getStringResponse();
			if (httpRequestBuilder.getStatusLine().getStatusCode() == 200 && StringUtils.isNotBlank(response) && getTracker().isAuthenticated(new Body(response))) {
				log.debug("content ok, auth ok");
				log.trace(response);
				listener.contentLoaded(response);
				reqOk = true;
			} else if (httpRequestBuilder.getStatusLine().getStatusCode() == 200 && StringUtils.isBlank(response)
					&& !getTracker().isAuthenticated(new Body(httpRequestBuilder.setUrl(VntUtil.getDomain(getTracker().getAuthenticationUrl())).setHttpMethod("GET").request().getStringResponse()))) {
				log.debug("content nok, auth nok");
				httpRequestBuilder = httpRequest;
				authenticated = false;
				authenticate();
			}
			loggedAttempt++;
			if (!reqOk) {
				try {
					Thread.sleep(TrackerManager.DELAY_BETWEEN_REQUESTS);
				} catch (InterruptedException dontcare) {

				}
			}
		}
		if (!reqOk) {
			authenticated = false;
			listener.loadFailed(httpRequest.getStatusLine(), httpRequestBuilder.getStringResponse());
		}
	}

	private void attempt(HttpRequestBuilder httpRequest, VntAttemptListener listener) {
		boolean reqOk = false;
		int attempt = 0;
		while (!reqOk && attempt < maxAttempts) {
			log.debug("Attempt " + attempt);
			try {
				httpRequest.request();
			} catch (TimeoutException e) {
				log.info("Attempt " + attempt + " timeout, trying again", e);
			}
			String response = httpRequest.getStringResponse();
			if (httpRequest.getStatusLine().getStatusCode() == 200 && StringUtils.isNotBlank(response)) {
				listener.contentLoaded(response);
				reqOk = true;
			}
			attempt++;
			if (!reqOk) {
				try {
					Thread.sleep(TrackerManager.DELAY_BETWEEN_REQUESTS);
				} catch (InterruptedException dontcare) {

				}
			}
		}
		if (!reqOk) {
			listener.loadFailed(httpRequest.getStatusLine(), httpRequest.getStringResponse());
		}
	}

	@Override
	public InputStream download(Torrent torrent) {
		lock.lock();
		try {
			httpRequest.getParameters().clear();
			httpRequest.setHttpMethod("GET");
			httpRequest.setUrl(torrent.getDownloadLink());
			return new ByteArrayInputStream(httpRequest.request().getByteResponse());
		} finally {
			lock.unlock();
		}
	}
}
