/**
 * Activité affichée après la validation d'une action, comme une commande ou une réservation.
 *
 * - `onCreate(Bundle savedInstanceState)`: Initialise l'interface utilisateur avec le layout `activity_confirmation`.
 *
 * Cette activité sert principalement à informer l'utilisateur que son action a été prise en compte.
 */


package com.example.appli20240829;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class ConfirmationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmation);


    }
}
