package com.example.appli20240829;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONObject;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity {

    private EditText emailInput, passwordInput;
    private Button loginButton;
    private TextView registerLink, forgotPasswordLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        emailInput = findViewById(R.id.editTextUsername);
        passwordInput = findViewById(R.id.editTextPassword);
        loginButton = findViewById(R.id.buttonLogin);
        registerLink = findViewById(R.id.textViewCreateAccount);
        forgotPasswordLink = findViewById(R.id.textViewForgotPassword);

        loginButton.setOnClickListener(view -> new LoginTask().execute(emailInput.getText().toString(), passwordInput.getText().toString()));
        registerLink.setOnClickListener(view -> startActivity(new Intent(this, RegisterActivity.class)));
        forgotPasswordLink.setOnClickListener(view -> Toast.makeText(this, "Fonctionnalité à implémenter", Toast.LENGTH_SHORT).show());
    }

    public class LoginTask extends AsyncTask<String, Void, String> {
        private int customerId;

        @Override
        protected String doInBackground(String... params) {
            String email = params[0];
            String password = params[1];
            try {
                URL url = new URL("http://10.0.2.2:8080/toad/customer/getByEmail?email=" + email);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("Accept", "application/json");

                Scanner scanner = new Scanner(connection.getInputStream());
                StringBuilder response = new StringBuilder();
                while (scanner.hasNext()) {
                    response.append(scanner.nextLine());
                }
                scanner.close();
                connection.disconnect();

                JSONObject jsonResponse = new JSONObject(response.toString());
                if (jsonResponse.getString("password").equals(password)) {
                    customerId = jsonResponse.getInt("customerId");

                    // ✅ Stocker le customer_id AVANT le return
                    SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putInt("customerId", customerId);
                    editor.apply();

                    return "success";
                } else {
                    return "Mot de passe incorrect";
                }
            } catch (Exception e) {
                e.printStackTrace();
                return "Erreur de connexion";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            if (result.equals("success")) {
                Log.d("MainActivity", "Connexion réussie, lancement de l'activité suivante.");
                Toast.makeText(MainActivity.this, "Connexion réussie", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(MainActivity.this, AfficherListeDvdsActivity.class));
            } else {
                Toast.makeText(MainActivity.this, result, Toast.LENGTH_SHORT).show();
            }
        }
    }

}


