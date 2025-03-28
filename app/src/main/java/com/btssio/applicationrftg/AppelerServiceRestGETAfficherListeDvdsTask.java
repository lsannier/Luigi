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

public class AppelerServiceRestGETAfficherListeDvdsTask extends AsyncTask<Void, Void, ArrayList<HashMap<String, String>>> {
    private ListView listeDvds;
    private Context context;
    private Exception exception;

    public AppelerServiceRestGETAfficherListeDvdsTask(ListView listeDvds) {
        this.listeDvds = listeDvds;
        this.context = listeDvds.getContext();
    }

    @Override
    protected ArrayList<HashMap<String, String>> doInBackground(Void... params) {
        ArrayList<HashMap<String, String>> listItem = new ArrayList<>();

        try {
            // Récupérer tous les DVDs sans pagination
            URL url = new URL("http://10.0.2.2:8080/toad/film/all");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(
                new InputStreamReader(connection.getInputStream())
            );
            StringBuilder response = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }

            JSONArray jsonArray = new JSONArray(response.toString());

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject film = jsonArray.getJSONObject(i);
                HashMap<String, String> map = new HashMap<>();
                
                map.put("film_id", String.valueOf(film.getInt("filmId")));
                map.put("title", film.getString("title"));
                map.put("description", film.getString("description"));
                map.put("release_year", String.valueOf(film.getInt("releaseYear")));
                map.put("rating", film.getString("rating"));
                
                listItem.add(map);
            }

            return listItem;
        } catch (Exception e) {
            this.exception = e;
            return null;
        }
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
            SimpleAdapter adapter = new SimpleAdapter(
                context,
                result,
                R.layout.item_dvd,
                new String[]{"title", "description"},
                new int[]{R.id.titleTextView, R.id.descriptionTextView}
            );

            listeDvds.setAdapter(adapter);

            listeDvds.setOnItemClickListener((parent, view, position, id) -> {
                HashMap<String, String> film = (HashMap<String, String>) parent.getItemAtPosition(position);
                
                Intent intent = new Intent(context, DetailsDVDActivity.class);
                intent.putExtra("film_id", Integer.parseInt(film.get("film_id")));
                intent.putExtra("film_title", film.get("title"));
                intent.putExtra("description", film.get("description"));
                intent.putExtra("release_year", film.get("release_year"));
                intent.putExtra("rating", film.get("rating"));
                
                context.startActivity(intent);
            });
        } else {
            Toast.makeText(context, 
                "Aucun film disponible", 
                Toast.LENGTH_SHORT).show();
        }
    }
}