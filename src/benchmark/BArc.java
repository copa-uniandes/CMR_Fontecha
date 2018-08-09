package benchmark;
/**
 * This class creates an arc in the Graph of the Acueducto - Benchmark
 * @author 	/John Edgar Fontecha Garcia
 * 			/je.fontecha10@uniandes.edu.co
 * 			/Universidad de los Andes
 *			/Departamento de Ingeniería Industrial
 *			/Combined Maintenance and Routing Optimization for large scale problems
 */
public class BArc {
	private int tail; 
	private int head;
	private double tcost; 
	/**
	 * An Arc is defined by the follow parameters. This is the constructor of the object.
	 * @param tail tail node identifier
	 * @param head head node identifier
	 * @param cost cost from tail to head
	 */
	public BArc(int tail, int head, double tcost) {
		this.setTail(tail);
		this.setHead(head);
		this.setTcost(tcost); 
	}
	/**
	 * This method returns the tail id
	 * @return tail tail id 
	 */
	public int getTail() {
		return tail;
	}
	/**
	 * This method sets the tail id
	 * @param tail tail id 
	 */
	public void setTail(int tail) {
		this.tail = tail;
	}
	/**
	 * This method returns the head id
	 * @return head head id 
	 */
	public int getHead() {
		return head;
	}
	/**
	 * This method sets the head id
	 * @param head head id 
	 */
	public void setHead(int head) {
		this.head = head;
	}
	/**
	 * This method returns the arc cost
	 * @return tcost arc cost 
	 */
	public double getTcost() {
		return tcost;
	}
	/**
	 * This method sets the arc cost
	 * @param tcost arc cost 
	 */
	public void setTcost(double tcost) {
		this.tcost = tcost;
	}
}
