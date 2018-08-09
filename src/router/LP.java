package router;
import gurobi.*;
/**
 * This class is used in order to define the basic information of a node
 * @author 	/John Edgar Fontecha Garcia
 * 			/je.fontecha10@uniandes.edu.co
 * 			/Universidad de los Andes
 *			/Departamento de Ingeniería Industrial
 *			/Combined Maintenance and Routing Optimization for large scale problems
 */
public class LP {
	static double CELLDISTANCE=170;
	static double VEL=0.1;
	static int INFO=2;
	static double bigM=1000;
	private double cost;
	/**
	 * An LP is a optimization problem to solve the problem to optimality. Maybe this class is not necessary
	 * @param route is an object Route
	 */
	public LP (Route r){
		GRBEnv env;
		try{
			env=new GRBEnv(null);
			env.set(GRB.IntParam.OutputFlag, 0);
			GRBModel  model = new GRBModel(env);
			// Create variables
			for(int i=0;i<r.getRoute().size();i++){
				model.addVar(0,GRB.INFINITY,1,GRB.CONTINUOUS,"z("+r.getRoute().get(i).getId2()+")");
				model.addVar(0,GRB.INFINITY,0,GRB.CONTINUOUS,"s("+r.getRoute().get(i).getId2()+")");				
			}
			model.set(GRB.IntAttr.ModelSense, 1);
			model.update();
			//Set of constrains
			//1. PieceWise Constrains
			for(int i=0;i<r.getRoute().size();i++){
				for(int j=0;j<r.getRoute().get(i).getPieces().length;j++){
					GRBLinExpr PieceWiseLeft=new GRBLinExpr(); 
					PieceWiseLeft.addTerm(1,model.getVarByName("z("+r.getRoute().get(i).getId2()+")"));
					PieceWiseLeft.addTerm(-r.getRoute().get(i).getPieces()[j][0],model.getVarByName("s("+r.getRoute().get(i).getId2()+")"));
					model.addConstr(PieceWiseLeft, GRB.GREATER_EQUAL, r.getRoute().get(i).getPieces()[j][1], "PieceWise"+i);
				}
			}
			//2. travel time constraints
			double ttime;
			for(int i=1;i<r.getRoute().size();i++){
				GRBLinExpr TravelTime=new GRBLinExpr(); 
				TravelTime.addTerm(1,model.getVarByName("s("+r.getRoute().get(i).getId2()+")"));
				TravelTime.addTerm(-1,model.getVarByName("s("+r.getRoute().get(i-1).getId2()+")"));
				ttime=(Math.abs(r.getRoute().get(i-1).getPosx()-r.getRoute().get(i).getPosx())+Math.abs(r.getRoute().get(i-1).getPosy()-r.getRoute().get(i).getPosy()))/VEL;
				model.addConstr(TravelTime, GRB.GREATER_EQUAL, r.getRoute().get(i-1).getExpectedservicetime()+ttime, "TravelTime"+i);
			}
			model.update();
			model.write("VRP.lp");
			model.optimize();
			if(model.get(GRB.IntAttr.Status) == GRB.INFEASIBLE){
				setCost(bigM);
				double [][] temp= new double [r.getRoute().size()+1][INFO];
				temp[0][0]=-1;
				temp[0][1]=bigM;
				for(int i=0;i<r.getRoute().size();i++){
					temp[i+1][0]=r.getRoute().get(i).getId2();
					temp[i+1][1]=0;
				}
				System.out.println("Esto da infactible ");
			}
			if (model.get(GRB.IntAttr.Status) == GRB.OPTIMAL) {
				setCost(model.get(GRB.DoubleAttr.ObjVal));
				double [][] temp= new double [r.getRoute().size()+1][INFO];
				temp[0][0]=-1;
				temp[0][1]=getCost();
				for(int i=0;i<r.getRoute().size();i++){
					temp[i+1][0]=r.getRoute().get(i).getId2();
					temp[i+1][1]=1;
				}
				
			} else {
			}
		}catch (GRBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public double getCost() {
		return cost;
	}
	public void setCost(double cost) {
		this.cost = cost;
	}
	


}
