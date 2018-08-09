package EduyinModel;
/**
 * This class define the arc object. Connection between two nodes
 * @author 	/John Edgar Fontecha Garcia para Modelo Eduyin
 * 			/je.fontecha10@uniandes.edu.co
 * 			/Universidad de los Andes
 *			/Departamento de Ingeniería Industrial
 *			/Combined Maintenance and Routing Optimization for large scale problems
 */
public class Arc {
	private int tail; 
	private int head;
	private double dcost; 
	private double dtcost; 
	/**
	 * An Arc is defined by the follow parameters. This is the constructor of the object.
	 * @param tail tail node identifier
	 * @param head head node identifier
	 * @param cost cost from tail to head
	 * @param cost cost from tail to head ditance-time
	 */
	public Arc(int tail, int head, double dcost, double dtcost) {
		this.setTail(tail);
		this.setHead(head);
		this.setDcost(dcost); 
		this.setDTcost(dtcost);
	}
	/**
	 * This method returns the tail node identifier
	 * @return tail
	 */
	public int getTail() {
		return tail;
	}
	/**
	 * This method sets the tail node identifier
	 * @param tail is a integer number that represents the tail node identifier 
	 */
	public void setTail(int tail) {
		this.tail = tail;
	}
	/**
	 * This method returns the head node identifier
	 * @return head
	 */
	public int getHead() {
		return head;
	}
	/**
	 * This method sets the head node identifier
	 * @param head is a integer number that represents the head node identifier 
	 */
	public void setHead(int head) {
		this.head = head;
	}
	/**
	 * This method returns the cost from tail to head
	 * @return cost
	 */
	public double getDcost() {
		return dcost;
	}
	/**
	 * This method sets the cost from tail to head
	 * @param cost is a double number that represents the cost from tail to head 
	 */
	public void setDcost(double dcost) {
		this.dcost = dcost;
	}
	/**
	 * This method returns the cost distance-time from tail to head
	 * @return dtcost
	 */
	public double getDTcost() {
		return dtcost;
	}
	/**
	 * This method sets the cost distance-time from tail to head
	 * @param dtcost is a double number that represents the cost distance-time from tail to head 
	 */
	public void setDTcost(double dtcost) {
		this.dtcost = dtcost;
	}
}