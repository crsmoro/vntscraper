package com.shuffle.vnt.trackers.manicomioshare;

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

public class ManicomioShare implements TrackerConfig {

	private final String name = "ManicomioShare";

	private final String url = "http://www.manicomio-share.com/pesquisa.php?order=desc&sort=id";

	private final String authenticationUrl = "http://www.manicomio-share.com/";

	private final String usernameField = "username";

	private final String passwordField = "password";

	private final String authenticationMethod = "POST";

	private final String pageField = "page";

	private final String searchField = "busca";

	private final String categoryField = "cat";
	
	private final List<TrackerCategory> categories = new ArrayList<>();
	
	{
	    categories.add(new TrackerCategory("Filmes : HD", "", "127"));
	    categories.add(new TrackerCategory("Filmes : HD Nacionais", "", "148"));
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
		return Jsoup.parse(body.getContent()).select("a[href=\"http://www.manicomio-share.com/account-recover.php\"]")
				.size() <= 0;
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
		map.put("dados", "ok");
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
		return new ManicomioShareTorrent();
	}

	@Override
	public TorrentDetailedParser getTorrentDetailedParser() {
		return new ManicomioShareTorrentDetail();
	}

}
