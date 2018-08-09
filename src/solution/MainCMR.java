package solution;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

import dataReaders.CSVReader4VRPSCD;
import drawer.Drawing;
import reader.Reading;
import reader.Reading2;
import router.RArc;
import router.DivideBigGraphSmallGraphs;
import router.InformationForGap;
import router.RGraph;
import router.RNode;
import router.RgvrpExecution;
import split.CMRVRPArcEvaluator;
import split.CMRVRPEvalComparator;
import split.Imprimir;
import split.Instancer;
import split.MyMSH;
import split.MyRandomGeneratorFactory;
import splitProcedures.VRPModelFacade;
import update.ListaNoplaneadasRealizadas;
import update.Pair;
import maintenancer.LeerEduyinSimu;
import maintenancer.MGraph;
import maintenancer.MM;
import maintenancer.MNode;
import maintenancer.Maintenance;
import maintenancer.Simulator;
import maintenancer.Tres;
import P1.GraspSplitSolver;
import benchmark.BGraph;
import benchmark.BenchmarkExecution;
/**
 * This class is used in order to solve the problem
 * @author 	/John Edgar Fontecha Garcia & Daniel Duque
 * 			/je.fontecha10@uniandes.edu.co & d.duque25@uniandes.edu.co
 * 			/Universidad de los Andes
 *			/Departamento de Ingeniería Industrial
 *			/Combined Maintenance and Routing Optimization for large scale problems
 */
public class MainCMR {
	public static double CELLDISTANCE=0.170;
	public static double VEL=5*24;// km/día//11.89
	public static int minimoenruta=30;
	public static int maximoenruta=58;
	private static double [] ns;
	private int idmin;
	public static double T=7*52; //Horizonte de planeación 
	public static int T1=7; //Horizonte de planeación por semanas
	public static double free_capacity=0.2; // Capacidad libre horizonte de planeación por semanas. (De 0 a 1)
	static PrintWriter writer;
	public static int TBench=30; //Horizonte del benchmark (acueducto)
	public static int numrutas=5;
	public static String ON="on"; //Switch for graph generation
	public static String OFF="off"; //Switch for graph generation
	public static String BENCHMARK=OFF; //To do a graph
	public static String INFORMATION_FOR_GAP=OFF; //To do a graph
	public static String instancia_1="./data/Instancia1.dat";
	public static String instancia_2="./data/Instancia2.dat";
	public static String instancia_3="./data/Instancia3.dat";
	public static String instancia_4="./data/Instancia4.dat";
	public static String instancia_5="./data/Instancia5.dat";
	public static String instancia_6="./data/Instancia6.dat";//los parámetros se ajustan para que el mantenimiento se haga en el horizonte de planeación
	public static String instancia_7="./data/Instancia7.dat";//incluye exponente
	public static String instancia_8="./data/Instancia8.dat";//solo es de prueba, luego la puedo borrar
	public static String rutiando=instancia_7;
	public static String HeuristicaconEduyindata=OFF;
	public static String EduyindataBIG=OFF;
	public static int Eduyindatasize=21; //debe ser 21 para datos con eduyindata
	public static String HeuristicasinMIP=ON;

