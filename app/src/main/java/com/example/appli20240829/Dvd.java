/**
 * Classe représentant un DVD avec ses principales caractéristiques.
 *
 * Attributs :
 * - `title` (String) : Titre du film.
 * - `releaseYear` (int) : Année de sortie.
 * - `languageId` (String) : Identifiant de la langue.
 * - `length` (int) : Durée du film en minutes.
 * - `genre` (String) : Genre du film.
 * - `description` (String) : Description du film (⚠️ Non initialisé dans le constructeur).
 *
 * Méthodes :
 * - Getters pour accéder aux attributs.
 * - `toString()` pour un affichage formaté des informations du DVD.
 */

package com.example.appli20240829;


import java.io.Serializable;

public class Dvd implements Serializable {
    private String title;
    private int releaseYear;
    private String languageId;
    private int length;

    private Boolean available;
    private Boolean isAvailable;
    private String description;

    private int filmId;
    private String rating;
    // Constructeur
    public Dvd(int filmId, String title, String description, String rating, int releaseYear, int length) {
        this.title = title;
        this.filmId = filmId;
        this.releaseYear = releaseYear;
        this.languageId = languageId;
        this.length = length;
        this.description = description;
        this.available = false;
    }

    public Dvd(String title, Integer annee) {

    }


    // Getters

    public int getFilmId(){ return filmId; }
    public String getrating(){ return rating; }

    public String getTitle() {
        return title;
    }

    public int getReleaseYear() {
        return releaseYear;
    }

    public String getLanguageId() {
        return languageId;
    }

    public int getLength() {
        return length;
    }

    public String getdescription() { return description; }

    public Boolean isAvailable() { return available; }


    // Méthode toString pour un affichage clair
    @Override
    public String toString() {
        return "Dvd{" +
                "title='" + title + '\'' +
                ", releaseYear=" + releaseYear +
                ", languageId='" + languageId + '\'' +
                ", length=" + length +
                ", description='" + description + '\'' +
                '}';
    }

    public Dvd(int filmId) {
        this.filmId = filmId;
        this.available = false; // Par défaut, le film est indisponible
    }

    public int getfilmId() {
        return filmId;
    }

    public void setAvailable(Boolean available) { // Ajout du setter
        this.available = available;
    }



}

