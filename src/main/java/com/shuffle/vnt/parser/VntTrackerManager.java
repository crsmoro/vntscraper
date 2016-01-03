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
import org.jsoup.Connection;
import org.jsoup.Connection.Method;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.shuffle.vnt.configuration.bean.TrackerUser;
import com.shuffle.vnt.core.parser.TrackerConfig;
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

    @Override
    public List<Torrent> fetchTorrents() {
	if (getTrackerConfig().getTorrentParser() == null) {
	    throw new IllegalArgumentException("Torrent Parser not set");
	}
	if (!isAuthenticated() && VntUtil.cookieExpired(getTrackerConfig().getName(), getTrackerUser().getUsername()) && !authenticate()) {
	    return Collections.emptyList();
	}
	Document document = null;
	Body body = new Body();
	try {
	    cookies = cookies.isEmpty() ? VntUtil.getCookies(VntUtil.getCookies(getTrackerConfig().getName(), getTrackerUser().getUsername())) : cookies;
	    StringBuilder url = new StringBuilder();
	    url.append(getTrackerConfig().getUrl() + (getTrackerConfig().getUrl().contains("?") ? "&" : "?"));
	    
	    StringBuilder urlCategory = new StringBuilder();
	    for (TrackerCategory trackerCategory : getQueryParameters().getTrackerCategories())
	    {
		if (getTrackerConfig().getCategories().contains(trackerCategory)) {
		    if (urlCategory.length() > 0) {
			    urlCategory.append("&");
			}
			if (StringUtils.isNotBlank(trackerCategory.getProperty())) {
			    urlCategory.append(trackerCategory.getProperty());
			}
			else if (StringUtils.isNotBlank(getTrackerConfig().getCategoryField())) {
			    urlCategory.append(getTrackerConfig().getCategoryField());
			}
			if (StringUtils.isNotBlank(trackerCategory.getCode())) {
			    urlCategory.append("=" + trackerCategory.getCode());
			}
		}
	    }
	    if (urlCategory.length() > 0) {
		url.append(urlCategory.toString());
	    }
	    
	    url.append("&" + getTrackerConfig().getSearchField() + "=" + getQueryParameters().getSearch());
	    url.append("&" + getTrackerConfig().getPageField() + "=" + getTrackerConfig().getPageValue(getPage()));
	    log.info("URL : " + url.toString());
	    Connection connection = Jsoup.connect(url.toString()).timeout(30000).userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64; rv:43.0) Gecko/20100101 Firefox/43.0.3").cookies(cookies).method(Method.GET);
	    Response response = connection.execute();
	    log.debug("Status Code : " + response.statusCode());
	    log.debug("Status Message : " + response.statusMessage());
	    document = response.parse();
	    body.setContent(document.body().html());
	} catch (IOException e) {
	    e.printStackTrace();
	    return Collections.emptyList();
	}
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
			}
			else if (torrentFilter.getOperation().equals(FilterOperation.NE) && !longTorrentValue.equals(longFilterValue)) {
			    add = true;
			    continue;
			}
			else if (torrentFilter.getOperation().equals(FilterOperation.GT) && longTorrentValue.compareTo(longFilterValue) > 0) {
			    add = true;
			    continue;
			}
			else if (torrentFilter.getOperation().equals(FilterOperation.LT) && longTorrentValue.compareTo(longFilterValue) < 0) {
			    add = true;
			    continue;
			}
			else if (torrentFilter.getOperation().equals(FilterOperation.GE) && longTorrentValue.compareTo(longFilterValue) >= 0) {
			    add = true;
			    continue;
			}
			else if (torrentFilter.getOperation().equals(FilterOperation.LE) && longTorrentValue.compareTo(longFilterValue) <= 0) {
			    add = true;
			    continue;
			}
		    }
		    else if (torrentValue instanceof Double) {
			Double doubleTorrentValue = Double.valueOf(torrentValue.toString());
			Double doubleFilterValue = Double.valueOf(torrentFilter.getValue().toString());
			if (torrentFilter.getOperation().equals(FilterOperation.EQ) && doubleTorrentValue.equals(doubleFilterValue)) {
			    add = true;
			    continue;
			}
			else if (torrentFilter.getOperation().equals(FilterOperation.NE) && !doubleTorrentValue.equals(doubleFilterValue)) {
			    add = true;
			    continue;
			}
			else if (torrentFilter.getOperation().equals(FilterOperation.GT) && doubleTorrentValue.compareTo(doubleFilterValue) > 0) {
			    add = true;
			    continue;
			}
			else if (torrentFilter.getOperation().equals(FilterOperation.LT) && doubleTorrentValue.compareTo(doubleFilterValue) < 0) {
			    add = true;
			    continue;
			}
			else if (torrentFilter.getOperation().equals(FilterOperation.GE) && doubleTorrentValue.compareTo(doubleFilterValue) >= 0) {
			    add = true;
			    continue;
			}
			else if (torrentFilter.getOperation().equals(FilterOperation.LE) && doubleTorrentValue.compareTo(doubleFilterValue) <= 0) {
			    add = true;
			    continue;
			}
		    }
		    else if (torrentValue instanceof String) {
			String stringTorrentValue = torrentValue.toString();
			String stringFilterValue = torrentFilter.getValue().toString();
			if (torrentFilter.getOperation().equals(FilterOperation.EQ) && stringTorrentValue.equals(stringFilterValue)) {
			    add = true;
			    continue;
			}
			else if (torrentFilter.getOperation().equals(FilterOperation.NE) && !stringTorrentValue.equals(stringFilterValue)) {
			    add = true;
			    continue;
			}
			else if (torrentFilter.getOperation().equals(FilterOperation.GT) && stringTorrentValue.compareTo(stringFilterValue) > 0) {
			    add = true;
			    continue;
			}
			else if (torrentFilter.getOperation().equals(FilterOperation.LT) && stringTorrentValue.compareTo(stringFilterValue) < 0) {
			    add = true;
			    continue;
			}
			else if (torrentFilter.getOperation().equals(FilterOperation.GE) && stringTorrentValue.compareTo(stringFilterValue) >= 0) {
			    add = true;
			    continue;
			}
			else if (torrentFilter.getOperation().equals(FilterOperation.LE) && stringTorrentValue.compareTo(stringFilterValue) <= 0) {
			    add = true;
			    continue;
			}
			else if (torrentFilter.getOperation().equals(FilterOperation.LIKE) && stringTorrentValue.contains(stringFilterValue)) {
			    add = true;
			    continue;
			}
			else if (torrentFilter.getOperation().equals(FilterOperation.NLIKE) && !stringTorrentValue.contains(stringFilterValue)) {
			    add = true;
			    continue;
			}
			else if (torrentFilter.getOperation().equals(FilterOperation.REGEX) && stringTorrentValue.matches(stringFilterValue)) {
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
	    Connection connection = Jsoup.connect(getTrackerConfig().getAuthenticationUrl()).userAgent("Mozilla");
	    connection.timeout(30000);
	    connection.data(getTrackerConfig().getAuthenticationAdditionalParameters());
	    connection.data(getTrackerConfig().getUsernameField(), getTrackerUser().getUsername()).data(trackerConfig.getPasswordField(), getTrackerUser().getPassword());
	    connection.method(Method.valueOf(getTrackerConfig().getAuthenticationMethod()));
	    Connection.Response response = connection.execute();
	    log.debug("Status Code : " + response.statusCode());
	    log.debug("Status Message : " + response.statusMessage());
	    Body body = new Body();
	    body.setContent(response.parse().html());
	    authenticated = getTrackerConfig().isAuthenticated(body);
	    if (isAuthenticated()) {
		cookies = response.cookies();
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
