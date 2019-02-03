package fr.cned.emdsgil.suividevosfrais;

import java.util.HashMap;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SessionManagement {
    // Shared Preferences
    SharedPreferences pref;

    // Editor for Shared preferences
    Editor editor;

    // Context
    Context _context;

    // Shared pref mode
    int PRIVATE_MODE = 0;

    // nom du fichier de préférences
    private static final String PREF_NAME = "sessionAndroidGSB";

    // Toutes les clés
    private static final String IS_LOGIN = "estConnecté";

    // nom du visiteur
    public static final String KEY_NAME = "nomVisiteur";

    // id du visiteur
    public static final String KEY_ID = "idVisiteur";

    // Constructeur
    public SessionManagement(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    /**
     * Creation de la session
     * */
    public void createLoginSession(String name, String idVisiteur){
        // Storing login value as TRUE
        editor.putBoolean(IS_LOGIN, true);

        // Storing name in pref
        editor.putString(KEY_NAME, name);

        // Storing email in pref
        editor.putString(KEY_ID, idVisiteur);

        // commit changes
        editor.commit();
    }

    /**
     * Sauvegardé les données de session
     */
    public HashMap<String, String> getUserDetails(){
        HashMap<String, String> user = new HashMap<String, String>();
        // nom visiteur
        user.put(KEY_NAME, pref.getString(KEY_NAME, null));

        // id visiteur
        user.put(KEY_ID, pref.getString(KEY_ID, null));

        // return user
        return user;
    }

    /**
     * Check login method wil check user login status
     * If false it will redirect user to login page
     * Else won't do anything
     * */
    public void checkLogin(){
        // Check login status
        if(!this.isLoggedIn()){
            // le visiteur n'est pas connecté, redirection vers l'activité de connexion
            Intent i = new Intent(_context, ConnexionActivity.class);
            // Ferme toutes les activités
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            // Ajout d'un nouveau flag pour démarrer une activité
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            // Démarre l'activité
            _context.startActivity(i);
        }
    }

    /**
     * Termine la session
     * */
    public void logoutUser(){
        // Efface toutes les données du SharedPreferences
        editor.clear();
        editor.commit();

    }

    /**
     * Vérification si connecté
     * **/
    // Get Login State
    public boolean isLoggedIn(){
        return pref.getBoolean(IS_LOGIN, false);
    }
}