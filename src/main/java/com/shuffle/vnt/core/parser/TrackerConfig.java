package com.shuffle.vnt.core.parser;

import java.util.List;
import java.util.Map;

import com.shuffle.vnt.core.parser.bean.Body;
import com.shuffle.vnt.core.parser.bean.TrackerCategory;

public interface TrackerConfig {

	String getName();

	String getUrl();

	String getAuthenticationUrl();
	
	public enum ParameterType {
	    DEFAULT, PATH
	}
	
	ParameterType getParameterType();

	boolean isAuthenticated(Body body);

	String getUsernameField();

	String getPasswordField();

	String getAuthenticationMethod();

	Map<String, String> getAuthenticationAdditionalParameters();

	String getPageField();

	String getSearchField();

	String getCategoryField();
	
	List<TrackerCategory> getCategories();

	String getPageValue(long page);

	TorrentParser getTorrentParser();
	
	TorrentDetailedParser getTorrentDetailedParser();
}