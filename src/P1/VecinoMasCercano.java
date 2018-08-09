package P1;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Vector;
/**
 * This class
 * @author 	/Daniel Duque
 * 			/d.duque25@uniandes.edu.co
 * 			/Universidad de los Andes
 *			/Departamento de Ingeniería Industrial
 *			/Combined Maintenance and Routing Optimization for large scale problems
 */
public class VecinoMasCercano 
{
	public DataHandler data;
	public double costoTot;
	//public int [] nodosSinIng;
	public double costoActual;
	public int posi;
	public int posj;
	public double[][] distancesVMC;
	public int nodoEscogido;
	public ArrayList<Integer> vectRes;
	
	public VecinoMasCercano(DataHandler nData)
	{
		data = nData;
		distancesVMC=data.distances.clone(); 
		distancesVMC = new double[data.distances.length][data.distances[0].length];
		for (int i = 0; i < distancesVMC.length; i++) {
			distancesVMC[i] = data.distances[i].clone();		
		}
		
	}

	
	public void getNodoRMC(int posi) 
	{
		double minCost= Double.POSITIVE_INFINITY;
		nodoEscogido=-1;
		for (int j = 1; j < data.n; j++) 
		{
			costoActual= distancesVMC[posi][j];
					if (costoActual<minCost && costoActual>0) 
					{
						minCost=costoActual;
						nodoEscogido=j;
					}
					
		}
		
	}
	
	public void actualizarDatos(int posi)
	{
		for (int j = 0; j < data.n; j++) 
		{
			distancesVMC[posi][j]=-100;
			distancesVMC[j][nodoEscogido]=-100;
		}
	}
	
	
	public void excecute ()
	{
		vectRes=new ArrayList<Integer>();
		vectRes.add(0);
		posi=0;
		while (nodoEscogido>=0) 
		{
			posi= vectRes.get(vectRes.size()-1);
			getNodoRMC(posi);
			
			if (nodoEscogido>=0) 
			{
				vectRes.add(nodoEscogido);
				actualizarDatos(posi);
			
			}
			
		}
		vectRes.add(0);
	
	}


}
