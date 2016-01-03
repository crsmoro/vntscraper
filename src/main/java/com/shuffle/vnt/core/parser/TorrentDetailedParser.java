package com.shuffle.vnt.core.parser;

import com.shuffle.vnt.core.parser.bean.Body;

public interface TorrentDetailedParser {

	String getImdbLink(Body body);

	String getYoutubeLink(Body body);

	long getAno(Body body);
}
