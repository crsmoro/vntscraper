package com.shuffle.vnt.parser;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.jsoup.Connection;
import org.jsoup.Connection.Method;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.shuffle.vnt.configuration.bean.TrackerUser;
import com.shuffle.vnt.core.parser.TrackerConfig;
import com.shuffle.vnt.core.parser.TrackerConfig.ParameterType;
import com.shuffle.vnt.core.parser.bean.Body;
import com.shuffle.vnt.core.parser.bean.QueryParameters;
import com.shuffle.vnt.core.parser.bean.Row;
import com.shuffle.vnt.core.parser.bean.Torrent;
import com.shuffle.vnt.core.parser.bean.TorrentFilter;
import com.shuffle.vnt.core.parser.bean.TorrentFilter.FilterOperation;
import com.shuffle.vnt.core.parser.bean.TrackerCategory;
import com.shuffle.vnt.core.service.TrackerManager;
import com.shuffle.vnt.util.VntUtil;

public class VntTrackerManager implements TrackerManager {

    private static final Log log = LogFactory.getLog(VntTrackerManager.class);

    private boolean authenticated;

    private TrackerConfig trackerConfig;

    private TrackerUser trackerUser;

    private QueryParameters queryParameters;

    private long page;

    private Map<String, String> cookies = new HashMap<String, String>();

    private int attempt = 0;

    private int maxAttempts = 2;

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
    public TrackerConfig getTrackerConfig() {
	return trackerConfig;
    }

    @Override
    public void setTrackerConfig(TrackerConfig trackerConfig) {
	this.trackerConfig = trackerConfig;
    }

    @Override
    public TrackerUser getTrackerUser() {
	return trackerUser;
    }

    @Override
    public void setTrackerUser(TrackerUser trackerUserData) {
	this.trackerUser = trackerUserData;
    }

    @Override
    public QueryParameters getQueryParameters() {
	return queryParameters;
    }

    @Override
    public void setQueryParameters(QueryParameters queryParameters) {
	this.queryParameters = queryParameters;
    }

    @Override
    public void setPage(long page) {
	this.page = page;
    }

    @Override
    public long getPage() {
	return page;
    }

