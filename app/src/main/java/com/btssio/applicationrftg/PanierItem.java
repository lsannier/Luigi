package com.btssio.applicationrftg;

public class PanierItem {
    private int id;
    private int filmId;
    private String filmTitle;
    private String rentalDate;

    public PanierItem(int id, int filmId, String filmTitle, String rentalDate) {
        this.id = id;
        this.filmId = filmId;
        this.filmTitle = filmTitle;
        this.rentalDate = rentalDate;
    }

    // Getters
    public int getId() {
        return id;
    }

    public int getFilmId() {
        return filmId;
    }

    public String getFilmTitle() {
        return filmTitle;
    }

    public String getRentalDate() {
        return rentalDate;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setFilmId(int filmId) {
        this.filmId = filmId;
    }

    public void setFilmTitle(String filmTitle) {
        this.filmTitle = filmTitle;
    }

    public void setRentalDate(String rentalDate) {
        this.rentalDate = rentalDate;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof PanierItem) {
            PanierItem other = (PanierItem) obj;
            return this.id == other.id;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return id;
    }
} 