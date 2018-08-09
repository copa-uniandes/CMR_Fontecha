package benchmark;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import maintenancer.MGraph;
import maintenancer.Maintenance;
/**
 * This class executes the acueducto benchmark
 * @author 	/John Edgar Fontecha Garcia
 * 			/je.fontecha10@uniandes.edu.co
 * 			/Universidad de los Andes
 *			/Departamento de Ingeniería Industrial
 *			/Combined Maintenance and Routing Optimization for large scale problems
 */
public class BenchmarkExecution {
	static double CELLDISTANCE=0.170;
	static double VEL=11.89*24;// km/día
	static PrintWriter writer;
	/**
	 * Sites graph
	 */
	MGraph ggm;
	/**
	 * Sites Graph for benchmark
	 */
	BGraph bgmm;
	/**
	 * Horizon planning of the benchmark
	 */
	double Tbench;
	/**
	 * Horizon planning
	 */
	double T;
	/**
	 * Routes available
	 */
	int numRutas;
	/**
	 * A Becnhmark execution class is defined by the follow parameters. This is the constructor of the object.
	 * @param g is a MGraph
	 * @param Tbench horizon planning in the benchmark
	 * @param T horizon planning
	 * @param numrutas number of available routes
	 */
	public BenchmarkExecution(MGraph g, double Tbench, double T, int numrutas){
		this.ggm=g;
		this.Tbench=Tbench;
		this.T=T;
		this.numRutas=numrutas;
	}
	
