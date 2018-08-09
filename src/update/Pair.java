package update;

import maintenancer.MNode;
/**
 * @author je.fontecha10
 * En esta clase se guarda un id y un tiempo de atención para un sitio que fallo antes de tiempo y fue atendido
 * 
 */
public class Pair {
	/**
	 * este es el id del sitio que fallo 
	 */
	private int id1; //Maintenance Graph ID
	/**
	 * esta es la hora de atención + tcm (corrective maintenance time)
	 */
	private double s;
	/**
	 * Constructor of Pair
	 * @param id1
	 * @param s
	 */
	public Pair(int id1, double s){
		this.setId1(id1);
		this.setS(s);
	}
	public int getId1() {
		return id1;
	}
	public void setId1(int id1) {
		this.id1 = id1;
	}
	public double getS() {
		return s;
	}
	public void setS(double s) {
		this.s = s;
	}
}
