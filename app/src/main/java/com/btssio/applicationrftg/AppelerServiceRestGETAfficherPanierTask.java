package com.btssio.applicationrftg;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import android.app.Activity;
import java.util.List;

import com.btssio.applicationrftg.DetailsDVDActivity;

public class AppelerServiceRestGETAfficherPanierTask extends AsyncTask<String, Void, ArrayList<HashMap<String, String>>> {
    private ListView listeDvds;
    private Context context;
    private Exception exception;

    public AppelerServiceRestGETAfficherPanierTask(ListView listeDvds) {
        this.listeDvds = listeDvds;
        this.context = listeDvds.getContext();
    }

    @Override
    protected ArrayList<HashMap<String, String>> doInBackground(String... params) {
        ArrayList<HashMap<String, String>> listItem = new ArrayList<>();
        List<Integer> panierIds = AppDatabase.getInstance().getPanierIds();

        for (Integer filmId : panierIds) {
            // Logique pour récupérer les détails du film par filmId
            // Ajouter les détails à listItem
        }

        return listItem;
    }

    @Override
    protected void onPostExecute(ArrayList<HashMap<String, String>> result) {
        if (exception != null) {
            Toast.makeText(context, 
                "Erreur : " + exception.getMessage(), 
                Toast.LENGTH_LONG).show();
            return;
        }

        if (result != null && !result.isEmpty()) {
            String[] from = {"film_title", "rental_date"};
            int[] to = {R.id.film_title, R.id.rental_date};

            SimpleAdapter adapter = new SimpleAdapter(
                context,
                result,
                R.layout.item_panier,
                from,
                to
            );
            listeDvds.setAdapter(adapter);
        } else {
            Toast.makeText(context, 
                "Le panier est vide", 
                Toast.LENGTH_SHORT).show();
        }
    }
}