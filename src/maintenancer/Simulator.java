package maintenancer;

import gurobi.GRB;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import router.RGraph;
import umontreal.iro.lecuyer.rng.MRG32k3a;

public class Simulator {
	static PrintWriter writer;
	/**
	 * Number of replicas of the simulations
	 */
	public static final int replicas = 10000;
	/*
	 * Models parameters:
	 * 	Graphs and input data
	 */
	private MGraph siteGraph;
	private RGraph operationsGraph;
	/**
	 * Times of the maintenance operations (sorted in increasing order) (s_i)
	 */
	private double[] eventsTimes;
	/**
	 * Maintenance operations ID (sorted according to the service time) (id 2)
	 */
	private int[] eventsOperationsID;
	/**
	 * Corresponding site of each maintenance operations (also sorted) (id 1)
	 */
	private int[] eventsSitesID;
	/**
	 * Time to next failure of each site.
	 */
	private double[] nextFail;
	private int [] nextFailId;
	/**
	 * Time at when the last maintenance occurred.
	 */
	private double[] lastVisit;
	/**
	 * Cost of the routing policy for each replica.
	 */
	private double[] replicaCosts;	
	/**
	 * Random [0,1] generator. Used to generate random variables realizations.
	 */
	private MRG32k3a rndGenerator;
	
	/**
	 * Constructor of the simulator class.
	 * 
	 * @param routes
	 *            Solution of the RM (replace Object by corresponding data
	 *            structure in which routes are stored)
	 * @param g
	 *            Graph comprised of site nodes
	 * @param g_prime
	 *            Graph comprised of maintenance nodes
	 * @param nInput
	 *            input data
	 */
	public Simulator(String nombre,Route routes, MGraph g, RGraph g_prime){
		rndGenerator = new MRG32k3a();
//		long[]seeed= new long[6];
//		seeed[0]=12345L;
//		seeed[1]=13456L;
//		seeed[2]=12345L;
//		seeed[3]=13456L;
//		seeed[4]=12355L;
//		seeed[5]=13476L;
//		rndGenerator.setSeed(seeed);
		siteGraph = g;
//		System.out.println(g.getNodes().size());
		operationsGraph = g_prime;
//		System.out.println(g_prime.getNodes().size());
		inicializedEvents(routes);
		simulate(nombre);
	}

