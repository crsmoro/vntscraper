package com.shuffle.vnt.trackers.demonoid;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.shuffle.vnt.core.parser.TorrentDetailedParser;
import com.shuffle.vnt.core.parser.bean.Body;

public class DemonoidTorrentDetail implements TorrentDetailedParser {

    @Override
    public String getImdbLink(Body body) {
	String imdbLink = "";
	Document document = Jsoup.parse(body.getContent());
	String docBody = document.select("#fslispc > table > tbody > tr > td > table:nth-child(3)").text();
	Pattern pattern = Pattern.compile("(http:\\/\\/www.imdb.com\\/title\\/tt(\\d+))", Pattern.CASE_INSENSITIVE);
	Matcher matcher = pattern.matcher(docBody);
	if (matcher.find() && matcher.groupCount() > 0) {
	    imdbLink = matcher.group(0);
	}
	return imdbLink;
    }

    @Override
    public String getYoutubeLink(Body body) {
	return null;
    }

    @Override
    public long getAno(Body body) {
	return 0;
    }

    @Override
    public String getContent(Body body) {
	return Jsoup.parse(body.getContent()).select("#fslispc > table > tbody > tr > td > table:nth-child(3)").outerHtml();
    }

}
