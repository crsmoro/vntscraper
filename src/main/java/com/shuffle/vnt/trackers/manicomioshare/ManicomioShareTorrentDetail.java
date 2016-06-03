package com.shuffle.vnt.trackers.manicomioshare;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import com.shuffle.vnt.core.parser.TorrentDetailedParser;
import com.shuffle.vnt.core.parser.bean.Body;

public class ManicomioShareTorrentDetail implements TorrentDetailedParser {

    @Override
    public String getImdbLink(Body body) {
	Document document = Jsoup.parse(body.getContent());
	String imdbLink = "";
	String docBody = document.select("#contentHolder1 fieldset.search").html();
	Pattern pattern = Pattern.compile("(http:\\/\\/www.imdb.com\\/title\\/tt(\\d+))", Pattern.CASE_INSENSITIVE);
	Matcher matcher = pattern.matcher(docBody);
	if (matcher.find() && matcher.groupCount() > 0) {
	    imdbLink = matcher.group(0);
	}

	if (StringUtils.isBlank(imdbLink)) {
	    docBody = document.select("#infoimdb div.modal-body").html();
	    pattern = Pattern.compile("(http:\\/\\/www.imdb.com\\/title\\/tt(\\d+))", Pattern.CASE_INSENSITIVE);
	    matcher = pattern.matcher(docBody);
	    if (matcher.find() && matcher.groupCount() > 0) {
		imdbLink = matcher.group(0);
	    }
	}

	return imdbLink;
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
	Elements elements = Jsoup.parse(body.getContent()).select("#contentHolder1 > div.well > ul.det > li:nth-child(3)");
	return Long.valueOf((elements.first() != null ? elements.first().text().replace("Ano: ", "").trim() : "0"));
    }

    @Override
    public String getContent(Body body) {
	Document document = Jsoup.parse(body.getContent());
	return document.select("#contentHolder1 fieldset.search").html();
    }

}
