package P1;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Random;
/**
 * This class .....
 * @author 	/Daniel Duque
 * 			/d.duque25@uniandes.edu.co
 * 			/Universidad de los Andes
 *			/Departamento de Ingeniería Industrial
 *			/Combined Maintenance and Routing Optimization for large scale problems
 */
public class GRASP 
{
	//debo iniciar con una sln factible, puede ser siempre la de la ruta más corta.
	//debo generar una sln aleatorizada 
	//luego hacerle una mejora con busqueda local
	//
	public DataHandler data;
	public Split splitGR;
	public TwoOpt twoOptGR;
	public ArrayList<Rutas> rutas; // Lista de tableros
	public ArrayList<FeasibleRoute> rutasfactibles=new ArrayList<FeasibleRoute>();
	
	public double bestFO; //Split
	public ArrayList<ArrayList<Integer>> bestRoutes;//las mejores rutas 
	public ArrayList<Integer> bestTSP;
	
	public double actualFO; //Split
	public ArrayList<ArrayList<Integer>> actualRoutes;//las mejores rutas 
	public ArrayList<Integer> actualTSP;
	
	private static Random random = new Random(13);
	
	public GRASP(DataHandler nData, Split splits, TwoOpt twoOpti)
	{
		data = nData;
		splitGR = splits;
		twoOptGR = twoOpti;
	}
	
	/**
	 * Qué hace esto? TODO
	 */
	public void aleatorizarSlnActual()
	{
		int numRemoved=random.nextInt((int) (data.n*0.1));
		
		ArrayList<Integer> removidos= new ArrayList<Integer>();
		
		for (int i = 0; i < numRemoved; i++) 
		{
			int removedIndex= 1+ random.nextInt(actualTSP.size()-2);
			int nodeRomovido = actualTSP.remove(removedIndex);	
			removidos.add(nodeRomovido);
		}
		
		for (int i = 0; i < removidos.size(); i++) 
		{
			int insertedNode= removidos.get(i);
			int insertionIndex= 1+ random.nextInt(actualTSP.size()-2);
			actualTSP.add(insertionIndex, insertedNode);	
		}		
	}
	
	private void localSearch() 
	{
		boolean condicion=true;
		int numeroiteraciones=0;
		while (condicion && numeroiteraciones<1000) 
		{
			numeroiteraciones=numeroiteraciones+1;
			twoOptGR.excecute(actualTSP);
			if (twoOptGR.rutaNueva.size()>0) 
			{
				actualTSP.clear();
				actualTSP.addAll(twoOptGR.rutaNueva);
			}
			else
			{
				condicion=false;
			}
		
		}
	}

	private void setup(ArrayList<Integer> rutaInicial) {
		rutas.add(splitGR.excecute(rutaInicial));
		setRutasfactibles(splitGR.getFeasibleRoutes());
//		System.out.println("Estoy en grasp, las rutas factibles son hasta ahora: "+getRutasfactibles().size());
		bestFO=splitGR.getFO();
		bestTSP= new ArrayList<Integer>();
		bestTSP.addAll(rutaInicial);
		
		actualFO=splitGR.getFO();
		actualTSP= new ArrayList<Integer>();
		actualTSP.addAll(rutaInicial);
	}
	/**
	 * 
	 * @param rutaInicial es un TSP
	 */
	public void excecute (ArrayList<Integer> rutaInicial)
	{
		rutas = new ArrayList<Rutas>();
//		System.out.println("Tamaño ruta inicial "+rutaInicial.size());
		
		setup(rutaInicial);
//		System.out.println("Ruta inicial "+rutaInicial.size());
		
		//para qué es este for?
		for (int d = 0; d<0; d++) 
		{
			aleatorizarSlnActual();
			localSearch();
			Rutas lasRutas = splitGR.excecute(actualTSP);
			actualFO=splitGR.getFO();
			
			if (bestFO > actualFO) {
				rutas.add(lasRutas);
				bestFO = actualFO;
				bestTSP = new ArrayList<Integer>();
				bestTSP.addAll(actualTSP);
			} else {
				actualFO = bestFO;
				actualTSP = new ArrayList<Integer>();
				actualTSP.addAll(bestTSP);
			}
		}	
	}

	public ArrayList<FeasibleRoute> getRutasfactibles() {
		return rutasfactibles;
	}

	public void setRutasfactibles(ArrayList<FeasibleRoute> rutasfactibles) {
		this.rutasfactibles = rutasfactibles;
	}
}