	/**
	 * Simulation engine. Runs all replicas, store the cost, and compute the
	 * average (i.e., expected cost)
	 * @param nombre 
	 */
	private void simulate(String nombre) {
		replicaCosts = new double[replicas];
		
		double replicaCost,expectedCost = 0;
		double waiting = 0;
		
		//Run all replicas
		for (int i = 0; i < replicas; i++) {
			replicaCost = 0;
			nextFail = new double[siteGraph.getNodes().size()];
			nextFailId = new int[siteGraph.getNodes().size()];
			scheduleFirstFail();
			lastVisit = new double[siteGraph.getNodes().size()];
			double TNOW = 0; 
			int operation = 0;
			int site = 0; 
			// Evaluate the events calendar. 
			for (int j = 0; j < eventsTimes.length; j++) {
				TNOW = eventsTimes[j];
				operation = eventsOperationsID[j];
				
				site = eventsSitesID[j];
				for (int p=0;p<nextFail.length;p++){
					if(nextFailId[p]==eventsSitesID[j]){
						site=p;
					}
				}
//				System.out.println("site_simulate: ->>"+site+" "+nextFailId[site]);
				if(TNOW <= nextFail[site]){
					//Preventive maintenance logic
					replicaCost +=siteGraph.getNodes().get(site).getCpm(); 
					lastVisit[site] = TNOW + siteGraph.getNodes().get(site).getTpm();
				}else{
					//Corrective maintenance logic
					waiting =TNOW-nextFail[site];
					if(waiting<0){System.err.println("Error en waiting! Simulación");}
					replicaCost+= siteGraph.getNodes().get(site).getCcm()+siteGraph.getNodes().get(site).getCw()*waiting;
					lastVisit[site] = TNOW + siteGraph.getNodes().get(site).getCpm();
				}
				scheduleNextFail(site);
			}
			replicaCosts[i] = replicaCost;
			expectedCost+=replicaCost;
		}
		
		System.out.println(nombre+" Total expected cost: " + (expectedCost/replicas));
		
		String rutaF = "./data/";
		String nombreF = nombre;
		File directorioFacturas = new File(rutaF);
		if(!directorioFacturas.exists())
			directorioFacturas.mkdirs();
		File file = new File(rutaF+nombreF);
		try {
			writer = new PrintWriter(new FileWriter(file,true));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		for(int i=0;i<replicaCosts.length;i++){
			writer.println(replicaCosts[i]);
		}
		writer.close();
	}

	/**
	 * Before starting the simulation, the first fail should be programmed in
	 * the simulation calendar.
	 */
	private void scheduleFirstFail() {
		double rndNumber=0;
		for (int i = 0; i < nextFail.length; i++) {
			rndNumber = rndGenerator.nextDouble();
//			System.out.println("tamaño grafo: "+siteGraph.getNodes().size());
			nextFail[i] = siteGraph.getNodes().get(i).getD().inverseF(rndNumber);
			nextFailId[i]=siteGraph.getNodes().get(i).getId();
		}
	}
	
	/**
	 * Schedules a fail in the simulation calendar for the site given as a
	 * parameter Precondition: LastVisit[site] should be updated before calling
	 * this method!
	 * 
	 * @param site i\in\G
	 */
	private void scheduleNextFail(int site){
		double rndNumber= rndGenerator.nextDouble();
		double timeToNextFail = siteGraph.getNodes().get(site).getD().inverseF(rndNumber);
		nextFail[site] = lastVisit[site]+timeToNextFail;
	}

	/**
	 * Initialize the simulation calendar with the routes of the RM or benchmark
	 * 
	 * @param routes
	 *            data structure that contains the routes.
	 */
	private void inicializedEvents(Route routes) {
		eventsOperationsID = new int[operationsGraph.getNodes().size()-1];
		eventsSitesID = new int[operationsGraph.getNodes().size()-1];
		eventsTimes = new double[operationsGraph.getNodes().size()-1];
//		System.out.println("Tamaño: "+operationsGraph.getNodes().size()-1);
		for(int i=0;i<eventsTimes.length;i++){
			eventsOperationsID[i]=routes.getRoute().get(i).getEventsOperationsID();
			eventsSitesID[i]=routes.getRoute().get(i).getEventsSitesID();
			eventsTimes[i]=routes.getRoute().get(i).getEventTimes();
		}
		
		
		/**
		 * La idea es hacer un calendario de eventos del ruteo. Es decir
		 * los tres arreglos tienen tantas obsevaciones como operaciones
		 * de mantenimento, y para cada fila, lo que debería hacer es escribir
		 * el tiempo de servicio, el id de la operación de mantenimiento y el ID 
		 * del site correspondiente. 
		 * 
		 * Finalmente, deber ordenar los tres arreglos de acuerdo al tiempo de ocurrencia. 
		 * eg. Si los conjuntos y  rutas son
		 * o_1={1,2,3}
		 * o_2={4,5,6}
		 * o_4={7,8,9}
		 * Rutas / tiempos de visita (variable s_i)
		 * r1->0-1-4-7-0  / [0.5, 6, 14]
		 * r2->0-2-0    / [4.5]
		 * r3->0-3-5-0  / [1, 9]
		 * r4->0-6-8-0   / [3, 20]
		 * r5->0-9-0   /  [0.8]
		 *  
		 * Los vectores "events" son 
		 *  times	|	operation	|	site 
		 *  0.5		|	1			|	1
		 *  0.8		|	9			|	3	
		 *  1.0		|	3			|	1	
		 *  3.0		|	6			|	2
		 *  4.5		|	2			|	1
		 *  6.0		|	4			|	2
		 *  9.0		|	5			|	2
		 *  14.0	|	7			|	3
		 *  20.0	|	9			|	3
		 *    
		 * ORDENADOS  por tiempo
		 */
	}	
}
