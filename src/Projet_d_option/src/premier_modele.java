package Projet_d_option.src;
import java.io.FileWriter;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solution;
import org.chocosolver.solver.constraints.Constraint;
import org.chocosolver.solver.variables.BoolVar;
import org.chocosolver.solver.variables.IntVar;

import org.javatuples.*;

public class premier_modele {
	
	public static List<Quartet<String, Integer, Integer, Integer>> indicateurs(Strategie strategie, IntVar[] stock, IntVar[] commande) {
		List<Quartet<String, Integer, Integer, Integer>> indic = new ArrayList<Quartet<String, Integer, Integer, Integer>>();
		
		indic.add(new Quartet<>("Stock Maximum",0,0,0));
		indic.add(new Quartet<>("Nombre de Commande pendant la transition", 0, 0, 0));
		indic.add(new Quartet<>("Duree de la periode", 0, 0, 0));
		indic.add(new Quartet<>("Somme des Rapports difference(Max-secu) / Duree de la periode", 0, 0, 0));
		indic.add(new Quartet<>("Frequence des commandes pour nouveau regime", 0, 0, 0));
		
		int dateFinTransition = 12+(strategie.getNbEtapes()*strategie.getTempsEtapes());
		int dureeTotale = strategie.getDuree();
		int stockSecu = strategie.getStructure().getStockSecurite();
		
		// Calcul indicateur Stock Maximal
		int stockMaxAvant = stock[0].getValue();
		for (int i = 1; i<12; i++) {
			if (stock[i].getValue() > stockMaxAvant) {
				stockMaxAvant = stock[i].getValue();
			}
		}
		
		int stockMaxPendant = stock[12].getValue();
		for (int i = 12; i<dateFinTransition + 1; i++) {
			if (stock[i].getValue() > stockMaxPendant) {
				stockMaxPendant = stock[i].getValue();
			}
		}
		
		int stockMaxApres = stock[dateFinTransition + 1].getValue();
		for (int i = dateFinTransition+1; i<dureeTotale; i++) {
			if (stock[i].getValue() > stockMaxApres) {
				stockMaxApres = stock[i].getValue();
			}
		}		
				
		// Calcul indicateur Nombre de commande
		int nbCommandeAvant = 0;
		for (int i = 1; i<12; i++) {
			if (commande[i].getValue() > 0) {
				nbCommandeAvant += 1;
			}
		}
		
		int nbCommandePendant = 0;
		for (int i = 12; i<dateFinTransition+1; i++) {
			if (commande[i].getValue() > 0) {
				nbCommandePendant += 1;
			}
		}
		
		int nbCommandeApres = 0;
		for (int i = dateFinTransition+1; i<dureeTotale; i++) {
			if (commande[i].getValue() > 0) {
				nbCommandeApres += 1;
			}
		}
		
		
		// Calcul indicateur duree de la periode de transition
		int dureeTransition = strategie.getNbEtapes()*strategie.getTempsEtapes();
		
		// Calcul indicateur Differences des stocks et stock Secu
			// AVANT
		int dureeAvant = 12;
		int sommeDesDifferencesAvant = 0;
		
		for(int i=0; i<strategie.getStructure().getDelai(); i++) {
			sommeDesDifferencesAvant += stock[i].getValue()-stockSecu;
		}
		
	    for (int i = strategie.getStructure().getDelai(); i<12; i++) {
	    	sommeDesDifferencesAvant += stock[i].getValue() + commande[i-strategie.getStructure().getDelai()].getValue() - stockSecu;
	   	}
	    
	    	// PENDANT
	    int sommeDesDifferencesPendant = 0;
		for(int i=12; i<dateFinTransition + 1; i++) {
			sommeDesDifferencesPendant += stock[i].getValue() + commande[i-strategie.getStructure().getDelai()].getValue() - stockSecu;
		}
		
			// APRES
		int dureeApres = dureeTotale - dureeTransition - dureeAvant; 
		int sommeDesDifferencesApres = 0;
	    for (int i = dateFinTransition + 1; i<dureeTotale; i++) {
	    	sommeDesDifferencesApres += stock[i].getValue() + commande[i-strategie.getStructure().getDelai()].getValue() - stockSecu;
	   	}
	    	
		indic.set(0, new Quartet<>("Stock Maximum", stockMaxAvant, stockMaxPendant, stockMaxApres));
		indic.set(1, new Quartet<>("Nombre de Commande pendant la periode", nbCommandeAvant, nbCommandePendant, nbCommandeApres));
		indic.set(2, new Quartet<>("Duree de la periode", dureeAvant, dureeTransition, dureeApres));
		indic.set(3, new Quartet<>("Somme des Rapports difference(Max-secu) / Duree de la periode", (int)((double)(sommeDesDifferencesAvant)/(double)(dureeAvant)), (int)((double)(sommeDesDifferencesPendant)/(double)(dureeTransition)), (int)((double)(sommeDesDifferencesApres)/(double)(dureeApres))));
		indic.set(4, new Quartet<>("Somme des Rapports difference(Max-secu) / Duree de la periode", (int)((double)(nbCommandeAvant)/(double)(dureeAvant)), (int)((double)(nbCommandePendant)/(double)(dureeTransition)), (int)((double)(nbCommandeApres)/(double)(dureeApres))));		
		
		return indic;
	}
	
	
	

