package com.shuffle.vnt.core.service;

import java.util.List;

import com.shuffle.vnt.configuration.bean.TrackerUser;
import com.shuffle.vnt.core.parser.bean.QueryParameters;
import com.shuffle.vnt.core.parser.bean.Torrent;

public interface ServiceParser extends Service {
	
	TrackerUser getTrackerUserData();
	
	void setTrackerUserData(TrackerUser trackerUserData);
	
	QueryParameters getQueryParameters();
	
	void setQueryParameters(QueryParameters queryParameters);

	List<Torrent> fetch();
}
