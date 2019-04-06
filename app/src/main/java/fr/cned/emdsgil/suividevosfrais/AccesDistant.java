package fr.cned.emdsgil.suividevosfrais;


import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Iterator;

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

                    try {
                        //Récupération du tableau et conversion en JSONObject
                        JSONObject lesDonnees = new JSONObject(message[2]);
                        Iterator x = lesDonnees.keys();
                        //Boucle sur les quantite et ajoute dans le fichier global
                        while (x.hasNext()){
                            String key = (String) x.next();
                            JSONObject lesFrais = (JSONObject)lesDonnees.get(key);

                            //Si la clé n'existe pas
                            if (!Global.listFraisMois.containsKey(Integer.parseInt(key))) {
                                // creation du mois et de l'annee s'ils n'existent pas déjà
                                Integer annee = Integer.parseInt(key.substring(0,4)) ;
                                Integer mois = Integer.parseInt(key.substring(4,6));
                                Global.listFraisMois.put(Integer.parseInt(key), new FraisMois(annee, mois)) ;
                            }
                            Global.listFraisMois.get(Integer.parseInt(key)).setEtape(lesFrais.getInt("ETP"));
                            Global.listFraisMois.get(Integer.parseInt(key)).setNuitee(lesFrais.getInt("NUI"));
                            Global.listFraisMois.get(Integer.parseInt(key)).setRepas(lesFrais.getInt("REP"));
                            Global.listFraisMois.get(Integer.parseInt(key)).setKm(lesFrais.getInt("KM"));

                            //Si les frais HF de la BDD sont plus récents
                            if(lesFrais.has("lesFraisHf")){

                                //Suppression de tous les frais HF existants
                                Global.listFraisMois.get(Integer.parseInt(key)).supprAllFraisHf();

                                //Ajout des nouveaux frais si le tableau de frais HF n'est pas vide
                                if(!lesFrais.isNull("lesFraisHf")){
                                    JSONObject lesFraisHF = (JSONObject)lesFrais.get("lesFraisHf");
                                    Iterator i = lesFraisHF.keys();
                                    while (i.hasNext()) {
                                        String keyHF = (String) i.next();
                                        JSONObject leFraisHF = (JSONObject)lesFraisHF.get(keyHF);
                                        Float montant = Float.parseFloat((String)leFraisHF.get("montant"));
                                        String libelle = leFraisHF.getString("libelle");
                                        Integer jour= leFraisHF.getInt("jour");
                                        Global.listFraisMois.get(Integer.parseInt(key)).addFraisHf(montant, libelle, jour);
                                    }
                                }
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    Serializer.serialize(Global.listFraisMois, this.context);
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
