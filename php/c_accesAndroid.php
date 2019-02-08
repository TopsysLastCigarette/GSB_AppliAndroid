<?php
require_once '../includes/class.pdogsb.inc.php';
require_once '../includes/fct.inc.php';

//Récupération de l'appel
$operation = $_REQUEST['operation'];

if (isset($operation)) {
	switch($operation){
		case 'connexion':
		print "connexion%";
		try {
			//Récupération des données
				$lesDonnees = json_decode($_REQUEST['lesdonnees']);
				$login = $lesDonnees[0];
				$mdp = $lesDonnees[1];
				//Connexion à la base de données
				$pdo = PdoGsb::getPdoGsb();
				$visiteur = $pdo->getInfosVisiteur($login, $mdp);

				if (!is_array($visiteur)) {
						//TODO Erreur de connexion
						print "WrongLogin";

				} else {
						//TODO Connexion valide
						print "LoginOK%";
						print (json_encode($visiteur));
				}
		}catch(PDOException $e){
			print "Erreur !%".$e->getMessage();
			die();
		}
		break;
		case 'synchro':
		print "synchro%";
			try {
				print "synchronized%";
				$idVisiteur = $_REQUEST['idvisiteur'];
				$objetDonnees = json_decode($_REQUEST['lesdonnees']);
				$lesDonnees = objectToArray($objetDonnees);

				$pdo = PdoGsb::getPdoGsb();

				foreach($lesDonnees as $uneLigne) {
					//Construction du mois au bon format
					$annee = $uneLigne['annee'];
					$mois =  $uneLigne['mois'];
					if (strlen($mois) == 1) {
	            $mois = '0'.$mois;
	        }
					$anneeMois = $annee.$mois;

					//Construction des frais forfaitis&s au bon format
					$lesFrais = array(
						'ETP' => $uneLigne['etape'],
						'NUI' => $uneLigne['nuitee'],
						'REP' => $uneLigne['repas'],
						'KM' => $uneLigne['km']
					);

					//Création d'une ligne de frais en cas de premiere declaration
					if ($pdo->estPremierFraisMois($idVisiteur, $anneeMois)) {
						$pdo->creeNouvellesLignesFrais($idVisiteur, $anneeMois);
					}

					//MAJ des frais forfaitisés
					$pdo->majFraisForfait($idVisiteur, $anneeMois, $lesFrais);

					//MAJ des frais hors forfait
					$lesFraisHf = $uneLigne['lesFraisHf'];
					//Suppression de tous les frais hors forfait du mois dans la BDD
					$lesFraisBDD = $pdo->getLesFraisHorsForfait($idVisiteur, $anneeMois);
					if(is_array($lesFraisBDD)){
						foreach($lesFraisBDD as $unFraisBDD){
							$pdo->supprimerFraisHorsForfait($unFraisBDD['id']);
						}
					}

					if(!empty($lesFraisHf)){
						foreach($lesFraisHf as $unFraisHf){
							//Ajout des frais hors forfait
							$jour =  $unFraisHf['jour'];
							$montant =  $unFraisHf['montant'];
							$motif =  $unFraisHf['motif'];
							$date = $jour."/".$mois."/".$annee;
							$pdo->creeNouveauFraisHorsForfait($idVisiteur,$anneeMois,$motif,$date,$montant);
						}
					}
				}

			}catch(PDOException $e){
				print "Erreur !%".$e->getMessage();
				die();
			}
		break;
	}
}
