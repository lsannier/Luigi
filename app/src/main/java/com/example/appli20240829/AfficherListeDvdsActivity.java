/**
 * Activité permettant d'afficher la liste des DVD disponibles.
 * Les données sont récupérées via un appel REST et affichées dans un RecyclerView.
 * Un bouton permet d'accéder au panier.
 */


package com.example.appli20240829;

import android.content.Intent;
import android.database.MatrixCursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class AfficherListeDvdsActivity extends AppCompatActivity {
    public static final String EXTRA_MESSAGE = "com.example.appli20240829.MESSAGE";
    private SimpleCursorAdapter adapter;
    private MatrixCursor dvdCursor;
    private RecyclerView recyclerView;
    private DvdAdapter dvdAdapter;
    private ArrayList<Dvd> dvdList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_afficherlistedvds);

        recyclerView = findViewById(R.id.recyclerViewDVD);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        dvdAdapter = new DvdAdapter(this, dvdList);
        recyclerView.setAdapter(dvdAdapter);

        String apiUrl = "http://10.0.2.2:8080/toad/film/all";
        new AppelerServiceRestGETAfficherListeDvdsTask().execute(apiUrl);

        Button btnLogout = findViewById(R.id.Disconnect);
        Button btnVoirPanier = findViewById(R.id.btnVoirPanier);
        btnVoirPanier.setOnClickListener(v -> {
            Intent intent = new Intent(AfficherListeDvdsActivity.this, PanierActivity.class);
            startActivity(intent);
        });
        Button Disconnect = findViewById(R.id.Disconnect);
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Action de déconnexion
                logout();
            }
        });
    }

    public void mettreAJourListeDvds(ArrayList<Dvd> newDvdList) {
        if (newDvdList != null) {
            dvdList.clear();
            dvdList.addAll(newDvdList);
            dvdAdapter.notifyDataSetChanged();
        } else {
            Log.e("mydebug", "Erreur : La liste des DVD est nulle");
        }
    }

    private class AppelerServiceRestGETAfficherListeDvdsTask extends AsyncTask<String, Void, ArrayList<Dvd>> {

        @Override
        protected ArrayList<Dvd> doInBackground(String... urls) {
            String urlString = urls[0];
            StringBuilder result = new StringBuilder();

            try {
                URL url = new URL(urlString);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                Log.d("API", "Connexion réussie à : " + urlString);

                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }
                reader.close();

                Log.d("API", "Réponse reçue : " + result.toString());

                return convertirJsonEnListeDvds(result.toString());

            } catch (Exception e) {
                Log.e("API", "Erreur de connexion ou de lecture : ", e);
                return null;
            }
        }

        private ArrayList<Dvd> convertirJsonEnListeDvds(String jsonResult) {
            Gson gson = new Gson();
            Type dvdListType = new TypeToken<ArrayList<Dvd>>() {}.getType();
            return gson.fromJson(jsonResult, dvdListType);
        }

        @Override
        protected void onPostExecute(ArrayList<Dvd> films) {
            if (films == null) {
                Log.e("API", "Erreur : liste de films récupérée est null");
                return;
            }

            mettreAJourListeDvds(films);
            Log.d("API", "Liste de DVD mise à jour avec succès !");
        }
    }
    private void logout() {

        Intent intent = new Intent(AfficherListeDvdsActivity.this, MainActivity.class);
        startActivity(intent);
        finish(); // Fermer l'activité actuelle
    }
}