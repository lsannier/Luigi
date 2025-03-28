package com.btssio.applicationrftg;

public class Film {
    private String filmId;
    private String title;
    private String releaseYear;
    private String languageId;
    private String length;

    public Film(String filmId, String title, String releaseYear, String languageId, String length) {
        this.filmId = filmId;
        this.title = title;
        this.releaseYear = releaseYear;
        this.languageId = languageId;
        this.length = length;
    }

    // Getters
    public String getFilmId() { return filmId; }
    public String getTitle() { return title; }
    public String getReleaseYear() { return releaseYear; }
    public String getLanguageId() { return languageId; }
    public String getLength() { return length; }
} 