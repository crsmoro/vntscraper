package com.shuffle.vnt.core.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.shuffle.vnt.core.parser.bean.Body;
import com.shuffle.vnt.core.parser.bean.TrackerCategory;

public interface Tracker {

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

	@JsonIgnore
	List<TrackerCategory> getCategories();

	String getPageValue(long page);

	@JsonIgnore
	TorrentParser getTorrentParser();

	@JsonIgnore
	TorrentDetailedParser getTorrentDetailedParser();
	
	static List<Tracker> loadedTrackers = new ArrayList<>();

	@SuppressWarnings("unchecked")
	static Class<? extends Tracker> getClass(String className) {
		Class<? extends Tracker> trackerClass = null;
		try {
			trackerClass = (Class<? extends Tracker>) Class.forName(className);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return trackerClass;
	}

	static Tracker getInstance(String className) {
		return getInstance(getClass(className));
	}

	static Tracker getInstance(Class<? extends Tracker> clazz) {
		Tracker tracker = null;
		try {
			tracker = clazz.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
		return tracker;
	}
	
	default TrackerCategory getCategory(String code) {
		return getCategories().stream().filter(c -> c.getCode().equals(code)).findFirst().orElse(null);
	}
}