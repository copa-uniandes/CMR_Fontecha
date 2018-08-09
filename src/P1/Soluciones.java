package P1;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
/**
 * This class
 * @author 	/Daniel Duque
 * 			/d.duque25@uniandes.edu.co
 * 			/Universidad de los Andes
 *			/Departamento de Ingeniería Industrial
 *			/Combined Maintenance and Routing Optimization for large scale problems
 */
/**
 * posee un arraylist con todos los tableros
 * @author Victor
 *
 */
public class Soluciones 
{

	public final static String route = "output/";
	public DataHandler data;
	public ArrayList<Rutas> rutasTotales;
	/**
	 * pool de ruticas, sin diferenciar de donde vienen ni su costo ni su load
	 */
	public ArrayList <ArrayList<Integer> > pool; 
	public ArrayList <Double>   poolCost;
	public ArrayList <Double>   poolLoad;
	

	
	public void setUpPool()
	{
//		sortSolutions();
		for (int i = 0; i < rutasTotales.size(); i++) 
		{
			Rutas tablerito =rutasTotales.get(i);
			for (int j = 0; j < tablerito.ruticas.size(); j++) 
			{
				Ruta route = tablerito.ruticas.get(j);
				ArrayList<Integer> newRoute=new ArrayList<Integer>();
				for (int k = 0; k < route.path.length; k++) 
				{
					newRoute.add(route.path[k]);
				}
				pool.add(newRoute);
				poolCost.add(route.cost);
				poolLoad.add(route.load);

			}
		}
	

		
	}
	
	
	public Soluciones(DataHandler pData)
	{
		this.data = pData;
		rutasTotales = new ArrayList<Rutas>();
		pool =new ArrayList<ArrayList<Integer>>();
		poolCost = new ArrayList<Double>();
		poolLoad = new ArrayList<Double>();
		
	}
	
	public void addSolutions(ArrayList<Rutas> newSolutions)
	{
		rutasTotales.addAll(newSolutions);
	}
	
	public void sortSolutions()
	{
		Collections.sort(rutasTotales);
	}
	
	public void writeSolutionsFile()
	{
		String nameFile = ""+System.currentTimeMillis()+".txt";
		File archivo = new File(route+nameFile);
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter(archivo));
			for(int i = 0; i < rutasTotales.size();i++)
			{
				out.write(rutasTotales.get(i).toString()+"\n");
			}
			out.close();
		} catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("Error de escritura de archivo, Soluciones.java");
		}
	}

	public int darTotalRuticas() 
	{
		int contador =0;
		for (int i = 0; i < rutasTotales.size(); i++) 
		{
			contador += rutasTotales.get(i).ruticas.size();
			
		}
		
		return contador;
	}
}
