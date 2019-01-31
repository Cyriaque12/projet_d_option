package projet_d_option;

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
	
	
	
	public Strategie(int pourcentage, int nbEtapes, int tempsEtapes, int duree, Structure structure, int moisEspacement) {
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
			for(int i=0;i<11+j;i++) {
				demande[j][i]= (1-(pourcentage/100))*structure.getNbPatients()[i]*structure.getConsoPatient()/(nbGroupes-1);
			}
		}
		
		for (int j = 0; j<nbGroupes; j++) {
			switch (j % moisEspacement){
				case 0: 
					for (int i = 11+j; i < structure.getStock().length; i+=moisEspacement) {
						// Je me suis arreté la, je reprendrai.
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
		
		
		return demande;
	}
	
	
	
	
	
}
