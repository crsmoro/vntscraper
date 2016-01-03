package com.shuffle.vnt.trackers.sceneaccess;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.Jsoup;

import com.shuffle.vnt.core.parser.TorrentDetailedParser;
import com.shuffle.vnt.core.parser.TorrentParser;
import com.shuffle.vnt.core.parser.TrackerConfig;
import com.shuffle.vnt.core.parser.bean.Body;
import com.shuffle.vnt.core.parser.bean.TrackerCategory;

public class SceneAccess implements TrackerConfig {

	private final String name = "SceneAccess";

	private final String url = "https://sceneaccess.eu/all?method=2";

	private final String authenticationUrl = "https://sceneaccess.eu/login";

	private final String usernameField = "username";

	private final String passwordField = "password";

	private final String authenticationMethod = "POST";

	private final String pageField = "page";

	private final String searchField = "search";

	private final String categoryField = "cat";
	
	private final List<TrackerCategory> categories = new ArrayList<>();
	
	{
	    categories.add(new TrackerCategory("Movies/DVD-R", "c8", "8"));
	    categories.add(new TrackerCategory("Movies/x264", "c22", "22"));
	    categories.add(new TrackerCategory("Movies/XviD", "c7", "7"));
	    categories.add(new TrackerCategory("Movies/Packs", "c4", "4"));
	    
	    categories.add(new TrackerCategory("TV/HD-x264", "c27", "27"));
	    categories.add(new TrackerCategory("TV/SD-x264", "c17", "17"));
	    categories.add(new TrackerCategory("TV/SD", "c11", "11"));
	    categories.add(new TrackerCategory("TV/Packs", "c26", "26"));
	    
	    categories.add(new TrackerCategory("Games/PC", "c3", "3"));
	    categories.add(new TrackerCategory("Games/PS3", "c5", "5"));
	    categories.add(new TrackerCategory("Games/PSP", "c20", "20"));
	    categories.add(new TrackerCategory("Games/WII", "c28", "28"));
	    categories.add(new TrackerCategory("Games/XBOX360", "c23", "23"));
	    categories.add(new TrackerCategory("Games/Packs", "c29", "29"));
	    
	    categories.add(new TrackerCategory("Music/FLAC", "c40", "40"));
	    categories.add(new TrackerCategory("Music/MP3", "c13", "13"));
	    categories.add(new TrackerCategory("Music/MVID", "c15", "15"));
	    
	    categories.add(new TrackerCategory("APPS/ISO", "c1", "1"));
	    categories.add(new TrackerCategory("APPS/0DAY", "c2", "2"));
	    categories.add(new TrackerCategory("DOX", "c14", "14"));
	    categories.add(new TrackerCategory("MISC", "c21", "21"));
	    
	    categories.add(new TrackerCategory("P2P/Movies/HD-x264", "c41", "41"));
	    categories.add(new TrackerCategory("P2P/Movies/SD-x264", "c42", "42"));
	    categories.add(new TrackerCategory("P2P/Movies/XviD", "c43", "43"));
	    categories.add(new TrackerCategory("P2P/TV/HD", "c44", "44"));
	    categories.add(new TrackerCategory("P2P/TV/HD", "c45", "45"));
	    
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getUrl() {
		return url;
	}

	@Override
	public String getAuthenticationUrl() {
		return authenticationUrl;
	}

	@Override
	public boolean isAuthenticated(Body body) {
		return Jsoup.parse(body.getContent()).select("#login_username").size() <= 0;
	}

	@Override
	public String getUsernameField() {
		return usernameField;
	}

	@Override
	public String getPasswordField() {
		return passwordField;
	}

	@Override
	public String getAuthenticationMethod() {
		return authenticationMethod;
	}

	@Override
	public Map<String, String> getAuthenticationAdditionalParameters() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("submit", "come on in");
		return map;
	}

	@Override
	public String getPageField() {
		return pageField;
	}

	@Override
	public String getSearchField() {
		return searchField;
	}

	@Override
	public String getCategoryField() {
		return categoryField;
	}

	@Override
	public List<TrackerCategory> getCategories() {
	    return categories;
	}

	@Override
	public String getPageValue(long page) {
		return String.valueOf(page);
	}

	@Override
	public TorrentParser getTorrentParser() {
		return new SceneAccessTorrent();
	}

	@Override
	public TorrentDetailedParser getTorrentDetailedParser() {
		return new SceneAccessTorrentDetail();
	}

}
