package maintenancer;
import java.util.ArrayList;
/**
 * This class is the representation of a original graph (original information)
 * @author 	/John Edgar Fontecha Garcia
 * 			/je.fontecha10@uniandes.edu.co
 * 			/Universidad de los Andes
 *			/Departamento de Ingeniería Industrial
 *			/Combined Maintenance and Routing Optimization for large scale problems
 */
public class MGraph {
	private ArrayList<MNode> nodes; 
	private ArrayList<MArc> arcs; 
	/**
	 * This is the first constructor of the class
	 * @param  
	 */
	public MGraph() {
		nodes=new ArrayList<MNode>();
		arcs=new ArrayList<MArc>();
	}
	/**
	 * This is the second constructor of the class
	 * @param nd array of nodes
	 */
	public MGraph(ArrayList<MNode> nd){
		this.setNodes(nd);
	}
	/**
	 * This method returns the array of nodes 
	 * @return nodes
	 */
	public ArrayList<MNode> getNodes() {
		return nodes;
	}
	/**
	 *  This method sets the array of nodes
	 * @param nodes array of nodes
	 */
	public void setNodes(ArrayList<MNode> nodes) {
		this.nodes = nodes;
	}
	/**
	 * This method adds nodes to the graph
	 * @param nd node
	 */
	public void addNode(MNode nd){
		nodes.add(nd);
	}
	
	public void addArc(MArc na){
		arcs.add(na);
	}
	public ArrayList<MArc> getArcs() {
		return arcs;
	}
	public void setArcs(ArrayList<MArc> arcs) {
		this.arcs = arcs;
	}
	
	public MNode getNodebyID(int ID){
		for (int i = 0; i < nodes.size(); i++) {
			if(nodes.get(i).getId() == ID){
				return nodes.get(i);
			}
		}
		return null;
	}
}