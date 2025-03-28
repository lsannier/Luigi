package com.btssio.applicationrftg;

import java.util.ArrayList;
import java.util.List;

public class PanierDAO {
    private static PanierDAO instance;
    private List<Film> filmsPanier;

    private PanierDAO() {
        filmsPanier = new ArrayList<>();
    }

    public static synchronized PanierDAO getInstance() {
        if (instance == null) {
            instance = new PanierDAO();
        }
        return instance;
    }

    public List<Film> getFilmsPanier() {
        List<Film> films = new ArrayList<>();
        for (Film film : filmsPanier) {
            films.add(new Film(film.getId(), film.getTitre(), film.getDateLocation()));
        }
        return films;
    }

    public void ajouterFilm(Film film) {
        if (!filmsPanier.contains(film)) {
            filmsPanier.add(film);
        }
    }

    public void supprimerFilm(Film film) {
        filmsPanier.remove(film);
    }

    public void viderPanier() {
        filmsPanier.clear();
    }

    public int getNombreFilms() {
        return filmsPanier.size();
    }

    public boolean contientFilm(Film film) {
        return filmsPanier.contains(film);
    }

    public static class Film {
        private int id;
        private String titre;
        private String dateLocation;

        public Film(int id, String titre, String dateLocation) {
            this.id = id;
            this.titre = titre;
            this.dateLocation = dateLocation;
        }

        public int getId() {
            return id;
        }

        public String getTitre() {
            return titre;
        }

        public String getDateLocation() {
            return dateLocation;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof Film) {
                Film other = (Film) obj;
                return this.id == other.id;
            }
            return false;
        }

        @Override
        public int hashCode() {
            return id;
        }
    }
} 