package projet_d_option;

import java.util.Random;

public class Structure {
	
	public int nbPatientsInitial;
	public int[] nbPatients;
	public int consoPatient;
	public int stockSecurite;
	public int stockInitial;
	public int lotCommande;
	public int delai;
	public int duree;
	public int pourcentageFluctuation;
	
	
	public Structure(int nbPatientsInitial, int[] nbPatients, int consoPatient, int stockSecurite, int stockInitial, int lotCommande,
			int delai, int pourcentageFluctuation) {
		this.nbPatientsInitial = nbPatientsInitial;
		this.nbPatients = nbPatients;
		this.consoPatient = consoPatient;
		this.stockSecurite = stockSecurite;
		this.stockInitial = stockInitial;
		this.lotCommande = lotCommande;
		this.delai = delai;
		this.pourcentageFluctuation = pourcentageFluctuation;
		
	}

	public int getNbPatientsInitial() {
		return nbPatientsInitial;
	}

	
	public int[] getNbPatients() {
		nbPatients[0] = nbPatientsInitial;
		for (int i = 0; i<duree; i++) {
			int fluctuation = (int)(Math.random()*nbPatients[i-1]*pourcentageFluctuation/100);
			double rd = Math.random();
			int signe;
			rd = rd-0.5;
			
			if (rd <0 ) {
				signe = -1;
			}
			else {
				if (rd > 0) {
					signe = 1;
				}
				else {
					signe = 0;
				}
			}
			nbPatients[i] = nbPatients[i-1] + fluctuation*signe ; 
		}
		return nbPatients;
	}


	public void setNbPatients(int[] nbPatients) {
		this.nbPatients = nbPatients;
	}


	public int getConsoPatient() {
		return consoPatient;
	}


	public void setConsoPatient(int consoPatient) {
		this.consoPatient = consoPatient;
	}


	public int getStockSecurite() {
		return stockSecurite;
	}


	public void setStockSecurite(int stockSecurite) {
		this.stockSecurite = stockSecurite;
	}


	public int getStockInitial() {
		return stockInitial;
	}


	public void setStockInitial(int stockInitial) {
		this.stockInitial = stockInitial;
	}


	public int getLotCommande() {
		return lotCommande;
	}


	public void setLotCommande(int lotCommande) {
		this.lotCommande = lotCommande;
	}


	public int getDelai() {
		return delai;
	}


	public void setDelai(int delai) {
		this.delai = delai;
	}
	
	
	
	
}
