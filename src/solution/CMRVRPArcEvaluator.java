/*
 * Log of changes:
 * Aug 10, 2012 Implemented
 * Sep 20, 2012 Refatored to separate feasibility and extensibility
 */

package solution;

import gurobi.GRB;
import gurobi.GRBEnv;
import gurobi.GRBException;
import gurobi.GRBLinExpr;
import gurobi.GRBModel;

import java.util.ArrayList;

import router.RGraph;
import splitProcedures.ArcEvaluation;
import splitProcedures.ArcEvaluator;

public class CMRVRPArcEvaluator implements ArcEvaluator {
	private RGraph graph;
	private double T;
	private double Tmin;
	static double CELLDISTANCE=0.170;
	static double VEL=11.89*24;
	static double bigM=10000;
	public CMRVRPArcEvaluator(RGraph graph,double T, double Tmin){
		super();
		this.graph=graph;
		this.T=T;
		this.Tmin=Tmin;
	}

	public ArcEvaluation evaluateArc(ArrayList<Integer> route, ArcEvaluation evaluation) {
		boolean extendible=false;
		boolean feasible = false;
		double cost = 0;
		if(route.size()<MainCMR.minimoenruta){
			extendible =true;
			feasible = false;
			cost=0;
			GRBEnv env;
			try {
				env = new GRBEnv(null);
				GRBModel model = null;
				env.set(GRB.IntParam.OutputFlag, 0);
				model=new GRBModel(env);
				return new CMRVRPRouteEvaluation(graph, feasible, extendible, cost,env,model);
			} catch (GRBException e1) {
				e1.printStackTrace();
				System.out.println("Algo paso con esto");
			}

		} else if(route.size()>MainCMR.maximoenruta){
			extendible =false;
			feasible = false;
			cost=0;
			return new CMRVRPRouteEvaluation(graph, feasible, extendible, cost,null,null);				
		} else {
			if(route.size()!=MainCMR.minimoenruta){
				extendible =true;
				GRBEnv env;				
				try {
					env=((CMRVRPRouteEvaluation)evaluation).getEnv();
					env.set(GRB.IntParam.OutputFlag, 0);
					GRBModel model=((CMRVRPRouteEvaluation)evaluation).getModel();
					model.addVar(0,GRB.INFINITY,1,GRB.CONTINUOUS,"z("+route.get((route.size()-1))+")");
					model.addVar(Tmin,T,0,GRB.CONTINUOUS,"s("+route.get((route.size()-1))+")");
					
//					double delta1=0.34;
//					double delta2=0.71;
//					for (int i = 0; i < 7; i++) {
//						model.addVar(0,1,0,GRB.BINARY,"y("+route.get((route.size()-1))+","+i+")");
//					}
//					model.update();
//					GRBLinExpr convexity= new GRBLinExpr();
//					for (int i = 0; i < 7; i++) {
//						convexity.addTerm(1, model.getVarByName("y("+route.get((route.size()-1))+","+i+")"));
//						GRBLinExpr intervalol=new GRBLinExpr();
//						GRBLinExpr intervalou=new GRBLinExpr();
//						intervalol.addTerm(1, model.getVarByName("s("+route.get((route.size()-1))+")"));
//						intervalol.addTerm(-((Tmin+delta1)+i), model.getVarByName("y("+route.get((route.size()-1))+","+i+")"));
//						model.addConstr(intervalol,GRB.GREATER_EQUAL, 0, "Intervalo l_"+(route.size()-1)+"_"+i);
//						intervalou.addTerm(1, model.getVarByName("s("+route.get((route.size()-1))+")"));
//						intervalou.addTerm(-((Tmin+delta2)+i-T), model.getVarByName("y("+route.get((route.size()-1))+","+i+")"));
//						model.addConstr(intervalou, GRB.LESS_EQUAL,T, "Intervalo u_"+(route.size()-1)+"_"+i);
//					}
//					model.addConstr(convexity, GRB.EQUAL,1,"Convexity_"+(route.size()-1));

					model.set(GRB.IntAttr.ModelSense, 1);
					model.update();
					int i=route.size()-1;
					for(int k=0;k<graph.getNodes().size();k++){
						if(graph.getNodes().get(k).getId3()==route.get(i)) {
							for(int j=0;j<graph.getNodes().get(k).getPieces().length;j++){
								GRBLinExpr PieceWiseLeft=new GRBLinExpr(); 
								PieceWiseLeft.addTerm(1,model.getVarByName("z("+route.get(i)+")"));
								PieceWiseLeft.addTerm(-graph.getNodes().get(k).getPieces()[j][0],model.getVarByName("s("+route.get(i)+")"));
								model.addConstr(PieceWiseLeft, GRB.GREATER_EQUAL, graph.getNodes().get(k).getPieces()[j][1], "PieceWise"+i+"-"+j);
							}
						}
					}
					double ttime;
					GRBLinExpr TravelTime=new GRBLinExpr(); 
					TravelTime.addTerm(1,model.getVarByName("s("+route.get(i)+")"));
					TravelTime.addTerm(-1,model.getVarByName("s("+route.get(i-1)+")"));
					for(int k=0;k<graph.getNodes().size();k++){
						for(int h=0;h<graph.getNodes().size();h++){
							if(graph.getNodes().get(k).getId3()==route.get(i)){
								if(graph.getNodes().get(h).getId3()==route.get(i-1)){
									ttime=graph.getNodes().get(h).getExpectedservicetime()+(Math.abs(graph.getNodes().get(h).getPosx()-graph.getNodes().get(k).getPosx())+Math.abs(graph.getNodes().get(h).getPosy()-graph.getNodes().get(k).getPosy()))*CELLDISTANCE/VEL;
									model.addConstr(TravelTime, GRB.GREATER_EQUAL, ttime, "TravelTime"+i);
									//graph.getNodes().get(h).getExpectedservicetime()+
								}
							}
						}
					}

					model.update();
					model.write("mirando este.lp");
					System.err.println("Entro en la cosa de arriba");
					model.optimize();

					if(model.get(GRB.IntAttr.Status) == GRB.INFEASIBLE){ 
						feasible=false;	
						cost=0;
					} else if (model.get(GRB.IntAttr.Status) == GRB.OPTIMAL){
						feasible=true;	
						cost=model.get(GRB.DoubleAttr.ObjVal);
					}				
					return new CMRVRPRouteEvaluation(graph, feasible, extendible, cost,env,model);

				} catch (GRBException e) {
					e.printStackTrace();
				}

			} else{
				extendible =true;
				GRBEnv env;
				try {
					env=new GRBEnv(null);
					env.set(GRB.IntParam.OutputFlag, 0);
					GRBModel  model = new GRBModel(env);
					// Create variables
					for(int i=0;i<route.size();i++){
						model.addVar(0,GRB.INFINITY,1,GRB.CONTINUOUS,"z("+route.get(i)+")"); 
						model.addVar(0,T,0,GRB.CONTINUOUS,"s("+route.get(i)+")");
					}

					model.set(GRB.IntAttr.ModelSense, 1);
					model.update();
					//Set of constrains
					//1. PieceWise Constrains
					for(int i=0;i<route.size();i++){
						for(int k=0;k<graph.getNodes().size();k++){
							if(graph.getNodes().get(k).getId3()==route.get(i)) {
								for(int j=0;j<graph.getNodes().get(k).getPieces().length;j++){
									GRBLinExpr PieceWiseLeft=new GRBLinExpr(); 
									PieceWiseLeft.addTerm(1,model.getVarByName("z("+route.get(i)+")"));
									PieceWiseLeft.addTerm(-graph.getNodes().get(k).getPieces()[j][0],model.getVarByName("s("+route.get(i)+")"));
									model.addConstr(PieceWiseLeft, GRB.GREATER_EQUAL, graph.getNodes().get(k).getPieces()[j][1], "PieceWise"+i+"-"+j);
								}
							}
						}
					}
					//2. travel time constraints
					double ttime;
					for(int i=1;i<route.size();i++){
						GRBLinExpr TravelTime=new GRBLinExpr(); 
						TravelTime.addTerm(1,model.getVarByName("s("+route.get(i)+")"));
						TravelTime.addTerm(-1,model.getVarByName("s("+route.get(i-1)+")"));
						for(int k=0;k<graph.getNodes().size();k++){
							for(int h=0;h<graph.getNodes().size();h++){
								if(graph.getNodes().get(k).getId3()==route.get(i)){
									if(graph.getNodes().get(h).getId3()==route.get(i-1)){
										ttime=graph.getNodes().get(h).getExpectedservicetime()+(Math.abs(graph.getNodes().get(h).getPosx()-graph.getNodes().get(k).getPosx())+Math.abs(graph.getNodes().get(h).getPosy()-graph.getNodes().get(k).getPosy()))*CELLDISTANCE/VEL;
										model.addConstr(TravelTime, GRB.GREATER_EQUAL, ttime, "TravelTime"+i);
										//graph.getNodes().get(h).getExpectedservicetime()+
									}
								}
							}
						}
					}
					model.update();
					System.err.println("ya entro ");
					model.optimize();

					if(model.get(GRB.IntAttr.Status) == GRB.INFEASIBLE){ 
						feasible=false;	
						cost=0;
					} else if (model.get(GRB.IntAttr.Status) == GRB.OPTIMAL){// && exist==true) { 
						feasible=true;	
						cost=model.get(GRB.DoubleAttr.ObjVal);
					}				
					return new CMRVRPRouteEvaluation(graph, feasible, extendible, cost,env,model);

				} catch (GRBException e) {
					e.printStackTrace();
				}
			}
		}

		return new CMRVRPRouteEvaluation(graph, false, false, 0,null,null);
	}
	public RGraph getGraph() {
		return graph;
	}
	public void setGraph(RGraph graph) {
		this.graph = graph;
	}

	public double getT() {
		return T;
	}

	public void setT(double t) {
		T = t;
	}

	public String ToStringRoute (ArrayList<Integer> nb){
		String name="";
		for(int i=0;i<nb.size();i++){
			name=name+" "+nb.get(i);
		}
		return name;
	}
}
