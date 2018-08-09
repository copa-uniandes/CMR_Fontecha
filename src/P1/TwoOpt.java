package P1;
import java.util.ArrayList;
import java.util.Random;

import org.omg.PortableServer.POA;
/**
 * This class
 * @author 	/Daniel Duque
 * 			/d.duque25@uniandes.edu.co
 * 			/Universidad de los Andes
 *			/Departamento de Ingeniería Industrial
 *			/Combined Maintenance and Routing Optimization for large scale problems
 */
public class TwoOpt 
{
	public DataHandler data;
	public ArrayList <Integer> rutaVieja;
	public ArrayList <Integer> rutaNueva;

	public TwoOpt(DataHandler nData) 
	{
		data=nData;
	}
	public void setUp(ArrayList<Integer> rutaEntrante)
	{
		rutaVieja= new ArrayList<Integer>();
		rutaVieja.addAll(rutaEntrante);
		rutaNueva= new ArrayList<Integer>();

	}

	public void doTwoOpt()
	{
		double minDelta=0;
		int bestPosi=-1;
		int bestPosj=-1;
		for (int a = 0; a <= rutaVieja.size()-4; a++) 
		{		
			for (int b = a+2; b <= rutaVieja.size()-2; b++) 
			{
				int nodo_i=rutaVieja.get(a);
				int nodo_k=rutaVieja.get(a+1);
				int nodo_j=rutaVieja.get(b);
				int nodo_l=rutaVieja.get(b+1);
				double costoViejo= data.distances[nodo_i][nodo_k]+data.distances[nodo_j][nodo_l];
				double costoNuevo= data.distances[nodo_i][nodo_j]+data.distances[nodo_k][nodo_l];
				double delta=costoNuevo-costoViejo;
				if (delta<minDelta) 
				{
					minDelta=delta;
					bestPosi=a;
					bestPosj=b;
				}
			}

		}
		if (minDelta<0) {
			int posAct=0;
			int nodo_i=rutaVieja.get(bestPosi);
			int nodo_k=rutaVieja.get(bestPosi+1);
			int nodo_j=rutaVieja.get(bestPosj);
			int nodo_l=rutaVieja.get(bestPosj+1);
			//System.out.println( "i,j,k,l:-->" + nodo_i + ", " + nodo_j + ", "+ nodo_k +", " +nodo_l);
			//dice si voy para adelante o para atras es 1 si voy para adelante
			int movimiento = 1;
			while (rutaNueva.size()<rutaVieja.size()) 
			{
				if (posAct<0) 
				{
					System.out.println("iguazo");

				}
				int nodoAct =rutaVieja.get(posAct);
				if (rutaNueva.contains(nodoAct)) {
					//	System.out.println("algo pasa");
				}
				if (nodo_i==nodoAct) 
				{
					rutaNueva.add(nodoAct);
					posAct=bestPosj;

				}
				else if (nodo_k==nodoAct) 
				{
					rutaNueva.add(nodoAct);
					movimiento=1;
					posAct=bestPosj+1;
				}
				else if (nodo_j==nodoAct) 
				{
					rutaNueva.add(nodoAct);
					movimiento=-1;
					posAct = posAct + movimiento;

				}
				else if (nodo_l==nodoAct) 
				{
					rutaNueva.add(nodoAct);
					movimiento=1;
					posAct=posAct+movimiento;
				}
				else 
				{
					rutaNueva.add(nodoAct);
					posAct = posAct + movimiento;
				}	
				//				System.out.println(nodoAct);
			}
		}
	}

	public void excecute(ArrayList<Integer>rutaEntrante)
	{
		setUp(rutaEntrante);
		doTwoOpt();
	}
}
