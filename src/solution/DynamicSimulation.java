package solution;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

import maintenancer.MGraph;
import maintenancer.Tres;
import router.RGraph;
import router.RNode;

public class DynamicSimulation {

	public DynamicSimulation(RGraph gvrp, MGraph gmm, ArrayList<RGraph> smallgraphs, ArrayList<ArrayList<ArrayList<Tres>>> rutas_todas2, ArrayList<ArrayList<ArrayList<Double>>> horas_todas2, double VEL, int iteraciones) throws FileNotFoundException, UnsupportedEncodingException{
		PrintWriter writer = new PrintWriter("Dynamic_simulation_results.txt", "UTF-8");
		PrintWriter writer2 = new PrintWriter("Dynamic_simulation_routes.txt", "UTF-8");
		//System.out.println(gmm.getNodes().size());
		//for (int i = 0; i < gmm.getNodes().size(); i++) {
		//	System.out.println("ID: "+gmm.getNodes().get(i).getId());
		//}
		
		for (int i = 0; i < smallgraphs.size(); i++) {
			System.out.println("El min de la semana " + i + " es " + smallgraphs.get(i).getTmin() + " y el max es " + smallgraphs.get(i).getTmax());
		}
		
		
		
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
		
		int cuantas_son = 0;
		for (int i2 = 0; i2 < rutas_todas2.size(); i2++) { //iterar sobre todas las semanas
			for (int j = 0; j < rutas_todas2.get(i2).size(); j++) { // itera sobre las rutas
				cuantas_son = cuantas_son + rutas_todas2.get(i2).get(j).size() - 1;
			}				
		}
		System.out.println(cuantas_son);
		
		System.out.println("termino de imprimir rutas normales");
		int[] cycletimes = getCycles(smallgraphs, gmm,gvrp);
		System.out.println("Salio de getcycles");
		
		
		
		// Para contar el promedio de la probabilidad de falla
		double prob_falla[] = new double[iteraciones];

		// Para contar el promedio del costo por tiemo de ciclo
		double costo_ciclo[] = new double[iteraciones];
		
		// Para contar cuantas fallaron
		int las_que_fallaron[] = new int[iteraciones];
		
		// Para contar cuantas hice
		int las_que_hice[] = new int[iteraciones];
				
		double main_cost[] = new double[iteraciones];
		//double total_cost[] = new double [3];
		
		
		for (int i = 0; i < iteraciones; i++) {
			Random RNG = new Random(1000000*i);
			
			//for (int j = 0; j < 50; j++) {
			//	System.out.println(gmm.getNodebyID(80).getD().inverseF(RNG.nextDouble()));
			//}

			// Para contar el promedio de la probabilidad de falla
			double prob_falla_prom = 0;
			int denom_prob_falla = 0;
			
			// Para contar el promedio del costo de espera
			double costo_ciclo_prom = 0;
			int denom_costo_ciclo = 0;
			
			// Para contar cuantas fallaron
			int cuantas_fallaron = 0;
			
			// Para contar cuantas hice
			int cuantas_hice = 0;
			
			ArrayList<ArrayList<ArrayList<Tres>>> rutas_todas = new ArrayList<ArrayList<ArrayList<Tres>>>();
			ArrayList<ArrayList<ArrayList<Double>>> horas_todas = new ArrayList<ArrayList<ArrayList<Double>>>();
			
			for (int j = 0; j < rutas_todas2.size(); j++) { //el numero de semanas
				ArrayList<ArrayList<Tres>> nuevas_rutas_semana = new ArrayList<ArrayList<Tres>>();
				ArrayList<ArrayList<Double>> nuevas_horas_semana = new ArrayList<ArrayList<Double>>();
				for (int j2 = 0; j2 < rutas_todas2.get(j).size(); j2++) { //el número de rutas
					ArrayList<Tres> nueva_ruta = new ArrayList<Tres>();
					ArrayList<Double> nueva_horas = new ArrayList<Double>();
					for (int k = 0; k < rutas_todas2.get(j).get(j2).size(); k++) { //las visitas de cada ruta
						Tres copia = new Tres(rutas_todas2.get(j).get(j2).get(k).getEventsSitesID(), rutas_todas2.get(j).get(j2).get(k).getEventsOperationsID(), rutas_todas2.get(j).get(j2).get(k).getEventTimes());
						nueva_ruta.add(copia);
						nueva_horas.add(horas_todas2.get(j).get(j2).get(k));
					}
					nuevas_rutas_semana.add(nueva_ruta);
					nuevas_horas_semana.add(nueva_horas);
				}
				rutas_todas.add(nuevas_rutas_semana);
				horas_todas.add(nuevas_horas_semana);
			}
			
		
			//System.out.println("iteracion "+i);
			
			//Genero el último tiempo de atención
			double LastVisit[] = new double[gmm.getNodes().size()];
			for (int j = 0; j < LastVisit.length; j++) {
				LastVisit[j] = 0;
			}
			
			
			
			// Genero la siguiente falla
			double NextFailure[] = new double[gmm.getNodes().size()];
			double sortedFailures[] = new double[gmm.getNodes().size()];
			for (int k = 0; k < gmm.getNodes().size(); k++) {
				double nexttime = smallgraphs.get(0).getTmin() + gmm.getNodebyID(k+1).getD().inverseF(RNG.nextDouble());
				NextFailure[k] = nexttime;
				sortedFailures[k] = nexttime;
			}
			
			// El orden de los eventos de falla
			Arrays.sort(sortedFailures);
			Arrays.asList(NextFailure).indexOf(sortedFailures[0]); // el nodo de la siguiente falla
			
			// La fila de las fallas y los tiempos de fallas
			ArrayList<Integer> fallas = new ArrayList<Integer>();
			ArrayList<Double> tiempo_fallas = new ArrayList<Double>();
			ArrayList<Double> s_tiempo_fallas = new ArrayList<Double>();
			
			//Este loop revisa cada semana
			for (int j = 0; j < 0*2 + 1*(smallgraphs.size()); j++) {
				//System.out.println("Semana "+j);
				ArrayList<Integer> sitios = new ArrayList<Integer>();
				for (int k = 0; k < gmm.getNodes().size(); k++) {
					sitios.add(k+1);
				}
				double[] ultimo_t = new double[rutas_todas.get(j).size()];
				double[] sorted_ultima = new double[rutas_todas.get(j).size()];
				
				//System.out.println("Numero de rutas de la semana: "+ rutas_todas.get(j).size());
				for (int k = 0; k < rutas_todas.get(j).size(); k++) { //Voy a revisar todas las rutas de la semana j
					for (int k2 = 0; k2 < rutas_todas.get(j).get(k).size(); k2++) { //Voy a revisar cada visita de cada ruta k de la semana j
						
						// Si está en las fallas la saco de la fila
						if(fallas.contains(rutas_todas.get(j).get(k).get(k2).getEventsSitesID())){
							s_tiempo_fallas.remove(tiempo_fallas.get(fallas.indexOf(rutas_todas.get(j).get(k).get(k2).getEventsSitesID())));
							tiempo_fallas.remove(fallas.indexOf(rutas_todas.get(j).get(k).get(k2).getEventsSitesID()));
							fallas.remove(fallas.indexOf(rutas_todas.get(j).get(k).get(k2).getEventsSitesID()));
							cuantas_fallaron--;
						}
						
						
						//	No hago nada para el nodo 0
						if(rutas_todas.get(j).get(k).get(k2).getEventsSitesID() != 0){
							//if(LastVisit[rutas_todas.get(j).get(k).get(k2).getEventsSitesID()-1] != 0){
							//	double prob_falla_i = gmm.getNodebyID(rutas_todas.get(j).get(k).get(k2).getEventsSitesID()).getD().cdf(horas_todas.get(j).get(k).get(k2) - LastVisit[rutas_todas.get(j).get(k).get(k2).getEventsSitesID()-1]);
							//	//writer.println("Visita \t " + horas_todas.get(j).get(k).get(k2) + "\t Ultima: \t " + LastVisit[rutas_todas.get(j).get(k).get(k2).getEventsSitesID()-1] + "\t Prob \t " +  gmm.getNodebyID(rutas_todas.get(j).get(k).get(k2).getEventsSitesID()).getD().cdf(horas_todas.get(j).get(k).get(k2) - LastVisit[rutas_todas.get(j).get(k).get(k2).getEventsSitesID()-1]));
							//	prob_falla_prom = prob_falla_prom + prob_falla_i;
							//	denom_prob_falla++;
							//}
							
							if (NextFailure[rutas_todas.get(j).get(k).get(k2).getEventsSitesID()-1] < horas_todas.get(j).get(k).get(k2) ) { //Si el sitio falló antes de ser reparado
								//System.out.println("Falló el sitio preventivamente " + rutas_todas.get(j).get(k).get(k2).getEventsSitesID() + " en la semana " + j);
								if(LastVisit[rutas_todas.get(j).get(k).get(k2).getEventsSitesID()-1] != 0){ // Si ya lo he visitado
									//double costo_ciclo_i = ( gmm.getNodebyID(rutas_todas.get(j).get(k).get(k2).getEventsSitesID()).getCw() * (horas_todas.get(j).get(k).get(k2) - NextFailure[rutas_todas.get(j).get(k).get(k2).getEventsSitesID()-1]) + gmm.getNodebyID(rutas_todas.get(j).get(k).get(k2).getEventsSitesID()).getCcm() ) / ( horas_todas.get(j).get(k).get(k2) - LastVisit[rutas_todas.get(j).get(k).get(k2).getEventsSitesID()-1]);  
											 // / ( horas_todas.get(j).get(k).get(k2) - LastVisit[rutas_todas.get(j).get(k).get(k2).getEventsSitesID()-1]);
									//System.out.println(horas_todas.get(j).get(k).get(k2) - LastVisit[rutas_todas.get(j).get(k).get(k2).getEventsSitesID()-1]);
									costo_ciclo_prom = costo_ciclo_prom + (horas_todas.get(j).get(k).get(k2) - NextFailure[rutas_todas.get(j).get(k).get(k2).getEventsSitesID()-1]);
									denom_costo_ciclo++;
									main_cost[i] = main_cost[i] + ((gmm.getNodebyID(rutas_todas.get(j).get(k).get(k2).getEventsSitesID()).getCw() * (horas_todas.get(j).get(k).get(k2) - NextFailure[rutas_todas.get(j).get(k).get(k2).getEventsSitesID()-1]) + gmm.getNodebyID(rutas_todas.get(j).get(k).get(k2).getEventsSitesID()).getCcm())/(horas_todas.get(j).get(k).get(k2) - LastVisit[rutas_todas.get(j).get(k).get(k2).getEventsSitesID()-1]));
									//System.out.println(horas_todas.get(j).get(k).get(k2) - LastVisit[rutas_todas.get(j).get(k).get(k2).getEventsSitesID()-1]);
								}else{
									costo_ciclo_prom = costo_ciclo_prom + (horas_todas.get(j).get(k).get(k2) - NextFailure[rutas_todas.get(j).get(k).get(k2).getEventsSitesID()-1]);
									denom_costo_ciclo++;
									main_cost[i] = main_cost[i] + ((gmm.getNodebyID(rutas_todas.get(j).get(k).get(k2).getEventsSitesID()).getCw() * (horas_todas.get(j).get(k).get(k2) - NextFailure[rutas_todas.get(j).get(k).get(k2).getEventsSitesID()-1]) + gmm.getNodebyID(rutas_todas.get(j).get(k).get(k2).getEventsSitesID()).getCcm())/(gvrp.getNodes().get(rutas_todas.get(j).get(k).get(k2).getEventsSitesID()-1).getCycletime()));
									//System.out.println(gvrp.getNodes().get(rutas_todas.get(j).get(k).get(k2).getEventsSitesID()-1).getCycletime());
								}
								cuantas_fallaron++;
								
								NextFailure[rutas_todas.get(j).get(k).get(k2).getEventsSitesID()-1] = horas_todas.get(j).get(k).get(k2) + gmm.getNodebyID(rutas_todas.get(j).get(k).get(k2).getEventsSitesID()).getTcm() + gmm.getNodebyID(rutas_todas.get(j).get(k).get(k2).getEventsSitesID()).getD().inverseF(RNG.nextDouble()); // Actualizo el tiempo de falla
							}else{ // Si el sitio no fallo, mantenimiento preventivo
								if(LastVisit[rutas_todas.get(j).get(k).get(k2).getEventsSitesID()-1] != 0){ // Si el sitio ya se visitó
									main_cost[i] = main_cost[i] + ((gmm.getNodebyID(rutas_todas.get(j).get(k).get(k2).getEventsSitesID()).getCpm())/(horas_todas.get(j).get(k).get(k2) - LastVisit[rutas_todas.get(j).get(k).get(k2).getEventsSitesID()-1]));
									// OJO CON ESTE
									//if(horas_todas.get(j).get(k).get(k2) - LastVisit[rutas_todas.get(j).get(k).get(k2).getEventsSitesID()-1] <= 0){
									//	System.out.println("Problema en la semana " + j  + " la ruta " + k  + " la operacion con ID " + rutas_todas.get(j).get(k).get(k2).getEventsSitesID() + " el ciclo es " + (horas_todas.get(j).get(k).get(k2) - LastVisit[rutas_todas.get(j).get(k).get(k2).getEventsSitesID()-1]));
									//}
									//System.out.println(horas_todas.get(j).get(k).get(k2) - LastVisit[rutas_todas.get(j).get(k).get(k2).getEventsSitesID()-1]);
									//System.out.println(rutas_todas.get(j).get(k).get(k2).getEventsSitesID()  +"\t" +  rutas_todas.get(j).get(k).get(k2).getEventTimes()  + "\t" + horas_todas.get(j).get(k).get(k2) + "\t" + LastVisit[rutas_todas.get(j).get(k).get(k2).getEventsSitesID()-1] + "\t" + j + "\t" + k + "\t" + k2);
								}else{
									main_cost[i] = main_cost[i] + ((gmm.getNodebyID(rutas_todas.get(j).get(k).get(k2).getEventsSitesID()).getCpm())/(gvrp.getNodes().get(rutas_todas.get(j).get(k).get(k2).getEventsSitesID()-1).getCycletime()));
									//System.out.println(gvrp.getNodes().get(rutas_todas.get(j).get(k).get(k2).getEventsSitesID()-1).getCycletime());
								}
								
								NextFailure[rutas_todas.get(j).get(k).get(k2).getEventsSitesID()-1] = horas_todas.get(j).get(k).get(k2) + gmm.getNodebyID(rutas_todas.get(j).get(k).get(k2).getEventsSitesID()).getTpm() + gmm.getNodebyID(rutas_todas.get(j).get(k).get(k2).getEventsSitesID()).getD().inverseF(RNG.nextDouble()); // Actualizo el tiempo de falla
							}
							LastVisit[rutas_todas.get(j).get(k).get(k2).getEventsSitesID()-1] = horas_todas.get(j).get(k).get(k2);
							//System.out.println(rutas_todas.get(j).get(k).get(k2).getEventsSitesID());
							for (int l = 0; l < sitios.size(); l++) {
								if (sitios.get(l) == rutas_todas.get(j).get(k).get(k2).getEventsSitesID()) {
									sitios.remove(l);
								}
							}
						}					
					}
					ultimo_t[k] = horas_todas.get(j).get(k).get(horas_todas.get(j).get(k).size()-1);
					sorted_ultima[k] = horas_todas.get(j).get(k).get(horas_todas.get(j).get(k).size()-1);
					
					
				}
				Arrays.sort(sorted_ultima);
				
				//for (int k = 0; k < sorted_ultima.length; k++) {
				//	System.out.println(ultimo_t[k] + "\t" + sorted_ultima[k]);
				//}

				// Reviso si los sitios que no visité fallaron esa semana
				for (int k = 0; k < sitios.size(); k++) {
					//System.out.println("Sitio "+sitios.get(k).getId()+", Tmin: "+smallgraphs.get(0).getTmin()+", T_Falla: " + NextFailure[sitios.get(k).getId()-1]);
					if (NextFailure[sitios.get(k)-1] < smallgraphs.get(j).getTmax() && !fallas.contains(sitios.get(k))) {
						// TO DO
						// Pegarlo a una ruta actual, calcular el costo y el tiempo en el que lo visita
						//Organizarlos FIFO, barriendo en las rutas cuál llegaría más temprano
						//System.out.println("Falló el sitio " + sitios.get(k).getId() + " en la semana " + j);
						cuantas_fallaron++;
						fallas.add(sitios.get(k));
						tiempo_fallas.add(NextFailure[sitios.get(k)-1]);
						s_tiempo_fallas.add(NextFailure[sitios.get(k)-1]);
						// Actualizar el tiempo de falla
						//NextFailure[sitios.get(k).getId()] =  gmm.getNodes().get(sitios.get(k).getId()).getD().inverseF(RNG.nextDouble()); // Actualizo el tiempo de falla
						
						// Actualizar las semanas siguientes
						
						// i. Quitarla de donde está
						
						// ii. Agregar las nuevas visitas	
					}
				}
				//System.out.println("En la semana "+j+" hay "+fallas.size()+" fallas");
				
				Collections.sort(s_tiempo_fallas);
				boolean continuar = !fallas.isEmpty();
				
				//for (int k = 0; k < fallas.size(); k++) {
				//	System.out.println("ID "+fallas.get(k) + "\t T_Falla " + tiempo_fallas.get(k) + "\t T_s " + s_tiempo_fallas.get(k) + "\t ID_s " + fallas.get(tiempo_fallas.indexOf(s_tiempo_fallas.get(k))) );
				//}
								
				while(continuar){ 
					
					// la ruta a la que le voy a pegar a algo
					int ruta_mod = indexOfArray(ultimo_t, sorted_ultima[0]);
					//int ruta_mod2 = indexOfArray(ultimo_t, sorted_ultima[1]);
					//if (rutas_todas.get(j).get(ruta_mod).size() == 1) {
					//	ruta_mod = ruta_mod2;
					//}
				
					// el momento en el que se hace la última visita
					double tiempo_ultima = sorted_ultima[0];
					
					double deltaX = 0;
					double deltaY = 0;
					
					if (rutas_todas.get(j).get(ruta_mod).get(horas_todas.get(j).get(ruta_mod).size()-1).getEventsSitesID() == 0) {
						deltaX = gmm.getNodebyID(fallas.get(tiempo_fallas.indexOf(s_tiempo_fallas.get(0)))).getPosx() - 114; 
						deltaY = gmm.getNodebyID(fallas.get(tiempo_fallas.indexOf(s_tiempo_fallas.get(0)))).getPosy() - 103;
					}else{
						deltaX = gmm.getNodebyID(fallas.get(tiempo_fallas.indexOf(s_tiempo_fallas.get(0)))).getPosx() - gmm.getNodebyID(rutas_todas.get(j).get(ruta_mod).get(horas_todas.get(j).get(ruta_mod).size()-1).getEventsSitesID()).getPosx();
						deltaY = gmm.getNodebyID(fallas.get(tiempo_fallas.indexOf(s_tiempo_fallas.get(0)))).getPosy() - gmm.getNodebyID(rutas_todas.get(j).get(ruta_mod).get(horas_todas.get(j).get(ruta_mod).size()-1).getEventsSitesID()).getPosy();
					}
									
					double travel_time = (0.170*(Math.abs(deltaX) + Math.abs(deltaY)) / VEL); //+ getNodeFromSGbyID(smallgraphs, rutas_todas.get(j).get(ruta_mod).get(horas_todas.get(j).get(ruta_mod).size()-1).getEventsSitesID()).getExpectedservicetime();
					
					double arrival_time = Math.max(tiempo_ultima + travel_time, s_tiempo_fallas.get(0) + travel_time);
					//System.out.println("Voy a intentar agregar un nodo a una ruta en la semana " + j + " y va a llegar en el momento " +  arrival_time + " pero el máximo es " + smallgraphs.get(j).getTmax() + " y el mínimo es " + smallgraphs.get(j).getTmin());
					if (arrival_time < smallgraphs.get(j).getTmax()) { // alcanzo a visitarla dentro de la semana?
						Tres nueva = new Tres(fallas.get(tiempo_fallas.indexOf(s_tiempo_fallas.get(0))),fallas.get(tiempo_fallas.indexOf(s_tiempo_fallas.get(0))), s_tiempo_fallas.get(0) );
						//System.out.println("Agregue un nodo a una ruta");
						rutas_todas.get(j).get(ruta_mod).add(nueva);
						horas_todas.get(j).get(ruta_mod).add(arrival_time);
						
						if(LastVisit[fallas.get(tiempo_fallas.indexOf(s_tiempo_fallas.get(0)))-1] != 0){
							prob_falla_prom = prob_falla_prom + gmm.getNodebyID(fallas.get(tiempo_fallas.indexOf(s_tiempo_fallas.get(0)))).getD().cdf(arrival_time - LastVisit[fallas.get(tiempo_fallas.indexOf(s_tiempo_fallas.get(0)))-1]);
							denom_prob_falla++;
							
							costo_ciclo_prom =  costo_ciclo_prom +  (horas_todas.get(j).get(ruta_mod).get(rutas_todas.get(j).get(ruta_mod).size()-1) - NextFailure[rutas_todas.get(j).get(ruta_mod).get(rutas_todas.get(j).get(ruta_mod).size()-1).getEventsSitesID()-1]);
							denom_costo_ciclo++;
							main_cost[i] = main_cost[i] + ((gmm.getNodebyID(rutas_todas.get(j).get(ruta_mod).get(rutas_todas.get(j).get(ruta_mod).size()-1).getEventsSitesID()).getCw() * (horas_todas.get(j).get(ruta_mod).get(rutas_todas.get(j).get(ruta_mod).size()-1) - NextFailure[rutas_todas.get(j).get(ruta_mod).get(rutas_todas.get(j).get(ruta_mod).size()-1).getEventsSitesID()-1]) +gmm.getNodebyID(rutas_todas.get(j).get(ruta_mod).get(rutas_todas.get(j).get(ruta_mod).size()-1).getEventsSitesID()).getCcm())/(arrival_time - LastVisit[fallas.get(tiempo_fallas.indexOf(s_tiempo_fallas.get(0)))-1])); 
							//System.out.println(arrival_time - LastVisit[fallas.get(tiempo_fallas.indexOf(s_tiempo_fallas.get(0)))-1]);
						}else{
							denom_costo_ciclo++;
							costo_ciclo_prom =  costo_ciclo_prom +  (horas_todas.get(j).get(ruta_mod).get(rutas_todas.get(j).get(ruta_mod).size()-1) - NextFailure[rutas_todas.get(j).get(ruta_mod).get(rutas_todas.get(j).get(ruta_mod).size()-1).getEventsSitesID()-1]);
							main_cost[i] = main_cost[i] + ((gmm.getNodebyID(rutas_todas.get(j).get(ruta_mod).get(rutas_todas.get(j).get(ruta_mod).size()-1).getEventsSitesID()).getCw() * (horas_todas.get(j).get(ruta_mod).get(rutas_todas.get(j).get(ruta_mod).size()-1) - NextFailure[rutas_todas.get(j).get(ruta_mod).get(rutas_todas.get(j).get(ruta_mod).size()-1).getEventsSitesID()-1]) +gmm.getNodebyID(rutas_todas.get(j).get(ruta_mod).get(rutas_todas.get(j).get(ruta_mod).size()-1).getEventsSitesID()).getCcm())/(gvrp.getNodes().get(fallas.get(tiempo_fallas.indexOf(s_tiempo_fallas.get(0)))-1).getCycletime())); //  arrival_time - LastVisit[fallas.get(tiempo_fallas.indexOf(s_tiempo_fallas.get(0)))-1]))
							//System.out.println(gvrp.getNodes().get(fallas.get(tiempo_fallas.indexOf(s_tiempo_fallas.get(0)))-1).getCycletime());
						}
						
						
						
						//System.out.println("Agrega el sitio " + rutas_todas.get(j).get(ruta_mod).get(rutas_todas.get(j).get(ruta_mod).size()-1).getEventsSitesID());
						//main_cost[i] = main_cost[i] + 
						//		gmm.getNodebyID(rutas_todas.get(j).get(ruta_mod).get(rutas_todas.get(j).get(ruta_mod).size()-1).getEventsSitesID()).getCw() * (horas_todas.get(j).get(ruta_mod).get(rutas_todas.get(j).get(ruta_mod).size()-1) - NextFailure[rutas_todas.get(j).get(ruta_mod).get(rutas_todas.get(j).get(ruta_mod).size()-1).getEventsSitesID()-1]) +
						//		 gmm.getNodebyID(rutas_todas.get(j).get(ruta_mod).get(rutas_todas.get(j).get(ruta_mod).size()-1).getEventsSitesID()).getCcm(); 
						
						//System.out.println("El nodo es " + rutas_todas.get(j).get(ruta_mod).get(rutas_todas.get(j).get(ruta_mod).size()-1).getEventsSitesID());
						//System.out.println("El costo de espera es  " + gmm.getNodebyID(rutas_todas.get(j).get(ruta_mod).get(rutas_todas.get(j).get(ruta_mod).size()-1).getEventsSitesID()).getCw());
						//System.out.println("La espera es  " + (horas_todas.get(j).get(ruta_mod).get(rutas_todas.get(j).get(ruta_mod).size()-1) - NextFailure[fallas.get(tiempo_fallas.indexOf(s_tiempo_fallas.get(0))) - 1]) );
						//System.out.println("La espera es  " + (arrival_time - NextFailure[fallas.get(tiempo_fallas.indexOf(s_tiempo_fallas.get(0))) - 1]) );
						//System.out.println("El mantenimiento me cuesta " + gmm.getNodebyID(rutas_todas.get(j).get(ruta_mod).get(rutas_todas.get(j).get(ruta_mod).size()-1).getEventsSitesID()).getCcm() );
						
						NextFailure[rutas_todas.get(j).get(ruta_mod).get(rutas_todas.get(j).get(ruta_mod).size()-1).getEventsSitesID()-1] = horas_todas.get(j).get(ruta_mod).get(rutas_todas.get(j).get(ruta_mod).size()-1) + 
								gmm.getNodebyID(rutas_todas.get(j).get(ruta_mod).get(rutas_todas.get(j).get(ruta_mod).size()-1).getEventsSitesID()).getTcm() + 
								gmm.getNodebyID(rutas_todas.get(j).get(ruta_mod).get(rutas_todas.get(j).get(ruta_mod).size()-1).getEventsSitesID()).getD().inverseF(RNG.nextDouble()); // Actualizo el tiempo de falla
						
						LastVisit[fallas.get(tiempo_fallas.indexOf(s_tiempo_fallas.get(0))) - 1] = arrival_time;
						
						// La saco de las fallas y tiempo de fallas
						fallas.remove(tiempo_fallas.indexOf(s_tiempo_fallas.get(0)));
						tiempo_fallas.remove(tiempo_fallas.indexOf(s_tiempo_fallas.get(0)));
						s_tiempo_fallas.remove(0);
											
						//Actualizo la ultima visita
						ultimo_t[ruta_mod] = horas_todas.get(j).get(ruta_mod).get(horas_todas.get(j).get(ruta_mod).size()-1);
						sorted_ultima[0] = horas_todas.get(j).get(ruta_mod).get(horas_todas.get(j).get(ruta_mod).size()-1);; 
						Arrays.sort(sorted_ultima);
						
						//System.out.println("Ultimas");
						//for (int k = 0; k < sorted_ultima.length; k++) {
						//	System.out.println(ultimo_t[k] + "\t" + sorted_ultima[k]);
						//}
						
						
						// Borrarlo de donde está
						for (int l = j+1; l < rutas_todas.size(); l++) { // itera sobre todas las semanas
							for (int k = 0; k < rutas_todas.get(l).size(); k++) { //itera sobre todas las rutas de una semana
								for (int k2 = 0; k2 < rutas_todas.get(l).get(k).size(); k2++) { //itera sobre todas las visitas de una ruta
									if(rutas_todas.get(l).get(k).get(k2).getEventsSitesID() == nueva.getEventsSitesID()){
										//System.out.println("Quite el nodo " + rutas_todas.get(l).get(k).get(k2).getEventsSitesID() + " de la ruta " + k + " de la semana " + l);
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
							for (int k2 = 0; k2 < rutas_todas.get(k).size(); k2++) { //Voy a revisar todas las rutas de la semana k
								ultimo_t_nueva[k2] = horas_todas.get(k).get(k2).get(horas_todas.get(k).get(k2).size()-1);
								sorted_ultima_nueva[k2] = horas_todas.get(k).get(k2).get(horas_todas.get(k).get(k2).size()-1); 
								
							}
							Arrays.sort(sorted_ultima);
							
							//La ruta a la que se le va a agregar la nueva operacion
							int ruta_mod_nueva = indexOfArray(ultimo_t_nueva, sorted_ultima_nueva[0]);
							
							// el momento en el que se hace la última visita
							double tiempo_ultima_n = sorted_ultima_nueva[0];
							
							double deltaX2 = gmm.getNodebyID(nueva.getEventsSitesID()).getPosx() - gmm.getNodebyID(rutas_todas.get(k).get(ruta_mod_nueva).get(horas_todas.get(k).get(ruta_mod_nueva).size()-1).getEventsSitesID()).getPosx();
							double deltaY2 = gmm.getNodebyID(nueva.getEventsSitesID()).getPosy() - gmm.getNodebyID(rutas_todas.get(k).get(ruta_mod_nueva).get(horas_todas.get(k).get(ruta_mod_nueva).size()-1).getEventsSitesID()).getPosy();
				
							double travel_time2 = (Math.abs(deltaX2) + Math.abs(deltaY2)) / VEL;
							
							//if(tiempo_ultima_n + travel_time2 < smallgraphs.get(k).getTmax()){
							//	System.out.println("OJO");
							//}
							
							Tres nueva2 = new Tres(nueva.getEventsSitesID(),nueva.getEventsOperationsID(), tiempo_ultima_n + travel_time2);
							rutas_todas.get(k).get(ruta_mod_nueva).add(nueva2);
							horas_todas.get(k).get(ruta_mod_nueva).add(tiempo_ultima_n + travel_time2);
							//System.out.println("Agregue un nodo a una ruta del futuro");
							
						}
											
					}else{
						continuar = false;
					}
					
					if(fallas.isEmpty()){
						continuar = false;
					}
				}
				
			}
			
			for (int i2 = 0; i2 < rutas_todas.size(); i2++) { //iterar sobre todas las semanas
				for (int j = 0; j < rutas_todas.get(i2).size(); j++) { // itera sobre las rutas
					cuantas_hice = cuantas_hice + rutas_todas.get(i2).get(j).size() - 1;
				}				
			}
			
			
			//for (int i2 = 0; i2 < rutas_todas.size(); i2++) { //iterar sobre todas las semanas
			//	writer2.println("Semana "+i);
			//	for (int j = 0; j < rutas_todas.get(i2).size(); j++) { // itera sobre las rutas
			//		String linea = new String();
			//		linea = j + "\t";
			//		for (int k = 0; k < rutas_todas.get(i2).get(j).size(); k++) {
			//			linea = linea + rutas_todas.get(i2).get(j).get(k).getEventsSitesID() + "\t";
			//		}
			//		writer2.println(linea);
			//	}
			//}
			
			
			
			las_que_fallaron[i] = cuantas_fallaron;
			prob_falla[i] = prob_falla_prom/denom_prob_falla;
			costo_ciclo[i] = costo_ciclo_prom/denom_costo_ciclo;
			las_que_hice[i] = cuantas_hice;
			//System.out.println(i + "\t" + main_cost[i] + "\t" + las_que_fallaron[i] + "\t" + las_que_hice[i] + "\t" + prob_falla[i] + "\t" +  costo_ciclo[i]);
			writer.println(i + "\t" + main_cost[i] + "\t" + las_que_fallaron[i] + "\t" + las_que_hice[i] + "\t" + prob_falla[i] + "\t" + costo_ciclo[i]);
			System.out.println(i + "\t" + main_cost[i] + "\t" + las_que_fallaron[i] + "\t" + las_que_hice[i] + "\t" + prob_falla[i]+ "\t" + costo_ciclo[i]);
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
	
	public int[] getCycles(ArrayList<RGraph> smallgraphs, MGraph gmm,RGraph gvrp){
		int[] cycles = new int[gmm.getNodes().size()];
		System.out.println("Va a entrar al ciclo");
		for (int i = 0; i < cycles.length; i++) {
			for(int j = 0; j < gvrp.getNodes().size();j++){
					if(gvrp.getNodes().get(j).getId1() == (i+1)){
						cycles[i] = (int) (gvrp.getNodes().get(j).getCycletime()/7);
						break;
					}
				}
			}
		
//		System.out.println("Va a entrar al ciclo");
//		for (int i = 0; i < cycles.length; i++) {
//			boolean continuar = true;
//			while(continuar){
//				for(int j = 0; j < smallgraphs.get(i).getNodes().size();j++){
//					if(smallgraphs.get(i).getNodes().get(j).getId1() == (j+1)){
//						cycles[i] = (int) (smallgraphs.get(i).getNodes().get(j).getCycletime()/7);
//						continuar = false;
//					}
//				}
//			}
//			
//		}
		
		
		return cycles;
	}
	
	public RNode getNodeFromSGbyID(ArrayList<RGraph> smallgraphs, int ID){
		for (int i = 0; i < smallgraphs.size(); i++) {
			for (int j = 0; j < smallgraphs.get(i).getNodes().size(); j++) {
				//System.out.println("He revisado " + smallgraphs.get(i).getNodes().get(j).getId1());
				if(smallgraphs.get(i).getNodes().get(j).getId1() == ID){
					return smallgraphs.get(i).getNodes().get(i);
				}
			}
		}
		return null;
	}
	
}