	/**
	 * Main
	 * @param args
	 */
	public static void main(String[] args) {
		/*
		 * Parámetro para tomar el tiempo computacional del Maintenanance
		 */
		double timeTotal=System.currentTimeMillis();
		/*
		 * Parámetro para tomar el tiempo computacional del read
		 */
		double timeRead=System.currentTimeMillis();
		/*
		 * Parámetro para tomar el tiempo computacional del Maintenanance
		 */
		double timeMaintenance=0;
		/*
		 * Parámetro para tomar el tiempo computacional del Routing
		 */
		double timeRouting=0;	
		/*
		 * Parámetro para tomar el tiempo computacional de la simulación
		 */
		double timeSimulation=0;		
		/*
		 * Número de grafos que se obtienen al dividir el horizonte de planeación en semanas
		 */
		int numgraphs=(int) Math.ceil((T*1.0/(T1*1.0))); 
		/*
		 * El límite inferior del horizonte de planeación
		 */
		int minT=0; 
		/*
		 * Límite superior del horizonte de planeación
		 */
		int maxT=T1; 
		/*
		 * Número de rectas para el piecewise
		 */
		int npieces=24; 
		/*
		 * Número de rectas fuera de la ventana de tiempo
		 */
		int nouts=2; 
		/*
		 * NO RECUERDO QUÉ HACE ESTE CONT
		 */
		int cont=0;
		/*
		 * Grafo de sitios
		 */
		MGraph gmm;
		/*
		 * Grafo de operaciones de mantenimiento
		 */
		RGraph gvrp=new RGraph();
		/*
		 * Ejecución benchmark
		 */
		BenchmarkExecution benchmark;
		/*
		 * Clase que crea el grafo de operaciones de mantenimiento
		 */
		RgvrpExecution rgvrp;
		/*
		 * Arreglo de grafos semanales
		 */
		ArrayList<RGraph> smallgraphs=new ArrayList<RGraph>();


		/**
		 * Lectura de datos
		 */
		if(HeuristicaconEduyindata=="on"){
			if(EduyindataBIG=="off"){
				Reading2 c=new Reading2("./data/Eduyin_a_exponente.dat","./data/Eduyin_b.dat");
				timeMaintenance=System.currentTimeMillis();
				timeRead=System.currentTimeMillis()-timeRead;
				setNs(c.getPos());
				gmm=c.getGraph();
			} else{
				EduyinsModel_InstanceGenerator instance= new EduyinsModel_InstanceGenerator(Eduyindatasize,T);
				timeMaintenance=System.currentTimeMillis();
				timeRead=System.currentTimeMillis()-timeRead;
				setNs(instance.getX_0());		
				gmm=instance.getSitegraph();		
			}
		}else{
			Reading c=new Reading(rutiando);
			timeMaintenance=System.currentTimeMillis();
			timeRead=System.currentTimeMillis()-timeRead;
			setNs(c.getPos());
			gmm=c.getGraph();
		}
		System.out.println("Termino: "+gmm.getNodes().size()+" "+gmm.getArcs().size());		
		//Information for GAP
		if(INFORMATION_FOR_GAP=="on"){
			InformationForGap infogap=new InformationForGap(gmm);
		}

		//Benchmark
		if(BENCHMARK=="on"){
			benchmark=new BenchmarkExecution(gmm, TBench, T, numrutas);
			benchmark.Execution(gmm, TBench);
		}
		System.out.println("benchmark listo");

		//********************Aquí arranca el código para mi implementación*********************************** -----------------------------------------------------------------------------------------
		//GraspSplitSolver routingSolver = new GraspSplitSolver();
		//System.out.println(gmm.getNodes().size());
		//******Aquí se crean los pequeños grafos para el ruteo (los sitios que deben ser atendidos cada semana) 
		rgvrp=new RgvrpExecution(gmm, T, ns, npieces, nouts);
		gvrp=rgvrp.Execution(gmm, T, ns, npieces, nouts);
		DivideBigGraphSmallGraphs smallgraph=new DivideBigGraphSmallGraphs(gmm, gvrp, ns, minT, cont, npieces, nouts, numgraphs, maxT,HeuristicasinMIP,free_capacity);
		smallgraphs=smallgraph.getSmallGraphs(gmm, gvrp, ns, minT, cont, npieces, nouts, numgraphs, maxT,HeuristicasinMIP,free_capacity);
		System.out.println("size small: "+smallgraphs.size()); 
		
		//******Dado que hay muchas semanas, solo vamos a seleccionar las últimas 16 (esto es el "WARM UP")
		if(HeuristicaconEduyindata=="on"){

		} else{
			for(int p=smallgraphs.size()-17;p>=0;p--){
				smallgraphs.remove(p);
				smallgraphs.get(p).getNodes().size();
			}
		}
		double timeLowerBound=smallgraphs.get(0).getTmin(); //Este es el tiempo_min del primer smallgraph, es basicamente el nuevo cero del sistema

		//Este es solo para revisar cuántos sitios y cuáles sitios tengo cada semana, solo es impresion
		for(int p=0;p<smallgraphs.size();p++){	
			System.out.println("smallgraph "+p+" size:"+smallgraphs.get(p).getNodes().size());
//								for(int pp=0;pp<smallgraphs.get(p).getNodes().size();pp++){
//									System.out.println(smallgraphs.get(p).getNodes().get(pp).getId1()+" "+smallgraphs.get(p).getNodes().get(pp).getId2()+" "+smallgraphs.get(p).getNodes().get(pp).getId3());
//								}
		}

				System.out.println("Tmin \t Tmax \t");
		timeMaintenance=System.currentTimeMillis()-timeMaintenance;
		timeRouting=System.currentTimeMillis();	
		
		ArrayList<ArrayList<ArrayList<Tres>>> rutas_todas = new ArrayList<ArrayList<ArrayList<Tres>>>(); 
		ArrayList<ArrayList<ArrayList<Double>>> horas_todas = new ArrayList<ArrayList<ArrayList<Double>>>();
		int cualvoy=0;
		//Esté es el codigo para ejecutar cada semana
		for (int i = cualvoy; i<1*(smallgraphs.size()-1);i++){
			System.out.println("======================================================================================================================================================================");
			System.out.println("Ruteando semana "+i);
			System.out.println("======================================================================================================================================================================");
		//for (int i = cualvoy; i <cualvoy+1+1*smallgraphs.size(); i++) {//cualvoy+1+0*
			//			System.out.println(smallgraphs.get(i).getTmin()+" \t "+smallgraphs.get(i).getTmax()+" "+smallgraphs.get(i).getNodes().size());
			//Primero se corre el split
			GraspSplitSolver routingSolver = new GraspSplitSolver();
			routingSolver.solve(gmm,smallgraphs.get(i), smallgraphs.get(i).getTmin(), smallgraphs.get(i).getTmax(),HeuristicasinMIP,numrutas,HeuristicaconEduyindata);
			
			ArrayList<ArrayList<Tres>> rutas2 = new ArrayList<ArrayList<Tres>>();
			
			
			//ArrayList<RGraph> rutas=new ArrayList<RGraph>();
			ArrayList<ArrayList<Double>> horas=new ArrayList<ArrayList<Double>>();
			for(int j=0;j<routingSolver.getRutascompletasTres().size();j++){
//				System.out.println("Route: "+j);
				// Cambiar esto a maintenancer.Route
				ArrayList<Tres> ruta2 = new ArrayList<Tres>();
				//RGraph ruta=new RGraph();
				
				ArrayList<Double> hour=new ArrayList<Double>();
				for(int k=0;k<routingSolver.getRutascompletasTres().get(j).getRoute().size();k++){
					for(int l=0;l<smallgraphs.get(i).getNodes().size();l++){
						//RNode rnodo=new RNode();
						if(routingSolver.getRutascompletasTres().get(j).getRoute().get(k).getEventsOperationsID()==smallgraphs.get(i).getNodes().get(l).getId3()){
							Tres nodo = new Tres(smallgraphs.get(i).getNodes().get(l).getId1() , smallgraphs.get(i).getNodes().get(l).getId2(), routingSolver.getRutascompletasTres().get(j).getRoute().get(k).getEventTimes());
							//rnodo=smallgraphs.get(i).getNodes().get(l);
							ruta2.add(nodo);
							//ruta.addNode(rnodo);
							hour.add(routingSolver.getRutascompletasTres().get(j).getRoute().get(k).getEventTimes());
						}
					}
				}
				rutas2.add(ruta2);
				//rutas.add(ruta);
				horas.add(hour);
			}		
			
			rutas_todas.add(rutas2);
			horas_todas.add(horas);
			
			
			ArrayList<ArrayList<ArrayList<Tres>>> rutas_todas_backup = new ArrayList<ArrayList<ArrayList<Tres>>>(rutas_todas);
			//rutas_todas_backup = rutas_todas;
			
			//Ya con el split se debe correr la simulación.
			
			
			
			
			//Ya con la simulación se debe hacer update de las operaciones no planeadas (realizadas y no realizadas en la simulación)

			//Pair uno=new Pair(11,255);
			//Pair dos=new Pair(23,256);
			//Pair tres=new Pair(15,257);
			//Pair cuatro=new Pair(20,258);
			//lista.addPair(uno);
			//lista.addPair(dos);
			//lista.addPair(tres);
			//lista.addPair(cuatro);
//			System.out.println("************************************");

			


			//Esta sección solo está imprimiendo no importante para el código
			//			System.out.println("------------Maintenance Cost----------------------------------");
			//			double costo=0;
			//			for(int j=0;j<smallgraphs.get(i).getNodes().size();j++){
			//				for(int k=0;k<gmm.getNodes().size();k++){
			//					RNode nodito=smallgraphs.get(i).getNodes().get(j);
			//					if(nodito.getId1()==gmm.getNodes().get(k).getId()){
			//						costo=costo+gmm.getNodes().get(k).getOptcost();
			//						//						System.out.println(nodito.getId1()+" "+gmm.getNodes().get(k).getOptcost());
			//					}
			//				}
			//			}
			//			System.out.println(costo);

			timeRouting=System.currentTimeMillis()-timeRouting;	
			timeSimulation=System.currentTimeMillis();	
			//						System.out.println("--------------Simulation-----------------------------");
			//						if(HeuristicaconEduyindata=="on"){
			//							Organizador organizado=new Organizador(routingSolver.getRutascompletasTres());
			//							LeerEduyinSimu leyendo=new LeerEduyinSimu("./data/simulacioneduyin"+T1+".txt");
			//							Simulator simulacion=new Simulator("Eduyin_t="+T1,leyendo.getRuta(), gmm, gvrp);
			//							Simulator simulacion1=new Simulator("John_t="+T1,organizado.getRuta(), gmm, gvrp);
			//						} 
			//						else{
			//							Organizador organizado=new Organizador(routingSolver.getRutascompletasTres());
			//							Simulator simulacion1=new Simulator("John_EAB-Semana: "+(i+1),organizado.getRuta(), gmm, gvrp);	
			//						}

		}  // Acá acaba el for de las semanas
		
		
		System.out.println(gmm.getNodes().size());
		for (int i = 0; i < gmm.getNodes().size(); i++) {
			System.out.println("ID: "+gmm.getNodes().get(i).getId());
		}
		
		double main_cost[] = new double[3];
		//double total_cost[] = new double [3];
		for (int i = 0; i < 3; i++) {
			
			Random RNG = new Random(10*i);
			System.out.println("iteracion "+i);
				
			// Genero la siguiente falla
			double NextFailure[] = new double[gmm.getNodes().size()];
			for (int k = 0; k < gmm.getNodes().size(); k++) {
				NextFailure[k] = smallgraphs.get(0).getTmin() + gmm.getNodes().get(k).getD().inverseF(RNG.nextDouble());
			}
			
			// El orden de los eventos de falla
			double sortedFailures[] = NextFailure;
			Arrays.sort(sortedFailures);
			Arrays.asList(NextFailure).indexOf(sortedFailures[0]); // el nodo de la siguiente falla
			
			// La fila de las fallas y los tiempos de fallas
			ArrayList<Integer> fallas = new ArrayList<Integer>();
			ArrayList<Double> tiempo_fallas = new ArrayList<Double>();
			
			//Este loop revisa cada semana
			for (int j = 0; j < smallgraphs.size(); j++) {
				ArrayList<MNode> sitios = new ArrayList<MNode>(gmm.getNodes());
				
				double[] ultimo_t = new double[rutas_todas.get(j).size()];
				double[] sorted_ultima = new double[rutas_todas.get(j).size()];
				
				//System.out.println("Numero de rutas de la semana: "+ rutas_todas.get(j).size());
				for (int k = 0; k < rutas_todas.get(j).size(); k++) { //Voy a revisar todas las rutas de la semana j
					//System.out.println("Tamaño de las rutas: " + rutas_todas.get(j).get(k).size());
					for (int k2 = 0; k2 < rutas_todas.get(j).get(k).size(); k2++) { //Voy a revisar cada visita de cada ruta k de la semana j
						if (NextFailure[rutas_todas.get(j).get(k).get(k2).getEventsSitesID()] < horas_todas.get(j).get(k).get(k2)) { //Si el sitio falló antes de ser reparado
							main_cost[i] = main_cost[i] + gmm.getNodes().get(rutas_todas.get(j).get(k).get(k2).getEventsSitesID()).getCw()*( horas_todas.get(j).get(k).get(k2) - NextFailure[rutas_todas.get(j).get(k).get(k2).getEventsSitesID()] );
						}
						NextFailure[rutas_todas.get(j).get(k).get(k2).getEventsSitesID()] = horas_todas.get(j).get(k).get(k2) + gmm.getNodes().get(rutas_todas.get(j).get(k).get(k2).getEventsSitesID()).getD().inverseF(RNG.nextDouble()); // Actualizo el tiempo de falla
						sitios.remove(rutas_todas.get(j).get(k).get(k2).getEventsSitesID());
					}
					ultimo_t[k] = horas_todas.get(j).get(k).get(horas_todas.get(j).get(k).size()-1);
					sorted_ultima = ultimo_t; 
					Arrays.sort(sorted_ultima);
				}
				
			
				// Reviso si los sitios que no visité fallaron esa semana
				for (int k = 0; k < sitios.size(); k++) {
					//System.out.println("Sitio "+sitios.get(k).getId()+", Tmin: "+smallgraphs.get(0).getTmin()+", T_Falla: " + NextFailure[sitios.get(k).getId()-1]);
					if (NextFailure[sitios.get(k).getId()-1] < smallgraphs.get(j).getTmax()) {
						// TO DO
						// Pegarlo a una ruta actual, calcular el costo y el tiempo en el que lo visita
						//Organizarlos FIFO, barriendo en las rutas cuál llegaría más temprano
						fallas.add(sitios.get(k).getId());
						tiempo_fallas.add(NextFailure[sitios.get(k).getId()]);
						// Actualizar el tiempo de falla
						//NextFailure[sitios.get(k).getId()] =  gmm.getNodes().get(sitios.get(k).getId()).getD().inverseF(RNG.nextDouble()); // Actualizo el tiempo de falla
						
						// Actualizar las semanas siguientes
						
						// i. Quitarla de donde está
						
						// ii. Agregar las nuevas visitas	
					}
				}
				System.out.println("En la semana "+j+" hay "+fallas.size()+" fallas");
				
				ArrayList<Double> s_tiempo_fallas = new ArrayList<Double>();
				s_tiempo_fallas = tiempo_fallas;
				Collections.sort(s_tiempo_fallas);
				boolean continuar = !fallas.isEmpty();
								
				while(continuar){ 
					
					// la ruta a la que le voy a pegar a algo
					System.out.println(sorted_ultima.length);
					int ruta_mod = indexOfArray(ultimo_t, sorted_ultima[0]);
									
					// el momento en el que se hace la última visita
					double tiempo_ultima = sorted_ultima[0];
					
					// El tiempo de viaje a la nueva visita con la distancia del taxista
					double deltaX = gmm.getNodes().get(fallas.get(tiempo_fallas.indexOf(s_tiempo_fallas.get(0)))).getPosx() - gmm.getNodes().get(rutas_todas.get(j).get(ruta_mod).get(horas_todas.get(j).get(ruta_mod).size()-1).getEventsSitesID()).getPosx();
					double deltaY = gmm.getNodes().get(fallas.get(tiempo_fallas.indexOf(s_tiempo_fallas.get(0)))).getPosy() - gmm.getNodes().get(rutas_todas.get(j).get(ruta_mod).get(horas_todas.get(j).get(ruta_mod).size()-1).getEventsSitesID()).getPosy();
		
					double travel_time = (Math.abs(deltaX) + Math.abs(deltaY)) / VEL;
					
					
					if (tiempo_ultima + travel_time < smallgraphs.get(j).getTmax()) { // alcanzo a visitarla dentro de la semana?
						Tres nueva = new Tres(fallas.get(tiempo_fallas.indexOf(s_tiempo_fallas.get(0))),fallas.get(tiempo_fallas.indexOf(s_tiempo_fallas.get(0))), s_tiempo_fallas.get(0) );
						rutas_todas.get(j).get(ruta_mod).add(nueva);
						horas_todas.get(j).get(ruta_mod).add(tiempo_ultima + travel_time);
						main_cost[i] = main_cost[i] + gmm.getNodes().get(rutas_todas.get(j).get(ruta_mod).get(rutas_todas.get(j).get(ruta_mod).size()-1).getEventsSitesID()).getCw()*( horas_todas.get(j).get(ruta_mod).get(rutas_todas.get(j).get(ruta_mod).size()-1) - NextFailure[rutas_todas.get(j).get(ruta_mod).get(rutas_todas.get(j).get(ruta_mod).size()-1).getEventsSitesID()] );
						NextFailure[rutas_todas.get(j).get(ruta_mod).get(rutas_todas.get(j).get(ruta_mod).size()-1).getEventsSitesID()] = horas_todas.get(j).get(ruta_mod).get(rutas_todas.get(j).get(ruta_mod).size()-1) + gmm.getNodes().get(rutas_todas.get(j).get(ruta_mod).get(rutas_todas.get(j).get(ruta_mod).size()-1).getEventsSitesID()).getD().inverseF(RNG.nextDouble()); // Actualizo el tiempo de falla
						
						// La saco de las fallas y tiempo de fallas
						fallas.remove(tiempo_fallas.indexOf(s_tiempo_fallas.get(0)));
						tiempo_fallas.remove(tiempo_fallas.indexOf(s_tiempo_fallas.get(0)));
						//s_tiempo_fallas.remove(0);
						
						//Actualizo la ultima visita
						ultimo_t[ruta_mod] = horas_todas.get(j).get(ruta_mod).get(horas_todas.get(j).get(ruta_mod).size()-1);
						sorted_ultima = ultimo_t; 
						Arrays.sort(sorted_ultima);
											
					}else{
						continuar = false;
					}
					
					if(fallas.isEmpty()){
						continuar = false;
					}
				}
				
			}
			
		}
		
		
		
		// Esto es de John
		timeSimulation=System.currentTimeMillis()-timeSimulation;
		timeTotal=System.currentTimeMillis()-timeTotal;
		System.out.println("--------------Time-----------------------------");
		System.out.println("Total time: "+(timeTotal));
		System.out.println("Total Read: "+(timeRead));
		System.out.println("Total Maintenance: "+(timeMaintenance));
		System.out.println("Total Routing: "+(timeRouting));
		System.out.println("Total Simulation: "+(timeSimulation));
	} 

	public double [] getNs() {
		return ns;
	}

	public static void setNs(double [] nns) {
		ns = nns;
	}

	public int getIdmin() {
		return idmin;
	}

	public static int indexOfArray(double[] array, double key) {
	    int returnvalue = -1;
	    for (int i = 0; i < array.length; ++i) {
	        if (key == array[i]) {
	            returnvalue = i;
	            break;
	        }
	    }
	    return returnvalue;
	}
	
	public void setIdmin(int idmin) {
		this.idmin = idmin;
	}
}