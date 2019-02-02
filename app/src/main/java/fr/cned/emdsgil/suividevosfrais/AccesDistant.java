package fr.cned.emdsgil.suividevosfrais;


import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;

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
                int duration = Toast.LENGTH_LONG;
                if(message[1].equals("WrongLogin")){
                    CharSequence text = "Login/Mdp erroné";
                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                } else if(message[1].equals("LoginOK")) {
                    CharSequence text = "Connexion réussie";
                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                }
            }else if(message[0].equals("synchro")) {
                //TODO Synchro
            }else if(message[0].equals("Erreur !")){
            Log.d("Erreur !","****************"+message[1]);
            }
        }
    }

    public void envoi(String operation, JSONArray lesDonneesJSON){
        AccesHTTP accesDonnees = new AccesHTTP();
        accesDonnees.delegate = this;

        accesDonnees.addParam("operation", operation );
        accesDonnees.addParam("lesdonnees", lesDonneesJSON.toString());

        accesDonnees.execute(SERVERADDR);
    }
}