	public void Execution(MGraph gmm,double TBench){
		BNode bnode;
		bgmm=new BGraph();
		for(int i=0;i<gmm.getNodes().size();i++){
			bnode=new BNode(gmm.getNodes().get(i),(1-gmm.getNodes().get(i).getD().cdf(TBench)),-1,0.0);
			bgmm.addNode(bnode);
		}
		
		BNode aux;
		for(int i=0;i<bgmm.getNodes().size();i++){
			aux=menor(bgmm,i);
			bgmm.removeNode(menorpos(bgmm,i));
			bgmm.addNode(i,aux);
		}
//			System.out.println(bgmm.getNodes().get(i).getRanking()+" "+bgmm.getNodes().get(i).getConfiability());
		
		
		int cantencadaruta=bgmm.getNodes().size()/numRutas;
		ArrayList<BNode> [] rutas = new ArrayList [numRutas];
		for(int i = 0; i<numRutas;i++){
			rutas[i]= new ArrayList<BNode>();
		}
		
		boolean entra=true;
		int dondevoy=0;
		while(entra){
			for(int i=0;i<numRutas;i++){
				if(dondevoy<bgmm.getNodes().size()){
					rutas[i].add(bgmm.getNodes().get(dondevoy));
					dondevoy++;
				} else{
					entra=false;
				}
			}
		}
		int diario=0;
		int nodoactual=0;
		int dia=0;
		double date=0.0;
		for(int i=0;i<numRutas;i++){
			diario=(int) Math.ceil(rutas[i].size()*1.0/(TBench-1));
			for (int j = 0; j < TBench; j++) {
				for (int k = j*(diario); k <(j+1)*(diario) && k<rutas[i].size() ; k++) {			
					if(k==j*(diario)){
						date=j+0.34;
						rutas[i].get(k).setDate(date);
						rutas[i].get(k).getNode().setOptcost(Maintenance.cost(rutas[i].get(k).getNode(), date));
					} else{
						date=rutas[i].get(k-1).getDate();
						date=date+Maintenance.service_time(rutas[i].get(k-1).getNode(), date);
						date=date+(Math.abs(rutas[i].get(k-1).getNode().getPosx()-rutas[i].get(k).getNode().getPosx())+Math.abs(rutas[i].get(k-1).getNode().getPosy()-rutas[i].get(k).getNode().getPosy()))*CELLDISTANCE/VEL;
						if(date>=j+0.5&&date<=j+0.54) {
							date=date-0.5;
							date=0.54+date;
							rutas[i].get(k).setDate(date);
							rutas[i].get(k).getNode().setOptcost(Maintenance.cost(rutas[i].get(k).getNode(), date));

						} else {							
							rutas[i].get(k).setDate(date);
							rutas[i].get(k).getNode().setOptcost(Maintenance.cost(rutas[i].get(k).getNode(), date));
						}
					}
				}
			}
		}
		
//		File file = new File("./data/"+"Benchmarck.dat");
//		try {
			File file = new File("./data/"+"Benchmarck.dat");
//			file.createNewFile();
//			writer = new PrintWriter(new FileWriter(file));
//			System.out.println("route"+" "+" node"+" "+"date"+" "+"cost"+" "+" "+"optimal cost"+" "+"optimal date" );
			for (int i = 0; i < rutas.length; i++) {
//				writer.println("Ruta "+i);
				for (int j = 0; j <rutas[i].size(); j++) {
//					writer.println("Nodo "+rutas[i].get(j).getNode().getId()+" Fecha "+rutas[i].get(j).getDate()+" Costo "+rutas[i].get(j).getNode().getOptcost()+" Maintenance optimal cost "+Maintenance.cost(rutas[i].get(j).getNode(), Maintenance.newton(rutas[i].get(j).getNode(),  1, T))+" "+Maintenance.newton(rutas[i].get(j).getNode(), 1, T));
//					System.out.println((i+1)+" "+rutas[i].get(j).getNode().getId()+" "+rutas[i].get(j).getDate()+" "+rutas[i].get(j).getNode().getOptcost()+" "+Maintenance.cost(rutas[i].get(j).getNode(), Maintenance.newton(rutas[i].get(j).getNode(),  1, T))+" "+Maintenance.newton(rutas[i].get(j).getNode(), 1, T));

				}
			}
			System.out.println("salio");
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}
	public static BNode menor(BGraph bgraph, int pos0){
		double menor=100;
		BNode node=null;
		for(int i=pos0;i<bgraph.getNodes().size();i++){
			if(bgraph.getNodes().get(i).getConfiability()<menor){
				menor=bgraph.getNodes().get(i).getConfiability();
				node = new BNode(bgraph.getNodes().get(i).getNode(),bgraph.getNodes().get(i).getConfiability(),bgraph.getNodes().get(i).getRanking(),0.0);
			}
		}	
		return node;
	}
	public static int menorpos(BGraph bgraph, int pos0){
		double menor=100;
		int pos=10000;
		for(int i=pos0;i<bgraph.getNodes().size();i++){
			if(bgraph.getNodes().get(i).getConfiability()<menor){
				menor=bgraph.getNodes().get(i).getConfiability();
				pos=i;
			}
		}	
		return pos;
	}
	/**
	 * This method returns the MGraph
	 * @return ggm MGraph 
	 */
	public MGraph getGgm() {
		return ggm;
	}
	/**
	 * This method sets the MGraph
	 * @param ggm MGraph 
	 */
	public void setGgm(MGraph ggm) {
		this.ggm = ggm;
	}
	/**
	 * This method returns the BGraph
	 * @return bggm BGraph 
	 */
	public BGraph getBgmm() {
		return bgmm;
	}
	/**
	 * This method sets the BGraph
	 * @param bggm BGraph 
	 */
	public void setBgmm(BGraph bgmm) {
		this.bgmm = bgmm;
	}
	/**
	 * This method returns the becnhmark horizon planning
	 * @return ggm MGraph 
	 */
	public double getTbench() {
		return Tbench;
	}
	/**
	 * This method sets the benchmark horizon planning
	 * @param tbench benchmark horizon planning 
	 */
	public void setTbench(double tbench) {
		Tbench = tbench;
	}
	/**
	 * This method returns the number of available routes
	 * @return numRutas number of available routes 
	 */
	public int getNumRutas() {
		return numRutas;
	}
	/**
	 * This method sets the number of available routes
	 * @param numRutas number of available routes 
	 */
	public void setNumRutas(int numRutas) {
		this.numRutas = numRutas;
	}
	/**
	 * This method returns the horizon planning
	 * @return T Horizon planning 
	 */
	public double getT() {
		return T;
	}
	/**
	 * This method sets the horizon planning
	 * @param T horizon planning 
	 */
	public void setT(double t) {
		T = t;
	}
	
}
