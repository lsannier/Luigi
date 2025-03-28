package com.btssio.applicationrftg;

import java.util.ArrayList;
import java.util.List;

public class AppDatabase {
    private static AppDatabase instance;
    private List<Integer> panierIds;

    private AppDatabase() {
        panierIds = new ArrayList<>();
    }

    public static synchronized AppDatabase getInstance() {
        if (instance == null) {
            instance = new AppDatabase();
        }
        return instance;
    }

    public List<Integer> getPanierIds() {
        return panierIds;
    }

    public void addToPanier(Integer filmId) {
        if (!panierIds.contains(filmId)) {
            panierIds.add(filmId);
        }
    }

    public void removeFromPanier(Integer filmId) {
        panierIds.remove(filmId);
    }

    public void clearPanier() {
        panierIds.clear();
    }

    public int getPanierSize() {
        return panierIds.size();
    }
} 