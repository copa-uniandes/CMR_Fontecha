package EduyinModel;

import java.util.ArrayList;
import drawer.Drawing;

/**
 * This class solve Maintanance Model (MM) for all nodes. From this class we have the routing graph
 * @author 	/John Edgar Fontecha Garcia para Modelo Eduyin
 * 			/je.fontecha10@uniandes.edu.co
 * 			/Universidad de los Andes
 *			/Departamento de Ingeniería Industrial
 *			/Combined Maintenance and Routing Optimization for large scale problems
 */
public class MM {
	static double CELLDISTANCE=0.170; //Length of one block
	static double VEL=11.89*24; //Average speed
	/**
	 * Routing graph
	 */
	RGraph gvrp=new RGraph(); //Routing graph 
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
		RNode inode;
		RNode jnode;
		Arc rarco;
		ArrayList<MNode> nodes= nd.getNodes();
		ArrayList<MArc> arcos=nd.getArcs();
 		Maintenance c;
		Arc arc;
		double opt;
		double l;
		double u;
		double [][] pieces;
		int cont=0;
		double [][]piece=new double[npiece][2];
		for(int j=0;j<npiece;j++){
			piece[j][0]=0;
			piece[j][1]=0;
		}
	
		gvrp.addNode(new RNode(0,cont,-1,0,posX,posY,0,0,0,0,horizon,piece));
		cont++;
		
		for(int i=0;i<nodes.size();i++){
			c= new Maintenance(nodes.get(i),horizon,npiece,nouts);
			nodes.get(i).setOptcost(Maintenance.cost(nodes.get(i), c.getOpt()));
			for(int j=0; j<c.getNum();j++){
				l=c.getL()+j*c.getTimecycle();
				opt=c.getOpt()+j*c.getTimecycle();
				u=c.getU()+j*c.getTimecycle();
				pieces=new double [npiece][2];
				for(int n=0;n<npiece;n++){
					pieces[n][0]=c.getPieces()[n][0];
					pieces[n][1]=c.getPieces()[n][1]-c.getPieces()[n][0]*j*c.getTimecycle();
				}		
				rnode=new RNode(nodes.get(i).getId(),cont,-1,0,nodes.get(i).getPosx(),nodes.get(i).getPosy(),nodes.get(i).getWaiting(),c.getExpectedservicetime(),opt,l,u,pieces);
				gvrp.addNode(rnode);
				cont++;
			}
		}
		cont=0;
		for(int k=0;k<arcos.size();k++){
			for(int i=0; i<gvrp.getNodes().size();i++){
				inode=gvrp.getNodes().get(i);
				for(int j=0;j<gvrp.getNodes().size();j++){
					jnode=gvrp.getNodes().get(j);
					if(inode.getId1()==arcos.get(k).getTail() && jnode.getId1()==arcos.get(k).getHead()){
						rarco=new Arc(inode.getId2(),jnode.getId2(),arcos.get(k).getTravelTime(),0);	
						gvrp.addArc(rarco);
					} else if(jnode.getId1()==arcos.get(k).getTail() && inode.getId1()==arcos.get(k).getHead()){
						rarco=new Arc(inode.getId2(),jnode.getId2(),arcos.get(k).getTravelTime(),0);
						gvrp.addArc(rarco);
					}
				}
			}
		}
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