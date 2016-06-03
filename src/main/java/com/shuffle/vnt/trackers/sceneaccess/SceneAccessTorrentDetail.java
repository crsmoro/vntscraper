package com.shuffle.vnt.trackers.sceneaccess;

import org.jsoup.Jsoup;

import com.shuffle.vnt.core.parser.TorrentDetailedParser;
import com.shuffle.vnt.core.parser.bean.Body;

public class SceneAccessTorrentDetail implements TorrentDetailedParser {

    @Override
    public String getImdbLink(Body body) {
	return Jsoup.parse(body.getContent()).select("a[href*=\"http://www.imdb.com/title\"]").attr("href");
    }

    @Override
    public String getYoutubeLink(Body body) {
	String youtubeLink = "";
	String embedLink = Jsoup.parse(body.getContent()).select("iframe[src^=\"http://www.youtube.com/embed\"]").attr("src");
	if (embedLink != null && !"".equalsIgnoreCase(embedLink)) {
	    String[] elSplit = embedLink.split("/");
	    youtubeLink = elSplit[elSplit.length - 1].split("\\?")[0];
	    youtubeLink = "https://www.youtube.com/watch?v=" + youtubeLink;
	}
	return youtubeLink;
    }

    @Override
    public long getAno(Body body) {
	return 0l;
    }

    @Override
    public String getContent(Body body) {
	return Jsoup.parse(body.getContent()).select("#details_table > tbody > tr:nth-child(3) > td.td_col").html();
    }

}
