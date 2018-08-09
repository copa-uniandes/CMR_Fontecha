package P1;

import gurobi.GRB;
import gurobi.GRBException;
import gurobi.GRBVar;

import java.util.ArrayList;
import java.util.Iterator;

public class FeasibleRoute {
	public ArrayList<Double> s;
	public ArrayList<Integer> path;
	public double routecost;

	public FeasibleRoute(ArrayList<Double> sVar, ArrayList<Integer> pathvar, double cost) throws GRBException {
		this.path=pathvar;
		this.routecost=cost;
		this.s=sVar;
	}
	
	/**
	 * Método que devuelve el String del path
	 * @return
	 */
	public String pathToString(){
		String s="";
			for (int i = 0; i < path.size(); i++) {
				s+=path.get(i)+"-";				
			}
			s=s.substring(0, s.length()-1);
		return s;
	}

	

	public ArrayList<Integer> getPath() {
		return path;
	}

	public void setPath(ArrayList<Integer> path) {
		this.path = path;
	}

	public double getRoutecost() {
		return routecost;
	}

	public void setRoutecost(double routecost) {
		this.routecost = routecost;
	}

	public ArrayList<Double> getS() {
		return s;
	}

	public void setS(ArrayList<Double> s) {
		this.s = s;
	}
}