package com.btssio.applicationrftg;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class DetailsDVDActivity extends AppCompatActivity {
    private PanierRepository panierRepository;
    private int filmId;
    private String filmTitle;
    private TextView titleTextView;
    private TextView descriptionTextView;
    private TextView releaseYearTextView;
    private TextView ratingTextView;
    private TextView specialFeaturesTextView;
    private TextView lastUpdateTextView;
    private TextView genreTextView;
    private TextView durationTextView;
    private TextView castTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.page_details_dvd);

        // Initialiser les vues
        titleTextView = findViewById(R.id.titleTextView);
        descriptionTextView = findViewById(R.id.descriptionTextView);
        releaseYearTextView = findViewById(R.id.releaseYearTextView);
        ratingTextView = findViewById(R.id.ratingTextView);
        Button addToPanierButton = findViewById(R.id.addToPanierButton);
        Button returnToShopButton = findViewById(R.id.returnToShopButton);
        specialFeaturesTextView = findViewById(R.id.specialFeaturesTextView);
        lastUpdateTextView = findViewById(R.id.lastUpdateTextView);
        genreTextView = findViewById(R.id.genreTextView);
        durationTextView = findViewById(R.id.durationTextView);
        castTextView = findViewById(R.id.castTextView);

        // Récupérer l'ID du film et le titre depuis l'intent
        filmId = getIntent().getIntExtra("film_id", -1);
        filmTitle = getIntent().getStringExtra("film_title");
        
        if (filmId != -1) {
            // Charger les détails du film
            new AppelerServiceRestGETAfficherDetailsDvdsTask(this).execute(String.valueOf(filmId));
        } else {
            Toast.makeText(this, "Erreur : ID du film non valide", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        panierRepository = PanierRepository.getInstance();

        // Gérer le clic sur "Ajouter au panier"
        addToPanierButton.setOnClickListener(v -> {
            if (filmId != -1 && filmTitle != null) {
                PanierDAO.Film film = new PanierDAO.Film(
                    filmId,
                    filmTitle,
                    java.time.LocalDateTime.now().toString()
                );

                if (!panierRepository.contientFilm(film)) {
                    panierRepository.ajouterFilm(film);
                    new AppelerServiceRestPOSTAjouterAuPanierTask(this, filmId, filmTitle).execute();
                    Toast.makeText(this, "Film ajouté au panier", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Ce film est déjà dans votre panier", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Gérer le clic sur "Retour à la boutique"
        returnToShopButton.setOnClickListener(v -> finish());
    }

    // Méthode pour afficher les détails du film
    public void afficherDetailsDvd(String title, String description, String releaseYear, String rating, String languageId, String originalLanguageId, String rentalDuration, String rentalRate, String length, String replacementCost, String specialFeatures, String lastUpdate, String genre, String cast) {
        titleTextView.setText(title);
        descriptionTextView.setText(description);
        releaseYearTextView.setText("Année : " + (releaseYear.equals("-1") ? "Inconnu" : releaseYear));
        ratingTextView.setText("Note : " + rating);
        genreTextView.setText("Genre : " + genre);
        durationTextView.setText("Durée : " + length + " minutes");
        castTextView.setText("Casting : " + cast);
        specialFeaturesTextView.setText("Fonctionnalités spéciales : " + specialFeatures);
        lastUpdateTextView.setText("Dernière mise à jour : " + lastUpdate);
        
        // Mettre à jour le titre stocké
        this.filmTitle = title;
    }
}