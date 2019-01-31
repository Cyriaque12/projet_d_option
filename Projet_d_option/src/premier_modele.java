import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solution;
import org.chocosolver.solver.constraints.Constraint;
import org.chocosolver.solver.variables.BoolVar;
import org.chocosolver.solver.variables.IntVar;


public class premier_modele {

	
	public static void main(String[] args) {
		
		Model model = new Model("Gestion des stocks");
		
		
	// Objectif : proportion à faire passer au nouveau traitement
		double proportion = 0.1;
		
		
	// Paramètres :
		// Durée en mois
		int duree = 6;
		
		// Durée entre chaque étape
		int tpEtape = 1; 
		
		// Nombre d'étape pour atteindre le nouvel espacement
		double nbEtapes = duree * tpEtape; 
		
		// nbGroupes = nbEtapes (car 1 étape = 1 mois) + 1 (groupe qui ne change pas de traitement)
		double nbGroupes = nbEtapes + 1;
		
		//double nbGroupes=  1/proportion;
		int nbPatients = 1000;
		int moisEspacement = 3;
		
		
		
		
		//Variables 
		IntVar[] stock = model.intVarArray("stock",24, 2000,100000);
		IntVar[] appro = model.intVarArray("appro",stock.length,new int[] {0,3000});
		IntVar[][] demande = model.intVarMatrix((int)nbGroupes, stock.length,0,3000);
		IntVar[] demandeMensuelle = model.intVarArray("demandeMensuelle",stock.length,new int[] {0,50000});
		
		
		for(int j=0;j<nbGroupes;j++) {
			for(int i=0;i<11+j;i++) {
				model.arithm(demande[j][i], "=", (int)(nbPatients * proportion)).post();
				BoolVar b1 = model.arithm(stock[i], "<=", 3000).reify();
				BoolVar b2 = model.arithm(appro[i], ">", 0).reify();
				model.arithm(b1, "=", b2).post();
			}
		}
		
		for (int j = 0; j<nbGroupes; j++) {
			switch (j % moisEspacement){
				case 0: 
					for (int i = 11+j; i < stock.length; i+=moisEspacement) {
						model.arithm(demande[j][i], "=", (int)(nbPatients * proportion)*moisEspacement).post();
						if (i+1<stock.length) {
							model.arithm(demande[j][i+1], "=", 0).post();
						}
						if (i+2<stock.length) {
							model.arithm(demande[j][i+2], "=", 0).post();
						}
						BoolVar b1 = model.arithm(stock[i], "<=", 3000).reify();
						BoolVar b2 = model.arithm(appro[i], ">", 0).reify();
						model.arithm(b1, "=", b2).post();
					}
				case 1:
					for (int i = 11+j; i < stock.length; i+=moisEspacement) {
						model.arithm(demande[j][i], "=", (int)(nbPatients * proportion)*moisEspacement).post();
						if (i+1<stock.length) {
							model.arithm(demande[j][i+1], "=", 0).post();
						}
						if (i+2<stock.length) {
							model.arithm(demande[j][i+2], "=", 0).post();
						}
							BoolVar b1 = model.arithm(stock[i], "<=", 3000).reify();
						BoolVar b2 = model.arithm(appro[i], ">", 0).reify();
						model.arithm(b1, "=", b2).post();
						
					}
				case 2:
					for (int i = 11+j; i < stock.length; i+=moisEspacement) {
						model.arithm(demande[j][i], "=", (int)(nbPatients * proportion)*moisEspacement).post();
						if (i+1<stock.length) {
							model.arithm(demande[j][i+1], "=", 0).post();
						}
						if (i+2<stock.length) {
							model.arithm(demande[j][i+2], "=", 0).post();
						}
						BoolVar b1 = model.arithm(stock[i], "<=", 3000).reify();
						BoolVar b2 = model.arithm(appro[i], ">", 0).reify();
						model.arithm(b1, "=", b2).post();
					}
			}
				
		}
		
		//Contraintes
		
		// demande Mensuelle
		int demandeMoisI = 0;
		for (int i=0; i<stock.length; i++) {
			demandeMoisI = 0;
			for (int j=0; j<nbGroupes; j++) {
				demandeMoisI += demande[j][i].getValue();
			}
			demandeMensuelle[i]=model.intVar(demandeMoisI);
		}
		
		
		//Stock initial
		model.arithm(stock[0], "=", 6000).post();
		for(int i=1;i<stock.length;i++) {
			
			for (int j=0; j<nbGroupes; j++) {
				model.scalar(new IntVar[] {stock[i-1],stock[i],appro[i-1],(demandeMensuelle[i-1])},new int[] {-1,1,-1,1 },"=", 0).post();
			}
		}
		
		Solution solution = model.getSolver().findSolution();
		if(solution != null){
			int [] dispo = new int[stock.length];
		    System.out.println(solution.toString());
		    
		    
		    for(int i =0; i < stock.length; i++) {
		    	System.out.println("appro necessaire au mois " + i + " :" + Math.max(0, demandeMensuelle[i].getValue()-stock[i].getValue()));
		    }
		    
		    System.out.println("");
		    System.out.println("");
		    
		    for (int j=0; j<nbGroupes; j++) {
		    	//dispo[i] = stock[i].getValue() + appro[i].getValue();
		    	//System.out.println("Disponibilité au mois " + i + ": " + dispo[i]) ;
		    	for(int i =0; i < stock.length; i++) {
		    		//System.out.print("demande au mois " + i + " pour le groupe " + j +": " + demande[j][i] + "    ");
		    		System.out.print(demande[j][i].getValue() + "    ");
		    	}
		    	System.out.println("");
		    	//System.out.println((nbPatients * proportion)*moisEspacement) ;
		    	//System.out.println(nbPatients);
		    	//System.out.println(proportion);
		    	//System.out.println(moisEspacement) ;
		    }
		}else {
			System.out.println("pas de solution");
		}
		
		}
	}
