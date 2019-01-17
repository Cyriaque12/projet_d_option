import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solution;
import org.chocosolver.solver.constraints.Constraint;
import org.chocosolver.solver.variables.BoolVar;
import org.chocosolver.solver.variables.IntVar;


public class premier_modele {

	
	public static void main(String[] args) {
		
		Model model = new Model("Gestion des stocks");
		
		//Variables
		IntVar[] stock = model.intVarArray("stock",24, 2000,100000);
		IntVar[] quantite= model.intVarArray("quantite",24,new int[] {0,3000});
		IntVar[] sortie = model.intVarArray("sortie", 24,0,3000);
		
		for(int i=0;i<stock.length;i++) {
			if(i<=11 || i==14) {
				model.arithm(sortie[i], "=", 1000).post();
			}if(i==13) {
				model.arithm(sortie[i], "=", 1250).post();
			}if(i==12 || (i>14&&i%3==0)) {
				model.arithm(sortie[i], "=", 1500).post();
			}if(i>15&&(i%3!=0)) {
				model.arithm(sortie[i], "=", 750).post();
			}
			
			BoolVar b1 = model.arithm(stock[i], "<=", 3000).reify();
			BoolVar b2 = model.arithm(quantite[i], ">", 0).reify();
			model.arithm(b1, "=", b2).post();
		}
		
		//Contraintes
		//Stock initial
		model.arithm(stock[0], "=", 6000).post();
	
		for(int i=1;i<stock.length;i++) {
			model.scalar(new IntVar[] {stock[i-1],stock[i],quantite[i-1],sortie[i-1]},new int[] {-1,1,-1,1 },"=", 0).post();
			}
		
		Solution solution = model.getSolver().findSolution();
		if(solution != null){
		    System.out.println(solution.toString());
		}else {
			System.out.println("pas de solution");
		}
		
		}
	}
