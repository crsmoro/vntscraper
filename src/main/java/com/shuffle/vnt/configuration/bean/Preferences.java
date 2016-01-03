package com.shuffle.vnt.configuration.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.shuffle.vnt.services.schedule.SchedulerData;

public class Preferences implements Serializable {

	private static final long serialVersionUID = 7482942706150638896L;
	
	private String baseUrl;
	
	private MailConfig mailConfig;

	private List<TrackerUser> trackerUsers = new ArrayList<>();

	private List<FetchNew> fetchNews = new ArrayList<>();

	private List<SchedulerData> schedulerDatas = new ArrayList<>();

	private List<Seedbox> seedboxes = new ArrayList<>();

	public String getBaseUrl() {
	    return baseUrl;
	}

	public void setBaseUrl(String baseUrl) {
	    this.baseUrl = baseUrl;
	}

	public MailConfig getMailConfig() {
	    return mailConfig;
	}

	public void setMailConfig(MailConfig mailConfig) {
	    this.mailConfig = mailConfig;
	}

	public List<TrackerUser> getTrackerUsers() {
		return trackerUsers;
	}

	public void setTrackerUsers(List<TrackerUser> trackerUsers) {
		this.trackerUsers = trackerUsers;
	}

	public List<FetchNew> getFetchNews() {
		return fetchNews;
	}

	public void setFetchNews(List<FetchNew> fetchNews) {
		this.fetchNews = fetchNews;
	}

	public List<SchedulerData> getSchedulerDatas() {
		return schedulerDatas;
	}

	public void setSchedulerDatas(List<SchedulerData> schedulerDatas) {
		this.schedulerDatas = schedulerDatas;
	}

	public List<Seedbox> getSeedboxes() {
		return seedboxes;
	}

	public void setSeedboxes(List<Seedbox> seedboxes) {
		this.seedboxes = seedboxes;
	}

	@Override
	public String toString() {
		return "Preferences [trackerUsers=" + trackerUsers + ", fetchNews=" + fetchNews + ", schedulerDatas="
				+ schedulerDatas + ", seedboexs=" + seedboxes + "]";
	}
}