	public static void creationCsvIndicateurs(String nomFichier, 
												List<Strategie> strategies,
												int delai, 
												IntVar[] stock, 
												int duree, 
												IntVar[] commande) throws IOException {
		
		File ff = new File("C:\\Users\\Lucas\\Documents\\Mines\\A3\\Projet d'Option\\ExportEclipse\\" + nomFichier + ".csv");
		PrintWriter out;
		out = new PrintWriter(new FileWriter(ff));
		out.write("Strategies;");
		List <Quartet<String, Integer, Integer, Integer>> indicateurs = indicateurs(strategies.get(0), stock, commande);
		for (Quartet i : indicateurs) {
			out.write("" + i.getValue(0)+ " avant la transition;"+ i.getValue(0) + " pendant la transition;" + i.getValue(0) + " apres la transition;");
		}
		out.println();
		for (Strategie s : strategies) {
			indicateurs = indicateurs(s, stock, commande);
			out.write(s.getNomStrategie());
			
			for (Quartet i : indicateurs) {
				for (int index = 1; index < i.getSize(); index ++) {
					out.write("" + i.getValue(index) + ";");
				}
			}
			out.println();
		}
			
			
			out.println();
			out.close();
	}

		
		
		

	public static void resoudStock(Strategie strategie1) throws IOException  {
		Model model = new Model("Gestion des stocks");
		int duree=strategie1.getStructure().getDuree();
		int delai=strategie1.getStructure().getDelai();
		int lotCommande=strategie1.getStructure().getLotCommande();
		int stockSecurite=strategie1.getStructure().getStockSecurite();
		int stockInitial=strategie1.getStructure().getStockInitial();
		int nbGroupes=strategie1.getNbGroupes();
		
		
		// Ces paramètres strategique determine la demande en medicaments des patients sur les 24 mois
		int[][] demande = strategie1.getDemande();
		
		// On somme ces demandes pour avoir la demande mensuelle;
		int[] demandeMensuelle=new int[duree];
		
		int demandeMoisI = 0;
		for (int i=0; i<strategie1.duree; i++) {
			demandeMoisI = 0;
			for (int j=0; j<strategie1.getNbGroupes(); j++) {
				demandeMoisI += demande[j][i];
			}
			demandeMensuelle[i]=demandeMoisI;
		}
		
		//Variables 
		IntVar[] stock = model.intVarArray("stock",duree, 0,100000);
		
		// commande[i] vaut lotCommande si stock[i + delai] <= stockSecurite, vaut 0 sinon
		IntVar[] commande = model.intVarArray("commande",duree,new int[] {0,lotCommande});
		
		
		//Contraintes

		// Une commande est lancé dès que le stock passe en dessous du stock de sécurité
		// Si le stock au mois i est inferieur à lotCommande alors l'appro vaut lot commande sinon elle vaut 0
			for(int i=0;i<duree-delai;i++) {
				BoolVar b1 = model.arithm(stock[i+delai], "<=", stockSecurite).reify();
				BoolVar b2 = model.arithm(commande[i], ">", 0).reify();
				model.arithm(b1, "=", b2).post();
			}
		
		
		//Stock initial
		model.arithm(stock[0], "=", stockInitial).post();
		
		// Evolution du stock au cours du temps : stock[i] = stock[i-1] + commande[i-1] - demandeMensuelle[i-1]
		for(int i=1; i <= 1+delai; i++) {
			model.scalar(new IntVar[] {stock[i-1],stock[i]},new int[] {-1,1},"=", -demandeMensuelle[i-1]).post();
		}
		
		for(int i=1+delai;i<duree;i++) {
			model.scalar(new IntVar[] {stock[i-1],stock[i],commande[i-1-delai]},new int[] {-1,1,-1 },"=", -demandeMensuelle[i-1]).post();
		}
		
		
		// Resolution et affichage des résultats
		
		Solution solution = model.getSolver().findSolution();
		if(solution != null){
		    System.out.println(solution.toString());
		    
		    
		    for(int i =0; i < duree-delai; i++) {
		    	System.out.println("Dispo au mois " + i + " :" + (stock[i+delai].getValue() + commande[i].getValue()));
		    }		    
		    System.out.println("");
		    System.out.println("");
		    
		    for (int j=0; j<nbGroupes; j++) {
		    	for(int i =0; i < stock.length; i++) {
		    		System.out.print(demande[j][i] + "    ");
		    	}
		    	System.out.println("");		    
		    }
		    
		    /* Tests
		    System.out.println(moisEspacement * (int)((1-(pourcentagePassageTotal/100))*(double)(structure1.getNbPatients()[3]*structure1.getConsoPatient())/(nbGroupes-1)));
		    System.out.println(moisEspacement);
		    System.out.println((pourcentagePassageTotal/100));
		    System.out.println(structure1.getNbPatients()[3]);
		    System.out.println(structure1.getConsoPatient());
		    System.out.println(structure1.getNbPatients()[3]*structure1.getConsoPatient()/(nbGroupes-1));
		    
		    */
		    
		    
		 
	        String nomFichier = "fichier";
	        creationCsv(nomFichier, delai, stock, duree, commande);
		    
		}else {
			System.out.println("pas de solution");
		}
		
	}
	
