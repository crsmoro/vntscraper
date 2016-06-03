package com.shuffle.vnt.core.parser.bean;

import java.util.Date;

import com.shuffle.vnt.omdbapi.OmdbResponse;

public class Torrent {

    private String tracker;

    private long id;

    private String name;

    private long year;

    private double size;

    private Date added;

    private String category;

    private String link;

    private String downloadLink;

    private String imdbLink;

    private String youtubeLink;
    
    private String content;

    private boolean detailed;
    
    private OmdbResponse imdb;

    public String getTracker() {
	return tracker;
    }

    public void setTracker(String tracker) {
	this.tracker = tracker;
    }

    public long getId() {
	return id;
    }

    public void setId(long id) {
	this.id = id;
    }

    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    public long getYear() {
	return year;
    }

    public void setYear(long year) {
	this.year = year;
    }

    public double getSize() {
	return size;
    }

    public void setSize(double size) {
	this.size = size;
    }

    public Date getAdded() {
	return added;
    }

    public void setAdded(Date added) {
	this.added = added;
    }

    public String getCategory() {
	return category;
    }

    public void setCategory(String category) {
	this.category = category;
    }

    public String getLink() {
	return link;
    }

    public void setLink(String link) {
	this.link = link;
    }

    public String getDownloadLink() {
	return downloadLink;
    }

    public void setDownloadLink(String downloadLink) {
	this.downloadLink = downloadLink;
    }

    public String getImdbLink() {
	return imdbLink;
    }

    public void setImdbLink(String imdbLink) {
	this.imdbLink = imdbLink;
    }

    public String getYoutubeLink() {
	return youtubeLink;
    }

    public void setYoutubeLink(String youtubeLink) {
	this.youtubeLink = youtubeLink;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isDetailed() {
	return detailed;
    }

    public void setDetailed(boolean detailed) {
	this.detailed = detailed;
    }

    public OmdbResponse getImdb() {
        return imdb;
    }

    public void setImdb(OmdbResponse imdb) {
        this.imdb = imdb;
    }

    @Override
    public String toString() {
	return "Torrent [tracker=" + tracker + ", id=" + id + ", name=" + name + ", year=" + year + ", size=" + size + ", added=" + added + ", category=" + category + ", link=" + link + ", downloadLink=" + downloadLink + ", imdbLink=" + imdbLink
		+ ", youtubeLink=" + youtubeLink + ", content=" + content + ", detailed=" + detailed + ", imdb=" + imdb + "]";
    }

}
