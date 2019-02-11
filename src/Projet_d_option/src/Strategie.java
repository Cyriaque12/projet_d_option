package Projet_d_option.src;

import org.chocosolver.solver.variables.BoolVar;

public class Strategie {
	public double pourcentagePassageTotal;
	public int nbEtapes;
	public int tempsEtapes;
	public int[][] demande;
	public int nbGroupes;
	public int duree;
	public Structure structure;
	public int moisEspacement;
	
	
	
	public Strategie(double pourcentagePassageTotal, 
					int nbEtapes, 
					int tempsEtapes, 
					int duree, 
					Structure structure, 
					int moisEspacement) {
		this.pourcentagePassageTotal = pourcentagePassageTotal;
		this.nbEtapes = nbEtapes;
		this.duree = duree;
		this.tempsEtapes = tempsEtapes;
		this.nbGroupes = this.nbEtapes +1;
		this.demande = new int[nbGroupes][duree];
		this.structure = structure;
		this.moisEspacement = moisEspacement;
	}

	


	public int getNbGroupes() {
		return nbGroupes;
	}




	public int getDuree() {
		return duree;
	}




	public Structure getStructure() {
		return structure;
	}




	public int getMoisEspacement() {
		return moisEspacement;
	}




	public double getpourcentagePassageTotal() {
		return pourcentagePassageTotal;
	}



	public int getNbEtapes() {
		return nbEtapes;
	}



	public int getTempsEtapes() {
		return tempsEtapes;
	}



	public int[][] getDemande() {
		for (int i = 0; i<this.duree; i++) {
			demande[0][i] = (int)((1-(pourcentagePassageTotal/100))*structure.getNbPatients()[i]*structure.getConsoPatient());					
		}
		
		for(int j=1;j<nbGroupes;j++) {
			for(int i=0;i<11+j-1;i++) {
				demande[j][i]= (int)(((pourcentagePassageTotal/100))*structure.getNbPatients()[i]*structure.getConsoPatient()/(nbGroupes-1));
			}
		}
		
		for (int j = 1; j<nbGroupes; j++) {
			//switch (j % moisEspacement){
				//case 1: 
					for (int i = 11+j-1; i < structure.getDuree(); i+=moisEspacement) {
						demande[j][i]= moisEspacement * (int)((pourcentagePassageTotal/100)*structure.getNbPatients()[i]*structure.getConsoPatient()/(nbGroupes-1));
						if (i+1 < duree) {
							demande[j][i+1] = 0;
						}
						if (i+2 < duree) {
							demande[j][i+2] = 0;
						}
					}
		}
		return demande;
	}
	
	
	
	
	
}