    private Body executeSearch() {
	Document document = null;
	Body body = new Body();
	try {
	    cookies = cookies.isEmpty() ? VntUtil.getCookies(VntUtil.getCookies(getTrackerConfig().getName(), getTrackerUser().getUsername())) : cookies;
	    StringBuilder url = new StringBuilder();
	    url.append(getTrackerConfig().getUrl() + (getTrackerConfig().getUrl().contains(urlPatterns.get(getTrackerConfig().getParameterType()).get("initial-separator"))
		    ? (getTrackerConfig().getParameterType().equals(ParameterType.DEFAULT) ? urlPatterns.get(getTrackerConfig().getParameterType()).get("separator") : "")
		    : urlPatterns.get(getTrackerConfig().getParameterType()).get("initial-separator")));

	    StringBuilder urlCategory = new StringBuilder();
	    for (TrackerCategory trackerCategory : getQueryParameters().getTrackerCategories()) {
		if (getTrackerConfig().getCategories().contains(trackerCategory)) {
		    if (getTrackerConfig().getParameterType().equals(ParameterType.DEFAULT)) {
			if (urlCategory.length() > 0) {
			    urlCategory.append(urlPatterns.get(getTrackerConfig().getParameterType()).get("separator"));
			}
			if (StringUtils.isNotBlank(trackerCategory.getProperty())) {
			    urlCategory.append(trackerCategory.getProperty());
			} else if (StringUtils.isNotBlank(getTrackerConfig().getCategoryField())) {
			    urlCategory.append(getTrackerConfig().getCategoryField());
			}
			if (StringUtils.isNotBlank(trackerCategory.getCode())) {
			    urlCategory.append(urlPatterns.get(getTrackerConfig().getParameterType()).get("assigner") + trackerCategory.getCode());
			}
		    } else if (getTrackerConfig().getParameterType().equals(ParameterType.PATH)) {
			if (urlCategory.length() > 0) {
			    urlCategory.append(",");
			}
			if (StringUtils.isNotBlank(trackerCategory.getCode())) {
			    urlCategory.append(trackerCategory.getCode());
			}
		    }
		}
	    }
	    if (getTrackerConfig().getParameterType().equals(ParameterType.PATH) && StringUtils.isNotBlank(getTrackerConfig().getCategoryField()) && !getQueryParameters().getTrackerCategories().isEmpty()) {
		url.append(getTrackerConfig().getCategoryField() + urlPatterns.get(getTrackerConfig().getParameterType()).get("separator"));
	    }
	    if (urlCategory.length() > 0) {
		url.append(urlCategory.toString());
	    }
	    if (!url.substring(url.length() - 1, url.length()).equals(urlPatterns.get(getTrackerConfig().getParameterType()).get("separator"))) {
		url.append(urlPatterns.get(getTrackerConfig().getParameterType()).get("separator"));
	    }
	    url.append(getTrackerConfig().getSearchField() + urlPatterns.get(getTrackerConfig().getParameterType()).get("assigner") + getQueryParameters().getSearch());
	    url.append(urlPatterns.get(getTrackerConfig().getParameterType()).get("separator") + getTrackerConfig().getPageField() + urlPatterns.get(getTrackerConfig().getParameterType()).get("assigner") + getTrackerConfig().getPageValue(getPage()));
	    log.info("URL : " + url.toString());
	    Connection connection = Jsoup.connect(url.toString()).timeout(30000).userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64; rv:43.0) Gecko/20100101 Firefox/43.0.3").cookies(cookies).method(Method.GET);
	    Response response = connection.execute();
	    log.debug("Status Code : " + response.statusCode());
	    log.debug("Status Message : " + response.statusMessage());
	    document = response.parse();
	    if (response.statusCode() != 200 || StringUtils.isBlank(document.body().html())) {
		return null;
	    }
	    body.setContent(document.body().html());
	    log.debug("Body Content : " + body.getContent());
	    return body;
	} catch (IOException e) {
	    e.printStackTrace();
	    return null;
	}
    }

    @Override
    public List<Torrent> fetchTorrents() {
	if (getTrackerConfig().getTorrentParser() == null) {
	    throw new IllegalArgumentException("Torrent Parser not set");
	}
	if (!isAuthenticated() && VntUtil.cookieExpired(getTrackerConfig().getName(), getTrackerUser().getUsername()) && !authenticate()) {
	    return Collections.emptyList();
	}
	Body body = executeSearch();
	if (body == null) {
	    return Collections.emptyList();
	}
	boolean auth = getTrackerConfig().isAuthenticated(body);
	while (attempt < maxAttempts && !auth) {
	    if (attempt > 0) {
		try {
		    Thread.sleep(1000);
		} catch (InterruptedException e) {

		}
	    }
	    attempt++;
	    auth = authenticate();
	    log.debug("attempt : " + attempt);
	}
	if (auth && attempt > 0) {
	    body = executeSearch();
	}
	return buildResults(body);
    }

    private List<Torrent> buildResults(Body body) {
	List<Torrent> torrents = new ArrayList<Torrent>();
	for (Row row : getTrackerConfig().getTorrentParser().getRows(body)) {
	    Torrent torrent = new Torrent();
	    torrent.setTracker(getTrackerConfig().getName());
	    torrent.setId(getTrackerConfig().getTorrentParser().getId(row));
	    torrent.setName(getTrackerConfig().getTorrentParser().getNome(row));
	    torrent.setCategory(getTrackerConfig().getTorrentParser().getCategory(row));
	    torrent.setAdded(getTrackerConfig().getTorrentParser().getAdded(row));
	    torrent.setSize(getTrackerConfig().getTorrentParser().getSize(row));
	    torrent.setLink(getTrackerConfig().getTorrentParser().getLink(row));
	    torrent.setDownloadLink(getTrackerConfig().getTorrentParser().getDownlodLink(row));

	    boolean add = getQueryParameters().getTorrentFilters().isEmpty();
	    if (!add) {
		torrent = getDetails(torrent);
	    }
	    log.debug(torrent);
	    for (TorrentFilter torrentFilter : getQueryParameters().getTorrentFilters()) {
		try {
		    Field field = Torrent.class.getDeclaredField(torrentFilter.getField());
		    field.setAccessible(true);
		    Object torrentValue = field.get(torrent);
		    if (torrentValue instanceof Long) {
			Long longTorrentValue = Long.valueOf(torrentValue.toString());
			Long longFilterValue = Long.valueOf(torrentFilter.getValue().toString());
			if (torrentFilter.getOperation().equals(FilterOperation.EQ) && longTorrentValue.equals(longFilterValue)) {
			    add = true;
			    continue;
			} else if (torrentFilter.getOperation().equals(FilterOperation.NE) && !longTorrentValue.equals(longFilterValue)) {
			    add = true;
			    continue;
			} else if (torrentFilter.getOperation().equals(FilterOperation.GT) && longTorrentValue.compareTo(longFilterValue) > 0) {
			    add = true;
			    continue;
			} else if (torrentFilter.getOperation().equals(FilterOperation.LT) && longTorrentValue.compareTo(longFilterValue) < 0) {
			    add = true;
			    continue;
			} else if (torrentFilter.getOperation().equals(FilterOperation.GE) && longTorrentValue.compareTo(longFilterValue) >= 0) {
			    add = true;
			    continue;
			} else if (torrentFilter.getOperation().equals(FilterOperation.LE) && longTorrentValue.compareTo(longFilterValue) <= 0) {
			    add = true;
			    continue;
			}
		    } else if (torrentValue instanceof Double) {
			Double doubleTorrentValue = Double.valueOf(torrentValue.toString());
			Double doubleFilterValue = Double.valueOf(torrentFilter.getValue().toString());
			if (torrentFilter.getOperation().equals(FilterOperation.EQ) && doubleTorrentValue.equals(doubleFilterValue)) {
			    add = true;
			    continue;
			} else if (torrentFilter.getOperation().equals(FilterOperation.NE) && !doubleTorrentValue.equals(doubleFilterValue)) {
			    add = true;
			    continue;
			} else if (torrentFilter.getOperation().equals(FilterOperation.GT) && doubleTorrentValue.compareTo(doubleFilterValue) > 0) {
			    add = true;
			    continue;
			} else if (torrentFilter.getOperation().equals(FilterOperation.LT) && doubleTorrentValue.compareTo(doubleFilterValue) < 0) {
			    add = true;
			    continue;
			} else if (torrentFilter.getOperation().equals(FilterOperation.GE) && doubleTorrentValue.compareTo(doubleFilterValue) >= 0) {
			    add = true;
			    continue;
			} else if (torrentFilter.getOperation().equals(FilterOperation.LE) && doubleTorrentValue.compareTo(doubleFilterValue) <= 0) {
			    add = true;
			    continue;
			}
		    } else if (torrentValue instanceof String) {
			String stringTorrentValue = torrentValue.toString();
			String stringFilterValue = torrentFilter.getValue().toString();
			if (torrentFilter.getOperation().equals(FilterOperation.EQ) && stringTorrentValue.equals(stringFilterValue)) {
			    add = true;
			    continue;
			} else if (torrentFilter.getOperation().equals(FilterOperation.NE) && !stringTorrentValue.equals(stringFilterValue)) {
			    add = true;
			    continue;
			} else if (torrentFilter.getOperation().equals(FilterOperation.GT) && stringTorrentValue.compareTo(stringFilterValue) > 0) {
			    add = true;
			    continue;
			} else if (torrentFilter.getOperation().equals(FilterOperation.LT) && stringTorrentValue.compareTo(stringFilterValue) < 0) {
			    add = true;
			    continue;
			} else if (torrentFilter.getOperation().equals(FilterOperation.GE) && stringTorrentValue.compareTo(stringFilterValue) >= 0) {
			    add = true;
			    continue;
			} else if (torrentFilter.getOperation().equals(FilterOperation.LE) && stringTorrentValue.compareTo(stringFilterValue) <= 0) {
			    add = true;
			    continue;
			} else if (torrentFilter.getOperation().equals(FilterOperation.LIKE) && stringTorrentValue.contains(stringFilterValue)) {
			    add = true;
			    continue;
			} else if (torrentFilter.getOperation().equals(FilterOperation.NLIKE) && !stringTorrentValue.contains(stringFilterValue)) {
			    add = true;
			    continue;
			} else if (torrentFilter.getOperation().equals(FilterOperation.REGEX) && stringTorrentValue.matches(stringFilterValue)) {
			    add = true;
			    continue;
			}
		    }

		} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
		    e.printStackTrace();
		}
	    }
	    if (add) {
		log.debug("added on return list!");
		torrents.add(torrent);
	    }
	}
	return torrents;
    }

    @Override
    public Torrent getDetails(Torrent torrent) {
	if (getTrackerConfig().getTorrentDetailedParser() == null) {
	    throw new IllegalArgumentException("Torrent Detailed Parser not set");
	}
	if (!isAuthenticated() && VntUtil.cookieExpired(getTrackerConfig().getName(), getTrackerUser().getUsername()) && !authenticate()) {
	    return null;
	}
	Document document = null;
	Body body = new Body();
	try {
	    cookies = cookies.isEmpty() ? VntUtil.getCookies(VntUtil.getCookies(getTrackerConfig().getName(), getTrackerUser().getUsername())) : cookies;
	    StringBuilder url = new StringBuilder();
	    url.append(torrent.getLink());
	    log.info("URL : " + url.toString());
	    document = Jsoup.connect(url.toString()).userAgent("Mozilla").cookies(cookies).get();
	    body.setContent(document.body().html());
	    torrent.setDetailed(true);
	    torrent.setYear(getTrackerConfig().getTorrentDetailedParser().getAno(body));
	    torrent.setYoutubeLink(getTrackerConfig().getTorrentDetailedParser().getYoutubeLink(body));
	    torrent.setImdbLink(getTrackerConfig().getTorrentDetailedParser().getImdbLink(body));
	    return torrent;

	} catch (IOException dontcare) {
	    return null;
	}
    }

    @Override
    public boolean authenticate() {
	if (!getTrackerUser().getTracker().equals(getTrackerConfig().getName())) {
	    throw new IllegalArgumentException("TrackerUserData does not match TrackerConfig");
	}
	return authenticate(getTrackerUser().getUsername(), getTrackerUser().getPassword());
    }

    private boolean authenticate(String username, String password) {
	log.info("Authenticate");
	try {
	    CookieStore cookieStore = new BasicCookieStore();

	    CloseableHttpClient httpClient = HttpClientBuilder.create().setDefaultCookieStore(cookieStore).setRedirectStrategy(new LaxRedirectStrategy()).build();

	    List<NameValuePair> nameValuePairs = new ArrayList<>();
	    nameValuePairs.add(new BasicNameValuePair(getTrackerConfig().getUsernameField(), getTrackerUser().getUsername()));
	    nameValuePairs.add(new BasicNameValuePair(getTrackerConfig().getPasswordField(), getTrackerUser().getPassword()));
	    for (final String additionalParametersKey : getTrackerConfig().getAuthenticationAdditionalParameters().keySet()) {
		nameValuePairs.add(new BasicNameValuePair(additionalParametersKey, getTrackerConfig().getAuthenticationAdditionalParameters().get(additionalParametersKey)));
	    }
	    CloseableHttpResponse response = null;

	    if (getTrackerConfig().getAuthenticationMethod().equals("POST")) {
		HttpPost httpPost = new HttpPost(getTrackerConfig().getAuthenticationUrl());
		httpPost.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:43.0) Gecko/20100101 Firefox/43.0.3");
		httpPost.addHeader("Accept-Encoding", "gzip");
		httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
		response = httpClient.execute(httpPost);
	    } else if (getTrackerConfig().getAuthenticationMethod().equals("GET")) {
		HttpGet httpGet = new HttpGet(getTrackerConfig().getAuthenticationUrl() + URLEncodedUtils.format(nameValuePairs, "UTF-8"));
		httpGet.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:43.0) Gecko/20100101 Firefox/43.0.3");
		httpGet.addHeader("Accept-Encoding", "gzip");
		response = httpClient.execute(httpGet);
	    }
	    
	    if (response.getStatusLine().getStatusCode() != 200) {
		return false;
	    }

	    HttpEntity httpEntity = response.getEntity();

	    String content = EntityUtils.toString(httpEntity);

	    log.debug("Status Code : " + response.getStatusLine().getStatusCode());
	    log.debug("Status Message : " + response.getStatusLine().getReasonPhrase());
	    Body body = new Body();
	    body.setContent(content);
	    authenticated = getTrackerConfig().isAuthenticated(body);
	    if (isAuthenticated()) {
		cookies = VntUtil.getCookies(cookieStore.getCookies());
		VntUtil.updateCookies(getTrackerConfig().getName(), getTrackerUser().getUsername(), cookies);
	    }
	} catch (IOException e) {
	    log.error("Error when trying to authenticate", e);
	}
	return isAuthenticated();
    }

    private boolean isAuthenticated() {
	return authenticated;
    }

}
