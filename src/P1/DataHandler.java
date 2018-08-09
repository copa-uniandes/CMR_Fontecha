package P1;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.StringTokenizer;
import maintenancer.MArc;
import maintenancer.MGraph;
import router.RGraph;
import router.RNode;
import solution.MainCMR;
/**
 * This class .....
 * @author 	/Daniel Duque
 * 			/d.duque25@uniandes.edu.co
 * 			/Universidad de los Andes
 *			/Departamento de Ingeniería Industrial
 *			/Combined Maintenance and Routing Optimization for large scale problems
 */
public class DataHandler{
	/**
	 * This penalty is used for the distances where the arc goes "back in time". 
	 */
	private static final double PENALTY_BACK_TIME = 10000;
	private static final double PENALTY_BACK_TIME_EDUYIN=10000;
	public double[] cordX;
	public double[] cordY;
	public double[] demand;
	public double[] twA;
	public double[] twB;
	public double[] service;
	public double vehCapacity;
	public MGraph mmg;
	/**
	 * These distances have a penalty.
	 */
	public double[][]distances;
	/**
	 * Real travel times, with no penalty.
	 */
	public double[][]times;
	/**
	 * Savings for CW heuristic.
	 */
	public double[][]savings;
	public int n;
	public Random aleatorio;
	public double sacrificio;
	private RGraph g;
	private double Tmin;
	private double Tmax;
	private String MIPShifts;
	private String Eduyindata;
	private double[] L = {0.33, 0.54};
	private double[] U = {0.5, 0.71};
	/**
	 * Number of days of the planning horizon.
	 */
	private double numDays;
	/**
	 * Number of shifts per day.
	 */
	private int numShifts;

	public DataHandler(MGraph mmgr, RGraph rGraph, double tmin, double tmax,String MIPShift,String eduindata){
		g=rGraph;
		Tmax = tmax;
		Tmin = tmin;
		numDays = tmax-tmin;
		numShifts = 2;
		MIPShifts=MIPShift;
		Eduyindata=eduindata;
		mmg=mmgr;
		readFile();
		setDistances();
		setAhorros();
		vehCapacity = 200;
		aleatorio= new Random(13);
		sacrificio=0.2;    //OJOOOOO cambiar , JEF: cambiar por qué? para qué es este sacrificio?
	}

	private void readFile(){
		n=g.getNodes().size();
		//		System.out.println("Tamaño de RGRaph: "+n+" Tamaño de mmgraph: "+(mmg.getNodes().size()));
		cordX = new double[n];
		cordY = new double[n];
		demand = new double[n];
		twA = new double[n];
		twB = new double[n];
		service = new double[n];
		ArrayList<RNode> splitNodes = g.getNodes();
		RNode n;
//		System.out.println("-------------***---");
		for (int i = 0; i < splitNodes.size(); i++) {
			n = splitNodes.get(i);

//			System.out.println(n.getId1()+" "+n.getId2()+" "+n.getId3());

			int numNode = n.getId3(); //numNode=i en mi caso por la forma en que organice el grafo es igual
			cordX[numNode]=n.getPosx();
			cordY[numNode]=n.getPosy();
			demand[numNode]=0;
			twA[numNode]=0;
			twB[numNode]=MainCMR.T;
			service[numNode]=n.getExpectedservicetime();
			//			System.out.println("Expected service time: "+n.getExpectedservicetime());
		}
//		System.out.println("---------***-------");
	}

