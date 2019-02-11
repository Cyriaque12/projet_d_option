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

public class premier_modele {

	public static void creationCsv(String nomFichier, int delai, IntVar[] stock, int duree, IntVar[] commande) throws IOException {
		File ff = new File("C:\\Users\\Lucas\\Documents\\Mines\\A3\\Projet d'Option\\ExportEclipse\\" + nomFichier + ".csv");
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
	}
	
	public static void main(String[] args) throws IOException {
		
		Model model = new Model("Gestion des stocks");
	
		
	// Donn�es structure
		int nbPatientsInitial = 1000;
		int consoPatient = 1; // Combien un patient consomme de boite de medicament par mois
		int stockSecurite=3000;
		int stockInitial=6000;
		int duree = 36; // Dur�e en mois de la simulation
		int lotCommande=3000; // Combien de boite de medicament arrive par commande
		int delai = 1; // d�lai entre le moment ou la commande est pass� et le moment ou elle arrive
		int pourcentageFluctuation=0; // Au mois suivant il y a une proportion al�atoire entre 0 et pourcentageFluctuation de patients de plus ou moins.
		
		Structure structure1 = new Structure(nbPatientsInitial,
											consoPatient, 
											stockSecurite, 
											stockInitial, 
											lotCommande, 
											delai, 
											pourcentageFluctuation,
											duree);
		
	// Donn�es Strategie :
		double pourcentagePassageTotal = 90; //pourcentage � faire passer au nouveau traitement
		double pourcentagePassageMensuel = 10;
		int tempsEtapes = 1; // Dur�e entre chaque �tape
		int nbEtapes = (int)(pourcentagePassageTotal / pourcentagePassageMensuel); // Nombre d'�tape pour atteindre le nouvel espacement
		int nbGroupes = nbEtapes + 1; // nbGroupes = nbEtapes (car 1 �tape = 1 mois) + 1 (groupe qui ne change pas de traitement)
		int moisEspacement = 3;
		
		Strategie strategie1 = new Strategie(pourcentagePassageTotal, nbEtapes, tempsEtapes, duree, structure1, moisEspacement);
		
		// Ces param�tres strategique determine la demande en medicaments des patients sur les 24 mois
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

		// Une commande est lanc� d�s que le stock passe en dessous du stock de s�curit�
		// Si le stock au mois i est inferieur � lotCommande alors l'appro vaut lot commande sinon elle vaut 0
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
		
		
		// Resolution et affichage des r�sultats
		
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
		    
		    
		  /* File ff = new File("C:\\Users\\Lucas\\Documents\\Mines\\A3\\Projet d'Option\\ExportEclipse\\fichier.csv");
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
		   */
	        String nomFichier = "fichier";
		    creationCsv(nomFichier, delai, stock, duree, commande);
		    
		}else {
			System.out.println("pas de solution");
		}
		
		}
	}