package com.example.appli20240829;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class DvdAdapter extends RecyclerView.Adapter<DvdAdapter.DvdViewHolder> {
    private List<Dvd> dvdList;
    private Context context;
    private static final String INVENTORY_URL = "http://10.0.2.2:8080/toad/inventory/stockFilm";
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    public DvdAdapter(Context context, List<Dvd> dvdList) {
        this.context = context;
        this.dvdList = dvdList;
    }

    private void addToCart(Dvd dvd) {
        PanierActivity.ajouterAuPanier(context, dvd);
        Toast.makeText(context, dvd.getTitle() + " ajouté au panier 🛒", Toast.LENGTH_SHORT).show();
    }

    @NonNull
    @Override
    public DvdViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_dvd, parent, false);
        return new DvdViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DvdViewHolder holder, int position) {
        Dvd dvd = dvdList.get(position);
        holder.title.setText(dvd.getTitle());

        // Affichage tronqué de la description (max 70 caractères)
        String description = dvd.getdescription();
        holder.description.setText((description != null && description.length() > 50)
                ? description.substring(0, 70) + "..."
                : (description != null ? description : "Pas de description disponible"));

        // Vérification de la disponibilité via l'API REST
        new CheckAvailabilityTask(holder, dvd, this).execute();

        // Clic sur un DVD pour ouvrir FilmDetailsActivity
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, FilmDetailsActivity.class);
            intent.putExtra("dvd", dvd); // ✅ On met l'objet complet
            context.startActivity(intent);
            context.startActivity(intent);
        });

        // Ajout au panier lorsqu'on clique sur le bouton
        holder.btnAddToCart.setOnClickListener(v -> {
            if (dvd.isAvailable()) {
                addToCart(dvd);
                Toast.makeText(context, dvd.getTitle() + " ajouté au panier 🛒", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return dvdList.size();
    }

    public static class DvdViewHolder extends RecyclerView.ViewHolder {
        TextView title, description;
        Button btnAddToCart;

        public DvdViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.dvdTitle);
            description = itemView.findViewById(R.id.dvdDescription);
            btnAddToCart = itemView.findViewById(R.id.btnAddToCart);
        }
    }

    private static class CheckAvailabilityTask extends AsyncTask<Void, Void, Boolean> {
        private final DvdViewHolder holder;
        private final Dvd dvd;
        private final DvdAdapter adapter;

        public CheckAvailabilityTask(DvdViewHolder holder, Dvd dvd, DvdAdapter adapter) {
            this.holder = holder;
            this.dvd = dvd;
            this.adapter = adapter;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                URL url = new URL(INVENTORY_URL);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Accept", "application/json");

                if (conn.getResponseCode() == 200) {
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        response.append(line);
                    }
                    br.close();
                    JSONArray inventoryArray = new JSONArray(response.toString());

                    for (int i = 0; i < inventoryArray.length(); i++) {
                        JSONObject inventoryItem = inventoryArray.getJSONObject(i);
                        String title = inventoryItem.getString("title");
                        int filmsDisponibles = inventoryItem.getInt("filmsDisponibles");

                        if (title.equalsIgnoreCase(dvd.getTitle())) {
                            return filmsDisponibles > 0;
                        }
                    }
                }
                conn.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean isAvailable) {
            dvd.setAvailable(isAvailable);
            updateAvailabilityUI(holder, isAvailable);
        }
    }

    private static void updateAvailabilityUI(DvdViewHolder holder, boolean isAvailable) {
        int position = holder.getAdapterPosition();
        if (position == RecyclerView.NO_POSITION) {
            Log.e("DvdAdapter", "Position invalide : " + position);
            return;
        }

        if (isAvailable) {
            holder.btnAddToCart.setEnabled(true);
            holder.btnAddToCart.setText("🛒 Ajouter au panier");
            holder.btnAddToCart.setBackgroundColor(ContextCompat.getColor(holder.btnAddToCart.getContext(), android.R.color.holo_green_dark));
        } else {
            holder.btnAddToCart.setEnabled(false);
            holder.btnAddToCart.setText("❌ Non disponible");
            holder.btnAddToCart.setBackgroundColor(ContextCompat.getColor(holder.btnAddToCart.getContext(), android.R.color.darker_gray));
        }
    }
}
