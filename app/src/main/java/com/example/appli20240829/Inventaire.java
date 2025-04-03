package com.example.appli20240829;

import android.content.Context;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import java.util.ArrayList;
import java.util.List;
public class Inventaire {

    private final String url = "http://10.0.2.2:8080/toad/inventory/stock/byFilm";
    private final List<Dvd> dvdList;
    private final DvdAdapter dvdAdapter;
    private final RequestQueue queue;

    // Constructeur
    public Inventaire(Context context, List<Dvd> dvdList, DvdAdapter dvdAdapter) {
        this.dvdList = dvdList;
        this.dvdAdapter = dvdAdapter;
        this.queue = Volley.newRequestQueue(context);
    }

    // Méthode pour récupérer les films disponibles
    public void fetchAvailableFilms() {
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    List<Integer> availableFilmIds = new ArrayList<>();
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            availableFilmIds.add(response.getInt(i));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    updateFilmAvailability(availableFilmIds);
                },
                error -> error.printStackTrace()
        );

        queue.add(request);
    }

    // Mise à jour de la disponibilité des films
    private void updateFilmAvailability(List<Integer> availableFilmIds) {
        for (Dvd dvd : dvdList) {
            dvd.setAvailable(availableFilmIds.contains(dvd.getfilmId()));
        }
        dvdAdapter.notifyDataSetChanged();
    }
}

