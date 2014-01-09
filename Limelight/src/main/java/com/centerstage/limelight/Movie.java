package com.centerstage.limelight;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Smitesh Kharat on 11/16/13.
 */
public class Movie {
    private String imdbId;
    private String title;
    private int year;
    private String certification;
    private int runtime;
    private List<String> genres;
    private String releaseDate;
    private int rating_trakt;
    private String synopsis;
    private String tagline;
    private String poster;
    private String posterLarge;
    private String trailer;
    private ArrayList<List<String>> actors;
    private ArrayList<List<String>> directors;
    private ArrayList<List<String>> producers;
    private ArrayList<List<String>> writers;

    private boolean inMovieHistory;
    private boolean inWatchlist;
    private boolean inRecommended;
    private boolean inTheatres;
    private boolean inComingSoon;

    public Movie() {
    }

    public String getImdbId() {
        return imdbId;
    }

    public void setImdbId(String imdbId) {
        this.imdbId = imdbId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getCertification() {
        return certification;
    }

    public void setCertification(String certification) {
        this.certification = certification;
    }

    public int getRuntime() {
        return runtime;
    }

    public void setRuntime(int runtime) {
        this.runtime = runtime;
    }

    public List<String> getGenres() {
        return genres;
    }

    public void setGenres(List<String> genres) {
        this.genres = genres;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public int getRating_trakt() {
        return rating_trakt;
    }

    public void setRating_trakt(int rating_trakt) {
        this.rating_trakt = rating_trakt;
    }

    public String getSynopsis() {
        return synopsis;
    }

    public void setSynopsis(String synopsis) {
        this.synopsis = synopsis;
    }

    public String getTagline() {
        return tagline;
    }

    public void setTagline(String tagline) {
        this.tagline = tagline;
    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    public String getPosterLarge() {
        return posterLarge;
    }

    public void setPosterLarge(String posterLarge) {
        this.posterLarge = posterLarge;
    }

    public String getTrailer() {
        return trailer;
    }

    public void setTrailer(String trailer) {
        this.trailer = trailer;
    }

    public boolean isInWatchlist() {
        return inWatchlist;
    }

    public void setInWatchlist(boolean inWatchlist) {
        this.inWatchlist = inWatchlist;
    }

    public boolean isInMovieHistory() {
        return inMovieHistory;
    }

    public void setInMovieHistory(boolean inMovieHistory) {
        this.inMovieHistory = inMovieHistory;
    }

    public ArrayList<List<String>> getActors() {
        return actors;
    }

    public void setActors(ArrayList<List<String>> actors) {
        this.actors = actors;
    }

    public ArrayList<List<String>> getDirectors() {
        return directors;
    }

    public void setDirectors(ArrayList<List<String>> directors) {
        this.directors = directors;
    }

    public ArrayList<List<String>> getProducers() {
        return producers;
    }

    public void setProducers(ArrayList<List<String>> producers) {
        this.producers = producers;
    }

    public ArrayList<List<String>> getWriters() {
        return writers;
    }

    public void setWriters(ArrayList<List<String>> writers) {
        this.writers = writers;
    }

    public boolean isInRecommended() {
        return inRecommended;
    }

    public void setInRecommended(boolean inRecommended) {
        this.inRecommended = inRecommended;
    }

    public boolean isInTheatres() {
        return inTheatres;
    }

    public void setInTheatres(boolean inTheatres) {
        this.inTheatres = inTheatres;
    }

    public boolean isInComingSoon() {
        return inComingSoon;
    }

    public void setInComingSoon(boolean inComingSoon) {
        this.inComingSoon = inComingSoon;
    }

}
