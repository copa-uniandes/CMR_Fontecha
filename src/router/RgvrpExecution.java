package router;
import maintenancer.MGraph;
import maintenancer.MM;
/**
 * This class is used in order to define the basic information of a node
 * @author 	/John Edgar Fontecha Garcia
 * 			/je.fontecha10@uniandes.edu.co
 * 			/Universidad de los Andes
 *			/Departamento de Ingeniería Industrial
 *			/Combined Maintenance and Routing Optimization for large scale problems
 */
public class RgvrpExecution {
	/**
	 * Sites graph
	 */
	MGraph gmm;
	/**
	 * Horizon planning
	 */
	double T;
	/** 
	 * Depot coordinates
	 */
	double [] ns;
	/**
	 * Number of piecewise lines
	 */
	int npieces;
	/**
	 * Number of piecewise lines out of time window
	 */
	int nouts;
	/**
	 * Routing graph
	 */
	RGraph gvrp;
	/**
	 * This is the constructor of RgvrpExecution Class. This class is defined by the following parameters
	 * @param gmm Sites Graph
	 * @param T Horizon planning
	 * @param ns Depot coordinates
	 * @param npieces number of piecewise lines
	 * @param nouts number of piecewise lines out of the time window
	 */
	public RgvrpExecution(MGraph gmm,double T,double[] ns,int npieces,int nouts){
		this.gmm=gmm;
		this.T=T;
		this.ns=ns;
		this.npieces=npieces;
		this.nouts=nouts;
	}
	/**
	 * This method calculates the routing graph from sites graph
	 * @param gmm Sites Graph
	 * @param T Horizon planning
	 * @param ns Depot coordinates
	 * @param npieces number of piecewise lines
	 * @param nouts number of piecewise lines out of the time window
	 */
	public RGraph Execution(MGraph gmm,double T,double[] ns,int npieces,int nouts){
		RGraph gvrp=new RGraph(); 
		System.out.println(ns.length);
		MM m=new MM(gmm,T,ns[0],ns[1],npieces,nouts);
		System.out.println("ya acabo m");
		gvrp=m.getGvrp();
		return gvrp;
	}
	/**
	 * This method returns the sites graph
	 * @return gmm sites graph
	 */
	public MGraph getGmm() {
		return gmm;
	}
	/**
	 * 	Tjis method sets the sites graph
	 * @param gmm sites graph
	 */
	public void setGmm(MGraph gmm) {
		this.gmm = gmm;
	}
	/**
	 * This method returns the horizon planning
	 * @return T horizon planning
	 */
	public double getT() {
		return T;
	}
	/**
	 * This method sets the horizon planning
	 * @param t horizon planning
	 */
	public void setT(double t) {
		T = t;
	}
	/**
	 * This method returns the depot coordinates
	 * @return ns Depot coordinates
	 */
	public double[] getNs() {
		return ns;
	}
	/**
	 * 	This method sets de Depot Coordinates
	 * @param ns
	 */
	public void setNs(double[] ns) {
		this.ns = ns;
	}
	/**
	 * This method returns the number of piecewise lines
	 * @return npieces number of piecewise lines
	 */
	public int getNpieces() {
		return npieces;
	}
	/**
	 * This method sets the number of piecewise lines
	 * @param npieces number of piecewise lines
	 */
	public void setNpieces(int npieces) {
		this.npieces = npieces;
	}
	/**
	 * This method returns number of piecewise lines out of time window
	 * @return nouts number of piecewise lines out of time window
	 */
	public int getNouts() {
		return nouts;
	}
	/**
	 *  This method sets the number of piecewise lines out of time window
	 * @param nouts number of piecewise lines out of time window
	 */
	public void setNouts(int nouts) {
		this.nouts = nouts;
	}
	/**
	 * This method returns the routing graph
	 * @return gvrp routing graph
	 */
	public RGraph getGvrp() {
		return gvrp;
	}
	/**
	 *  This method sets the routing graph
	 * @param gvrp routing graph
	 */
	public void setGvrp(RGraph gvrp) {
		this.gvrp = gvrp;
	}
}