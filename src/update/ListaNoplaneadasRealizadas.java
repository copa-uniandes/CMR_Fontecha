package update;

import java.util.ArrayList;

public class ListaNoplaneadasRealizadas {
	/**
	 * ArrayList de no planeadas y realizadas
	 */
	private ArrayList<Pair> realizadas;
	/**
	 * This is the first constructor of the class de arraylist de planeadas y realizadas
	 */
	public ListaNoplaneadasRealizadas() {
		realizadas=new ArrayList<Pair>();
	}
	/**
	 * This is the second constructor of the class
	 * @param nd ArrayList of Pair, no planeadas y realizadas
	 * @param na arcs array
	 */
	public ListaNoplaneadasRealizadas(ArrayList<Pair> nd){
		this.setRealizadas(nd);
	}
	
	public void addPair(Pair nd){
		realizadas.add(nd);
	}
	public ArrayList<Pair> getRealizadas() {
		return realizadas;
	}
	public void setRealizadas(ArrayList<Pair> realizadas) {
		this.realizadas = realizadas;
	}
	
}
