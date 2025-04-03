package com.example.appli20240829;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class RegisterActivity extends AppCompatActivity {

    private EditText firstNameInput, lastNameInput, emailInput, passwordInput, ageInput;
    private Button registerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        firstNameInput = findViewById(R.id.editTextFirstName);
        lastNameInput = findViewById(R.id.editTextLastName);
        emailInput = findViewById(R.id.editTextEmail);
        passwordInput = findViewById(R.id.editTextPassword);
        ageInput = findViewById(R.id.editTextAge);
        registerButton = findViewById(R.id.buttonRegister);

        registerButton.setOnClickListener(view -> {
            String firstName = firstNameInput.getText().toString();
            String lastName = lastNameInput.getText().toString();
            String email = emailInput.getText().toString();
            String password = passwordInput.getText().toString();
            String age = ageInput.getText().toString();

            if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || password.isEmpty() || age.isEmpty()) {
                Toast.makeText(RegisterActivity.this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
            } else {
                new RegisterTask().execute(firstName, lastName, email, password, age);
            }
        });
    }

    private class RegisterTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String firstName = params[0];
            String lastName = params[1];
            String email = params[2];
            String password = params[3];
            String age = params[4];

            try {
                URL url = new URL("http://10.0.2.2:8080/toad/customer/add");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("Accept", "application/json");
                connection.setDoOutput(true);

                // Création du corps de la requête JSON
                JSONObject jsonParam = new JSONObject();
                jsonParam.put("firstName", firstName); // Correction ici
                jsonParam.put("lastName", lastName);
                jsonParam.put("email", email);
                jsonParam.put("password", password);
                jsonParam.put("age", Integer.parseInt(age)); // Assurer que c'est un int


                OutputStream os = connection.getOutputStream();
                os.write(jsonParam.toString().getBytes());
                os.flush();
                os.close();

                connection.getInputStream(); // Effectue la requête

                connection.disconnect();
                return "Utilisateur ajouté avec succès";
            } catch (Exception e) {
                e.printStackTrace();
                return "Erreur d'ajout d'utilisateur";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(RegisterActivity.this, result, Toast.LENGTH_SHORT).show();
            if (result.equals("Utilisateur ajouté avec succès")) {
                finish(); // Fermer l'activité après succès
            }
        }
    }
}
