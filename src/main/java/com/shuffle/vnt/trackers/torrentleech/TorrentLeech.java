package com.shuffle.vnt.trackers.torrentleech;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.Jsoup;

import com.shuffle.vnt.core.parser.TorrentDetailedParser;
import com.shuffle.vnt.core.parser.TorrentParser;
import com.shuffle.vnt.core.parser.Tracker;
import com.shuffle.vnt.core.parser.bean.Body;
import com.shuffle.vnt.core.parser.bean.TrackerCategory;

public class TorrentLeech implements Tracker {

	private final String name = "TorrentLeech";

	private final String url = "https://www.torrentleech.org/torrents/browse/index/";

	private final String authenticationUrl = "https://www.torrentleech.org/user/account/login/";

	private final ParameterType parameterType = ParameterType.PATH;

	private final String usernameField = "username";

	private final String passwordField = "password";

	private final String authenticationMethod = "POST";

	private final String pageField = "page";

	private final String searchField = "query";

	private final String categoryField = "categories";

	private final List<TrackerCategory> categories = new ArrayList<>();

	{
		categories.add(new TrackerCategory("Movies: Cam", "", "8"));
		categories.add(new TrackerCategory("Movies: TS/TC", "", "9"));
		categories.add(new TrackerCategory("Movies: R5/Screeners", "", "10"));
		categories.add(new TrackerCategory("Movies: DVDRip/DVDScreener", "", "11"));
		categories.add(new TrackerCategory("Movies: DVD-R", "", "12"));
		categories.add(new TrackerCategory("Movies: Bluray", "", "13"));
		categories.add(new TrackerCategory("Movies: BDRip", "", "14"));
		categories.add(new TrackerCategory("Movies: Boxsets", "", "15"));
		categories.add(new TrackerCategory("Movies: Documentaries", "", "29"));
		categories.add(new TrackerCategory("Movies: Foreign", "", "36"));
		categories.add(new TrackerCategory("Movies: WEBRip", "", "37"));

		categories.add(new TrackerCategory("TV: Episodes", "", "26"));
		categories.add(new TrackerCategory("TV: BoxSets", "", "27"));
		categories.add(new TrackerCategory("TV: Episodes HD", "", "32"));

		categories.add(new TrackerCategory("Games: PC", "", "17"));
		categories.add(new TrackerCategory("Games: XBOX", "", "18"));
		categories.add(new TrackerCategory("Games: XBOX360", "", "19"));
		categories.add(new TrackerCategory("Games: PS2", "", "20"));
		categories.add(new TrackerCategory("Games: PS3", "", "21"));
		categories.add(new TrackerCategory("Games: PSP", "", "22"));
		categories.add(new TrackerCategory("Games: Wii", "", "28"));
		categories.add(new TrackerCategory("Games: Nintendo DS", "", "30"));

		categories.add(new TrackerCategory("Music: Music Videos", "", "16"));
		categories.add(new TrackerCategory("Music: Audio", "", "31"));

		categories.add(new TrackerCategory("Animation: Anime", "", "34"));
		categories.add(new TrackerCategory("Animation: Cartoons", "", "35"));

		categories.add(new TrackerCategory("Books", "", "5"));

		categories.add(new TrackerCategory("Applications: PC-ISO", "", "23"));
		categories.add(new TrackerCategory("Applications: Mac", "", "24"));
		categories.add(new TrackerCategory("Applications: PDA", "", "25"));
		categories.add(new TrackerCategory("Applications: 0-day", "", "33"));
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
	public ParameterType getParameterType() {
		return parameterType;
	}

	@Override
	public boolean isAuthenticated(Body body) {
		return Jsoup.parse(body.getContent()).select("#loginform").size() <= 0;
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
		map.put("login", "submit");
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
		return String.valueOf(page + 1);
	}

	@Override
	public TorrentParser getTorrentParser() {
		return new TorrentLeechTorrent();
	}

	@Override
	public TorrentDetailedParser getTorrentDetailedParser() {
		return new TorrentLeechTorrentDetail();
	}

}
