package com.btssio.applicationrftg;

import android.os.Bundle;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;

public class AfficherListeDvdsActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_afficherlistedvds);

        ListView listeDvds = findViewById(R.id.listeDvds);
        // Charger la liste des DVDs
        new AppelerServiceRestGETAfficherListeDvdsTask(listeDvds).execute();
    }
}