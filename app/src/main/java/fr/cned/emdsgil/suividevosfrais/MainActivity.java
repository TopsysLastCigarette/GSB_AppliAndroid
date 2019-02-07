package fr.cned.emdsgil.suividevosfrais;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    SessionManagement session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("GSB : Suivi des frais");
        //Récupération de la session
        session = new SessionManagement(this);

        //Récupère le layout de session et l'inflater
        ViewGroup sessionLayout = (ViewGroup)findViewById(R.id.layoutSession);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(this.LAYOUT_INFLATER_SERVICE);

        if(!session.isLoggedIn()){
            //Si pas connecté
            View layout = inflater.inflate(R.layout.layout_session_disconnected, sessionLayout);
            cmdSessionCo_clic();
        } else {
            // Get infos session
            HashMap<String, String> user = session.getUserDetails();
            String nom = user.get(SessionManagement.KEY_NAME);
            View layout = inflater.inflate(R.layout.layout_session_connected, sessionLayout);
            TextView nomVisiteur = (TextView) findViewById(R.id.txtNomVisiteur);
            nomVisiteur.setText(nom);
            cmdSessionDeco_clic();
        }

        // récupération des informations sérialisées
        recupSerialize();
        // chargement des méthodes événementielles
        cmdMenu_clic(((ImageButton) findViewById(R.id.cmdKm)), KmActivity.class);
        cmdMenu_clic(((ImageButton) findViewById(R.id.cmdNuitee)), NuiteeActivity.class);
        cmdMenu_clic(((ImageButton) findViewById(R.id.cmdEtape)), EtapeActivity.class);
        cmdMenu_clic(((ImageButton) findViewById(R.id.cmdRepas)), RepasActivity.class);
        cmdMenu_clic(((ImageButton) findViewById(R.id.cmdHf)), HfActivity.class);
        cmdMenu_clic(((ImageButton) findViewById(R.id.cmdHfRecap)), HfRecapActivity.class);
        cmdTransfert_clic();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    /**
     * Récupère la sérialisation si elle existe
     */
    private void recupSerialize() {
        /* Pour éviter le warning "Unchecked cast from Object to Hash" produit par un casting direct :
         * Global.listFraisMois = (Hashtable<Integer, FraisMois>) Serializer.deSerialize(Global.filename, MainActivity.this);
         * On créé un Hashtable générique <?,?> dans lequel on récupère l'Object retourné par la méthode deSerialize, puis
         * on cast chaque valeur dans le type attendu.
         * Seulement ensuite on affecte cet Hastable à Global.listFraisMois.
        */
        Hashtable<?, ?> monHash = (Hashtable<?, ?>) Serializer.deSerialize(MainActivity.this);
        if (monHash != null) {
            Hashtable<Integer, FraisMois> monHashCast = new Hashtable<>();
            for (Hashtable.Entry<?, ?> entry : monHash.entrySet()) {
                monHashCast.put((Integer) entry.getKey(), (FraisMois) entry.getValue());
            }
            Global.listFraisMois = monHashCast;
        }
        // si rien n'a été récupéré, il faut créer la liste
        if (Global.listFraisMois == null) {
            Global.listFraisMois = new Hashtable<>();
            /* Retrait du type de l'HashTable (Optimisation Android Studio)
			 * Original : Typage explicit =
			 * Global.listFraisMois = new Hashtable<Integer, FraisMois>();
			*/

        }
    }

    /**
     * Sur la sélection d'un bouton dans l'activité principale ouverture de l'activité correspondante
     */
    private void cmdMenu_clic(ImageButton button, final Class classe) {
        button.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                // ouvre l'activité
                Intent intent = new Intent(MainActivity.this, classe);
                startActivity(intent);
            }
        });
    }

    /**
     * Cas particulier du bouton pour le transfert d'informations vers le serveur
     */
    private void cmdTransfert_clic() {
        findViewById(R.id.cmdTransfert).setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                //Si la session n'existe pas
                if(!session.isLoggedIn()){
                    startActivity(new Intent(MainActivity.this, ConnexionActivity.class));
                } else {
                    HashMap<String, String> user = session.getUserDetails();
                    String id = user.get(SessionManagement.KEY_ID);

                    //Conversion en String au format JSONObject
                    String json = new Gson().toJson(Global.listFraisMois);

                    //Conversion en JSONObject
                    try{
                        JSONObject jsonObj = new JSONObject(json);
                        //Conversion en JSONArray
                        Iterator x = jsonObj.keys();
                        JSONArray jsonArray = new JSONArray();
                        while (x.hasNext()){
                            String key = (String) x.next();
                            jsonArray.put(jsonObj.get(key));
                        }
                        //Envoi au serveur
                        AccesDistant accesDistant = new AccesDistant(MainActivity.this);
                        accesDistant.envoi("synchro", jsonArray, id);
                    }catch(JSONException e){
                        e.getMessage();
                    }



                }
            }
        });
    }

    /**
     * Clic sur le bouton de déconnexion du layout de session
     */
    private void cmdSessionDeco_clic() {
        findViewById(R.id.cmdSessionDeco).setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                //Se déconnecte de la session
                session.logoutUser();
                //Rafraichis l'activity
                Intent intent = getIntent();
                finish();
                startActivity(intent);
            }
        });
    }

    /**
     * Clic sur le bouton de connexion du layout de session
     */
    private void cmdSessionCo_clic() {
        findViewById(R.id.cmdSessionCo).setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ConnexionActivity.class));
            }
        });
    }
}
