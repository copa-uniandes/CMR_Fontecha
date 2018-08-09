package maintenancer;

import java.util.ArrayList;

import router.RNode;

public class Route {
	private ArrayList<Tres> route;
	
	public Route(){
		route=new ArrayList<Tres>();
	}

	public Route(ArrayList<Tres> ruta){
		this.setRoute(ruta);
	}
	
	public void addTres(Tres na){
		route.add(na);
	}
	
	public ArrayList<Tres> getRoute() {
		return route;
	}

	public void setRoute(ArrayList<Tres> route) {
		this.route = route;
	}

	public void addFirst(Tres tres) {
		// TODO Auto-generated method stub
		route.add(0,tres);
	}
	
	public void addBefore(Tres tres,int pos) {
		// TODO Auto-generated method stub
		route.add(pos,tres);
	}
}
