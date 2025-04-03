package com.example.appli20240829;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class FilmDetailsActivity extends AppCompatActivity {
    private TextView titleView, descriptionView, ratingView, yearView;
    private Dvd dvd;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_film_details);

        titleView = findViewById(R.id.filmTitle);
        descriptionView = findViewById(R.id.filmDescription);
        ratingView = findViewById(R.id.rating);
        yearView = findViewById(R.id.releaseYear);

        // Récupérer l'objet Dvd depuis DvdAdapter
        dvd = (Dvd) getIntent().getSerializableExtra("dvd");

        if (dvd != null) {
            titleView.setText(dvd.getTitle());
            descriptionView.setText(dvd.getdescription());
            ratingView.setText(dvd.getrating());
            yearView.setText(String.valueOf(dvd.getReleaseYear()));
        }
    }
}

