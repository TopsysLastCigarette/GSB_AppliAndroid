package fr.cned.emdsgil.suividevosfrais;


import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class AccesDistant implements AsyncResponse{

    private final String SERVERADDR = "https://gsb.joel-deligne.com/controleurs/c_accesAndroid.php";
    private Context context;

    public AccesDistant(Context context) {
        super();
        this.context = context;
    }

    @Override
    public void processFinish(String output) {
        Log.d("serveur", "*********" + output);

        String [] message = output.split("%");

        if(message.length > 1){
            if(message[0].equals("connexion")){
                if(message[1].equals("WrongLogin")){
                    //Mauvais Login-MDP
                    AlertDialogManager adm = new AlertDialogManager();
                    adm.showAlertDialog(context, "Echec", "Login/Mot de passe incorrect");
                } else if(message[1].equals("LoginOK")) {
                    //Connexion ok
                    SessionManagement session = new SessionManagement(context);
                    try{
                        JSONObject infoVisiteur = new JSONObject(message[2]);
                        String prenom = infoVisiteur.getString("prenom");
                        String nom = infoVisiteur.getString("nom");
                        String idVisiteur = infoVisiteur.getString("id");
                        //Création de la session
                        session.createLoginSession(prenom +" "+ nom, idVisiteur);
                    }catch(JSONException e){
                        e.printStackTrace();
                    }
                    //Appel de l'activité principale
                    context.startActivity(new Intent(context, MainActivity.class));
                    Toast toast = Toast.makeText(context, "Connexion Réussie !", Toast.LENGTH_LONG);
                    toast.show();
                }
            }else if(message[0].equals("synchro")) {
                String texte;
                if(message[1].equals("synchronized")){
                    texte = "Synchronisation réussie !";
                } else {
                    texte = "Synchronisation échouée !";
                    texte += message[1];
                }
                Toast toast = Toast.makeText(context, texte, Toast.LENGTH_LONG);
                toast.show();
            }else if(message[0].equals("Erreur !")){
                Log.d("Erreur !","****************"+message[1]);
            }
        }
    }

    public void envoi(String operation, JSONArray lesDonneesJSON, String idVisiteur){
        AccesHTTP accesDonnees = new AccesHTTP();
        accesDonnees.delegate = this;

        accesDonnees.addParam("operation", operation );
        accesDonnees.addParam("lesdonnees", lesDonneesJSON.toString());

        if(idVisiteur != null){
            accesDonnees.addParam("idvisiteur", idVisiteur);
        }

        accesDonnees.execute(SERVERADDR);
    }
}