	public static void creationCsv(String nomFichier, int delai, IntVar[] stock, int duree, IntVar[] commande) throws IOException {
		//File ff = new File("D:\\Fichiers\\Documents\\Cours\\A3\\Antiretroviraux\\exportcsv" + nomFichier + ".csv");
		File ff = new File("C:\\Users\\Lucas\\Documents\\Mines\\A3\\Projet d'Option\\ExportEclipse\\" + nomFichier + ".csv");
		   PrintWriter out;
		   out = new PrintWriter(new FileWriter(ff));
		   out.write("Mois;Stock");
		   out.println();
		    
		   for(int i=0; i<delai; i++) {
			   out.write(i+1 +";" + stock[i].getValue());
			   out.println();
		   }
	       for (int i =delai; i<duree; i++) {
	    	   out.write(i+1 + ";" + (stock[i].getValue() + commande[i-delai].getValue()));
	    	   out.println();
	       }
	        out.close();
	}
	
	
	
	public static void main(String[] args) throws IOException {
		

		
	// Données structure1
		int nbPatientsInitial = 1000;
		int consoPatient = 1; // Combien un patient consomme de boite de medicament par mois
		int stockSecurite=3000;
		int stockInitial=6000;
		int duree = 36; // Durée en mois de la simulation
		int lotCommande=3000; // Combien de boite de medicament arrive par commande
		int delai = 1; // délai entre le moment ou la commande est passé et le moment ou elle arrive
		int pourcentageFluctuation=0; // Au mois suivant il y a une proportion aléatoire entre 0 et pourcentageFluctuation de patients de plus ou moins.
		
		Structure structure1 = new Structure(nbPatientsInitial,
											consoPatient, 
											stockSecurite, 
											stockInitial, 
											lotCommande, 
											delai, 
											pourcentageFluctuation,
											duree);		
	
	// Données Strategie :
		double pourcentagePassageTotal = 90; //pourcentage à faire passer au nouveau traitement
		double pourcentagePassageMensuel = 10;
		int tempsEtapes = 1; // Durée entre chaque étape
		int nbEtapes = (int)(pourcentagePassageTotal / pourcentagePassageMensuel); // Nombre d'étape pour atteindre le nouvel espacement
		int nbGroupes = nbEtapes + 1; // nbGroupes = nbEtapes (car 1 étape = 1 mois) + 1 (groupe qui ne change pas de traitement)
		int moisEspacement = 3;
		
		Strategie strategie1 = new Strategie(pourcentagePassageTotal, nbEtapes, tempsEtapes, duree, structure1, moisEspacement, "StrategieA2");
		
		resoudStock(strategie1);
		
		}
	}