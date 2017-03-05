package com.shuffle.vnt.core.service;

import java.io.InputStream;
import java.util.List;

import com.shuffle.vnt.core.parser.Tracker;
import com.shuffle.vnt.core.parser.bean.QueryParameters;
import com.shuffle.vnt.core.parser.bean.Torrent;

public interface TrackerManager extends Service {

	static long DELAY_BETWEEN_REQUESTS = 2000;

	Tracker getTracker();

	void setTracker(Tracker tracker);

	String getUsername();

	void setUsername(String username);

	String getPassword();

	void setPassword(String password);
	
	void setCaptcha(String captcha);

	void setUser(String username, String password);
	
	void setUser(String username, String password, String captcha);

	QueryParameters getQueryParameters();

	void setQueryParameters(QueryParameters queryParameters);

	void setPage(long page);

	long getPage();

	boolean authenticate();

	List<Torrent> fetchTorrents();

	Torrent getDetails(Torrent torrent);

	InputStream download(Torrent torrent);
}
