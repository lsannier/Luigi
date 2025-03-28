package com.btssio.applicationrftg;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private ListView listeDvds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        // Initialisation des vues
        listeDvds = findViewById(R.id.listeDvds);

        // Configuration initiale
        loadDvdList();

        Button panierButton = findViewById(R.id.buttonPanier);
        if (panierButton == null) {
            Toast.makeText(this, "Erreur : panierButton est null", Toast.LENGTH_SHORT).show();
            return; // Sortir si le bouton est null
        }

        panierButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, PanierActivity.class);
            startActivity(intent);
        });
    }

    private void loadDvdList() {
        try {
            new AppelerServiceRestGETAfficherListeDvdsTask(listeDvds).execute();
        } catch (Exception e) {
            Toast.makeText(this, "Erreur lors du chargement de la liste des DVDs", Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressLint("InflateParams")
    private void showPopup() {
        try {
            LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);



            View rootView = findViewById(android.R.id.content);

        } catch (Exception e) {
            Toast.makeText(this, "Erreur lors de l'affichage de la popup", Toast.LENGTH_SHORT).show();
        }
    }

    public void openCart(View view) {
        Intent intent = new Intent(this, PanierActivity.class);
        startActivity(intent);
    }
}
