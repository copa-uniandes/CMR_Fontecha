package solution;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import maintenancer.MGraph;
import maintenancer.Maintenance;
import router.RGraph;
import gurobi.GRB;
import gurobi.GRBEnv;
import gurobi.GRBException;
import gurobi.GRBLinExpr;
import gurobi.GRBModel;

public class MyLP {
	static PrintWriter writer;
	static double CELLDISTANCE=0.170;
	static double VEL=11.89*24;
	ArrayList<Double> s;
	double cost;
	public MyLP(MGraph gmm,RGraph graph,ArrayList<Integer> route,int instance,int ruta,double T,double Tmin, double hp){
		System.out.println(" Tmin: "+(Tmin));
		s=new ArrayList<Double>();
		GRBEnv env;
		try {
			env = new GRBEnv(null);
			GRBModel model = new GRBModel(env);
			env.set(GRB.IntParam.OutputFlag,1);
			double delta1h1=0.33;
			double delta2h1=0.5;
			double delta1h2=0.54;
			double delta2h2=0.71;
			for(int o=0;o<route.size()-1;o++){
				model.addVar(0,GRB.INFINITY,1,GRB.CONTINUOUS,"z("+route.get(o)+")");
				model.addVar(Tmin,T,0,GRB.CONTINUOUS,"s("+route.get(o)+")");
				for (int i = 0; i < 7; i++) {
					for(int j=0;j<2;j++)
					model.addVar(0,1,0,GRB.BINARY,"y("+route.get(o)+","+i+","+j+")");
				}
				model.update();
				GRBLinExpr convexity= new GRBLinExpr();
				for (int i = 0; i < 7; i++) {
					for(int j=0;j<2;j++){
					convexity.addTerm(1, model.getVarByName("y("+route.get(o)+","+i+","+j+")"));
					}
					GRBLinExpr intervalol1=new GRBLinExpr();
					GRBLinExpr intervalou1=new GRBLinExpr();
					GRBLinExpr intervalol2=new GRBLinExpr();
					GRBLinExpr intervalou2=new GRBLinExpr();
					
					intervalol1.addTerm(1, model.getVarByName("s("+route.get(o)+")"));
					intervalol1.addTerm(-((Tmin+delta1h1)+i), model.getVarByName("y("+route.get(o)+","+i+","+0+")"));
					model.addConstr(intervalol1,GRB.GREATER_EQUAL, 0, "Intervalo l_h1"+(route.get(o))+"_"+i);
					
					intervalol2.addTerm(1, model.getVarByName("s("+route.get(o)+")"));
					intervalol2.addTerm(-((Tmin+delta1h2)+i), model.getVarByName("y("+route.get(o)+","+i+","+1+")"));
					model.addConstr(intervalol2,GRB.GREATER_EQUAL, 0, "Intervalo l_h2"+(route.get(o))+"_"+i);
					
					intervalou1.addTerm(1, model.getVarByName("s("+route.get(o)+")"));
					intervalou1.addTerm(-((Tmin+delta2h1)+i-T), model.getVarByName("y("+route.get(o)+","+i+","+0+")"));
					model.addConstr(intervalou1, GRB.LESS_EQUAL,T, "Intervalo u_"+(route.get(o))+"_"+i);
					
					intervalou2.addTerm(1, model.getVarByName("s("+route.get(o)+")"));
					intervalou2.addTerm(-((Tmin+delta2h2)+i-T), model.getVarByName("y("+route.get(o)+","+i+","+1+")"));
					model.addConstr(intervalou2, GRB.LESS_EQUAL,T, "Intervalo u_"+(route.get(o))+"_"+i);

				}
				model.addConstr(convexity, GRB.EQUAL,1,"Convexity_"+(route.get(o)));
			}

			model.set(GRB.IntAttr.ModelSense, 1);
			model.update();
			//int i=route.size()-1;
			for(int o=1;o<route.size()-1;o++){
				for(int k=0;k<graph.getNodes().size();k++){
					if(graph.getNodes().get(k).getId3()==route.get(o)) {
						for(int j=0;j<graph.getNodes().get(k).getPieces().length;j++){
							GRBLinExpr PieceWiseLeft=new GRBLinExpr(); 
							PieceWiseLeft.addTerm(1,model.getVarByName("z("+route.get(o)+")"));
							PieceWiseLeft.addTerm(-graph.getNodes().get(k).getPieces()[j][0],model.getVarByName("s("+route.get(o)+")"));
							model.addConstr(PieceWiseLeft, GRB.GREATER_EQUAL, graph.getNodes().get(k).getPieces()[j][1], "PieceWise"+route.get(o)+"-"+j);
						}
					}
				}
			}

			double ttime;
			for(int o=1;o<route.size()-1;o++){
				GRBLinExpr TravelTime=new GRBLinExpr(); 
				TravelTime.addTerm(1,model.getVarByName("s("+route.get(o)+")"));
				TravelTime.addTerm(-1,model.getVarByName("s("+route.get(o-1)+")"));
				for(int k=0;k<graph.getNodes().size();k++){
					for(int h=0;h<graph.getNodes().size();h++){
						if(graph.getNodes().get(k).getId3()==route.get(o)){
							if(graph.getNodes().get(h).getId3()==route.get(o-1)){
								ttime=graph.getNodes().get(h).getExpectedservicetime()+(Math.abs(graph.getNodes().get(h).getPosx()-graph.getNodes().get(k).getPosx())+Math.abs(graph.getNodes().get(h).getPosy()-graph.getNodes().get(k).getPosy()))*CELLDISTANCE/VEL;
								model.addConstr(TravelTime, GRB.GREATER_EQUAL, ttime, "TravelTime"+route.get(o));
								//graph.getNodes().get(h).getExpectedservicetime()+
							}
						}
					}
				}
			}
			model.update();
			model.write("mirando_"+ruta+".lp");
			model.optimize();

			boolean feasible;

			if(model.get(GRB.IntAttr.Status) == GRB.INFEASIBLE){ 
				feasible=false;
				cost=0;
			} else if (model.get(GRB.IntAttr.Status) == GRB.OPTIMAL){
				feasible=true;	
				cost=model.get(GRB.DoubleAttr.ObjVal);
				for(int o=1;o<route.size()-1;o++){
					s.add(model.getVarByName("s("+route.get(o)+")").get(GRB.DoubleAttr.X));
				}

				String rutaF = "./data/";
				//				String nombreF = "waiting_"+ruta+"_"+instance+".dat";
				String nombreF = "Cost_"+ruta+"_"+instance+".dat";
				try{
					File directorioFacturas = new File(rutaF);
					if(!directorioFacturas.exists())
						directorioFacturas.mkdirs();
					File file = new File(rutaF+nombreF);
					file.createNewFile();
					writer = new PrintWriter(new FileWriter(file));
					double tiempoviajees=0;
					double si=0;
					double sj=0;
					double delta=0;
					double punto=0;
					double temporal=0;
					for(int o=1;o<route.size()-1;o++){
						for(int h=0;h<graph.getNodes().size();h++){
							if(graph.getNodes().get(h).getId3()==route.get(o)){
								for(int k=0;k<gmm.getNodes().size();k++){
									if(graph.getNodes().get(h).getId1()==gmm.getNodes().get(k).getId()){
										delta=model.getVarByName("s("+route.get(o)+")").get(GRB.DoubleAttr.X)-graph.getNodes().get(h).getOpt();
										punto = Maintenance.newton(gmm.getNodes().get(k), 1, hp)+delta;
										for (int i = 0; i < graph.getNodes().size(); i++) {
											if(graph.getNodes().get(i).getId3()==route.get(o-1)){
												si=model.getVarByName("s("+route.get(o-1)+")").get(GRB.DoubleAttr.X);
												//model.getVarByName("s("+route.get(o-1)+")").get(GRB.DoubleAttr.X)+
												tiempoviajees=graph.getNodes().get(i).getExpectedservicetime()+(Math.abs(graph.getNodes().get(i).getPosx()-graph.getNodes().get(h).getPosx())+Math.abs(graph.getNodes().get(i).getPosy()-graph.getNodes().get(h).getPosy()))*CELLDISTANCE/VEL;
											}
										}
										//id1 id3 w cost si sj tij delta(i) cost(delta(i))
//										writer.println(graph.getNodes().get(h).getId1()+" "+graph.getNodes().get(h).getId3()+" "+(punto-Maintenance.expectedm(gmm.getNodes().get(k), punto))+" "+Maintenance.cost(gmm.getNodes().get(k),input,punto)+" "+si+" "+tiempoviajees+" "+model.getVarByName("s("+route.get(o)+")").get(GRB.DoubleAttr.X)+" "+graph.getNodes().get(h).getOpt()+" "+gmm.getNodes().get(k).getOptcost());
									}
								}
							}
						}
					}
					writer.close();
				}
				catch(FileNotFoundException e)
				{
					System.out.println("File Not Found");
					System.exit( 1 );
				}
				catch(IOException e)
				{
					System.out.println("something messed up");
					System.exit( 1 );
				}

			}				
		} catch (GRBException e1) {
			e1.printStackTrace();
			System.out.println("Algo paso con esta mierda");
		}
	}
	public ArrayList<Double> getS() {
		return s;
	}
	public void setS(ArrayList<Double> s) {
		this.s = s;
	}
	public double getCost() {
		return cost;
	}
	public void setCost(double cost) {
		this.cost = cost;
	}
}
