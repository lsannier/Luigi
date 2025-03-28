package com.btssio.applicationrftg;

import android.os.AsyncTask;
import java.util.function.Consumer;

public class AppelerServiceRestGETConnexion extends AsyncTask<Void, Void, Boolean> {
    private final String username;
    private final String password;
    private final Consumer<Boolean> onComplete;

    public AppelerServiceRestGETConnexion(String username, String password, Consumer<Boolean> onComplete) {
        this.username = username;
        this.password = password;
        this.onComplete = onComplete;
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        // TODO: Implémenter l'appel au service REST pour la vérification des identifiants
        // Pour l'exemple, on utilise une vérification simple
        return username.equals("Dummy") && password.equals("MDP");
    }

    @Override
    protected void onPostExecute(Boolean success) {
        super.onPostExecute(success);
        onComplete.accept(success);
    }
}
