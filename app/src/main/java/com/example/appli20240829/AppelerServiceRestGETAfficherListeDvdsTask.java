/**
 * Classe permettant d'effectuer un appel REST en tâche de fond pour récupérer une liste de DVD.
 * Utilise AsyncTask pour exécuter la requête HTTP GET en arrière-plan et mettre à jour l'UI.
 */


package com.example.appli20240829;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class AppelerServiceRestGETAfficherListeDvdsTask extends AsyncTask<URL, Integer, String> {
    private final AfficherListeDvdsActivity screen;

    public AppelerServiceRestGETAfficherListeDvdsTask(AfficherListeDvdsActivity screen) {
        this.screen = screen;
    }

    // Méthode pour convertir le JSON en ArrayList de Dvd
    public ArrayList<Dvd> convertitListeDvdsEnArrayList(String dvdJson) {
        Gson gson = new Gson();
        Type dvdListType = new TypeToken<ArrayList<Dvd>>() {}.getType();
        ArrayList<Dvd> dvdArray = gson.fromJson(dvdJson, dvdListType);

        // Log des résultats pour contrôle
        Log.d("mydebug", ">>>> Liste des DVD reçue >>>>>>>>>>>>>>> DÉBUT");
        for (Dvd dvd : dvdArray) {
            Log.d("mydebug", "Titre = " + dvd.getTitle() + " / Année = " + dvd.getReleaseYear());
        }
        Log.d("mydebug", ">>>> Liste des DVD reçue >>>>>>>>>>>>>>> FIN");
        return dvdArray;
    }

    @Override
    protected void onPreExecute() {
        // Préparation avant l'appel (par exemple : afficher un indicateur de chargement)
    }

    @Override
    protected String doInBackground(URL... urls) {
        URL urlAAppeler = urls[0];
        return appelerServiceRestHttp(urlAAppeler);
    }

    @Override
    protected void onPostExecute(String resultat) {
        Log.d("mydebug", ">>> onPostExecute / Résultat = " + resultat);
        if (resultat != null && !resultat.isEmpty()) {
            // Convertir le JSON en ArrayList de Dvd
            ArrayList<Dvd> dvdList = convertitListeDvdsEnArrayList(resultat);
            if (screen != null) {
                // Appeler la méthode de mise à jour avec la liste des DVD
                screen.mettreAJourListeDvds(dvdList);
            }
        } else {
            Log.d("mydebug", "Résultat de l'appel REST est vide ou nul.");
        }
    }



    private String appelerServiceRestHttp(URL urlAAppeler) {
        HttpURLConnection urlConnection = null;
        StringBuilder sResultatAppel = new StringBuilder();
        try {
            urlConnection = (HttpURLConnection) urlAAppeler.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setRequestProperty("Accept", "application/json");
            urlConnection.setRequestProperty("User-Agent", System.getProperty("http.agent"));

            int responseCode = urlConnection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                try (InputStream in = new BufferedInputStream(urlConnection.getInputStream())) {
                    int codeCaractere;
                    while ((codeCaractere = in.read()) != -1) {
                        sResultatAppel.append((char) codeCaractere);
                    }
                }
            } else {
                Log.d("mydebug", "Erreur lors de l'appel - Code réponse: " + responseCode);
            }
        } catch (IOException ioe) {
            Log.e("mydebug", "IOException lors de l'appel : " + ioe.toString());
        } catch (Exception e) {
            Log.e("mydebug", "Exception lors de l'appel : " + e.toString());
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return sResultatAppel.toString();
    }
}
