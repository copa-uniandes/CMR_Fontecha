package benchmark;
import java.util.ArrayList;

import router.RArc;
import router.RNode;
/**
 * This class is used in order to define a benchmark graph
 * @author 	/John Edgar Fontecha Garcia
 * 			/je.fontecha10@uniandes.edu.co
 * 			/Universidad de los Andes
 *			/Departamento de Ingeniería Industrial
 *			/Combined Maintenance and Routing Optimization for large scale problems
 */
public class BGraph {
	private ArrayList<BNode> nodes; 
	private ArrayList<BArc> arcs; 
	
	/**
	 * This is the first constructor of the class Bgraph
	 * @param _ without parameters
	 */
	public BGraph() {
		nodes=new ArrayList<BNode>();
		arcs=new ArrayList<BArc>();
	}
	/**
	 * This is the second constructor of the class
	 * @param nd nodes array
	 * @param na arcs array
	 */
	public BGraph(ArrayList<BNode> nd, ArrayList<BArc> na){
		this.setNodes(nd);
		this.setArcs(na);
	}
	/**
	 * This method returns the nodes of the benchmark Graph
	 * @return nodes nodes of the benchmark Graph 
	 */
	public ArrayList<BNode> getNodes() {
		return nodes;
	}
	/**
	 * This method sets the nodes of the benchmark Graph
	 * @param nodes nodes of the benchmark Graph 
	 */
	public void setNodes(ArrayList<BNode> nodes) {
		this.nodes = nodes;
	}
	/**
	 * This method returns the arcs of the benchmark Graph
	 * @return arcs arcs of the benchmark Graph 
	 */
	public ArrayList<BArc> getArcs() {
		return arcs;
	}
	/**
	 * This method sets the arcs of the benchmark Graph
	 * @param arcs arcs of the benchmark Graph 
	 */
	public void setArcs(ArrayList<BArc> arcs) {
		this.arcs = arcs;
	}
	/**
	 * This method adds one node to the benchmark Graph
	 * @param nd a node of the benchmark Graph 
	 */
	public void addNode(BNode nd){
		nodes.add(nd);
	}
	/**
	 * This method adds one node to the benchmark Graph in a specific position
	 * @param pos specific position to locate the node 
	 * @param nd a node of the benchmark Graph 
	 */
	public void addNode(int pos,BNode nd){
		nodes.add(pos,nd);
	}
	/**
	 * This method removes one node to the benchmark Graph in a specific position
	 * @param pos specific position to remove the node 
	 */
	public void removeNode(int pos){
		nodes.remove(pos);
	}
	/**
	 * This method adds one arc to the benchmark graph
	 * @param na
	 */
	public void addArc(BArc na){
		arcs.add(na);
	}
}
