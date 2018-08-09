package P1;
/**
 * This class represents one sub-route with its cost, charge and path characteristics
 * @author 	/Daniel Duque
 * 			/d.duque25@uniandes.edu.co
 * 			/Universidad de los Andes
 *			/Departamento de Ingeniería Industrial
 *			/Combined Maintenance and Routing Optimization for large scale problems
 */
public class Ruta 
{

	public int[] path;
	public double cost;
	public double load;
	
	public Ruta(int[] nPath, double nCost, double nLoad)
	{
		this.path = nPath;
		this.cost = nCost;
		this.load = nLoad;
	}
	
	public String toString(){
		String toReturn = "";
		for(int i = 1; i < path.length; i++){
			toReturn += (path[i-1]+"-");
		}
		toReturn += path[path.length-1];
		toReturn += "_"+this.cost+"_"+this.load;
		return toReturn;
	}
}
