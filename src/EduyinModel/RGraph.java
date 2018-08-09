package EduyinModel;
import java.util.ArrayList;
/**
 * This class is used in order to define the basic information of a node
 * @author 	/John Edgar Fontecha Garcia para Modelo Eduyin
 * 			/je.fontecha10@uniandes.edu.co
 * 			/Universidad de los Andes
 *			/Departamento de Ingeniería Industrial
 *			/Combined Maintenance and Routing Optimization for large scale problems
 */
public class RGraph {
	private ArrayList<RNode> nodes; 
	private ArrayList<Arc> arcs; 
	private double tmin;
	private double tmax;
	
	/**
	 * This is the first constructor of the class
	 */
	public RGraph() {
		nodes=new ArrayList<RNode>();
		arcs=new ArrayList<Arc>();
	}
	/**
	 * This is the second constructor of the class
	 * @param nd nodes array
	 * @param na arcs array
	 */
	public RGraph(ArrayList<RNode> nd, ArrayList<Arc> na){
		this.setNodes(nd);
		this.setArcs(na);
	}
	/**
	 * This method returns the nodes array 
	 * @return nodes
	 */
	public ArrayList<RNode> getNodes() {
		return nodes;
	}
	/**
	 * This method sets the nodes array
	 * @param nodes nodes array
	 */
	public void setNodes(ArrayList<RNode> nodes) {
		this.nodes = nodes;
	}
	/**
	 * This method adds one node to the Routing Graph
	 * @param nd
	 */
	public void addNode(RNode nd){
		nodes.add(nd);
	}
	/**
	 * This method adds one arc to the Routing Graph
	 * @param na
	 */
	public void addArc(Arc na){
		arcs.add(na);
	}
	/**
	 * This method returns the arcs 
	 * @return arcs
	 */
	public ArrayList<Arc> getArcs() {
		return arcs;
	}
	/**
	 * This method sets the arcs 
	 * @return arcs
	 */
	public void setArcs(ArrayList<Arc> arcs) {
		this.arcs = arcs;
	}
	/**
	 * This method adds one node to the Routing Graph in position 0
	 * @param rnode
	 */
	public void addFirst(RNode rnode) {
		nodes.add(0,rnode);
	}
	/**
	 * This method returns the lower bound of the planning  horizon 
	 * @return tmin
	 */
	public double getTmin() {
		return tmin;
	}
	/**
	 * This method sets the lower bound of the planning horizon
	 * @param tmin
	 */
	public void setTmin(double tmin) {
		this.tmin = tmin;
	}
	/**
	 * This method returns the upper bound of the planning horizon 
	 * @return tmax
	 */
	public double getTmax() {
		return tmax;
	}
	/**
	 *  This method sets the upper bound of the planning horizon 
	 * @param tmax
	 */
	public void setTmax(double tmax) {
		this.tmax = tmax;
	}
}