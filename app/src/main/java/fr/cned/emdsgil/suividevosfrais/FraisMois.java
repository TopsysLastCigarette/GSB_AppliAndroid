package fr.cned.emdsgil.suividevosfrais;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Classe métier contenant les informations des frais d'un mois
 */
class FraisMois implements Serializable {

    private Integer mois; // mois concerné
    private Integer annee; // année concernée
    private Integer etape; // nombre d'étapes du mois
    private Integer km; // nombre de km du mois
    private Integer nuitee; // nombre de nuitées du mois
    private Integer repas; // nombre de repas du mois

    private String majEtape; //dernière saisie du nb d'étapes
    private String majKm; //dernière saisie du nb de km
    private String majNuitee; //dernière saisie du nb de nuitee
    private String majRepas; //dernière saisie du nb de repas
    private String majFraisHf; //Dernière entrée dans les frais Hors forfait

    private final ArrayList<FraisHf> lesFraisHf; // liste des frais hors forfait du mois

    public FraisMois(Integer annee, Integer mois) {
        this.annee = annee;
        this.mois = mois;
        this.etape = 0;
        this.km = 0;
        this.nuitee = 0;
        this.repas = 0;

        /*
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        this.majEtape = df.format(new Date());
        this.majKm = df.format(new Date());
        this.majNuitee = df.format(new Date());
        this.majRepas = df.format(new Date());
        this.majFraisHf = df.format(new Date());
        */

        lesFraisHf = new ArrayList<>();
        /* Retrait du type de l'ArrayList (Optimisation Android Studio)
		 * Original : Typage explicit =
		 * lesFraisHf = new ArrayList<FraisHf>() ;
		*/
    }

    /**
     * Ajout d'un frais hors forfait
     *
     * @param montant Montant en euros du frais hors forfait
     * @param motif Justification du frais hors forfait
     */
    public void addFraisHf(Float montant, String motif, Integer jour) {
        lesFraisHf.add(new FraisHf(montant, motif, jour));
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        this.majFraisHf = df.format(new Date());
    }

    /**
     * Suppression d'un frais hors forfait
     *
     * @param index Indice du frais hors forfait à supprimer
     */
    public void supprFraisHf(Integer index) {
        lesFraisHf.remove(index);
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        this.majFraisHf = df.format(new Date());
    }

    /**
     * Suppression de tous les frais hors forfait
     */
    public void supprAllFraisHf() {
        lesFraisHf.clear();
    }

    public Integer getMois() {
        return mois;
    }

    public void setMois(Integer mois) {
        this.mois = mois;
    }

    public Integer getAnnee() {
        return annee;
    }

    public void setAnnee(Integer annee) {
        this.annee = annee;
    }

    public Integer getEtape() {
        return etape;
    }

    public void setEtape(Integer etape) {
        this.etape = etape;
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        this.majEtape = df.format(new Date());
    }

    public Integer getKm() {
        return km;
    }

    public void setKm(Integer km) {
        this.km = km;
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        this.majKm = df.format(new Date());
    }

    public Integer getNuitee() {
        return nuitee;
    }

    public void setNuitee(Integer nuitee) {
        this.nuitee = nuitee;
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        this.majNuitee = df.format(new Date());
    }

    public Integer getRepas() {
        return repas;
    }

    public void setRepas(Integer repas) {
        this.repas = repas;
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        this.majRepas = df.format(new Date());
    }

    public ArrayList<FraisHf> getLesFraisHf() {
        return lesFraisHf;
    }

}
