package Projet_d_option.src;

import org.chocosolver.solver.variables.BoolVar;

public class Strategie {
	public int pourcentage;
	public int nbEtapes;
	public int tempsEtapes;
	public int[][] demande;
	public int nbGroupes;
	public int duree;
	public Structure structure;
	public int moisEspacement;
	
	
	
	public Strategie(int pourcentage, 
					int nbEtapes, 
					int tempsEtapes, 
					int duree, 
					Structure structure, 
					int moisEspacement) {
		this.pourcentage = pourcentage;
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




	public int getPourcentage() {
		return pourcentage;
	}



	public int getNbEtapes() {
		return nbEtapes;
	}



	public int getTempsEtapes() {
		return tempsEtapes;
	}



	public int[][] getDemande() {
		for (int i = 0; i<this.duree; i++) {
			demande[0][i] = (1-(pourcentage/100))*structure.getNbPatients()[i]*structure.getConsoPatient();					
		}
		
		for(int j=1;j<nbGroupes;j++) {
			for(int i=0;i<11+j-1;i++) {
				demande[j][i]= (1-(pourcentage/100))*structure.getNbPatients()[i]*structure.getConsoPatient()/(nbGroupes-1);
			}
		}
		
		for (int j = 1; j<nbGroupes; j++) {
			//switch (j % moisEspacement){
				//case 1: 
					for (int i = 11+j-1; i < structure.getDuree(); i+=moisEspacement) {
						demande[j][i] = structure.getNbPatients()[i] * (this.pourcentage/100)*moisEspacement;
						if (i+1 < duree) {
							demande[j][i+1] = 0;
						}
						if (i+2 < duree) {
							demande[j][i+2] = 0;
						}
					}
/*				case 2:
					for (int i = 11+j-1; i < structure.getStock().length; i+=moisEspacement) {
						demande[j][i], "=", structure.getNbPatients[i] * (this.pourcentage/100))*moisEspacement);
						if (i+1 < structure.getStock().length) {
							demande[j][i+1] = 0;
						}
						if (i+2 < structure.getStock().length) {
							demande[j][i+2] = 0;
						}
					}

				case 0:
					for (int i = 11+j-1; i < stock.length; i+=moisEspacement) {
						model.arithm(demande[j][i], "=", (int)(nbPatients * proportion)*moisEspacement).post();
						if (i+1<stock.length) {
							model.arithm(demande[j][i+1], "=", 0).post();
						}
						if (i+2<stock.length) {
							model.arithm(demande[j][i+2], "=", 0).post();
						}
					}
			}
	*/			
		}
		
		
		return demande;
	}
	
	
	
	
	
}
