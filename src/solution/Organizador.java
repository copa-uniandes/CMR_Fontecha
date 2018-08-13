package solution;

import java.util.ArrayList;
import maintenancer.Route;

public class Organizador {
	Route ruta;
	public Organizador(ArrayList<Route> rutas){
		ruta=new Route();
		//		System.out.println("Organizando "+rutas.size());
		boolean entro;
		int contador=0;
		/**
		 * Delete depot
		 */
		for(int i=0;i<rutas.size();i++){
			for(int j=rutas.get(i).getRoute().size()-1;j>=0;j--){
				if(rutas.get(i).getRoute().get(j).getEventsSitesID()==0) {
					rutas.get(i).getRoute().remove(j);
				}
			}
		}
		/**
		 * Finish depot delete
		 */
		for(int i=0;i<rutas.size();i++){
			for(int j=0;j<rutas.get(i).getRoute().size();j++){
				if(j==0 && i==0){//ruta.getRoute().size()==0
//					System.out.println("Estoy en organizador: "+" "+i+" "+rutas.get(i).getRoute().get(j).getEventsSitesID()+" "+rutas.get(i).getRoute().get(j).getEventsOperationsID()+" "+rutas.get(i).getRoute().get(j).getEventTimes());
					ruta.addTres(rutas.get(i).getRoute().get(j));
				} else{
					entro=false;
//					System.out.println("Estoy en organizador: "+" "+i+" "+rutas.get(i).getRoute().get(j).getEventsSitesID()+" "+rutas.get(i).getRoute().get(j).getEventsOperationsID()+" "+rutas.get(i).getRoute().get(j).getEventTimes());
					for(int p=0;p<ruta.getRoute().size();p++){
						contador++;
						//						System.out.println(contador+" "+i+" "+j+" "+p+" "+" "+rutas.get(i).getRoute().get(j).getEventTimes()+" "+ruta.getRoute().get(p).getEventTimes());
						if (rutas.get(i).getRoute().get(j).getEventTimes()<ruta.getRoute().get(p).getEventTimes()){
							ruta.addBefore(rutas.get(i).getRoute().get(j), p);

							//							System.out.println(" Viene un break");
							entro=true;
							break;
						}
					}

					if (entro){

					}else{
						ruta.addTres(rutas.get(i).getRoute().get(j));;
					}
				}
			}
		}
	}
	public Route getRuta() {
		return ruta;
	}
	public void setRuta(Route ruta) {
		this.ruta = ruta;
	}

	//	public void addRuta(Route ruta) {
	//		ruta.add(ruta);
	//	}
}
