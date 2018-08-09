package benchmark;
import maintenancer.MNode;
/**
 * This class is used in order to define a benchmark graph
 * @author 	/John Edgar Fontecha Garcia
 * 			/je.fontecha10@uniandes.edu.co
 * 			/Universidad de los Andes
 *			/Departamento de Ingeniería Industrial
 *			/Combined Maintenance and Routing Optimization for large scale problems
 */
public class BNode {
	private MNode node;
	private double confiability;
	private double date;
	private int ranking;
	/**
	 * A benchmark node is defined by the follow parameters. This is the constructor of the benchmark node 
	 * @param node node from Maintenance
	 * @param conf reliability of the node in a specific time
	 * @param rank Position in the ranking of all benchmark nodes
	 * @param date date of maintenance execution		
	 */
	public BNode(MNode node, double conf, int rank,double date) {
		this.node=node;
		this.confiability=conf;
		this.ranking=rank;
		this.date=date;
	}
	/**
	 * This method returns the nodes of the MGraph
	 * @return nodes nodes of the MGraph 
	 */
	public MNode getNode() {
		return node;
	}
	/**
	 * This method sets the  node
	 * @param node MGraph node
	 */
	public void setNode(MNode node) {
		this.node = node;
	}
	/**
	 * This method returns the reliability of the benchmark node
	 * @return confiability reliability of the node 
	 */
	public double getConfiability() {
		return confiability;
	}
	/**
	 * This method sets the reliability of the node
	 * @param date attention date of the node
	 */
	public void setConfiability(double confiability) {
		this.confiability = confiability;
	}
	/**
	 * This method returns the position in the ranking of the node
	 * @return ranking p in the ranking of the node
	 */
	public int getRanking() {
		return ranking;
	}
	/**
	 * This method sets the ranking (position) of the node
	 * @param date attention date of the node
	 */
	public void setRanking(int ranking) {
		this.ranking = ranking;
	}
	/**
	 * This method returns the attention date of the node
	 * @return date attention date of the node
	 */
	public double getDate() {
		return date;
	}
	/**
	 * This method sets the attention date of the node
	 * @param date attention date of the node
	 */
	public void setDate(double date) {
		this.date = date;
	}
}