package com.example.appli20240829;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class PanierActivity extends AppCompatActivity {
    private static final String PREFS_NAME = "PanierPrefs";
    private static final String PANIER_KEY = "panier";
    private static final String API_URL = "http://10.0.2.2:8080/toad/rental/add";

    private static final String API_URL_INVENTORY_ID = "http://10.0.2.2:8080/toad/inventory/available/getById?id=";
    private RecyclerView recyclerView;
    private PanierAdapter panierAdapter;
    private ArrayList<Dvd> panierList;
    private Button btnValiderPanier;

    private static CompletableFuture<Integer> avoirInventoryID(int filmId, Context context) {
        CompletableFuture<Integer> future = new CompletableFuture<>();
        String URL = "http://10.0.2.2:8080/toad/inventory/available/getById?id=" + filmId;

        StringRequest request = new StringRequest(Request.Method.GET, URL,
                response -> {
                    try {
                        int inventoryId = Integer.parseInt(response.trim()); // ✅ Convertit la réponse en int
                        future.complete(inventoryId);
                    } catch (Exception e) {
                        future.completeExceptionally(e);
                    }
                },
                error -> future.completeExceptionally(new Exception("Erreur de réseau"))
        );

        Volley.newRequestQueue(context).add(request);
        return future;
    }


    public interface CustomerIdCallback {
        void onSuccess(int customerId);
        void onError(String error);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_panier);

        recyclerView = findViewById(R.id.panier_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        btnValiderPanier = findViewById(R.id.button_valider_panier);
        btnValiderPanier.setOnClickListener(v -> envoyerPanier(this));

        panierList = getPanier(this);
        panierAdapter = new PanierAdapter(this, panierList, dvd -> {
            panierList.remove(dvd);
            sauvegarderPanier(this, panierList);
            panierAdapter.notifyDataSetChanged();
            Toast.makeText(this, "DVD retiré du panier", Toast.LENGTH_SHORT).show();
            verifierDisponibilite(); // ✅ Vérifier après suppression
        });
        recyclerView.setAdapter(panierAdapter);

        verifierDisponibilite(); // ✅ Vérifier après chargement du panier
    }

    private void verifierDisponibilite() {
        if (panierList.isEmpty()) {
            btnValiderPanier.setEnabled(false);
            Log.d("PanierActivity", "Bouton désactivé : panier vide");
            return;
        }

        CompletableFuture<Boolean> disponibiliteFuture = CompletableFuture.completedFuture(true);

        for (Dvd dvd : panierList) {
            disponibiliteFuture = disponibiliteFuture.thenCombine(avoirInventoryID(dvd.getFilmId(), this),
                    (currentStatus, inventoryId) -> {
                        boolean isAvailable = inventoryId != null && inventoryId > 0;
                        Log.d("PanierActivity", "FilmID: " + dvd.getFilmId() + ", InventoryID: " + inventoryId + ", Disponible: " + isAvailable);
                        return currentStatus && isAvailable; // ❌ Désactive si un seul film est indisponible
                    });
        }

        disponibiliteFuture.thenAccept(isAvailable -> {
            runOnUiThread(() -> {
                btnValiderPanier.setEnabled(isAvailable);
                Log.d("PanierActivity", "Bouton mis à jour : " + (isAvailable ? "Activé" : "Désactivé"));
            });
        });
    }



    public static void ajouterAuPanier(Context context, Dvd dvd) {
        ArrayList<Dvd> panier = getPanier(context);
        panier.add(dvd);
        sauvegarderPanier(context, panier);
        Log.d("Panier", "DVD ajouté : " + dvd.getTitle());
    }

    public static ArrayList<Dvd> getPanier(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String json = prefs.getString(PANIER_KEY, null);
        if (json == null) return new ArrayList<>();
        return new Gson().fromJson(json, new TypeToken<ArrayList<Dvd>>() {}.getType());
    }

    public static void sauvegarderPanier(Context context, ArrayList<Dvd> panier) {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit();
        editor.putString(PANIER_KEY, new Gson().toJson(panier));
        editor.apply();
    }

    public static void viderPanier(Context context) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit().remove(PANIER_KEY).apply();

        // ✅ Désactiver le bouton si le panier est vidé
        if (context instanceof PanierActivity) {
            ((PanierActivity) context).verifierDisponibilite();
        }
    }


    private void envoyerPanier(Context context) {
        RequestQueue queue = Volley.newRequestQueue(context);
        if (panierList.isEmpty()) {
            Toast.makeText(context, "Le panier est vide", Toast.LENGTH_SHORT).show();
            return;
        }

        SharedPreferences prefs = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        int customerId = prefs.getInt("customerId", -1);
        if (customerId == -1) {
            Toast.makeText(context, "Utilisateur non authentifié", Toast.LENGTH_SHORT).show();
            return;
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String rentalDate = dateFormat.format(new Date());
        String lastUpdate = rentalDate;

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, 180);
        String returnDate = dateFormat.format(calendar.getTime());

        for (Dvd dvd : panierList) {
            avoirInventoryID(dvd.getFilmId(), context).thenAccept(inventoryId -> {
                if (inventoryId == null) {
                    Log.e("PanierActivity", "Film indisponible, location annulée.");
                    return;
                }

                StringRequest request = new StringRequest(Request.Method.POST, API_URL,
                        response -> {
                            Log.d("PanierManager", "Réponse de l'API : " + response);
                            Toast.makeText(context, "Location enregistrée", Toast.LENGTH_SHORT).show();
                            viderPanier(context);
                        },
                        error -> {
                            Log.e("PanierActivity", "Erreur lors de l'enregistrement", error);
                            Toast.makeText(context, "Erreur lors de l'enregistrement", Toast.LENGTH_SHORT).show();
                        }) {
                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> params = new HashMap<>();
                        params.put("inventory_id", String.valueOf(inventoryId));
                        params.put("customer_id", String.valueOf(customerId));
                        params.put("rental_date", rentalDate);
                        params.put("return_date", returnDate);
                        params.put("staff_id", "1");
                        params.put("last_update", lastUpdate);
                        return params;
                    }
                };

                queue.add(request);
            }).exceptionally(error -> {
                Log.e("PanierManager", "Erreur lors de la récupération de inventoryId", error);
                return null;
            });
        }
    }



    public static class PanierAdapter extends RecyclerView.Adapter<PanierAdapter.PanierViewHolder> {
        private final List<Dvd> panier;
        private final Context context;
        private final OnItemRemoveListener removeListener;

        public interface OnItemRemoveListener { void onItemRemove(Dvd dvd); }

        public PanierAdapter(Context context, List<Dvd> panier, OnItemRemoveListener removeListener) {
            this.context = context;
            this.panier = panier;
            this.removeListener = removeListener;
        }

        @NonNull
        @Override
        public PanierViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_dvd, parent, false);
            return new PanierViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull PanierViewHolder holder, int position) {
            Dvd dvd = panier.get(position);
            holder.title.setText(dvd.getTitle());
            holder.description.setText(dvd.getdescription());
            holder.btnRemoveFromCart.setOnClickListener(v -> removeListener.onItemRemove(dvd));
        }

        @Override
        public int getItemCount() { return panier.size(); }

        public static class PanierViewHolder extends RecyclerView.ViewHolder {
            TextView title, description;
            Button btnRemoveFromCart;
            public PanierViewHolder(@NonNull View itemView) {
                super(itemView);
                title = itemView.findViewById(R.id.dvdTitle);
                description = itemView.findViewById(R.id.dvdDescription);
                btnRemoveFromCart = itemView.findViewById(R.id.btnAddToCart);
                btnRemoveFromCart.setText("Retirer");
            }
        }
    }
}
