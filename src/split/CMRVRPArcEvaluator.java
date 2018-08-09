package split;

import gurobi.GRB;
import gurobi.GRBEnv;
import gurobi.GRBException;
import gurobi.GRBLinExpr;
import gurobi.GRBModel;


import java.util.ArrayList;

import router.RGraph;
import solution.CMRVRPRouteEvaluation;
import solution.MainCMR;
import splitProcedures.ArcEvaluation;
import splitProcedures.ArcEvaluator;

public class CMRVRPArcEvaluator implements ArcEvaluator {
	private RGraph graph;
	private double T;
	private double Tmin;
	static double CELLDISTANCE=0.170;
	static double VEL=11.89*24;
	static double bigM=10000;
	public CMRVRPArcEvaluator(RGraph graph, double T, double Tmin){
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
		
					
					double delta1h1=0.33;
					double delta2h1=0.5;
					double delta1h2=0.54;
					double delta2h2=0.71;
					for (int i = 0; i < 7; i++) {
						for(int j=0;j<2;j++)
						model.addVar(0,1,0,GRB.BINARY,"y("+route.get((route.size()-1))+","+i+","+j+")");
					}
					model.update();
					GRBLinExpr convexity= new GRBLinExpr();
					for (int i = 0; i < 7; i++) {
						for(int j=0;j<2;j++){
						convexity.addTerm(1, model.getVarByName("y("+route.get((route.size()-1))+","+i+","+j+")"));				
						}
						GRBLinExpr intervalol1=new GRBLinExpr();
						GRBLinExpr intervalou1=new GRBLinExpr();
						GRBLinExpr intervalol2=new GRBLinExpr();
						GRBLinExpr intervalou2=new GRBLinExpr();
						intervalol1.addTerm(1, model.getVarByName("s("+route.get((route.size()-1))+")"));
						intervalol1.addTerm(-((Tmin+delta1h1)+i), model.getVarByName("y("+route.get((route.size()-1))+","+i+","+0+")"));
						model.addConstr(intervalol1,GRB.GREATER_EQUAL, 0, "Intervalo l_h1"+(route.get(route.size()-1))+"_"+i);
						
						intervalol2.addTerm(1, model.getVarByName("s("+route.get((route.size()-1))+")"));
						intervalol2.addTerm(-((Tmin+delta1h2)+i), model.getVarByName("y("+route.get((route.size()-1))+","+i+","+1+")"));
						model.addConstr(intervalol2,GRB.GREATER_EQUAL, 0, "Intervalo l_h2"+(route.get(route.size()-1))+"_"+i);
	
						
						intervalou1.addTerm(1, model.getVarByName("s("+route.get((route.size()-1))+")"));
						intervalou1.addTerm(-((Tmin+delta2h1)+i-T), model.getVarByName("y("+route.get((route.size()-1))+","+i+","+0+")"));
						model.addConstr(intervalou1, GRB.LESS_EQUAL,T, "Intervalo u_h1"+(route.get(route.size()-1))+"_"+i);
						
						intervalou2.addTerm(1, model.getVarByName("s("+route.get((route.size()-1))+")"));
						intervalou2.addTerm(-((Tmin+delta2h2)+i-T), model.getVarByName("y("+route.get((route.size()-1))+","+i+","+1+")"));
						model.addConstr(intervalou1, GRB.LESS_EQUAL,T, "Intervalo u_h2"+(route.get(route.size()-1))+"_"+i);
					}
					model.addConstr(convexity, GRB.EQUAL,1,"Convexity_"+(route.get(route.size()-1)));

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
					//model.write("mirando este 1.lp");
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
					double delta1h1=0.33;
					double delta2h1=0.5;
					double delta1h2=0.54;
					double delta2h2=0.71;
//					
					for(int i=0;i<route.size();i++){
						model.addVar(0,GRB.INFINITY,1,GRB.CONTINUOUS,"z("+route.get(i)+")"); 
						model.addVar(0,T,0,GRB.CONTINUOUS,"s("+route.get(i)+")");
						
						for (int j = 0; j < 7; j++) {
							for(int h=0;h<2;h++)
							model.addVar(0,1,0,GRB.BINARY,"y("+route.get(i)+","+j+","+h+")");
						}
						model.update();
						GRBLinExpr convexity= new GRBLinExpr();
						for (int h = 0; h < 7; h++) {
							for(int j=0;j<2;j++){
							convexity.addTerm(1, model.getVarByName("y("+route.get(i)+","+h+","+j+")"));
							}
							GRBLinExpr intervalol1=new GRBLinExpr();
							GRBLinExpr intervalou1=new GRBLinExpr();
							GRBLinExpr intervalol2=new GRBLinExpr();
							GRBLinExpr intervalou2=new GRBLinExpr();
							
							intervalol1.addTerm(1, model.getVarByName("s("+route.get(i)+")"));
							intervalol1.addTerm(-((Tmin+delta1h1)+h), model.getVarByName("y("+route.get(i)+","+h+","+0+")"));
							model.addConstr(intervalol1,GRB.GREATER_EQUAL, 0, "Intervalo l_h1"+route.get(i)+"_"+h);
							
							intervalol2.addTerm(1, model.getVarByName("s("+route.get(i)+")"));
							intervalol2.addTerm(-((Tmin+delta1h2)+h), model.getVarByName("y("+route.get(i)+","+h+","+1+")"));
							model.addConstr(intervalol2,GRB.GREATER_EQUAL, 0, "Intervalo l_h2"+route.get(i)+"_"+h);
							
							intervalou1.addTerm(1, model.getVarByName("s("+route.get(i)+")"));
							intervalou1.addTerm(-((Tmin+delta2h1)+h-T), model.getVarByName("y("+route.get(i)+","+h+","+0+")"));
							model.addConstr(intervalou1, GRB.LESS_EQUAL,T, "Intervalo u_h1"+route.get(i)+"_"+h);
							
							intervalou2.addTerm(1, model.getVarByName("s("+route.get(i)+")"));
							intervalou2.addTerm(-((Tmin+delta2h2)+h-T), model.getVarByName("y("+route.get(i)+","+h+","+1+")"));
							model.addConstr(intervalou2, GRB.LESS_EQUAL,T, "Intervalo u_h2"+route.get(i)+"_"+h);
						
							}
						model.addConstr(convexity, GRB.EQUAL,1,"Convexity_"+route.get(i));
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
					model.write("mirando este 2.lp");
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
