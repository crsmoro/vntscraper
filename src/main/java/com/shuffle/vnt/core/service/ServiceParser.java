package com.shuffle.vnt.core.service;

import java.util.List;

import com.shuffle.vnt.core.model.TrackerUser;
import com.shuffle.vnt.core.parser.bean.QueryParameters;
import com.shuffle.vnt.core.parser.bean.Torrent;

public interface ServiceParser extends Service {

	ServiceParserData getData();

	/**
	 * Needs this because of ORMLite to load the data (since it doesnt support inheritance)
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
