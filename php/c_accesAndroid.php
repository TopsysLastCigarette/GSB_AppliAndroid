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

				//Si l'envoi est null (si il s'agit de la toute première connexion)
				if(empty($lesDonnees)){

					$dernierMois = $pdo->dernierMoisSaisi($idVisiteur);

					//Récupération des frais qui sont présent dans la BDD
					$lesFrais = $pdo->getLesFraisForfaitBasic($idVisiteur, $dernierMois);
					$lesFraisHorsForfait = $pdo->getLesFraisHorsForfait($idVisiteur, $dernierMois);

					if(!empty($lesFraisHorsForfait)){

						foreach($lesFraisHorsForfait as $unFrais){
							$lesFraisHfBDD[$unFrais['id']]['libelle'] =  $unFrais['libelle'];
							$lesFraisHfBDD[$unFrais['id']]['montant'] =  $unFrais['montant'];
							$date = explode('/', $unFrais['date']);
							$lesFraisHfBDD[$unFrais['id']]['jour'] = $date[0] ;
						}

						$lesFrais['lesFraisHf'] = $lesFraisHfBDD;
						$lesFraisSynchro[$dernierMois] = $lesFrais;
					}

				} else {
					foreach($lesDonnees as $uneLigne) {
						//Construction du mois au bon format
						$annee = $uneLigne['annee'];
						$mois =  $uneLigne['mois'];
						if (strlen($mois) == 1) {
		            $mois = '0'.$mois;
		        }
						$anneeMois = $annee.$mois;

						//Création d'une ligne de frais en cas de premiere declaration
						if ($pdo->estPremierFraisMois($idVisiteur, $anneeMois)) {
							$pdo->creeNouvellesLignesFrais($idVisiteur, $anneeMois);
						}

						//Récupération de la dernière date de modification de la fiche
						$ficheFrais = $pdo->getLesInfosFicheFrais($idVisiteur, $anneeMois);
						$derniereMaj = date($ficheFrais['dateModif']);

						//Récupération des frais qui sont présent dans la BDD
						$lesFraisBDD = $pdo->getLesFraisForfaitBasic($idVisiteur, $anneeMois);
						//Comparaison des dates de MAJ
						$majEtape = date($uneLigne['majEtape']);
						$majNuitee = date($uneLigne['majNuitee']);
						$majRepas = date($uneLigne['majRepas']);
						$majKm = date($uneLigne['majKm']);

						//Comparaison etape
						if($majEtape > $derniereMaj){
							$qteEtape = $uneLigne['etape'];
						} else {
							$qteEtape =  $lesFraisBDD['ETP'];
						}
						//Comparaison nuitee
						if($majNuitee > $derniereMaj){
							$qteNuitee = $uneLigne['nuitee'];
						} else {
							$qteNuitee =  $lesFraisBDD['NUI'];
						}
						//Comparaison Repas
						if($majRepas > $derniereMaj){
							$qteRepas = $uneLigne['repas'];
						} else {
							$qteRepas =  $lesFraisBDD['REP'];
						}
						//Comparaison km
						if($majKm > $derniereMaj){
							$qteKm = $uneLigne['km'];
						} else {
							$qteKm =  $lesFraisBDD['KM'];
						}

						//Construction des frais forfaitisés au bon format
						$lesFrais = array(
							'ETP' => $qteEtape,
							'NUI' => $qteNuitee,
							'REP' => $qteRepas,
							'KM' => $qteKm
						);

						//MAJ des frais forfaitisés
						$pdo->majFraisForfait($idVisiteur, $anneeMois, $lesFrais);

						//Récupération de la dernière date de modif mobile des frais HF
						$majFraisHf = date($uneLigne['majFraisHf']);
						//Récupération des frais HF de la BDD
						$lesFraisBDD = $pdo->getLesFraisHorsForfait($idVisiteur, $anneeMois);

						//Si les frais HF sont plus récent
						if($majFraisHf > $derniereMaj){
							//MAJ des frais hors forfait
							$lesFraisHf = $uneLigne['lesFraisHf'];
							//Suppression de tous les frais hors forfait du mois dans la BDD
							if(is_array($lesFraisBDD)){
								foreach($lesFraisBDD as $unFraisBDD){
									$pdo->supprimerFraisHorsForfait($unFraisBDD['id']);
								}
							}
							//Envoi de tous les frais HF du mobile en BDD
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
						} else {
							//Si la date de modif en BDD est plus récente
							foreach($lesFraisBDD as $unFrais){
								$lesFraisHfBDD[$unFrais['id']]['libelle'] =  $unFrais['libelle'];
								$lesFraisHfBDD[$unFrais['id']]['montant'] =  $unFrais['montant'];
								$date = explode('/', $unFrais['date']);
								$lesFraisHfBDD[$unFrais['id']]['jour'] = $date[0] ;
							}
							$lesFrais['lesFraisHf'] = $lesFraisHfBDD;
						}
						//Récupère un tableau de tous les frais des mois concernés
						$lesFraisSynchro[$anneeMois] = $lesFrais;
					}
				}


				//Envoi du nouveau tableau de frais côté mobile
				print json_encode($lesFraisSynchro);

			}catch(PDOException $e){
				print "Erreur !%".$e->getMessage();
				die();
			}
		break;
	}
}
