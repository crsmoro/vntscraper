package com.shuffle.vnt.core.parser;

import com.shuffle.vnt.core.service.TrackerManager;
import com.shuffle.vnt.parser.VntTrackerManager;

public abstract class TrackerManagerFactory {
	
	private TrackerManagerFactory()
	{
		
	}
	
	public static TrackerManager getInstance(Class<? extends TrackerConfig> trackerParserConfig) {
		return getInstance(VntTrackerManager.class, trackerParserConfig);
	}

	public static TrackerManager getInstance(Class<? extends TrackerManager> tracketParser, Class<? extends TrackerConfig> trackerParserConfig) {
		TrackerManager trackerParser = null;
		try {
			trackerParser = tracketParser.newInstance();
			trackerParser.setTrackerConfig(trackerParserConfig.newInstance());
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
		return trackerParser;
	}
}
