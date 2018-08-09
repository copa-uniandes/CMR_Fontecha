package EduyinModel;

import java.util.ArrayList;

import P1.FeasibleRoute;
import gurobi.GRB;
import gurobi.GRBEnv;
import gurobi.GRBException;
import gurobi.GRBLinExpr;
import gurobi.GRBModel;
import gurobi.GRBVar;

public class EduyinsMIP {

	public double bigM=10000;
	private ArrayList<Solucion> resultado=new ArrayList<Solucion>();
	private double optCost;
	public EduyinsMIP(RGraph grafo, int availablecrews, double horizon){

		GRBEnv ambiente;
		try {
			ambiente = new GRBEnv();
			ambiente.set(GRB.IntParam.OutputFlag, 0);
			GRBModel eduynsmodel = new GRBModel(ambiente);
			GRBVar []s;
			GRBVar [] z;
			GRBVar [][] x;

			//Declaración de conjuntos
			int N = grafo.getNodes().size()+1; //Porque replico el depot para ir al final
			int K = availablecrews;

			//Declaración de variables
			s = new GRBVar[N];
			z = new GRBVar[N];
			x = new GRBVar[N][N];

			//Restricciones de naturaleza de variables
			//Restricciones de s y z
			RNode node;
			for (int i = 1; i < N-1; i++) {
				for(int h=0;h<grafo.getNodes().size();h++){
					node=grafo.getNodes().get(h);
					if(i==node.getId2()){
						s[i]= eduynsmodel.addVar(node.getL(), node.getU(), 0, GRB.CONTINUOUS, "s" + i);
						z[i] = eduynsmodel.addVar(0, Double.POSITIVE_INFINITY, 1, GRB.CONTINUOUS, "z" + i);
					}
				}
			}
			s[0] = eduynsmodel.addVar(0, horizon, 0, GRB.CONTINUOUS, "s" + 0);
			s[N-1] = eduynsmodel.addVar(0, horizon, 0, GRB.CONTINUOUS, "s" + (N-1));

			//Restricciones de x
			for (int i =0; i < N-1; i++) {
				for(int j = 0; j < N-1; j++){
					x[i][j]=eduynsmodel.addVar(0, 1, 0, GRB.BINARY, "x" + i+","+j);
				}
			}			
			for(int i=1;i<N-1;i++){
				x[i][N-1]=eduynsmodel.addVar(0, 1, 0, GRB.BINARY, "x" + i+","+(N-1));
			}

			eduynsmodel.update();
			//Restricciones de tiempo de viaje
			Arc arco;
			for(int h=0;h<grafo.getArcs().size();h++){
				arco=grafo.getArcs().get(h);
				for(int i=0;i<N-1;i++){
					node=grafo.getNodes().get(i);
					for(int j=1;j<N-1;j++){
						if(arco.getTail()==i && arco.getHead()==j){
							GRBLinExpr time = new GRBLinExpr();
							time.addTerm(1, s[i]);
							time.addTerm(-1, s[j]);
							time.addTerm(bigM, x[i][j]);
							time.addConstant(arco.getDcost());
							time.addConstant(node.getExpectedservicetime());
							eduynsmodel.addConstr(time, GRB.LESS_EQUAL, bigM, "travel time"+i+","+j);
						}
					}
				}
			}

			for(int h=0;h<grafo.getArcs().size();h++){
				arco=grafo.getArcs().get(h);
				for(int i=1;i<N-1;i++){
					node=grafo.getNodes().get(i);
					for(int j=N-1;j<N;j++){
						if(arco.getTail()==0 && arco.getHead()==i){
							GRBLinExpr time = new GRBLinExpr();
							time.addTerm(1, s[i]);
							time.addTerm(-1, s[j]);
							time.addTerm(bigM, x[i][j]);
							time.addConstant(arco.getDcost());
							time.addConstant(node.getExpectedservicetime());
							eduynsmodel.addConstr(time, GRB.LESS_EQUAL, bigM, "travel time"+i+","+j);
						}
					}
				}
			}

			//Restricciones de salida del Deapot
			GRBLinExpr salida = new GRBLinExpr();
			for(int j=1;j<grafo.getNodes().size();j++){
				salida.addTerm(1, x[0][j]);
			}
			eduynsmodel.addConstr(salida,GRB.LESS_EQUAL,availablecrews,"Salida del Deapot");

			//restricción llegada al Deapot
			GRBLinExpr llegada = new GRBLinExpr();
			for(int i=1;i<grafo.getNodes().size();i++){
				llegada.addTerm(1, x[0][i]);
				llegada.addTerm(-1, x[i][N-1]);
			}
			eduynsmodel.addConstr(llegada,GRB.EQUAL,0,"Llegada del Deapot");


			//Restricciones de balance
			for(int i=1;i<grafo.getNodes().size();i++){
				GRBLinExpr balance = new GRBLinExpr();
				for(int j=1;j<grafo.getNodes().size();j++){
					balance.addTerm(1, x[i][j]);
					balance.addTerm(-1, x[j][i]);
				}
				balance.addTerm(1, x[i][N-1]);
				balance.addTerm(-1, x[0][i]);
				eduynsmodel.addConstr(balance,GRB.EQUAL,0,"balance "+i);
			}

			GRBLinExpr exp1, exp2;
			for(int i=1;i<N-1;i++){
				RNode v_i;
				int pieces;
				//			for (int i = 0; i < N; i++) {
				v_i = grafo.getNodes().get(i);
				pieces = v_i.getPieces().length;
				for (int o = 0; o < pieces; o++) {
					exp1 = new GRBLinExpr();
					exp1.addTerm(v_i.getPieces()[o][0], s[i]);
					exp1.addConstant(v_i.getPieces()[o][1]);
					eduynsmodel.addConstr(exp1, GRB.LESS_EQUAL, z[i], "pwa,"+i+","+o);
				}
			}

			//restricciones bounds s
			for(int i=1;i<N-1;i++){
				node=grafo.getNodes().get(i);
				exp1 = new GRBLinExpr();
				exp2 = new GRBLinExpr();
				for(int j=1;j<N-1;j++){
					exp1.addTerm(node.getL(), x[i][j]);
					exp2.addTerm(node.getU(), x[i][j]);
				}
				exp1.addTerm(node.getL(), x[i][N-1]);
				exp2.addTerm(node.getU(), x[i][N-1]);
				eduynsmodel.addConstr(exp1, GRB.LESS_EQUAL, s[i], "lowerbound s_"+i);
				eduynsmodel.addConstr(exp2, GRB.GREATER_EQUAL, s[i], "upper bound s_"+i);
			}

			eduynsmodel.update();
			eduynsmodel.write("Eduyin_"+".lp");
			eduynsmodel.optimize();

			if(eduynsmodel.get(GRB.IntAttr.Status)==GRB.OPTIMAL){
				optCost= eduynsmodel.get(GRB.DoubleAttr.ObjVal);
				System.out.println("Le pagamos");

				Solucion sol;
				RNode nodito;
				for(int i=1;i<N-1;i++){
					nodito=grafo.getNodes().get(i);
					sol=new Solucion(nodito.getId1(),nodito.getId2(), nodito.getOpt()-s[i].get(GRB.DoubleAttr.X), s[i].get(GRB.DoubleAttr.X),z[i].get(GRB.DoubleAttr.X));
					resultado.add(sol);
				}
			}else{
				System.out.println("Revisar");
			}
		} catch (GRBException e) {
			System.out.println("Algo paso");
			e.printStackTrace();
		}
	}
	public double getBigM() {
		return bigM;
	}
	public void setBigM(double bigM) {
		this.bigM = bigM;
	}
	public ArrayList<Solucion> getResultado() {
		return resultado;
	}
	public void setResultado(ArrayList<Solucion> resultado) {
		this.resultado = resultado;
	}
	public double getOptCost() {
		return optCost;
	}
	public void setOptCost(double optCost) {
		this.optCost = optCost;
	} 

}
