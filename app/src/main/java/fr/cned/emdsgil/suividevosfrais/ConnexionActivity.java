package fr.cned.emdsgil.suividevosfrais;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONArray;

import java.util.Arrays;

public class ConnexionActivity extends AppCompatActivity {

    private EditText login;
    private EditText mdp;
    private Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connexion);

        cmdConnexion_clic();
    }

    /**
     * Evenement sur le clic de connexion à la base de données distante
     */
    private void cmdConnexion_clic(){
        findViewById(R.id.cmdConnexion).setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                //Récupération des informations de la vue
                login = findViewById(R.id.txtLogin);
                mdp = findViewById(R.id.txtMdp);

                String [] lesDonnees = {login.getText().toString(), mdp.getText().toString()};
                JSONArray lesDonneesJSON = new JSONArray(Arrays.asList(lesDonnees));

                //Accès à la base distance
                AccesDistant accesDistant = new AccesDistant(context);
                accesDistant.envoi("connexion", lesDonneesJSON);

                retourActivityPrincipale() ;
            }
        }) ;
    }

    /**
     * Retour à l'activité principale (le menu)
     */
    private void retourActivityPrincipale() {
        Intent intent = new Intent(ConnexionActivity.this, MainActivity.class) ;
        startActivity(intent) ;
    }
}
