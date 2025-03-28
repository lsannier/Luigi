package com.btssio.applicationrftg;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.CheckBox;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import android.widget.SimpleAdapter;

public class PanierActivity extends AppCompatActivity {
    private ListView listeDvds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.panier);

        listeDvds = findViewById(R.id.listeDvds);
        
        // Charger le contenu du panier
        refreshList();

        // Bouton Supprimer
        Button deleteBtn = findViewById(R.id.deleteSelectedBtn);
        deleteBtn.setOnClickListener(v -> {
            ArrayList<String> selectedIds = getSelectedRentalIds();
            if (selectedIds == null || selectedIds.isEmpty()) {
                Toast.makeText(this, "Veuillez sélectionner des articles à supprimer", 
                    Toast.LENGTH_SHORT).show();
            } else {
                new AppelerServiceRestPOSTSupprimerDuPanierTask(this, selectedIds).execute();
            }
        });

        // Bouton Valider
        Button validateBtn = findViewById(R.id.validateBtn);
        validateBtn.setOnClickListener(v -> {
            ArrayList<String> selectedIds = getSelectedRentalIds();
            if (selectedIds != null && !selectedIds.isEmpty()) {
                new AppelerServiceRestPOSTValiderPanierTask(this, selectedIds).execute();
            } else {
                Toast.makeText(this, "Veuillez sélectionner des articles à valider", 
                    Toast.LENGTH_SHORT).show();
            }
            if (!selectedIds.isEmpty()) {
                new AppelerServiceRestPOSTValiderPanierTask(this, selectedIds).execute();
            } else {
                Toast.makeText(this, "Veuillez sélectionner des articles à valider", 
                    Toast.LENGTH_SHORT).show();
            }
        });

        // Bouton Retour
        Button returnBtn = findViewById(R.id.returnToShopBtn);
        returnBtn.setOnClickListener(v -> finish());
    }

    private ArrayList<String> getSelectedRentalIds() {
        ArrayList<String> selectedIds = new ArrayList<>();
        for (int i = 0; i < listeDvds.getCount(); i++) {
            @SuppressWarnings("unchecked")
            HashMap<String, String> item = 
                (HashMap<String, String>) listeDvds.getItemAtPosition(i);
            CheckBox checkBox = listeDvds.getChildAt(i).findViewById(R.id.checkBox);
            if (checkBox != null && checkBox.isChecked()) {
                selectedIds.add(item.get("rental_id"));
            }
        }
        return selectedIds;
    }

    public void refreshList() {
        ArrayList<HashMap<String, String>> listItem = new ArrayList<>();
        List<PanierDAO.Film> filmsPanier = PanierDAO.getInstance().getFilmsPanier(); // Utiliser la classe interne Film

        for (PanierDAO.Film film : filmsPanier) { // Utiliser la classe interne Film
            HashMap<String, String> map = new HashMap<>();
            map.put("film_id", String.valueOf(film.getId())); // Assurez-vous d'utiliser les bons getters
            map.put("film_title", film.getTitre()); // Assurez-vous d'utiliser les bons getters
            // Ajouter d'autres détails si nécessaire
            listItem.add(map);
        }

        // Mettre à jour l'adaptateur avec listItem
        SimpleAdapter adapter = new SimpleAdapter(
            this,
            listItem,
            R.layout.item_panier,
            new String[]{"film_title"}, // Champs à afficher
            new int[]{R.id.film_title} // IDs des TextViews
        );
        listeDvds.setAdapter(adapter);
    }
}