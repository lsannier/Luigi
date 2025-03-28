package com.btssio.applicationrftg;

import android.content.Context;
import androidx.lifecycle.LiveData;
import java.util.List;

public class PanierRepository {
    private PanierDAO panierDAO;
    private static PanierRepository instance;

    private PanierRepository() {
        panierDAO = PanierDAO.getInstance();
    }

    public static synchronized PanierRepository getInstance() {
        if (instance == null) {
            instance = new PanierRepository();
        }
        return instance;
    }

    public List<PanierDAO.Film> getFilmsPanier() {
        return panierDAO.getFilmsPanier();
    }

    public void ajouterFilm(PanierDAO.Film film) {
        panierDAO.ajouterFilm(film);
    }

    public void supprimerFilm(PanierDAO.Film film) {
        panierDAO.supprimerFilm(film);
    }

    public void viderPanier() {
        panierDAO.viderPanier();
    }

    public int getNombreFilms() {
        return panierDAO.getNombreFilms();
    }

    public boolean contientFilm(PanierDAO.Film film) {
        return panierDAO.contientFilm(film);
    }
} 