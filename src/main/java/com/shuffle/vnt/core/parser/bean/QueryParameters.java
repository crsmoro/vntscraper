package com.shuffle.vnt.core.parser.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class QueryParameters implements Serializable {

    private static final long serialVersionUID = 6563726222280875899L;

    private String search;

    private List<TrackerCategory> trackerCategories = new ArrayList<>();;

    private List<TorrentFilter> torrentFilters = new ArrayList<>();

    public String getSearch() {
	return search;
    }

    public void setSearch(String search) {
	this.search = search;
    }

    public List<TrackerCategory> getTrackerCategories() {
	return trackerCategories;
    }

    public void setTrackerCategories(List<TrackerCategory> trackerCategories) {
	this.trackerCategories = trackerCategories;
    }

    public List<TorrentFilter> getTorrentFilters() {
	return torrentFilters;
    }

    public void setTorrentFilters(List<TorrentFilter> torrentFilters) {
	this.torrentFilters = torrentFilters;
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((search == null) ? 0 : search.hashCode());
	result = prime * result + ((trackerCategories == null) ? 0 : trackerCategories.hashCode());
	result = prime * result + ((torrentFilters == null) ? 0 : torrentFilters.hashCode());
	return result;
    }

    @Override
    public boolean equals(Object obj) {
	if (this == obj)
	    return true;
	if (obj == null)
	    return false;
	if (getClass() != obj.getClass())
	    return false;
	QueryParameters other = (QueryParameters) obj;
	if (search == null) {
	    if (other.search != null)
		return false;
	} else if (!search.equals(other.search))
	    return false;
	if (trackerCategories == null) {
	    if (other.trackerCategories != null)
		return false;
	} else if (!trackerCategories.equals(other.trackerCategories))
	    return false;
	if (torrentFilters == null) {
	    if (other.torrentFilters != null)
		return false;
	} else if (!torrentFilters.equals(other.torrentFilters))
	    return false;
	return true;
    }
}
