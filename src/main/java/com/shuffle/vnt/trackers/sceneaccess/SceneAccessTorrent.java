package com.shuffle.vnt.trackers.sceneaccess;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

import com.shuffle.vnt.core.parser.TorrentParser;
import com.shuffle.vnt.core.parser.bean.Body;
import com.shuffle.vnt.core.parser.bean.Row;
import com.shuffle.vnt.util.VntUtil;

public class SceneAccessTorrent implements TorrentParser {
    
    private final String baseUrl = "https://sceneaccess.eu/";

    @Override
    public List<Row> getRows(Body body) {
	List<Row> rows = new ArrayList<Row>();
	for (Element element : Jsoup.parse(body.getContent()).select("#torrents-table > tbody > tr.tt_row")) {
	    Row row = new Row();
	    row.setContent(element.outerHtml());
	    rows.add(row);
	}
	return rows;
    }

    @Override
    public long getId(Row row) {
	return Long.valueOf(Jsoup.parse(getFullContentToParse(row)).select("tr > td.ttr_name > a").first().attr("href").replace("details?id=",""));
    }

    @Override
    public String getNome(Row row) {
	return Jsoup.parse(getFullContentToParse(row)).select("tr > td.ttr_name > a").first().attr("title");
    }

    @Override
    public String getLink(Row row) {
	return baseUrl + Jsoup.parse(getFullContentToParse(row)).select("tr > td.ttr_name > a").first().attr("href");
    }

    @Override
    public String getDownlodLink(Row row) {
	return baseUrl + Jsoup.parse(getFullContentToParse(row)).select("tr > td.td_dl > a").first().attr("href");
    }

    private String getFullContentToParse(Row row) {
	StringBuilder rowContent = new StringBuilder();
	rowContent.append("<table>");
	rowContent.append(row.getContent());
	rowContent.append("</table>");
	return rowContent.toString();
    }

    @Override
    public double getSize(Row row) {
	return VntUtil.parseSize(Jsoup.parse(getFullContentToParse(row)).select("tr > td.ttr_size").first().html().split("<br>")[0]);
    }

    @Override
    public Date getAdded(Row row) {
	DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	String dateString = Jsoup.parse(getFullContentToParse(row)).select("tr > td.ttr_added").first().html().replaceAll("<br>", " ");
	Date date = null;
	try {
	    date = dateFormat.parse(dateString);
	}
	catch (ParseException e) {
	    e.printStackTrace();
	}
	return date;
    }

    @Override
    public String getCategory(Row row) {
	return Jsoup.parse(getFullContentToParse(row)).select("tr > td.ttr_type > a > img").first().attr("alt");
    }

}
