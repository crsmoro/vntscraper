package com.shuffle.vnt.trackers.manicomioshare;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

import com.shuffle.vnt.core.parser.TorrentParser;
import com.shuffle.vnt.core.parser.bean.Body;
import com.shuffle.vnt.core.parser.bean.Row;
import com.shuffle.vnt.util.VntUtil;

public class ManicomioShareTorrent implements TorrentParser {

    @Override
    public List<Row> getRows(Body body) {
	List<Row> rows = new ArrayList<Row>();
	for (Element element : Jsoup.parse(body.getContent()).select("#tbltorrent > tbody > tr[data-id]")) {
	    Row row = new Row();
	    row.setContent(element.outerHtml());
	    rows.add(row);
	}
	return rows;
    }

    @Override
    public long getId(Row row) {
	return Long.valueOf(Jsoup.parse(getFullContentToParse(row)).select("tr").attr("data-id"));
    }

    @Override
    public String getNome(Row row) {
	return Jsoup.parse(getFullContentToParse(row)).select("tr > td:nth-child(2) > a").first().attr("title");
    }

    @Override
    public String getLink(Row row) {
	return Jsoup.parse(getFullContentToParse(row)).select("tr > td:nth-child(2) > a").first().attr("href");
    }

    @Override
    public String getDownlodLink(Row row) {
	return Jsoup.parse(getFullContentToParse(row)).select("tr > td:nth-child(8) > a").first().attr("href");
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
	return VntUtil.parseSize(Jsoup.parse(getFullContentToParse(row)).select("tr > td:nth-child(4) > span").first().text().replaceAll(",", ""));
    }

    @Override
    public Date getAdded(Row row) {
	return null;
    }

    @Override
    public String getCategory(Row row) {
	return Jsoup.parse(getFullContentToParse(row)).select("tr > td:nth-child(1) > a > img").first().attr("title");
    }

}
