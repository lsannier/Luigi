package com.btssio.applicationrftg;

import android.os.AsyncTask;
import android.widget.Toast;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.lang.ref.WeakReference;

public class AppelerServiceRestGETAfficherDetailsDvdsTask extends AsyncTask<String, Void, JSONObject> {
    private final WeakReference<DetailsDVDActivity> activityReference;
    private Exception exception;

    public AppelerServiceRestGETAfficherDetailsDvdsTask(DetailsDVDActivity activity) {
        this.activityReference = new WeakReference<>(activity);
    }

    @Override
    protected JSONObject doInBackground(String... params) {
        try {
            String filmId = params[0];
            URL url = new URL("http://10.0.2.2:8080/toad/film/getById?id=" + filmId);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                throw new Exception("Erreur serveur: " + responseCode);
            }

            BufferedReader reader = new BufferedReader(
                new InputStreamReader(connection.getInputStream())
            );
            StringBuilder response = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }

            return new JSONObject(response.toString());
        } catch (Exception e) {
            this.exception = e;
            return null;
        }
    }

    @Override
    protected void onPostExecute(JSONObject result) {
        DetailsDVDActivity activity = activityReference.get();
        if (activity == null) return;

        if (exception != null) {
            Toast.makeText(activity, 
                "Erreur : " + exception.getMessage(), 
                Toast.LENGTH_LONG).show();
            return;
        }

        if (result != null) {
            try {
                String title = result.optString("title", "Inconnu");
                String description = result.optString("description", "Inconnu");
                String releaseYear = String.valueOf(result.optInt("releaseYear", -1));
                String rating = result.optString("rating", "Inconnu");
                String languageId = result.optString("languageId", "Inconnu");
                String originalLanguageId = result.optString("originalLanguageId", "Inconnu");
                String rentalDuration = result.optString("rentalDuration", "Inconnu");
                String rentalRate = result.optString("rentalRate", "Inconnu");
                String length = result.optString("length", "Inconnu");
                String replacementCost = result.optString("replacementCost", "Inconnu");
                String specialFeatures = result.optString("specialFeatures", "Inconnu");
                String lastUpdate = result.optString("lastUpdate", "Inconnu");
                String genre = result.optString("genre", "Inconnu");
                String cast = result.optString("cast", "Inconnu");

                activity.afficherDetailsDvd(title, description, releaseYear, rating, languageId, originalLanguageId, rentalDuration, rentalRate, length, replacementCost, specialFeatures, lastUpdate, genre, cast);
            } catch (Exception e) {
                Toast.makeText(activity, 
                    "Erreur lors du traitement des données : " + e.getMessage(), 
                    Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(activity, 
                "Impossible de récupérer les détails du film", 
                Toast.LENGTH_SHORT).show();
        }
    }
}