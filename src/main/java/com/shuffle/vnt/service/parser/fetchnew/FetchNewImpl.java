package com.shuffle.vnt.service.parser.fetchnew;

import java.util.ArrayList;
import java.util.List;

import com.shuffle.vnt.core.db.PersistenceManager;
import com.shuffle.vnt.core.model.TrackerUser;
import com.shuffle.vnt.core.parser.TrackerManagerFactory;
import com.shuffle.vnt.core.parser.bean.QueryParameters;
import com.shuffle.vnt.core.parser.bean.Torrent;
import com.shuffle.vnt.core.service.ServiceParserData;
import com.shuffle.vnt.core.service.TrackerManager;
import com.shuffle.vnt.service.parser.fetchnew.model.FetchNew;

public class FetchNewImpl implements com.shuffle.vnt.service.parser.fetchnew.FetchNew {

	private long last;

	private TrackerManager trackerManager;

	private TrackerUser trackerUserData;

	private QueryParameters queryParameters;

	private FetchNew fetchNew;

	private List<Torrent> torrents = new ArrayList<Torrent>();

	@Override
	public ServiceParserData getData() {
		return fetchNew;
	}

	@Override
	public ServiceParserData getData(Long id) {
		return PersistenceManager.getDao(FetchNew.class).findOne(id);
	}

	@Override
	public void setData(ServiceParserData data) {
		this.fetchNew = (FetchNew) data;
	}

	@Override
	public long getLast() {
		return last;
	}

	@Override
	public void setLast(long last) {
		this.last = last;
	}

	public FetchNew getFetchNew(TrackerUser trackerUser, QueryParameters queryParameters) {
		return PersistenceManager.getDao(FetchNew.class).eq("trackerUser", trackerUser).eq("queryParameters", queryParameters).and(2).findOne();
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
		trackerManager = TrackerManagerFactory.getInstance(getTrackerUserData().getTracker());
		if (trackerManager == null) {
			throw new IllegalArgumentException("Could not instantiate trackerParser");
		}
		trackerManager.setUser(getTrackerUserData().getUsername(), getTrackerUserData().getPassword());
		trackerManager.setQueryParameters(getQueryParameters());
		if (fetchNew == null) {
			fetchNew = new FetchNew();
		}
		if (getLast() == 0) {
			setLast(fetchNew.getLast());
		}
		if (getLast() == 0) {
			torrents = trackerManager.fetchTorrents();
		}
		boolean old = getLast() == 0 || false;
		while (!old) {
			List<Torrent> newTorrents = trackerManager.fetchTorrents();
			List<Torrent> addTorrents = new ArrayList<Torrent>();
			for (Torrent torrent : newTorrents) {
				if (torrent.getId() > getLast()) {
					addTorrents.add(torrent);
				}
			}
			torrents.addAll(addTorrents);
			if (newTorrents.isEmpty() || newTorrents.size() != addTorrents.size()) {
				old = true;
			}
			try {
				Thread.sleep(2000);
			} catch (InterruptedException dontcare) {

			}
			trackerManager.setPage(trackerManager.getPage() + 1);
		}
		if (!torrents.isEmpty()) {

			if (fetchNew.getTrackerUser() == null) {
				fetchNew.setTrackerUser(getTrackerUserData());
				fetchNew.setQueryParameters(getQueryParameters());
				PersistenceManager.getDao(FetchNew.class).save(fetchNew);
			}
			fetchNew.setLast(torrents.get(0).getId());
			PersistenceManager.getDao(FetchNew.class).save(fetchNew);
		}
		return torrents;
	}
}