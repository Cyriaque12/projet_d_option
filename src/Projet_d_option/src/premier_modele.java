package Projet_d_option.src;
import java.io.FileWriter;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solution;
import org.chocosolver.solver.constraints.Constraint;
import org.chocosolver.solver.variables.BoolVar;
import org.chocosolver.solver.variables.IntVar;





public class premier_modele {

	
	public static void main(String[] args) throws IOException {
		
		Model model = new Model("Gestion des stocks");
	
		
	// Données structure
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
		double pourcentage = 90; //pourcentage à faire passer au nouveau traitement
		int tempsEtapes = 1; // Durée entre chaque étape
		int nbEtapes = 9; // Nombre d'étape pour atteindre le nouvel espacement
		int nbGroupes = nbEtapes + 1; // nbGroupes = nbEtapes (car 1 étape = 1 mois) + 1 (groupe qui ne change pas de traitement)
		int moisEspacement = 3;
		
		Strategie strategie1 = new Strategie(pourcentage, nbEtapes, tempsEtapes, duree, structure1, moisEspacement);
		
		// Ces paramètres strategique determine la demande en medicaments des patients sur les 24 mois
		int[][] demande = strategie1.getDemande();
		
		// On somme ces demandes pour avoir la demande mensuelle;
		int[] demandeMensuelle=new int[duree];
		
		int demandeMoisI = 0;
		for (int i=0; i<duree; i++) {
			demandeMoisI = 0;
			for (int j=0; j<nbGroupes; j++) {
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
		    System.out.println(moisEspacement * (int)((1-(pourcentage/100))*(double)(structure1.getNbPatients()[3]*structure1.getConsoPatient())/(nbGroupes-1)));
		    System.out.println(moisEspacement);
		    System.out.println((pourcentage/100));
		    System.out.println(structure1.getNbPatients()[3]);
		    System.out.println(structure1.getConsoPatient());
		    System.out.println(structure1.getNbPatients()[3]*structure1.getConsoPatient()/(nbGroupes-1));
		    
		    */
		    
		    
		   File ff = new File("C:\\Users\\Lucas\\Documents\\Mines\\A3\\Projet d'Option\\ExportEclipse\\fichier.csv");
		   PrintWriter out;
		   out = new PrintWriter(new FileWriter(ff));
		   String st = "";
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
		   
	        
		}else {
			System.out.println("pas de solution");
		}
		
		}
	}