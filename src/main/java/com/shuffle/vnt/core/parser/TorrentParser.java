package com.shuffle.vnt.core.parser;

import java.util.Date;
import java.util.List;

import com.shuffle.vnt.core.parser.bean.Body;
import com.shuffle.vnt.core.parser.bean.Row;

public interface TorrentParser {

	List<Row> getRows(Body body);

	long getId(Row row);

	String getNome(Row row);

	double getSize(Row row);

	Date getAdded(Row row);

	String getCategory(Row row);

	String getLink(Row row);

	String getDownlodLink(Row row);
}
