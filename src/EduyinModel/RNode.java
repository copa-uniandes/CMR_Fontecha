package EduyinModel;
/**
 * This class is used in order to define the basic information of a node in the routing graph
 * @author 	/John Edgar Fontecha Garcia para Modelo Eduyin
 * 			/je.fontecha10@uniandes.edu.co
 * 			/Universidad de los Andes
 *			/Departamento de Ingeniería Industrial
 *			/Combined Maintenance and Routing Optimization for large scale problems
 */
public class RNode {
	private int id1; //Maintenance Graph ID
	private int id2; //Routing Graph ID
	private int id3; //MSH ID
	private int posFS;
	private double posx;
	private double posy;
	private double waiting;
	private double expectedservicetime;
	private double opt;
	private double l;
	private double u;
	private double [][] pieces; 

	/**
	 * Constructor of a RNode used for the split graph
	 * @param id1 id in the sites graphTODO!!!
	 * @param id2 id in the routing graph
	 * @param id3 id in the split procedure
	 * @param posFS position in the forward star
	 * @param posx depot - x coordinate 
	 * @param posy depot - y coordinate
	 * @param waiting expected waiting time
	 * @param expectedservicetime expected service time
	 * @param opt optimal time to perform the maintenance operation
	 * @param l lower bound for time window
	 * @param u upper bound for time window
	 * @param np parameters if piecewise linear 
	 */
	public RNode(int id1,int id2, int id3, int posFS,double posx, double posy, double waiting, double expectedservicetime, 
				double opt,double l,double u,double [][] np) {
		this.setId1(id1);
		this.setId2(id2);
		this.setId3(id3);
		this.setPosFS(posFS);
		this.setPosx(posx);
		this.setPosy(posy);
		this.setWaiting(waiting);
		this.setExpectedservicetime(expectedservicetime);
		this.setPieces(np);
		this.setOpt(opt);
		this.setL(l);
		this.setU(u);
	}
	/**
	 * This method returns the node x-Axis position
	 * @return posx
	 */
	public double getPosx() {
		return posx;
	}
	/**
	 * This method sets the node's x-Axis position
	 * @param posx double number that represents the node's x-Axis position 
	 */
	public void setPosx(double posx) {
		this.posx = posx;
	}
	/**
	 * This method returns the node y-Axis position
	 * @return posy
	 */
	public double getPosy() {
		return posy;
	}
	/**
	 * This method sets the node's y-Axis position
	 * @param posx double number that represents the node's y-Axis position 
	 */
	public void setPosy(double posy) {
		this.posy = posy;
	}
	/**
	 * This method sets the node's identifier in the sites graph
	 * @param id1 
	 */
	public void setId1(int id1) {
		this.id1 = id1;
	}
	/**
	 * This method returns the node's identifier in the sites graph
	 * @return id1
	 */
	public int getId1() {
		return id1;
	}
	/**
	 * This method sets the node's identifier in the routing graph
	 * @param id2
	 */
	public void setId2(int id2) {
		this.id2 = id2;
	}
	/**
	 * This method returns the node's identifier in the routing graph
	 * @return id2
	 */
	public int getId2() {
		return id2;
	}
	/**
	 * This method returns the node's waiting time
	 * @return waiting
	 */
	public double getWaiting() {
		return waiting;
	}
	/**
	 * This method sets the node's waiting time
	 * @param waiting waiting time
	 */
	public void setWaiting(double waiting) {
		this.waiting = waiting;
	}
	/**
	 * This method returns the node's expected service time
	 * @return expectedservicetime
	 */
	public double getExpectedservicetime() {
		return expectedservicetime;
	}
	/**
	 * This method sets the node's expected service time
	 * @param expectedservicetime expected service time
	 */
	public void setExpectedservicetime(double expectedservicetime) {
		this.expectedservicetime = expectedservicetime;
	}
	/**
	 * This method returns the slope and intercept of each piecewise line
	 * @return pieces
	 */
	public double [][] getPieces() {
		return pieces;
	}
	/**
	 * This method sets the slope and intercept of each piecewise line
	 * @param pieces
	 */
	public void setPieces(double [][] pieces) {
		this.pieces = pieces;
	}
	/**
	 * This method returns the optimum
	 * @return opt
	 */
	public double getOpt() {
		return opt;
	}
	/**
	 * This method sets the optimum
	 * @return opt
	 */
	public void setOpt(double opt) {
		this.opt = opt;
	}
	/**
	 * This method sets the optimum
	 * @return opt
	 */
	public int getPosFS() {
		return posFS;
	}
	/**
	 * This method sets the optimum
	 * @param opt
	 */
	public void setPosFS(int posFS) {
		this.posFS = posFS;
	}
	/**
	 * This method returns the lower bound of time window
	 * @return l
	 */
	public double getL() {
		return l;
	}
	/**
	 * This method sets the lower bound of time window
	 * @param l
	 */
	public void setL(double l) {
		this.l = l;
	}
	/**
	 * This method sets the upper bound of time window
	 * @return opt
	 */
	public double getU() {
		return u;
	}
	/**
	 * This method sets the upper bound of time window
	 * @param u
	 */
	public void setU(double u) {
		this.u = u;
	}
	/**
	 * This method sets the node id in the split procedure
	 * @return id3
	 */
	public int getId3() {
		return id3;
	}
	/**
	 * This method sets the node id in the split procedure
	 * @return ide3
	 */
	public void setId3(int id3) {
		this.id3 = id3;
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "myID1:" + id1 + "myID2:" + id2+"myID3:" + id3;
	}
}
