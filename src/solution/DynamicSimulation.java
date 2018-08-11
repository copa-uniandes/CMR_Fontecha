package solution;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

import maintenancer.MGraph;
import maintenancer.MNode;
import maintenancer.Tres;
import router.RGraph;

public class DynamicSimulation {
	
	
	public DynamicSimulation(MGraph gmm, ArrayList<RGraph> smallgraphs, ArrayList<ArrayList<ArrayList<Tres>>> rutas_todas2, ArrayList<ArrayList<ArrayList<Double>>> horas_todas2, double VEL, int iteraciones) throws FileNotFoundException, UnsupportedEncodingException{
		
		PrintWriter writer = new PrintWriter("Dynamic_costs.txt", "UTF-8");
		PrintWriter writer2 = new PrintWriter("Dynamic_routes.txt", "UTF-8");
		System.out.println(gmm.getNodes().size());
		//for (int i = 0; i < gmm.getNodes().size(); i++) {
		//	System.out.println("ID: "+gmm.getNodes().get(i).getId());
		//}
		
		//Imprimir las rutas normales
		writer2.println("Rutas Base");
		for (int i = 0; i < rutas_todas2.size(); i++) { //iterar sobre todas las semanas
			writer2.println("Semana "+i);
			for (int j = 0; j < rutas_todas2.get(i).size(); j++) { // itera sobre las rutas
				String linea = new String();
				linea = j + "\t";
				for (int k = 0; k < rutas_todas2.get(i).get(j).size(); k++) {
					linea = linea + rutas_todas2.get(i).get(j).get(k).getEventsSitesID() + "\t";
				}
				writer2.println(linea);
			}
			
		}
				
		int[] cycletimes = getCycles(smallgraphs, gmm);
		
		double main_cost[] = new double[iteraciones];
		//double total_cost[] = new double [3];
		for (int i = 0; i < iteraciones; i++) {
			
			ArrayList<ArrayList<ArrayList<Tres>>> rutas_todas = new ArrayList<ArrayList<ArrayList<Tres>>>(rutas_todas2);
			ArrayList<ArrayList<ArrayList<Double>>> horas_todas = new ArrayList<ArrayList<ArrayList<Double>>>(horas_todas2);
			
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
				System.out.println("Semana "+j);
				ArrayList<MNode> sitios = new ArrayList<MNode>(gmm.getNodes());
				double[] ultimo_t = new double[rutas_todas.get(j).size()];
				double[] sorted_ultima = new double[rutas_todas.get(j).size()];
				
				//System.out.println("Numero de rutas de la semana: "+ rutas_todas.get(j).size());
				for (int k = 0; k < rutas_todas.get(j).size(); k++) { //Voy a revisar todas las rutas de la semana j
					//System.out.println("Tamaño de las rutas: " + rutas_todas.get(j).get(k).size());
					for (int k2 = 0; k2 < rutas_todas.get(j).get(k).size(); k2++) { //Voy a revisar cada visita de cada ruta k de la semana j
						if(rutas_todas.get(j).get(k).get(k2).getEventsSitesID() != 0){
							if (NextFailure[rutas_todas.get(j).get(k).get(k2).getEventsSitesID()-1] < horas_todas.get(j).get(k).get(k2) ) { //Si el sitio falló antes de ser reparado
								main_cost[i] = main_cost[i] + gmm.getNodes().get(rutas_todas.get(j).get(k).get(k2).getEventsSitesID()).getCw()*( horas_todas.get(j).get(k).get(k2) - NextFailure[rutas_todas.get(j).get(k).get(k2).getEventsSitesID()-1] );
							}
							NextFailure[rutas_todas.get(j).get(k).get(k2).getEventsSitesID()-1] = horas_todas.get(j).get(k).get(k2) + gmm.getNodes().get(rutas_todas.get(j).get(k).get(k2).getEventsSitesID()-1).getD().inverseF(RNG.nextDouble()); // Actualizo el tiempo de falla
							//System.out.println(rutas_todas.get(j).get(k).get(k2).getEventsSitesID());
							for (int l = 0; l < sitios.size(); l++) {
								if (sitios.get(l).getId() == rutas_todas.get(j).get(k).get(k2).getEventsSitesID()) {
									sitios.remove(l);
								}
							}
						}					
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
						tiempo_fallas.add(NextFailure[sitios.get(k).getId()-1]);
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
					int ruta_mod = indexOfArray(ultimo_t, sorted_ultima[0]);
									
					// el momento en el que se hace la última visita
					double tiempo_ultima = sorted_ultima[0];
					
					// El tiempo de viaje a la nueva visita con la distancia del taxista
					double deltaX = gmm.getNodes().get(fallas.get(tiempo_fallas.indexOf(s_tiempo_fallas.get(0)))-1).getPosx() - gmm.getNodes().get(rutas_todas.get(j).get(ruta_mod).get(horas_todas.get(j).get(ruta_mod).size()-1).getEventsSitesID()-1).getPosx();
					double deltaY = gmm.getNodes().get(fallas.get(tiempo_fallas.indexOf(s_tiempo_fallas.get(0)))-1).getPosy() - gmm.getNodes().get(rutas_todas.get(j).get(ruta_mod).get(horas_todas.get(j).get(ruta_mod).size()-1).getEventsSitesID()-1).getPosy();
		
					double travel_time = (Math.abs(deltaX) + Math.abs(deltaY)) / VEL;
					
					if (tiempo_ultima + travel_time < smallgraphs.get(j).getTmax()) { // alcanzo a visitarla dentro de la semana?
						System.out.println("Agregué el sitio " + fallas.get(tiempo_fallas.indexOf(s_tiempo_fallas.get(0)))  + " a la ruta " + ruta_mod);
						Tres nueva = new Tres(fallas.get(tiempo_fallas.indexOf(s_tiempo_fallas.get(0))),fallas.get(tiempo_fallas.indexOf(s_tiempo_fallas.get(0))), s_tiempo_fallas.get(0) );
						rutas_todas.get(j).get(ruta_mod).add(nueva);
						horas_todas.get(j).get(ruta_mod).add(tiempo_ultima + travel_time);
						main_cost[i] = main_cost[i] + gmm.getNodes().get(rutas_todas.get(j).get(ruta_mod).get(rutas_todas.get(j).get(ruta_mod).size()-1).getEventsSitesID()).getCw()*( horas_todas.get(j).get(ruta_mod).get(rutas_todas.get(j).get(ruta_mod).size()-1) - NextFailure[rutas_todas.get(j).get(ruta_mod).get(rutas_todas.get(j).get(ruta_mod).size()-1).getEventsSitesID()] );
						NextFailure[rutas_todas.get(j).get(ruta_mod).get(rutas_todas.get(j).get(ruta_mod).size()-1).getEventsSitesID()-1] = horas_todas.get(j).get(ruta_mod).get(rutas_todas.get(j).get(ruta_mod).size()-1) + gmm.getNodes().get(rutas_todas.get(j).get(ruta_mod).get(rutas_todas.get(j).get(ruta_mod).size()-1).getEventsSitesID()).getD().inverseF(RNG.nextDouble()); // Actualizo el tiempo de falla
						
						// La saco de las fallas y tiempo de fallas
						fallas.remove(tiempo_fallas.indexOf(s_tiempo_fallas.get(0)));
						tiempo_fallas.remove(tiempo_fallas.indexOf(s_tiempo_fallas.get(0)));
						//s_tiempo_fallas.remove(0);
						
						//Actualizo la ultima visita
						ultimo_t[ruta_mod] = horas_todas.get(j).get(ruta_mod).get(horas_todas.get(j).get(ruta_mod).size()-1);
						sorted_ultima = ultimo_t; 
						Arrays.sort(sorted_ultima);
						
						
						// Borrarlo de donde está
						for (int l = j; l < rutas_todas.size(); l++) { // itera sobre todas las semanas
							for (int k = 0; k < rutas_todas.get(l).size(); k++) { //itera sobre todas las rutas de una semana
								for (int k2 = 0; k2 < rutas_todas.get(l).get(k).size(); k2++) { //itera sobre todas las visitas de una ruta
									if(rutas_todas.get(l).get(k).get(k2).getEventsSitesID() == nueva.getEventsSitesID()){
										rutas_todas.get(l).get(k).remove(k2);
										horas_todas.get(l).get(k).remove(k2);
									}
								}
							}
						}
						
						
						//Agregarlo donde debe estar
						for (int k = j; k < rutas_todas.size(); k = k + cycletimes[nueva.getEventsSitesID()-1]) {
							double[] ultimo_t_nueva = new double[rutas_todas.get(k).size()];
							double[] sorted_ultima_nueva = new double[rutas_todas.get(k).size()];
							
							//Obtengo la ultima visita de cada ruta de la nueva semana
							for (int k2 = 0; k2 < rutas_todas.get(k).size(); k++) { //Voy a revisar todas las rutas de la semana k
								ultimo_t_nueva[k] = horas_todas.get(k).get(k2).get(horas_todas.get(k).get(k2).size()-1);
								sorted_ultima_nueva = ultimo_t_nueva; 
								Arrays.sort(sorted_ultima);
							}
							
							//La ruta a la que se le va a agregar la nueva operacion
							int ruta_mod_nueva = indexOfArray(ultimo_t_nueva, sorted_ultima_nueva[0]);
							
							// el momento en el que se hace la última visita
							double tiempo_ultima_n = sorted_ultima_nueva[0];
							
							double deltaX2 = gmm.getNodes().get(nueva.getEventsSitesID()-1).getPosx() - gmm.getNodes().get(rutas_todas.get(k).get(ruta_mod_nueva).get(horas_todas.get(k).get(ruta_mod_nueva).size()-1).getEventsSitesID()-1).getPosx();
							double deltaY2 = gmm.getNodes().get(nueva.getEventsSitesID()-1).getPosy() - gmm.getNodes().get(rutas_todas.get(k).get(ruta_mod_nueva).get(horas_todas.get(k).get(ruta_mod_nueva).size()-1).getEventsSitesID()-1).getPosy();
				
							double travel_time2 = (Math.abs(deltaX2) + Math.abs(deltaY2)) / VEL;
							
							Tres nueva2 = new Tres(nueva.getEventsSitesID(),nueva.getEventsOperationsID(), tiempo_ultima_n + travel_time2);
							rutas_todas.get(k).get(ruta_mod_nueva).add(nueva2);
							horas_todas.get(k).get(ruta_mod_nueva).add(tiempo_ultima + travel_time2);
							
						}
											
					}else{
						continuar = false;
					}
					
					if(fallas.isEmpty()){
						continuar = false;
					}
				}
				
			}
			
			writer2.println();
			writer2.println("Iteración " + i);
			for (int i2 = 0; i2 < rutas_todas.size(); i2++) { //iterar sobre todas las semanas
				writer2.println("Semana "+i);
				for (int j = 0; j < rutas_todas.get(i2).size(); j++) { // itera sobre las rutas
					String linea = new String();
					linea = j + "\t";
					for (int k = 0; k < rutas_todas.get(i2).get(j).size(); k++) {
						linea = linea + rutas_todas.get(i2).get(j).get(k).getEventsSitesID() + "\t";
					}
					writer2.println(linea);
				}
				
			}
			
			writer.println(i + "\t" + main_cost[i]);
		}
		writer.close();
		writer2.close();
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
	
	public int[] getCycles(ArrayList<RGraph> smallgraphs, MGraph gmm){
		int[] cycles = new int[gmm.getNodes().size()];
		
		for (int i = 0; i < cycles.length; i++) {
			boolean continuar = true;
			while(continuar){
				for(int j = 0; j < smallgraphs.get(i).getNodes().size();j++){
					if(smallgraphs.get(i).getNodes().get(j).getId1() == (j+1)){
						cycles[i] = (int) (smallgraphs.get(i).getNodes().get(j).getCycletime()/7);
						continuar = false;
					}
				}
			}
			
		}
		
		
		return cycles;
	}
	
}
