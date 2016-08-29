package com.shuffle.vnt.trackers.demonoid;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

import com.shuffle.vnt.core.parser.TorrentParser;
import com.shuffle.vnt.core.parser.bean.Body;
import com.shuffle.vnt.core.parser.bean.Row;
import com.shuffle.vnt.util.VntUtil;

public class DemonoidTorrent implements TorrentParser {

    private final String baseUrl = Demonoid.BASE_URL;

    @Override
    public List<Row> getRows(Body body) {
	List<Row> rows = new ArrayList<Row>();
	Iterator<Element> iElement = Jsoup.parse(body.getContent())
		.select("td.ctable_content_no_pad > table.font_12px > tbody > tr:not([align])").iterator();
	while (iElement.hasNext()) {
	    Element element = iElement.next();
	    if (element.select("td").size() > 1) {
		Row row = new Row();
		row.setContent(element.outerHtml());
		element = iElement.next();
		row.setContent(row.getContent() + element.outerHtml());
		rows.add(row);
	    }

	}
	return rows;
    }

    @Override
    public long getId(Row row) {
	return Long.valueOf(Jsoup.parse(getFullContentToParse(row)).select("tr").first().select("td[colspan=\"9\"] > a").attr("href").split("/")[3]);
    }

    @Override
    public String getNome(Row row) {
	return Jsoup.parse(getFullContentToParse(row)).select("tr").first().select("td[colspan=\"9\"] > a").text();
    }

    @Override
    public String getLink(Row row) {
	return baseUrl + Jsoup.parse(getFullContentToParse(row)).select("tr").first().select("td[colspan=\"9\"] > a").attr("href");
    }

    @Override
    public String getDownlodLink(Row row) {
	return baseUrl + Jsoup.parse(getFullContentToParse(row)).select("tr").last().select("td").get(2).select("a").last().attr("href");
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
	return VntUtil.parseSize(Jsoup.parse(getFullContentToParse(row)).select("tr").last().select("td[align=\"right\"").text().replaceAll(",", ""));
    }

    @Override
    public Date getAdded(Row row) {
	return null;
    }

    @Override
    public String getCategory(Row row) {
	return Jsoup.parse(getFullContentToParse(row)).select("tr").first().select("td[rowspan=\"2\"] > a > img").attr("alt");
    }

}
