package maintenancer;

import java.util.ArrayList;

import drawer.Drawing;
import router.RArc;
import router.RGraph;
import router.RNode;
/**
 * This class solve Maintanance Model (MM) for all nodes. From this class we have the routing graph
 * @author 	/John Edgar Fontecha Garcia
 * 			/je.fontecha10@uniandes.edu.co
 * 			/Universidad de los Andes
 *			/Departamento de Ingeniería Industrial
 *			/Combined Maintenance and Routing Optimization for large scale problems
 */
public class MM {
	private static final double PENALTY_BACK_TIME = 10000;
	/**
	 * Length of one block
	 */
	static double CELLDISTANCE=0.170;
	/**
	 * Average speed
	 */
	static double VEL=11.89*24; 
	/**
	 * Routing graph
	 */
	RGraph gvrp=new RGraph(); 
	/**
	 * Expected cycle time
	 */
	private double timecycle; //Cycle time
	/**
	 * MM is the maintenance model applied to each node in order to create the maintenance graph (i.e., routing graph)
	 * @param nd is a MGraph, is the graph of sites with the information of each site.
	 * @param horizon is the planing horizon
	 * @param posX is the posx of the depot
	 * @param posY is the posy of the depot
	 * @param npieces is the number of piecewise approximation lines
	 * @param nouts is the number of piecewise approximation lines out from time windows
	 */	
	public  MM(MGraph nd,double horizon,double posX, double posY, int npiece, int nouts) {
		RNode rnode;
		ArrayList<MNode> nodes= nd.getNodes();
		Maintenance c;
		RArc arc;
		double opt;
		double l;
		double u;
		double [][] pieces;
		int cont=1;
//		double [][]piece=new double[npiece][2];
//		for(int j=0;j<npiece;j++){
//			piece[j][0]=0;
//			piece[j][1]=0;
//			
//		}
//		System.out.println("para savongs");
//		System.out.println("cuantos nodos: "+nodes.size());
//		System.out.println("Costo optimo de cada operacion");
		for(int i=0;i<nodes.size();i++){
			c= new Maintenance(nodes.get(i),horizon,npiece,nouts);
			nodes.get(i).setOptcost(Maintenance.cost(nodes.get(i), c.getOpt()));
			System.out.println(nodes.get(i).getId()+" "+c.getOpt()+" "+nodes.get(i).getOptcost());
			nodes.get(i).setOpt(c.getOpt());
			for(int j=0; j<c.getNum();j++){
				l=c.getL()+j*c.getTimecycle();
				opt=c.getOpt()+j*c.getTimecycle();
				u=c.getU()+j*c.getTimecycle();
				pieces=new double [npiece][2];
				for(int n=0;n<npiece;n++){
					pieces[n][0]=c.getPieces()[n][0];
					pieces[n][1]=c.getPieces()[n][1]-c.getPieces()[n][0]*j*c.getTimecycle();
				}		
				rnode=new RNode(nodes.get(i).getId(),cont,-1,0,nodes.get(i).getPosx(),nodes.get(i).getPosy(),nodes.get(i).getWaiting(),c.getExpectedservicetime(),opt,c.getOpt(),l,u,c.getL(),c.getU(),pieces,c.getPieces(),nodes.get(i).getCcm(),nodes.get(i).getCpm(),nodes.get(i).getCw(),nodes.get(i).getTcm(),nodes.get(i).getTpm(),c.getTimecycle());//
//				System.out.println(rnode.getId1()+" "+rnode.getId2()+" "+rnode.getOpt()+" "+nodes.get(i).getOptcost());
				gvrp.addNode(rnode);
//				System.out.println("tamaño: "+gvrp.getNodes().size());
				cont++;
			}
		}
		
//		ESTO SIRVE PARA AGREGAR ARCOS EN EL MAINTENANCE OPERATIONS GRAPH
//		cont=0;
//		int cuentasuma=0;
//		double distance;
//		double timedistance;
//		double penalization;
//		System.out.println("i_id1"+" "+"j_id1"+" "+"i_id2"+" "+"j_id2"+" "+"t_(ij)");
//		for(int i=0; i<gvrp.getNodes().size();i++){
//			gvrp.getNodes().get(i).setPosFS(cont);
//			for(int j=0; j<gvrp.getNodes().size();j++){
//				for(int k=0;k<nd.getArcs().size();k++){
//					if(i!=j && (gvrp.getNodes().get(i).getId1()==nd.getArcs().get(k).getHead() && gvrp.getNodes().get(j).getId1()==nd.getArcs().get(k).getTail())){
//						cuentasuma++;
//						distance=nd.getArcs().get(k).getTravelTime()*VEL;
//						if(gvrp.getNodes().get(j).getOpt()<gvrp.getNodes().get(i).getOpt()){
//							penalization=PENALTY_BACK_TIME;
//						}else{
//							penalization=0;
//						}					
//						timedistance=distance+Math.abs(gvrp.getNodes().get(i).getOpt()-gvrp.getNodes().get(j).getOpt())*VEL+penalization;
//						arc=new RArc(gvrp.getNodes().get(i).getId2(),gvrp.getNodes().get(j).getId2(),distance,timedistance); //hice cambio aca						
//						System.out.println(gvrp.getNodes().get(i).getId1()+" "+gvrp.getNodes().get(j).getId1()+" "+gvrp.getNodes().get(i).getId2()+" "+gvrp.getNodes().get(j).getId2()+" "+timedistance);
//						gvrp.addRArc(arc);
//					}
//				}
//			}
//		}
		
		gvrp.setTmin(0);
		gvrp.setTmax(horizon);
	}		
	/**
	 * This method returns the routing graph
	 * @return gvrp
	 */
	public RGraph getGvrp() {
		return gvrp;
	}
	/**
	 * This method returns the expected cycle time
	 * @return
	 */
	public double getTimecycle() {
		return timecycle;
	}
	/**
	 * This method sets the routing graph
	 * @param gvrp
	 */
	public void setGvrp(RGraph gvrp) {
		this.gvrp = gvrp;
	}
	/**
	 * This method returns the expected cycle time
	 * @param timecycle
	 */
	public void setTimecycle(double timecycle) {
		this.timecycle = timecycle;
	}
}