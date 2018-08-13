package P1;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import maintenancer.MNode;
import maintenancer.Maintenance;
import maintenancer.Route;
import maintenancer.Tres;
import gurobi.*;
import gurobi.GRB.DoubleAttr;
/**
 * This class
 * @author 	/Daniel Duque
 * 			/d.duque25@uniandes.edu.co
 * 			/Universidad de los Andes
 *			/Departamento de Ingeniería Industrial
 *			/Combined Maintenance and Routing Optimization for large scale problems
 */
public class SetCovering 
{
	public DataHandler data;
	public ArrayList<FeasibleRoute> soluciones;
	static PrintWriter writer;
	public GRBEnv env;
	public GRBModel modelo1;
	public GRBModel modelo2;
	public double FO1;
	public double[] xSolBestCost; //Modelo 1
	public GRBVar [] x ;
	public GRBVar [] xModel2;
	private ArrayList<Route> rutascompletasTres=new ArrayList<Route>();
	private int rutasdisponibles; 

	public SetCovering (String Nombre,DataHandler nData,  ArrayList<FeasibleRoute> nSoluciones, int numerorutas)
	{
		data = nData;
		setSoluciones(nSoluciones);
		rutasdisponibles=numerorutas;
	}

	public void initializeMP() throws GRBException
	{

		for(int i=0;i<soluciones.size();i++){
			//			System.out.println("Ruta "+i);
			for(int j=0;j<soluciones.get(i).getPath().size();j++){
				//				System.out.println(soluciones.get(i).getPath().get(j));
			}
		}

		//		System.out.println("LOADING MODEL 1");
		env = new GRBEnv(null);
		env.set(GRB.IntParam.OutputFlag, 1);
		modelo1 = new GRBModel(env);
		x =new GRBVar [soluciones.size()];

		modelo1.update();


		//restricciones
		//recorrer primero las filas

		//		System.out.println("el tamaño de x: "+x.length);
		for(int i=0;i<soluciones.size();i++){
			x[i] = modelo1.addVar(0, 1, soluciones.get(i).getRoutecost(), GRB.BINARY, "x["+i+"]");
		}
		modelo1.update();
		//		modelo1.write("Probando esta vaina.lp");		

		GRBLinExpr crews = new GRBLinExpr();
		for(int i=0;i<soluciones.size();i++){
			crews.addTerm(1, x[i]);
		}
		modelo1.addConstr(crews, GRB.LESS_EQUAL, rutasdisponibles, "Crew");
		//modelo1.getConstrByName("Crew").get(GRB.DoubleAttr.Slack);
		modelo1.update();

		for (int j = 1; j < data.n; j++){
			GRBLinExpr ladoIzq = new GRBLinExpr();
			String res = "Restricciòn " + j+ ":  ";
			//barrer columnas(ruticas)
			for(int i=0;i<soluciones.size();i++){
				ArrayList<Integer>ruta=soluciones.get(i).getPath();// arraylist que captura los nodos de una rutica
				for (int k = 0; k < ruta.size(); k++) 
				{
					if (j== ruta.get(k)) 
					{
						res += "+x["+i+"]";//.get(GRB.StringAttr.VarName);
						//						System.out.println("Entro");
						ladoIzq.addTerm(1, x[i]);	
						//						k =1000000;//No hay una forma más eficiente de romper el ciclo cómo un break? intenteré break ahorita
					}
				}
			}	
			//			System.out.println(res);
			modelo1.addConstr(ladoIzq, GRB.EQUAL, 1, "restriccion_nodo"+j);
			modelo1.update();
			modelo1.write("Probando esta vaina2.lp");	
		}
	}

