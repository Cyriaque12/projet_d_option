import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solution;
import org.chocosolver.solver.constraints.Constraint;
import org.chocosolver.solver.variables.BoolVar;
import org.chocosolver.solver.variables.IntVar;

import projet_d_option.Strategie;
import projet_d_option.Structure;


public class premier_modele {

	
	public static void main(String[] args) {
		
		Model model = new Model("Gestion des stocks");
	
		
	// Données structure
		int nbPatientsInitial = 1000;
		int[] nbPatients = Structure.getNbPatients();
		int consoPatient = 1; // Combien un patient consomme de boite de medicament par mois
		int stockSecurite=3000;
		int stockInitial=6000;
		int lotCommande=3000; // Combien de boite de medicament arrive par commande
		int delai=0; // délai entre le moment ou la commande est passé et le moment ou elle arrive
		int pourcentageFluctuation=1; // Au mois suivant il y a une proportion aléatoire entre 0 et pourcentageFluctuation de patients de plus ou moins.
		
		Structure structure1 = new Structure(nbPatientsInitial, nbPatients, consoPatient, stockSecurite, stockInitial, lotCommande, delai, pourcentageFluctuation);
		
	// Données Strategie :
		int pourcentage = 10; //pourcentage à faire passer au nouveau traitement
		int duree = 24; // Durée en mois de la simulation		
		int tempsEtapes = 1; // Durée entre chaque étape
		int nbEtapes = 3; // Nombre d'étape pour atteindre le nouvel espacement
		int nbGroupes = nbEtapes + 1; // nbGroupes = nbEtapes (car 1 étape = 1 mois) + 1 (groupe qui ne change pas de traitement)
		
		Strategie strategie1 = new Strategie(pourcentage, nbEtapes, tempsEtapes, duree, structure1);
		
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
		IntVar[] appro = model.intVarArray("appro",duree,new int[] {0,lotCommande});
		
		
		//Contraintes
		
		// Une commande est lancé dès que le stock passe en dessous du stock de sécurité
		// Si le stock au mois i est inferieur à 3000 alors l'appro vaut 3000 sinon elle vaut 0
		for(int j=0;j<nbGroupes;j++) {
			for(int i=0;i<11+j;i++) {
				BoolVar b1 = model.arithm(stock[i], "<=", stockSecurite).reify();
				BoolVar b2 = model.arithm(appro[i], ">", 0).reify();
				model.arithm(b1, "=", b2).post();
			}
		}
		
		//Stock initial
		model.arithm(stock[0], "=", stockInitial).post();
		
		// Evolution du stock au cours du temps : stock[i] = stock[i-1] + appro[i-1] - demandeMensuelle[i-1]
		for(int i=1;i<stock.length;i++) {
			for (int j=0; j<nbGroupes; j++) {
				model.scalar(new IntVar[] {stock[i-1],stock[i],appro[i-1]},new int[] {-1,1,-1 },"=", demandeMensuelle[i-1]).post();
			}
		}
		
		
		// Resolution et affichage des résultats
		
		Solution solution = model.getSolver().findSolution();
		if(solution != null){
			int [] dispo = new int[stock.length];
		    System.out.println(solution.toString());
		    
		    
		    for(int i =0; i < stock.length; i++) {
		    	System.out.println("appro necessaire au mois " + i + " :" + Math.max(0, demandeMensuelle[i]-stock[i].getValue()));
		    }
		    
		    System.out.println("");
		    System.out.println("");
		    
		    for (int j=0; j<nbGroupes; j++) {
		    	//dispo[i] = stock[i].getValue() + appro[i].getValue();
		    	//System.out.println("Disponibilité au mois " + i + ": " + dispo[i]) ;
		    	for(int i =0; i < stock.length; i++) {
		    		//System.out.print("demande au mois " + i + " pour le groupe " + j +": " + demande[j][i] + "    ");
		    		System.out.print(demande[j][i] + "    ");
		    	}
		    	System.out.println("");
		    	//System.out.println((nbPatientsInitialInitial * proportion)*moisEspacement) ;
		    	//System.out.println(nbPatientsInitialInitial);
		    	//System.out.println(proportion);
		    	//System.out.println(moisEspacement) ;
		    }
		}else {
			System.out.println("pas de solution");
		}
		
		}
	}