	public void setDistances(){
		ArrayList<MArc> arcos=new ArrayList<MArc>();
		arcos=mmg.getArcs();
		int ntail;
		int nhead;
		int gtail;
		int ghead;
		distances = new double [n][n];
		times = new double[n][n];
		//		System.out.println("distancias");
		for (int i = 0; i < n; i++){
			for (int j = 0; j < n; j++){
				if(Eduyindata=="on"){
					distances[i][j]=0;
					if(i!=j){
						distances[i][j]=1;
					}
					for(int k=0;k<arcos.size();k++){
						ntail=arcos.get(k).getTail();
						nhead=arcos.get(k).getHead();
						gtail=g.getNodes().get(i).getId1();
						ghead=g.getNodes().get(j).getId1();
						if(ntail==gtail && nhead==ghead){
							distances[i][j]=distances[i][j]+arcos.get(k).getTravelTime();
							times[i][j]=arcos.get(k).getTravelTime();
							//							System.out.println(i+" "+j+" "+times[i][j]);
							if(g.getNodes().get(i).getOpt() > g.getNodes().get(j).getOpt()) {
								distances[i][j] = (distances[i][j] +g.getNodes().get(i).getOpt()- g.getNodes().get(j).getOpt() + PENALTY_BACK_TIME_EDUYIN)*10;
								//								System.out.println(g.getNodes().get(i).getId1()+" "+g.getNodes().get(j).getId1()+" "+i+" "+j+" "+distances[i][j]);
							} else {
								distances[i][j] = (distances[i][j] +g.getNodes().get(j).getOpt() - g.getNodes().get(i).getOpt())*10;
								//								System.out.println(g.getNodes().get(i).getId1()+" "+g.getNodes().get(j).getId1()+" "+i+" "+j+" "+distances[i][j]);
							}
						}
					}
				}else{
					//Real distance
					distances[i][j]=Math.sqrt(Math.pow(cordX[i]-cordX[j], 2)+Math.pow(cordY[j]-cordY[i], 2))*MainCMR.CELLDISTANCE;
					//Real time
					times[i][j] = (distances[i][j]/MainCMR.VEL);					
					//Penalties for arcs that goes "back in time" 
					if (g.getNodes().get(i).getOpt() > g.getNodes().get(j).getOpt()) {
						distances[i][j] += (g.getNodes().get(i).getOpt()- g.getNodes().get(j).getOpt() + PENALTY_BACK_TIME) * MainCMR.VEL;
					} else {
						distances[i][j] += (g.getNodes().get(j).getOpt() - g.getNodes().get(i).getOpt())* MainCMR.VEL;
					}	
//					System.out.println("Entro a la verdadera: "+ i+" "+" "+j+" "+" "+g.getNodes().get(i).getId1()+" "+g.getNodes().get(j).getId1()+" "+distances[i][j]+" "+times[i][j]);
				}
			}
		}
	}
	public void setAhorros(){
		savings = new double [n][n];
		for (int i = 0; i < n; i++){
			for (int j = 0; j < n; j++){
				if (i==j || i==0 || j==0){
					savings[i][j]=0;
					
				}else{
					savings[i][j]= distances[0][i]+distances[j][0]-distances[i][j];	
					if (savings[i][j]<0){
						savings[i][j]=10/Math.abs(distances[0][i]+distances[j][0]-distances[i][j]);
					}
				}
			}
		}
		//		System.out.println("hola mundo: ");
	}
	public RGraph getGraph(){
		return g;
	}
	public MGraph getMGraph(){
		return mmg;
	}
	public double getTmin(){
		return Tmin;
	}
	public double getTmax(){
		return Tmax;
	}
	public double getNumDays(){
		return numDays;
	}
	public int getNumShifts(){
		return numShifts;
	}
	/**
	 * @return the upper bounds of the shifts
	 */
	public double[] getU(){
		return U;
	}
	/**
	 * @param u the u to set the upper bounds of the shifts
	 */
	public void setU(double[] u){
		U = u;
	}
	/**
	 * @return the lower bounds of the shifts
	 */
	public double[] getL(){
		return L;
	}
	/**
	 * @param l the l to set the lower bounds of the shifts
	 */
	public void setL(double[] l){
		L = l;
	}

	public String getMIPShifts(){
		return MIPShifts;
	}

	public void setMIPShifts(String eduyin){
		MIPShifts = eduyin;
	}

	public String getEduyindata() {
		return Eduyindata;
	}

	public void setEduyindata(String eduyindata) {
		Eduyindata = eduyindata;
	}
}

