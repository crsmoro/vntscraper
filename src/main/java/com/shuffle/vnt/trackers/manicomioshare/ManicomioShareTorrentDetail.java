package com.shuffle.vnt.trackers.manicomioshare;

import org.jsoup.Jsoup;
import org.jsoup.select.Elements;

import com.shuffle.vnt.core.parser.TorrentDetailedParser;
import com.shuffle.vnt.core.parser.bean.Body;

public class ManicomioShareTorrentDetail implements TorrentDetailedParser {

    @Override
    public String getImdbLink(Body body) {
	return Jsoup.parse(body.getContent()).select("a[href*=\"http://www.imdb.com/title\"").text();
    }

    @Override
    public String getYoutubeLink(Body body) {
	String youtubeLink = "";
	String embedLink = Jsoup.parse(body.getContent()).select("iframe[src^=\"http://www.youtube.com/embed\"").attr("src");
	if (embedLink != null && !"".equalsIgnoreCase(embedLink)) {
	    String[] elSplit = embedLink.split("/");
	    youtubeLink = elSplit[elSplit.length - 1].split("\\?")[0];
	    youtubeLink = "https://www.youtube.com/watch?v=" + youtubeLink;
	}
	return youtubeLink;
    }

    @Override
    public long getAno(Body body) {
	Elements elements = Jsoup.parse(body.getContent()).select("#contentHolder1 > div.well > ul.det > li:nth-child(3)");
	return Long.valueOf((elements.first() != null ? elements.first().text().replace("Ano: ", "").trim(): "0"));
    }

}
