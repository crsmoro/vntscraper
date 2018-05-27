package com.shuffle.vnt.core.service;

import java.util.List;

import com.shuffle.sieve.core.parser.bean.QueryParameters;
import com.shuffle.sieve.core.parser.bean.Torrent;
import com.shuffle.vnt.core.model.TrackerUser;

public interface Service {

	ServiceParserData getData();

	/**
	 * Needs this because of ORMLite to load the data (since it doesnt
	 * support inheritance)
	 * 
	 * @param id
	 * @return
	 */
	ServiceParserData getData(Long id);

	void setData(ServiceParserData data);

	TrackerUser getTrackerUserData();

	void setTrackerUserData(TrackerUser trackerUserData);

	QueryParameters getQueryParameters();

	void setQueryParameters(QueryParameters queryParameters);

	List<Torrent> fetch();
}
