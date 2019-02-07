<?php
/**
 * Fonctions pour l'application GSB
 *
 * PHP Version 7
 *
 * @category  PPE
 * @package   GSB
 * @author    Cheri Bibi - Réseau CERTA <contact@reseaucerta.org>
 * @author    José GIL <jgil@ac-nice.fr>
 * @copyright 2017 Réseau CERTA
 * @license   Réseau CERTA
 * @version   GIT: <0>
 * @link      http://www.php.net/manual/fr/book.pdo.php PHP Data Objects sur php.net
 */

/**
 * Teste si un quelconque visiteur est connecté
 *
 * @return Boolean vrai ou faux
 */
function estConnecte()
{
    return isset($_SESSION['idVisiteur']);
}

/**
 * Enregistre dans une variable session les infos d'un visiteur
 *
 * @param String $idVisiteur ID du visiteur
 * @param String $nom        Nom du visiteur
 * @param String $prenom     Prénom du visiteur
 * @param int    $type       Type d'utilisateur
 *
 * @return null
 */
function connecter($idVisiteur, $nom, $prenom, $type)
{
    $_SESSION['idVisiteur'] = $idVisiteur;
    $_SESSION['nom'] = $nom;
    $_SESSION['prenom'] = $prenom;
    $_SESSION['type'] = $type;
}

/**
 * Détruit la session active
 *
 * @return null
 */
function deconnecter()
{
    session_destroy();
}

/**
 * Transforme une date au format français jj/mm/aaaa vers le format anglais
 * aaaa-mm-jj
 *
 * @param String $maDate au format  jj/mm/aaaa
 *
 * @return Date au format anglais aaaa-mm-jj
 */
function dateFrancaisVersAnglais($maDate)
{
    @list($jour, $mois, $annee) = explode('/', $maDate);
    return date('Y-m-d', mktime(0, 0, 0, $mois, $jour, $annee));
}

/**
 * Transforme une date au format format anglais aaaa-mm-jj vers le format
 * français jj/mm/aaaa
 *
 * @param String $maDate au format  aaaa-mm-jj
 *
 * @return Date au format format français jj/mm/aaaa
 */
function dateAnglaisVersFrancais($maDate)
{
    @list($annee, $mois, $jour) = explode('-', $maDate);
    $date = $jour . '/' . $mois . '/' . $annee;
    return $date;
}

/**
 * Retourne le mois au format aaaamm selon le jour dans le mois
 *
 * @param String $date au format  jj/mm/aaaa
 *
 * @return String Mois au format aaaamm
 */
function getMois($date)
{
    @list($jour, $mois, $annee) = explode('/', $date);
    unset($jour);
    if (strlen($mois) == 1) {
        $mois = '0' . $mois;
    }
    return $annee . $mois;
}

/* gestion des erreurs */

/**
 * Indique si une valeur est un entier positif ou nul
 *
 * @param Integer $valeur Valeur
 *
 * @return Boolean vrai ou faux
 */
function estEntierPositif($valeur)
{
    return preg_match('/[^0-9]/', $valeur) == 0;
}

/**
 * Indique si un tableau de valeurs est constitué d'entiers positifs ou nuls
 *
 * @param Array $tabEntiers Un tableau d'entier
 *
 * @return Boolean vrai ou faux
 */
function estTableauEntiers($tabEntiers)
{
    $boolReturn = true;
    foreach ($tabEntiers as $unEntier) {
        if (!estEntierPositif($unEntier)) {
            $boolReturn = false;
        }
    }
    return $boolReturn;
}

/**
 * Vérifie si une date est inférieure d'un an à la date actuelle
 *
 * @param String $dateTestee Date à tester
 *
 * @return Boolean vrai ou faux
 */
function estDateDepassee($dateTestee)
{
    $dateActuelle = date('d/m/Y');
    @list($jour, $mois, $annee) = explode('/', $dateActuelle);
    $annee--;
    $anPasse = $annee . $mois . $jour;
    @list($jourTeste, $moisTeste, $anneeTeste) = explode('/', $dateTestee);
    return ($anneeTeste . $moisTeste . $jourTeste < $anPasse);
}

/**
 * Vérifie la validité du format d'une date française jj/mm/aaaa
 *
 * @param String $date Date à tester
 *
 * @return Boolean vrai ou faux
 */
function estDateValide($date)
{
    $tabDate = explode('/', $date);
    $dateOK = true;
    if (count($tabDate) != 3) {
        $dateOK = false;
    } else {
        if (!estTableauEntiers($tabDate)) {
            $dateOK = false;
        } else {
            if (!checkdate($tabDate[1], $tabDate[0], $tabDate[2])) {
                $dateOK = false;
            }
        }
    }
    return $dateOK;
}

/**
 * Vérifie que le tableau de frais ne contient que des valeurs numériques
 *
 * @param Array $lesFrais Tableau d'entier
 *
 * @return Boolean vrai ou faux
 */
function lesQteFraisValides($lesFrais)
{
    return estTableauEntiers($lesFrais);
}