	public void solveSetCovering (String Nombre) throws GRBException
	{


		System.out.println("Optimizing MODEL 1");
		modelo1.set(GRB.IntAttr.ModelSense, GRB.MINIMIZE);
		modelo1.write("Setcovering.lp");
		modelo1.optimize();


		String rutaF = "./data/";
		File directorioFacturas = new File(rutaF);
		if(!directorioFacturas.exists())
			directorioFacturas.mkdirs();
		File file = new File(rutaF+Nombre+".dat");
		System.out.println(rutaF+Nombre+".dat");
		try {
			writer = new PrintWriter(new FileWriter(file,true));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if(modelo1.get(GRB.IntAttr.Status)==GRB.OPTIMAL){
			Route rutainfoTres;
			ArrayList<Tres> rutica=new ArrayList<Tres>();
			int id1=0;
			System.out.println("Aquí imprimo soluciones");
			System.out.println("Numero de crew disponibles:"+modelo1.getConstrByName("Crew").get(GRB.DoubleAttr.Slack));
			for(int i=0;i<soluciones.size();i++){
				if(x[i].get(GRB.DoubleAttr.X)==1){
					System.out.println("i: "+i+"cost: "+soluciones.get(i).getRoutecost());
					Route solution=new Route();
					System.out.println("id1"+"\t"+"id3"+" \t "+"s"+" \t "+"delta*"+" \t "+"gap"+" "+"real_cost"+" "+"gap_cycletime");
					writer.println("id1"+"\t"+"id3"+" \t "+"s"+" \t "+"delta*"+" \t "+"gap"+" "+"real_cost"+" "+"gap_cycletime");
					for(int k =0;k<soluciones.get(i).getPath().size();k++){
						for(int l=0;l<data.getGraph().getNodes().size();l++){
							if(soluciones.get(i).getPath().get(k)==data.getGraph().getNodes().get(l).getId3()){
								id1=data.getGraph().getNodes().get(l).getId1();
							}
						}
						Tres nuevoTres=new Tres(id1,soluciones.get(i).getPath().get(k),soluciones.get(i).getS().get(k));
						//						System.out.println("Estoy en setcovering: "+id1+" "+soluciones.get(i).getPath().get(k)+" "+soluciones.get(i).getS().get(k));
						solution.addTres(nuevoTres);
						double punto=0;
						MNode nodobuscado = null;
						double gap=0;
						double gap1=0;
						for(int h=0; h<data.getMGraph().getNodes().size();h++){
							if(data.getMGraph().getNodes().get(h).getId()==id1){//+data.getMGraph().getNodes().get(h).getOpt()+      -(data.getGraph().getNodes().get(soluciones.get(i).getPath().get(k)).getOpt())
								nodobuscado=data.getMGraph().getNodes().get(h);
								gap=Math.abs(soluciones.get(i).getS().get(k)-(data.getGraph().getNodes().get(soluciones.get(i).getPath().get(k)).getOpt()))/data.getGraph().getNodes().get(soluciones.get(i).getPath().get(k)).getOpt();
								gap1=Math.abs(soluciones.get(i).getS().get(k)-(data.getGraph().getNodes().get(soluciones.get(i).getPath().get(k)).getOpt()))/data.getGraph().getNodes().get(soluciones.get(i).getPath().get(k)).getCycletime();
								
								punto=data.getMGraph().getNodes().get(h).getOpt()+(soluciones.get(i).getS().get(k)-(data.getGraph().getNodes().get(soluciones.get(i).getPath().get(k)).getOpt()));
								if(punto<=0) {
									System.out.println("Juemadre!!!!!!!!!!!!!!!!!!!!!!!!!!"+punto+" "+data.getMGraph().getNodes().get(h).getOpt()+" "+soluciones.get(i).getS().get(k)+" "+data.getGraph().getNodes().get(soluciones.get(i).getPath().get(k)).getOpt());
								}
								break;
							}
						}

						//						System.out.println("Este es el punto: "+punto);
						if(id1==0){
							System.out.println(id1+" "+soluciones.get(i).getPath().get(k)+" "+soluciones.get(i).getS().get(k)+" "+0+" "+0+" "+0);
							writer.println(id1+" "+soluciones.get(i).getPath().get(k)+" "+soluciones.get(i).getS().get(k)+" "+0+" "+0+" "+0);


						}else{
							double mycosto=0;
							if(punto>0) {
								mycosto=Maintenance.cost(nodobuscado, punto);
							} else {
								mycosto=200;
							}
						
							
							System.out.println(id1+" "+soluciones.get(i).getPath().get(k)+" "+soluciones.get(i).getS().get(k)+" "+data.getGraph().getNodes().get(soluciones.get(i).getPath().get(k)).getOpt()+" "+gap+" "+mycosto+" "+gap1);//+" "+Maintenance.cost(nodobuscado, punto));
							writer.println(id1+" "+soluciones.get(i).getPath().get(k)+" "+soluciones.get(i).getS().get(k)+" "+data.getGraph().getNodes().get(soluciones.get(i).getPath().get(k)).getOpt()+" "+gap+" "+mycosto+" "+gap1);
						}
					}
					rutascompletasTres.add(solution);
				}
			}	
		}else{
			//	System.out.println("Se hizo infactible");
			//	routeFeasibility= false;
			//	routeCost = Double.POSITIVE_INFINITY;
		}
		writer.println("Routing_cost: "+modelo1.get(GRB.DoubleAttr.ObjVal));
		writer.close();
		int probStatus = modelo1.get(GRB.IntAttr.Status);
		FO1= 0;
		if(probStatus!= GRB.INFEASIBLE)
		{

		}
		else{
			System.out.println("infeasible");
		}

	}

	public void excecute(String Nombre) throws GRBException 
	{
		initializeMP();
		solveSetCovering(Nombre);
	}


	public class myCB extends GRBCallback{

		private double   lastnode;
		private double   lastiter;

		public myCB() throws GRBException {

			lastnode = lastiter = -1;


		}

		boolean agregado= false; 

		@Override
		protected void callback() {
			double nodecnt;
			try {
				//				GRB.CB
				if (where == GRB.CB_MIPNODE) {
					//						System.out.println("ESTO PAS?");
					//						nodecnt = getDoubleInfo(GRB.CB_MIP_NODCNT);
					if (!agregado && xSolBestCost!=null) {
						System.out.println("Cargo la solucion  "+ GRB.CB_MIPNODE_OBJBND);
						setSolution(xModel2, xSolBestCost);
						modelo2.update();
						agregado= true;
					}
					double objbst = getDoubleInfo(GRB.CB_MIPNODE_OBJBST);
					//	System.out.println(objbst);
				}

				else if (where == GRB.CB_MIP) {

					nodecnt = getDoubleInfo(GRB.CB_MIP_NODCNT);


					//	if (nodecnt - lastnode >= 100) {
					double objbst = getDoubleInfo(GRB.CB_MIP_OBJBST);
					double objbnd = getDoubleInfo(GRB.CB_MIP_OBJBND);
					//						
					int solcnt = getIntInfo(GRB.CB_MIP_SOLCNT);

					//						System.out.println(nodecnt+" " + " "
					//								+ objbst + " " + objbnd + " " + solcnt + " ");

					//	}
				}

			} catch (GRBException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}


	public ArrayList<FeasibleRoute> getSoluciones() {
		return soluciones;
	}
	public void setSoluciones(ArrayList<FeasibleRoute> soluciones) {
		this.soluciones = soluciones;
	}

	public ArrayList<Route> getRutascompletasTres() {
		return rutascompletasTres;
	}

	public void setRutascompletasTres(ArrayList<Route> rutascompletasTres) {
		this.rutascompletasTres = rutascompletasTres;
	}

}
