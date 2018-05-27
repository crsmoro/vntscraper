package com.shuffle.vnt.api.bean;

public class Movie {
	private String title;

	private String originalTitle;

	private long year;

	private long runtime;

	private String poster;

	private String plot;

	private double imdbRating;

	private long imdbVotes;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getOriginalTitle() {
		return originalTitle;
	}

	public void setOriginalTitle(String originalTitle) {
		this.originalTitle = originalTitle;
	}

	public long getYear() {
		return year;
	}

	public void setYear(long year) {
		this.year = year;
	}

	public long getRuntime() {
		return runtime;
	}

	public void setRuntime(long runtime) {
		this.runtime = runtime;
	}

	public String getPoster() {
		return poster;
	}

	public void setPoster(String poster) {
		this.poster = poster;
	}

	public String getPlot() {
		return plot;
	}

	public void setPlot(String plot) {
		this.plot = plot;
	}

	public double getImdbRating() {
		return imdbRating;
	}

	public void setImdbRating(double imdbRating) {
		this.imdbRating = imdbRating;
	}

	public long getImdbVotes() {
		return imdbVotes;
	}

	public void setImdbVotes(long imdbVotes) {
		this.imdbVotes = imdbVotes;
	}

	@Override
	public String toString() {
		return "Movie [title=" + title + ", originalTitle=" + originalTitle + ", year=" + year + ", runtime=" + runtime + ", poster=" + poster + ", plot=" + plot + ", imdbRating=" + imdbRating + ", imdbVotes=" + imdbVotes + "]";
	}
}