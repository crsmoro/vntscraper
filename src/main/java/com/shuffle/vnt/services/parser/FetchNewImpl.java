package com.shuffle.vnt.services.parser;

import java.util.ArrayList;
import java.util.List;

import com.shuffle.vnt.configuration.PreferenceManager;
import com.shuffle.vnt.configuration.bean.TrackerUser;
import com.shuffle.vnt.core.parser.TrackerManagerFactory;
import com.shuffle.vnt.core.parser.bean.QueryParameters;
import com.shuffle.vnt.core.parser.bean.Torrent;
import com.shuffle.vnt.core.service.TrackerManager;
import com.shuffle.vnt.util.VntUtil;

public class FetchNewImpl implements FetchNew {

	private long last;

	private TrackerManager torrentManager;

	private TrackerUser trackerUserData;

	private QueryParameters queryParameters;

	private List<Torrent> torrents = new ArrayList<Torrent>();

	@Override
	public long getLast() {
		return last;
	}

	@Override
	public void setLast(long last) {
		this.last = last;
	}

	@Override
	public TrackerUser getTrackerUserData() {
		return trackerUserData;
	}

	@Override
	public void setTrackerUserData(TrackerUser trackerUserData) {
		this.trackerUserData = trackerUserData;
	}

	@Override
	public QueryParameters getQueryParameters() {
		return queryParameters;
	}

	@Override
	public void setQueryParameters(QueryParameters queryParameters) {
		this.queryParameters = queryParameters;
	}

	@Override
	public List<Torrent> fetch() {
		torrentManager = TrackerManagerFactory
				.getInstance(VntUtil.getTrackerConfig(getTrackerUserData().getTracker()).getClass());
		if (torrentManager == null) {
			throw new IllegalArgumentException("Could not instantiate trackerParser");
		}
		torrentManager.setTrackerUser(getTrackerUserData());
		torrentManager.setQueryParameters(getQueryParameters());
		if (getLast() == 0) {
			setLast(PreferenceManager.getInstance().getFetchNew(getTrackerUserData(), getQueryParameters()).getLast());
		}
		if (getLast() == 0) {
			torrents = torrentManager.fetchTorrents();
		}
		boolean old = getLast() == 0 || false;
		while (!old) {
			List<Torrent> newTorrents = torrentManager.fetchTorrents();
			List<Torrent> addTorrents = new ArrayList<Torrent>();
			for (Torrent torrent : newTorrents) {
				if (torrent.getId() > getLast()) {
					addTorrents.add(torrent);
				}
			}
			torrents.addAll(addTorrents);
			if (newTorrents.size() != addTorrents.size()) {
				old = true;
			}
			try {
				Thread.sleep(2000);
			} catch (InterruptedException dontcare) {

			}
			torrentManager.setPage(torrentManager.getPage() + 1);
		}
		if (!torrents.isEmpty()) {
			com.shuffle.vnt.configuration.bean.FetchNew fetchNew = PreferenceManager.getInstance().getFetchNew(getTrackerUserData(), getQueryParameters());
			if (fetchNew.getTrackerUser() == null)
			{
				fetchNew.setTrackerUser(getTrackerUserData());
				fetchNew.setQueryParameters(getQueryParameters());
				fetchNew.setLast(torrents.get(0).getId());
				PreferenceManager.getInstance().getPreferences().getFetchNews().add(fetchNew);
			}
			PreferenceManager.getInstance().savePreferences();
			// FIXME ?
		}
		return torrents;
	}
}