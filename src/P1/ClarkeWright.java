package P1;
import java.io.IOException;
import java.util.ArrayList;
/**
 * This class .....
 * @author 	/Daniel Duque, John Fontecha
 * 			/d.duque25@uniandes.edu.co, je.fontecha TODO:!!!
 * 			/Universidad de los Andes
 *			/Departamento de Ingeniería Industrial
 *			/Combined Maintenance and Routing Optimization for large scale problems
 */
public class ClarkeWright 
{
	/**
	 * como se inicia con tantas rutas como nodos existen entonces se crea este arraylist que 
	 * contiene todas las rutas, este se va acortando a medida que se arma el TSP
	 */
	public ArrayList<Integer>[] rutas;
	/**
	 * me dice en cual ruta se encuentra el nodo de la posicion
	 */
	public int[] posNodo;
	/**
	 * vector que contiene la ruta de sln incluido el deapot al comienzo y al final.
	 */
	public ArrayList<Integer> vectRes;

	public DataHandler data;
	/**
	 * MAtriz de ahorros de CW
	 */
	public double[][] AhorrosCW;
	/**
	 * valor del costo total actual de la ruta
	 */
	public double costoTot;

	/**
	 * es el ahorro más grande de la matriz de ahorros
	 */
	public double maxAhorro;

	/**
	 * es la posición de la fila del máx ahorro
	 */
	public int posi;
	/**
	 * Es la pos de la col del máx ahorro
	 */
	public int posj;

	/**
	 * constructor de la clase
	 * @param nData
	 */
	public ClarkeWright(DataHandler nData) 
	{
		data = nData;
		AhorrosCW= new double[data.savings.length][data.savings[0].length];
		for (int i = 0; i < AhorrosCW.length; i++) {
			AhorrosCW[i] = data.savings[i].clone();
		}
	}

	/**
	 * Este método va a crear las rutas desde el deapot de ida y regreso para inicializar el vector 
	 * de rutas
	 * Existen tantas rutas como nodos (excluyendo el deapot).
	 */
	public void setRutas ()
	{
		rutas = new ArrayList[data.n];
		posNodo = new int [data.n];
		for (int i = 0; i <data.n; i++) {
			rutas[i] =new ArrayList<Integer>();
			rutas[i].add(i);
			posNodo[i]=i;
		}
	}

	/**
	 * Search or the largest saving
	 */
	public void getMaxAhorro ()
	{
		maxAhorro= 0;
		// se inicializan en -1 x q ellos no existen y cuando el máximo ahorro
		// sea 0 pues no va a meterse en una posicion real
		posi=-1;
		posj=-1;
		for (int i = 0; i < data.n; i++) 
		{
			for (int j = 0; j < data.n; j++) 
			{
				if (AhorrosCW[i][j]> maxAhorro) 
				{
					maxAhorro= AhorrosCW[i][j];
					posi=i;
					posj=j;
				}
			}
		}
	}

	/**
	 * Check if adding a now arc generates a subtour.
	 * 
	 * @return
	 */
	public int checkSubTour() {
		// cuando hay SubTour devuelve 1 dlc 0
		int hayST = 0;
		int dondeEstaPosi = posNodo[posi];
		for (int i = 0; i < rutas[dondeEstaPosi].size(); i++) {
			if (rutas[dondeEstaPosi].get(i) == posj) {
				hayST = 1;
			}
		}
		return hayST;
	}

	/**
	 * Aquí se combinan las rutas independientes, así se empieza a armar una ruta más grande
	 * @param haySubTour
	 */
	public void actualizarDatos(int haySubTour) {
		if (haySubTour == 1) {
			AhorrosCW[posi][posj] = 0;
		} else {
			for (int k = 0; k < data.n; k++) {
				AhorrosCW[k][posj] = 0;
				AhorrosCW[posi][k]=0;
			}
			AhorrosCW[posj][posi]=0;
			int dondeEstaPosi=posNodo [posi];
			int dondeEstaPosj=posNodo [posj];
			//movi el apuntador de j a la ruta donde esta i
			for (int i = 0; i <rutas[dondeEstaPosj].size(); i++) 
			{
				int nodo = rutas[dondeEstaPosj].get(i);
				posNodo[nodo]=posNodo[posi];
				rutas[dondeEstaPosi].add(nodo);
			}
			rutas[dondeEstaPosj].clear();
		}
	}

	/**
	 * método que me da la distancia total
	 * @return
	 */
	public double getTotalCost()
	{
		costoTot=0;
		int dondeEstaRuta=posNodo[1];
		//este arraylist (i.e., ruta) no incluye el depot
		ArrayList<Integer> ruta=(dondeEstaRuta<rutas.length&&dondeEstaRuta>=0)?rutas[dondeEstaRuta]:null;
		if(ruta!=null)
		{
			for (int i = 0; i < ruta.size()-1; i++) 
			{
				costoTot+=data.distances[ruta.get(i)][ruta.get(i+1)];
			}
			costoTot+=data.distances[0][ruta.get(0)];
			costoTot+=data.distances[ruta.get(ruta.size()-1)][0];
		}
		return costoTot;		
	}

	/**
	 * Este método crea un TSP
	 */
	public void createTSPvector ()
	{
		int dondeEstaRuta=posNodo[1];
		ArrayList<Integer> ruta=(dondeEstaRuta<rutas.length&&dondeEstaRuta>=0)?rutas[dondeEstaRuta]:null;
		vectRes= new ArrayList<Integer>();
		if(ruta!=null)
		{
			vectRes.add(0); 
			for (Integer nodoR : ruta) 
			{
				vectRes.add(nodoR);
			}
			vectRes.add(0);
		}
	}

	/**
	 * Este método ejecuta el CW para generar un TSP
	 */
	public void executeCW() 
	{
		setRutas();

		boolean stop = false;

		while(stop == false){
			getMaxAhorro();
			if(posi!=-1){ //este -1 se coloca en getMaxAhorro
				int haySubT = checkSubTour();
				actualizarDatos(haySubT);	
			}else
			{	
				stop=true;
			}
		}

		getTotalCost();
		createTSPvector();
	}
}