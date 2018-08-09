package maintenancer;

/**
 * This class define the arc object. Connection between two nodes
 * @author 	/John Edgar Fontecha Garcia
 * 			/je.fontecha10@uniandes.edu.co
 * 			/Universidad de los Andes
 *			/Departamento de Ingeniería Industrial
 *			/Proyecto de Tesis
 */
public class MArc {
	private int tail; //position in x axis for this node
	private int head;//position in y axis for this node
	private double traveltime; 
	/**
	 * An Arc is defined by the follow parameters. This is the constructor of the object.
	 * @param tail tail node identifier
	 * @param head head node identifier
	 * @param cost cost from tail to head
	 */
	public MArc(int tail, int head, double traveltime) {
		this.setTail(tail);
		this.setHead(head);
		this.setTravelTime(traveltime);
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
	public double getTravelTime() {
		return traveltime;
	}
	/**
	 * This method sets the cost from tail to head
	 * @param cost is a double number that represents the cost from tail to head 
	 */
	public void setTravelTime(double traveltime) {
		this.traveltime = traveltime;
	}
}