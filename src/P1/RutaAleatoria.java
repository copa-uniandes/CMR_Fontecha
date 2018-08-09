package P1;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
/**
 * This class....
 * @author 	/Daniel Duque
 * 			/d.duque25@uniandes.edu.co
 * 			/Universidad de los Andes
 *			/Departamento de Ingeniería Industrial
 *			/Combined Maintenance and Routing Optimization for large scale problems
 */
public class RutaAleatoria 
{
	public DataHandler data;
	public int [] rutaAleator;
	public  ArrayList<Integer> candidatos;
	public ArrayList<Integer> vectRes;
	
	
 public RutaAleatoria(DataHandler nData) 
	{
		data = nData;	
	}
 
 public void setCandidatos()
	{
		candidatos= new ArrayList<Integer>();
		for (int i = 1; i < data.n; i++) 
		{
			candidatos.add(i);
		}
	}
 
 public void setRutaAleatoria()
 {
	vectRes=new ArrayList<Integer>();
	vectRes.add(0);
	for (int i = 1; i < data.n; i++) 
	{
		while (candidatos.size()>0) 
		{
			int posIngresar=data.aleatorio.nextInt(candidatos.size()) ;
			int nodoIngresar=candidatos.get(posIngresar) ;
			vectRes.add(i, nodoIngresar);
			candidatos.remove(posIngresar);
		}
	}
	
	vectRes.add(0);
	 
 }

 
 public void excecute ()
	{
		setCandidatos();
		setRutaAleatoria();
	
	}


}
