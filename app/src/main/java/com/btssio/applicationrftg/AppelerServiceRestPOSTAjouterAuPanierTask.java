package com.btssio.applicationrftg;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;
import org.json.JSONObject;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class AppelerServiceRestPOSTAjouterAuPanierTask extends AsyncTask<Void, Void, Boolean> {
    private final Context context;
    private final int filmId;
    private final String filmTitle;
    private Exception exception;

    public AppelerServiceRestPOSTAjouterAuPanierTask(Context context, int filmId, String filmTitle) {
        this.context = context;
        this.filmId = filmId;
        this.filmTitle = filmTitle;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        try {
            // Préparer l'URL
            URL url = new URL("http://10.0.2.2:8080/toad/rental/add");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            // Préparer les données JSON
            JSONObject jsonParam = new JSONObject();
            jsonParam.put("filmId", filmId);
            jsonParam.put("filmTitle", filmTitle); // Assurez-vous que le titre est inclus
            String jsonString = jsonParam.toString();

            // Envoyer les données
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonString.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            // Vérifier la réponse
            int responseCode = connection.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                // Log l'erreur
                String errorMessage = "Erreur lors de l'ajout au panier : " + responseCode;
                Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show();
                return false;
            }

            return true;

        } catch (Exception e) {
            this.exception = e;
            return false;
        }
    }

    @Override
    protected void onPostExecute(Boolean success) {

            Toast.makeText(context, "Film ajouté au panier avec succès", Toast.LENGTH_SHORT).show();

    }
} 