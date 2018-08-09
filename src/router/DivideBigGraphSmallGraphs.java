package router;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import split.Instancer;
import maintenancer.MGraph;
import maintenancer.Maintenance;
/**
 * This class is used to divide the big Graph in small graphs the basic information of a node
 * @author 	/John Edgar Fontecha Garcia
 * 			/je.fontecha10@uniandes.edu.co
 * 			/Universidad de los Andes
 *			/Departamento de Ingeniería Industrial
 *			/Combined Maintenance and Routing Optimization for large scale problems
 */
public class DivideBigGraphSmallGraphs {
	/**
	 * Array of small routing graphs
	 */
	ArrayList<RGraph> Splitgraphs=new ArrayList<RGraph>();
	/** 
	 * This instance is used to print the information for MSH
	 */
	Instancer instancia;
	/**
	 * This is the folder name for instance
	 */
	private static String outputDir="CMR";
	/**
	 * This is the name of the instance for MSH 
	 */
	private static String instanceName="Zona2";
	static double CELLDISTANCE=0.170;
	static double VEL=11.89*24;// km/día
	/**
	 * @param gmm Sites Graph
	 * @param gvrp Original routing graph
	 * @param ns Depot coordinates
	 * @param T1 planing horizon smaller than planning horizon  
	 * @param T planing horizon
	 * @param npieces number of piecewise lines
	 * @param nouts	number of piecewise lines out of time windows
	 * @param numgraphs number of graphs
	 * @param maxT Is the same that T1?
	 */
	public DivideBigGraphSmallGraphs(MGraph gmm, RGraph gvrp, double []ns, int T1, double T, int npieces, int nouts, int numgraphs, double maxT, String sinmip, double free_cap){

	}
	
	public ArrayList<RGraph> getSmallGraphs(MGraph gmm, RGraph gvrp, double []ns, int T1, double T, int npieces, int nouts, int numgraphs, double maxT,String sinmip, double free_cap){
		RNode rnode;
		RArc arc;
		RGraph splitgraph;
		double [][]piece=new double[npieces][2];
		for(int j=0;j<npieces;j++){
			piece[j][0]=0;
			piece[j][1]=0;
		}
		/**
		 * AQUI ESTOY DIVIDIENDO EN GRAPHNUM GRAFOS
		 */
		Maintenance cc;
		double free_capacity=free_cap*maxT;
		for(int i=0;i<numgraphs;i++){
			splitgraph=new RGraph();
			for(int j=0;j<gvrp.getNodes().size();j++){
				if(gvrp.getNodes().get(j).getOpt()>=maxT*i && gvrp.getNodes().get(j).getOpt()<maxT*(i+1)){
					splitgraph.addNode(gvrp.getNodes().get(j));
					if(gvrp.getNodes().get(j).getId1()==11||gvrp.getNodes().get(j).getId1()==23||gvrp.getNodes().get(j).getId1()==15||gvrp.getNodes().get(j).getId1()==20){
						System.out.println("id1: "+gvrp.getNodes().get(j).getId1()+" smallgraph: "+i+" hora: "+gvrp.getNodes().get(j).getOpt());
					}
					splitgraph.setTmin(maxT*i);
					splitgraph.setTmax(maxT*(i+1)-free_capacity);
				}
			}
			if(splitgraph.getNodes().size()>0){
				Splitgraphs.add(splitgraph);
				try {
					instancia=new Instancer(outputDir,instanceName,i);
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		/**
		 * AQUI SETEO EL ID3 Y AGREGO EL DEPOT PARA LA IMPRESIÓN, TAMBIEN GENERO LOS ARCOS POSIBLES DEL RGRAPH
		 */
		int cont;
		for(int i=0;i<Splitgraphs.size();i++){
			splitgraph=Splitgraphs.get(i);
			cont=1;
			for(int j=0;j<splitgraph.getNodes().size();j++){
				splitgraph.getNodes().get(j).setId3(cont);
				cont++;
			}
//			if(splitgraph.getNodes().size()==2)
//				for(int j=0;j<splitgraph.getNodes().size();j++)
//					for(int k=0;k<gmm.getNodes().size();k++)
//						if(gmm.getNodes().get(k).getId()==splitgraph.getNodes().get(j).getId1()){
//							cc= new Maintenance(gmm.getNodes().get(k),T,npieces,nouts);
//						}
			if (sinmip=="on"){
				cont=0;
				rnode=new RNode(0,0,cont,0,ns[0],ns[1],0,0,0,0,0,0,0,T1,piece,piece,0,0,0,0,0,0); //el primero es 0 es decir el DEPOT porque no existe dicho nodo
				splitgraph.addFirst(rnode);
			}else{
				cont=0;
				rnode=new RNode(0,0,cont,0,ns[0],ns[1],0,0,0,0,0,0,0,T1,piece,piece,0,0,0,0,0,0); //el primero es 0 es decir el DEPOT porque no existe dicho nodo
				splitgraph.addFirst(rnode);
			}
					}
		return Splitgraphs;
	}	
}
