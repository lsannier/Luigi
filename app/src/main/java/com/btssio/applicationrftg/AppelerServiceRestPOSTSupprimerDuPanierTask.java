package com.btssio.applicationrftg;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;
import org.json.JSONArray;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class AppelerServiceRestPOSTSupprimerDuPanierTask extends AsyncTask<Void, Void, Boolean> {
    @SuppressLint("StaticFieldLeak")
    private final Context context;
    private final ArrayList<String> rentalIds;
    private Exception exception;

    public AppelerServiceRestPOSTSupprimerDuPanierTask(Context context, ArrayList<String> rentalIds) {
        this.context = context;
        this.rentalIds = rentalIds;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        if (rentalIds.isEmpty()) {
            return false; // Vérifie si la liste des IDs est vide
        }
        try {
            // Supprimer le film de la base de données
            for (String rentalId : rentalIds) {
                AppDatabase.getInstance().removeFromPanier(Integer.parseInt(rentalId));
            }
            // Logique pour envoyer la requête au serveur
            URL url = new URL("http://10.0.2.2:8080/toad/rental/remove");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            JSONArray jsonArray = new JSONArray(rentalIds);
            String jsonString = jsonArray.toString();

            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonString.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            int responseCode = connection.getResponseCode();
            return responseCode == HttpURLConnection.HTTP_OK;
        } catch (Exception e) {
            this.exception = e;
            return false;
        }
    }

    @Override
    protected void onPostExecute(Boolean success) {
        if (exception != null) {
            Toast.makeText(context, "Erreur : " + exception.getMessage(), Toast.LENGTH_LONG).show();
            return;
        }

        if (success) {
            Toast.makeText(context, "Articles supprimés avec succès", Toast.LENGTH_SHORT).show();
            if (context instanceof PanierActivity) {
                ((PanierActivity) context).refreshList();
            }
        } else {
            Toast.makeText(context, "Erreur lors de la suppression", Toast.LENGTH_SHORT).show();
        }
    }
} 