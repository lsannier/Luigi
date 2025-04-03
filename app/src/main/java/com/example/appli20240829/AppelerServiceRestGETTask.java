/**
 * Classe permettant d'effectuer un appel REST GET en arrière-plan pour récupérer une liste de DVD.
 * Utilise AsyncTask pour exécuter la requête réseau et mettre à jour l'interface utilisateur.
 *
 * - `doInBackground(URL... urls)`: Effectue l'appel REST et retourne la réponse sous forme de chaîne JSON.
 * - `onPostExecute(String resultat)`: Traite la réponse et met à jour l'interface utilisateur avec la liste des DVD.
 * - `convertitListeDvdsEnArrayList(String dvdJson)`: Convertit le JSON en une liste d'objets `Dvd`.
 * - `appelerServiceRestHttp(URL urlAAppeler)`: Gère la connexion HTTP et lit la réponse.
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

public class AppelerServiceRestGETTask extends AsyncTask<URL, Integer, String> {
    private volatile AfficherListeDvdsActivity screen; // Référence à l'écran

    public AppelerServiceRestGETTask(AfficherListeDvdsActivity s) {
        this.screen = s;
    }

    @Override
    protected void onPreExecute() {
        // Prétraitement de l'appel, par exemple afficher un indicateur de chargement
        Log.d("mydebug", ">>> Préparation de l'appel REST");
    }

    @Override
    protected String doInBackground(URL... urls) {
        URL urlAAppeler = urls[0];
        return appelerServiceRestHttp(urlAAppeler);
    }

    @Override
    protected void onPostExecute(String resultat) {
        Log.d("mydebug", ">>> onPostExecute / résultat = " + resultat);

        if (resultat != null && !resultat.isEmpty()) {
            // Désérialisation du JSON en une liste de DVD
            ArrayList<Dvd> dvdList = convertitListeDvdsEnArrayList(resultat);

            if (screen != null) {
                // Mise à jour de l'interface utilisateur
                screen.mettreAJourListeDvds(dvdList);
            }
        } else {
            Log.d("mydebug", ">>> Résultat vide ou nul.");
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

    private ArrayList<Dvd> convertitListeDvdsEnArrayList(String dvdJson) {
        Gson gson = new Gson();
        Type dvdListType = new TypeToken<ArrayList<Dvd>>() {}.getType();
        ArrayList<Dvd> dvdArray = gson.fromJson(dvdJson, dvdListType);

        // Log pour contrôle
        Log.d("mydebug", ">>> Liste des DVD reçue :");
        for (Dvd dvd : dvdArray) {
            Log.d("mydebug", "Titre = " + dvd.getTitle() + ", Année = " + dvd.getReleaseYear());
        }
        return dvdArray;
    }
}
