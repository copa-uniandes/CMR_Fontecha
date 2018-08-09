package router;
import java.util.ArrayList;
/**
 * This class is used in order to define the basic information of a node
 * @author 	/John Edgar Fontecha Garcia
 * 			/je.fontecha10@uniandes.edu.co
 * 			/Universidad de los Andes
 *			/Departamento de Ingeniería Industrial
 *			/Combined Maintenance and Routing Optimization for large scale problems
 */
public class Route {
	private ArrayList<RNode> route;
	public Route(){
	route=new ArrayList<RNode>();
	}
	public Route(ArrayList<RNode> r){
		setRoute(r);
	}
	public ArrayList<RNode> getRoute() {
		return route;
	}
	public void setRoute(ArrayList<RNode> route) {
		this.route = route;
	}
	public void addNode(RNode nd){
		route.add(nd);
	}
}
