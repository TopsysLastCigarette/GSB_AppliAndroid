<?php
include dirname(__DIR__).'/includes/class.pdogsb.inc.php';

$param = $_REQUEST['operation'];

if (isset($param)) {
	switch ($param) {
	case 'connexion':
      try {
        //Récupération des données
          $lesDonnees = json_decode($_REQUEST['lesdonnees']);
		  		$login = $lesDonnees[0];
		  		$mdp = $lesDonnees[1];

          //Insertion dans la BDD
		  		print ("Connexion à la BDD");
					$visiteur = $pdo->getInfosVisiteur($login, $mdp);
					if (!is_array($visiteur)) {
							//TODO Erreur de connexion
					} else {
							//TODO Connexion valide
					}

      }catch(PDOException $e){
        print "Erreur !%".$e->getMessage();
        die();
      }
    break;

    case 'maj':
	  try {
		print("MAJ");

		$lesDonnees = json_decode($_REQUEST['lesdonnees']);
		$donnee = array();
		$idVisiteur = $lesDonnees[0];
		$mois = $lesDonnees[1];
		$fraisForfait = $lesDonnees[2];
		$fraisHorsForfait = $lesDonnees[3];

		//Création d'une ligne de frais en cas de premiere declaration
		if ($pdo->estPremierFraisMois($idVisiteur, $mois)) {
			$pdo->creeNouvellesLignesFrais($idVisiteur, $mois);
		}





	  } catch(PDOException $e) {
		print "Erreur !%".$e->getMessage();
		die();
	  }
	  break;
	}
}

?>
