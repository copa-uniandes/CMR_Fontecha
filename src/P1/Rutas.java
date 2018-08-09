package P1;
import java.util.ArrayList;
/**
 * This class
 * @author 	/Daniel Duque
 * 			/d.duque25@uniandes.edu.co
 * 			/Universidad de los Andes
 *			/Departamento de Ingeniería Industrial
 *			/Combined Maintenance and Routing Optimization for large scale problems
 */
public class Rutas implements Comparable<Rutas>
{

	public DataHandler data;
	public ArrayList<Ruta> ruticas;
	public Double costTotal;
	
	public Rutas (DataHandler nData ) 
	{
		this.data = nData;
		this.ruticas = new ArrayList<Ruta>();
	}
	
	public void addRoute(int[] path, double costo){
		double load = 0.0;
		for(int i = 0; i < path.length; i++)
		{
			load += data.demand[path[i]];
		}
		ruticas.add(new Ruta(path, costo, load));
		
	}
	public String toString(){
		String toReturn = "Ruta, costo: "+costTotal+"\n"+"\n";
		for(int i = 0; i < ruticas.size();i++)
		{
			toReturn += (ruticas.get(i).toString()+"\n"+"\n");
		}
		return toReturn;
	}
	
	public int compareTo(Rutas arg0) {
		// TODO Auto-generated method stub
		return this.costTotal.compareTo(arg0.costTotal);
	}

}