/**
 * Vérifie la validité des trois arguments : la date, le libellé du frais
 * et le montant
 *
 * Des message d'erreurs sont ajoutés au tableau des erreurs
 *
 * @param String $dateFrais Date des frais
 * @param String $libelle   Libellé des frais
 * @param Float  $montant   Montant des frais
 *
 * @return null
 */
function valideInfosFrais($dateFrais, $libelle, $montant)
{
    if ($dateFrais == '') {
        ajouterErreur('Le champ date ne doit pas être vide');
    } else {
        if (!estDatevalide($dateFrais)) {
            ajouterErreur('Date invalide');
        } else {
            if (estDateDepassee($dateFrais)) {
                ajouterErreur(
                    "date d'enregistrement du frais dépassé, plus de 1 an"
                );
            }
        }
    }
    if ($libelle == '') {
        ajouterErreur('Le champ description ne peut pas être vide');
    }
    if ($montant == '') {
        ajouterErreur('Le champ montant ne peut pas être vide');
    } elseif (!is_numeric($montant)) {
        ajouterErreur('Le champ montant doit être numérique');
    }
}

/**
 * Ajoute le libellé d'une erreur au tableau des erreurs
 *
 * @param String $msg Libellé de l'erreur
 *
 * @return null
 */
function ajouterErreur($msg)
{
    if (!isset($_REQUEST['erreurs'])) {
        $_REQUEST['erreurs'] = array();
    }
    $_REQUEST['erreurs'][] = $msg;
}

/**
 * Retoune le nombre de lignes du tableau des erreurs
 *
 * @return Integer le nombre d'erreurs
 */
function nbErreurs()
{
    if (!isset($_REQUEST['erreurs'])) {
        return 0;
    } else {
        return count($_REQUEST['erreurs']);
    }
}

/**
 * Ajoute le libellé d'une mise à jour au tableau des mises à jour
 *
 * @param String $msg Libellé de mise à jour
 *
 * @return null
 */
function ajouterMajFrais($msg)
{
    if (!isset($_REQUEST['infoMaj'])) {
        $_REQUEST['infoMaj'] = array();
    }
    $_REQUEST['infoMaj'][] = $msg;
}

/**
 * Retourne le nombre de lignes du tableau de mises à jour
 *
 * @return Integer le nombre de mises à jour
 */
function nbMajs()
{
    if (!isset($_REQUEST['infoMaj'])) {
        return 0;
    } else {
        return count($_REQUEST['infMaj']);
    }
}

/**
 * Retourne le mois suivant celui passé en paramètre au format aaaamm
 *
 * @param String $unMois le mois au format aaaamm
 *
 * @return String $leMoisSuivant au format aaaamm
 */
function moisSuivant ($unMois)
{
    $annee = substr($unMois, 0, 4);
    $mois = substr($unMois, -2);

    if ($mois == 12) {
        $mois = '01';
        $annee ++;
    } else {
        $mois ++;
        if (strlen($mois) == 1) {
            $mois = '0'.$mois;
        }
    }

    return $leMoisSuivant = $annee.$mois;
}

/**
 * Retourne vrai si le frais hors forfait passé en paramètre est un frais qui a été refusé par un comptable
 *
 * @param Array $unFraisHorsForfait un tableau associatif correspondant à un frais hors forfait
 *
 * @return Bool vrai si le libelle du frais commence par 'REFUSE'
 */
function estRefuse($unFraisHorsForfait)
{
    return substr($unFraisHorsForfait['libelle'], 0, 6) === 'REFUSE';
}

/**
 * Retourne la date en lettres, par exemple 02-08-1998 = 2 aout 1998
 *
 * @param String $laDate la Date au format jj/mm/aaaa
 *
 * @return String $laDateEnLettres la Date en Lettres
 */
function laDateEnLettres($laDate)
{
    $laDateCoupee = explode("/", $laDate);
    $Jour = $laDateCoupee[0];
    $Mois = $laDateCoupee[1];
    $Annee = $laDateCoupee[2];
    if (substr($Jour, 0, 1) === '0') {
        $Jour = substr($Jour, 1, 1);
    }
    $Mois = moisEnLettres($Mois);
    return $laDateEnLettres = $Jour . ' ' . $Mois . ' ' . $Annee;
}

/**
 * Retourne un mois en lettres. ex: 02 > Février
 *
 * @param String $numMois le mois en chiffre
 *
 * @return String $leMois le mois en lettres
 */
function moisEnLettres($numMois)
{
    switch($numMois){
    case '01':
        $leMois = "Janvier";
        break;
    case '02':
        $leMois = "Février";
        break;
    case '03':
        $leMois = "Mars";
        break;
    case '04':
        $leMois = "Avril";
        break;
    case '05':
        $leMois = "Mai";
        break;
    case '06':
        $leMois = "Juin";
        break;
    case '07':
        $leMois = "Juillet";
        break;
    case '08':
        $leMois = "Aout";
        break;
    case '09':
        $leMois = "Septembre";
        break;
    case '10':
        $leMois = "Octobre";
        break;
    case '11':
        $leMois = "Novembre";
        break;
    case '12':
        $leMois = "Décembre";
        break;
    }
    return $leMois;
}