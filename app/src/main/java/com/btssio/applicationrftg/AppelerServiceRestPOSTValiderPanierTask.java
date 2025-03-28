package com.btssio.applicationrftg;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;
import org.json.JSONArray;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class AppelerServiceRestPOSTValiderPanierTask extends AsyncTask<Void, Void, Boolean> {
    private final Context context;
    private final ArrayList<String> rentalIds;
    private Exception exception;

    public AppelerServiceRestPOSTValiderPanierTask(Context context, ArrayList<String> rentalIds) {
        this.context = context;
        this.rentalIds = rentalIds;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        try {
            URL url = new URL("http://10.0.2.2:8080/toad/rental/validate");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            // Convertir les IDs en JSON
            JSONArray jsonArray = new JSONArray(rentalIds);
            String jsonString = jsonArray.toString();

            // Envoyer les données
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonString.getBytes("utf-8");
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
            Toast.makeText(context, "Commande validée avec succès", Toast.LENGTH_SHORT).show();
            if (context instanceof PanierActivity) {
                ((PanierActivity) context).finish();
            }
        } else {
            Toast.makeText(context, "Erreur lors de la validation : " + (exception != null ? exception.getMessage() : "inconnue"), Toast.LENGTH_SHORT).show();
        }
    }
} 