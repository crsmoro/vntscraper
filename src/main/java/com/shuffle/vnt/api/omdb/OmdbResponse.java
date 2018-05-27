package com.shuffle.vnt.api.omdb;

public class OmdbResponse {

    private String Actors;

    private String Awards;

    private String Country;

    private String Director;

    private String Episode;

    private String Genre;

    private String Language;

    private String Metascore;

    private String Plot;

    private String Poster;

    private String Rated;

    private String Released;

    private boolean Response;

    private String Runtime;

    private String Season;

    private String Title;

    private String Type;

    private String Writer;

    private String Year;

    private String imdbID;

    private String imdbRating;

    private String imdbVotes;

    private String seriesID;

    private String Error;

    public String getActors() {
	return Actors;
    }

    public void setActors(String actors) {
	Actors = actors;
    }

    public String getAwards() {
	return Awards;
    }

    public void setAwards(String awards) {
	Awards = awards;
    }

    public String getCountry() {
	return Country;
    }

    public void setCountry(String country) {
	Country = country;
    }

    public String getDirector() {
	return Director;
    }

    public void setDirector(String director) {
	Director = director;
    }

    public String getEpisode() {
	return Episode;
    }

    public void setEpisode(String episode) {
	Episode = episode;
    }

    public String getGenre() {
	return Genre;
    }

    public void setGenre(String genre) {
	Genre = genre;
    }

    public String getLanguage() {
	return Language;
    }

    public void setLanguage(String language) {
	Language = language;
    }

    public String getMetascore() {
	return Metascore;
    }

    public void setMetascore(String metascore) {
	Metascore = metascore;
    }

    public String getPlot() {
	return Plot;
    }

    public void setPlot(String plot) {
	Plot = plot;
    }

    public String getPoster() {
	return Poster;
    }

    public void setPoster(String poster) {
	Poster = poster;
    }

    public String getRated() {
	return Rated;
    }

    public void setRated(String rated) {
	Rated = rated;
    }

    public String getReleased() {
	return Released;
    }

    public void setReleased(String released) {
	Released = released;
    }

    public boolean isResponse() {
	return Response;
    }

    public void setResponse(boolean response) {
	Response = response;
    }

    public String getRuntime() {
	return Runtime;
    }

    public void setRuntime(String runtime) {
	Runtime = runtime;
    }

    public String getSeason() {
	return Season;
    }

    public void setSeason(String season) {
	Season = season;
    }

    public String getTitle() {
	return Title;
    }

    public void setTitle(String title) {
	Title = title;
    }

    public String getType() {
	return Type;
    }

    public void setType(String type) {
	Type = type;
    }

    public String getWriter() {
	return Writer;
    }

    public void setWriter(String writer) {
	Writer = writer;
    }

    public String getYear() {
	return Year;
    }

    public void setYear(String year) {
	Year = year;
    }

    public String getImdbID() {
	return imdbID;
    }

    public void setImdbID(String imdbID) {
	this.imdbID = imdbID;
    }

    public String getImdbRating() {
	return imdbRating;
    }

    public void setImdbRating(String imdbRating) {
	this.imdbRating = imdbRating;
    }

    public String getImdbVotes() {
	return imdbVotes;
    }

    public void setImdbVotes(String imdbVotes) {
	this.imdbVotes = imdbVotes;
    }

    public String getSeriesID() {
	return seriesID;
    }

    public void setSeriesID(String seriesID) {
	this.seriesID = seriesID;
    }

    public String getError() {
	return Error;
    }

    public void setError(String error) {
	Error = error;
    }

    @Override
    public String toString() {
	return "OmdbResponse [Actors=" + Actors + ", Awards=" + Awards + ", Country=" + Country + ", Director=" + Director + ", Episode=" + Episode + ", Genre=" + Genre + ", Language=" + Language + ", Metascore=" + Metascore + ", Plot=" + Plot
		+ ", Poster=" + Poster + ", Rated=" + Rated + ", Released=" + Released + ", Response=" + Response + ", Runtime=" + Runtime + ", Season=" + Season + ", Title=" + Title + ", Type=" + Type + ", Writer=" + Writer + ", Year=" + Year
		+ ", imdbID=" + imdbID + ", imdbRating=" + imdbRating + ", imdbVotes=" + imdbVotes + ", seriesID=" + seriesID + ", Error=" + Error + "]";
    }
}
