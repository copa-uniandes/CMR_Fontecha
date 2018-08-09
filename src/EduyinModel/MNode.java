package EduyinModel;
import umontreal.iro.lecuyer.probdist.ContinuousDistribution;
/**
 * This class is used in order to define the basic information of a maintenance node
 * @author 	/John Edgar Fontecha Garcia para Modelo Eduyin
 * 			/je.fontecha10@uniandes.edu.co
 * 			/Universidad de los Andes
 *			/Departamento de Ingeniería Industrial
 *			/Combined Maintenance and Routing Optimization for large scale problems
 */
public class MNode {
	private int id; 
	private double ccm;
	private double cpm;
	private double cw;
	private double tcm;
	private double tpm;
	private double deltacost;
	private ContinuousDistribution d;
	private double waiting;
	private int iteration;
	private double opt;
	private double optcost;
	private double posx; 
	private double posy;
	/**
	 * A node is defined by the follow parameters. This is the constructor of maintenance node object.
	 * @param id identifier of the node
	 * @param ccm cost of corrective maintenance
	 * @param cpm cost of preventive maintenance
	 * @param cwt cost of waiting time
	 * @param tcm time of corrective maintenance
	 * @param tpm time of preventive maintenance
	 * @param deltacost value in order to calculate de time windows from the minimum
	 * @param d continuos distribution in order to describe the time between failures 
	 * @param waiting average,min or max waiting time of each real node
	 * @param iteration number of iteration in this node
	 * @param optcost optimum cost
	 * @param posx x_coordinate of the node
	 * @param posy y_coordinate of the node  		
	 */
	
	public MNode(int id, double ccm, double cpm, double cw, double tcm, double tpm, double deltacost, double posx, double posy, ContinuousDistribution d,
			double opt, double waiting, int iteration, double cost) {
		this.id = id;
		this.ccm=ccm;
		this.cpm=cpm;
		this.cw=cw;
		this.tcm=tcm;
		this.tpm=tpm;
		this.deltacost=deltacost;
		this.d = d;
		this.opt=opt;
		this.waiting = waiting;
		this.setIteration(iteration);
		this.optcost=cost;
		this.posx = posx;
		this.posy = posy;
	}
	/**
	 * This method returns the node x-axis position
	 * @return posx double number that represents the node's x-axis position 
	 */
	public double getPosx() {
		return posx;
	}
	/**
	 * This method sets the node's x-axis position
	 * @param posx double number that represents the node's x-axis position 
	 */
	public void setPosx(double posx) {
		this.posx = posx;
	}
	/**
	 * This method returns the node y-axis position
	 * @return posy double number that represents the node's y-axis position 
	 */
	public double getPosy() {
		return posy;
	}
	/**
	 * This method sets the node's y-axis position
	 * @param posx double number that represents the node's y-axis position 
	 */
	public void setPosy(double posy) {
		this.posy = posy;
	}
	/**
	 * This method returns the node's identifier
	 * @return id integer number that represents the identifier of the node
	 */
	public int getId() {
		return id;
	}
	/**
	 * This method sets the node's identifier
	 * @param id integer number that represents the identifier of the node
	 */
	public void setId(int id) {
		this.id = id;
	}
	/**
	 * This method returns the node's distribution
	 * @return d continues distribution of the time between failures
	 */
	public ContinuousDistribution getD() {
		return d;
	}
	/**
	 * This method sets the node's distribution
	 * @param d continues distribution of the time between failures
	 */
	public void setD(ContinuousDistribution d) {
		this.d = d;
	}
	/**
	 * This method returns the node's waiting time
	 * @return waiting double number that represents the expected waiting time
	 */
	public double getWaiting() {
		return waiting;
	}
	/**
	 * This method sets the node's waiting time
	 * @param waiting double number that represents the expected waiting time
	 */
	public void setWaiting(double waiting) {
		this.waiting = waiting;
	}
	/**
	 * This method returns the node's maintenance iteration
	 * @return iteration i´m not sure that we use this
	 */
	public int getIteration() {
		return iteration;
	}
	/**
	 * This method sets the node's maintenance iteration
	 * @param iteration i´m not sure that we use this
	 */
	public void setIteration(int iteration) {
		this.iteration = iteration;
	}
	/**
	 * This method returns the optimum node value
	 * @return optcost	optimum cost of maintenance
	 */
	public double getOptcost() {
		return optcost;
	}
	/**
	 * This method sets the optimum node value
	 * @param optcost optimum cost of maintenance
	 */
	public void setOptcost(double optcost) {
		this.optcost = optcost;
	}
	/**
	 * This method returns the cost of corrective maintenance
	 * @return ccm cost of corrective maintenance  
	 */
	public double getCcm() {
		return ccm;
	}
	/**
	 * This method sets the cost of corrective maintenance
	 * @param ccm cost of corrective maintenance  
	 */
	public void setCcm(double ccm) {
		this.ccm = ccm;
	}
	/**
	 * This method returns the cost of preventive maintenance
	 * @return cpm cost of preventive maintenance  
	 */
	public double getCpm() {
		return cpm;
	}
	/**
	 * This method sets the cost of preventive maintenance
	 * @param cpm cost of preventive maintenance  
	 */
	public void setCpm(double cpm) {
		this.cpm = cpm;
	}
	/**
	 * This method returns the waiting cost
	 * @return cw waiting cost  
	 */
	public double getCw() {
		return cw;
	}
	/**
	 * This method sets the cost of preventive maintenance
	 * @param cw cost of preventive maintenance  
	 */
	public void setCw(double cw) {
		this.cw = cw;
	}
	/**
	 * This method returns the time of corrective maintenance
	 * @return tcm time of corrective maintenance  
	 */
	public double getTcm() {
		return tcm;
	}
	/**
	 * This method sets the time of corrective maintenance
	 * @param tcm time of corrective maintenance  
	 */
	public void setTcm(double tcm) {
		this.tcm = tcm;
	}
	/**
	 * This method returns the time of preventive maintenance
	 * @return tpm time of preventive maintenance  
	 */
	public double getTpm() {
		return tpm;
	}
	/**
	 * This method returns the time of preventive maintenance
	 * @param tpm time of preventive maintenance  
	 */
	public void setTpm(double tpm) {
		this.tpm = tpm;
	}
	/**
	 * This method returns the deltacost in order to calculate the time windows
	 * @return deltacost value needed in order to calculate the time windows.  
	 */
	public double getDeltacost() {
		return deltacost;
	}
	/**
	 * This method returns the deltacost in order to calculate the time windows
	 * @oaran deltacost value needed in order to calculate the time windows.  
	 */
	public void setDeltacost(double deltacost) {
		this.deltacost = deltacost;
	}
	public double getOpt() {
		return opt;
	}
	public void setOpt(int opt) {
		this.opt = opt;
	}

}