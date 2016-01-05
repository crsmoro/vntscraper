package com.shuffle.vnt.trackers.demonoid;

import com.shuffle.vnt.core.parser.TorrentDetailedParser;
import com.shuffle.vnt.core.parser.bean.Body;

public class DemonoidTorrentDetail implements TorrentDetailedParser {

    @Override
    public String getImdbLink(Body body) {
	return null;
    }

    @Override
    public String getYoutubeLink(Body body) {
	return null;
    }

    @Override
    public long getAno(Body body) {
	return 0;
    }

}
