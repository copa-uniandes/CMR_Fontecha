package EduyinModel;

import java.util.ArrayList;

import com.sun.org.apache.xerces.internal.util.SynchronizedSymbolTable;

import EduyinModel.*;

public class EduyinsModel {
	private static double [] ns;
	private static double T=224;
	private static int npieces=20;
	private static int nouts=0;
	private static int crews =6;
	public static String ON="on"; //Switch for graph generation
	public static String OFF="off"; //Switch for graph generation
	private static String Eduyin2016=ON;

	public static void main(String[] args) {
		double time=System.currentTimeMillis();
		Reading2 c=new Reading2("./data/Eduyin_a.dat");
		setNs(c.getPos());
		MGraph gmm=c.getGraph();
		MNode node;
		RGraph gvrp=new RGraph();
		RgvrpExecution rgvrp;
		boolean objfunctiondisminution=true;
		double funcion=10000;
		int iteraciones=0;

		while(iteraciones<100){// && objfunctiondisminution){
			iteraciones++;
			rgvrp=new RgvrpExecution(gmm, T, ns, npieces, nouts);
			gvrp=rgvrp.Execution(gmm, T, ns, npieces, nouts);
			EduyinsMIP modelo =new EduyinsMIP(gvrp, crews, T);
			ArrayList<Solucion> sol=new ArrayList<Solucion>();
			sol=modelo.getResultado();	
			double suma=0;
			int contador=0;
			double waiting=0;
			double nuevox=0;
			for(int j=0;j<gmm.getNodes().size();j++){
				suma=0;
				contador=0;
				waiting=0;
				for(int i=0;i<sol.size();i++){
					if(gmm.getNodes().get(j).getId()==sol.get(i).getId1()){
						suma+=sol.get(i).getCostz();
						contador++;
						nuevox=Maintenance.force(gmm.getNodes().get(j), 1, T)+ sol.get(i).getDif();
						waiting+=nuevox-CostFunction.calcMdeltaLib(gmm.getNodes().get(j).getD(), 0, nuevox);
					}
				}
				waiting=waiting/contador;
				gmm.getNodes().get(j).setWaiting(waiting);
			}
			
			System.out.println("Iteración: "+iteraciones);
			for(int p=0;p<sol.size();p++){
				System.out.println(sol.get(p).getId1()+" "+sol.get(p).getId2()+" "+sol.get(p).getS()+" "+sol.get(p).getCostz());
			}
				
			System.out.println("Computational time: "+(System.currentTimeMillis()-time));
			time=System.currentTimeMillis();
			if(modelo.getOptCost()<funcion){
				System.out.println("Entra al if "+modelo.getOptCost()+" "+funcion);
				objfunctiondisminution=true;
				funcion=modelo.getOptCost();				
			} else{
				System.out.println("Entra al if "+modelo.getOptCost()+" "+funcion);
				System.out.println("Entra al else");
				objfunctiondisminution=false;
			}
		}

	}


	public static void EduyinsModel(){

	}

	public static double[] getNs() {
		return ns;
	}

	public static void setNs(double[] ns) {
		EduyinsModel.ns = ns;
	}

}
