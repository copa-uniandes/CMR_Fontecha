/*
* Log of changes:
* Aug 27, 2012 Implemented
* Sep 20, 2012 Refatored to separate feasibility and extensibility
*/

package solution;

import gurobi.GRBEnv;
import gurobi.GRBException;
import gurobi.GRBModel;
import router.RGraph;
import splitProcedures.ArcEvaluation;

public class CMRVRPRouteEvaluation implements ArcEvaluation{
	private double cost;
	private boolean feasible;
	private boolean extendible;
	private RGraph graph;
	private GRBEnv env;
	private GRBModel model;
	
	
	public CMRVRPRouteEvaluation(RGraph graph,boolean feasible,boolean extendible, double cost, GRBEnv env,GRBModel model){
		this.cost=cost;
		this.extendible=extendible;
		this.feasible=feasible;
		this.setGraph(graph);
		this.env=env;
		this.model=model;
//		try {
//			env=new GRBEnv(null);
//		} catch (GRBException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}
	

	public boolean arcIsExtendible() {
		// TODO Auto-generated method stub
		return this.extendible;
	}


	public boolean arcIsFeasible() {
		// TODO Auto-generated method stub
		return this.feasible;
	}

	
	public double getArcCost() {
		// TODO Auto-generated method stub
		return cost;
	}

	public String toString(){
		StringBuilder string=new StringBuilder();
		string.append("\n");
		string.append("COST \t"+this.cost);
		return string.toString();
	}

	public RGraph getGraph() {
		return graph;
	}

	public void setGraph(RGraph graph) {
		this.graph = graph;
	}

	public GRBEnv getEnv() {
		return env;
	}

	public void setEnv(GRBEnv env) {
		this.env = env;
	}

	public GRBModel getModel() {
		return model;
	}

	public void setModel(GRBModel model) {
		this.model = model;
	}
}
