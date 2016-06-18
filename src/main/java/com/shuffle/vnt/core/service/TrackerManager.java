package com.shuffle.vnt.core.service;

import java.util.List;

import com.shuffle.vnt.core.model.TrackerUser;
import com.shuffle.vnt.core.parser.Tracker;
import com.shuffle.vnt.core.parser.bean.QueryParameters;
import com.shuffle.vnt.core.parser.bean.Torrent;

public interface TrackerManager extends Service {

	Tracker getTrackerConfig();

	void setTrackerConfig(Tracker trackerConfig);

	TrackerUser getTrackerUser();

	void setTrackerUser(TrackerUser trackerUser);

	QueryParameters getQueryParameters();

	void setQueryParameters(QueryParameters queryParameters);

	void setPage(long page);

	long getPage();

	boolean authenticate();

	List<Torrent> fetchTorrents();

	Torrent getDetails(Torrent torrent);
}
