package P1;
import gurobi.GRBException;

import java.io.IOException;
import java.util.ArrayList;

import maintenancer.MGraph;
import maintenancer.MNode;
import maintenancer.Maintenance;
import maintenancer.Route;
import maintenancer.Tres;
import router.RGraph;
/**
 * This class .....
 * @author 	/Daniel Duque
 * 			/d.duque25@uniandes.edu.co
 * 			/Universidad de los Andes
 *			/Departamento de Ingeniería Industrial
 *			/Combined Maintenance and Routing Optimization for large scale problems
 */
public class GraspSplitSolver {
	
	
	/**
	 * Solves the VRP for the given graph.
	 * @param rGraph
	 *            VRP graph that includes the depot
	 * @param Tmin
	 *            minimum time to attend all nodes
	 * @param Tmax
	 *            maximum time to attand all nodes
	 */
	
	private ArrayList<Route> rutascompletasTres=new ArrayList<Route>();
	
	public void solve(String Nombre,MGraph mmgr, RGraph rGraph, double Tmin, double Tmax, String MIPshifts, int rutasdisponibles, String eduindata,int dd,int deep) {
		DataHandler data = new DataHandler(mmgr,rGraph, Tmin, Tmax, MIPshifts, eduindata); 
		ClarkeWright cw = new ClarkeWright(data);
		Split splitter = new Split(data);
		VecinoMasCercano VMC = new VecinoMasCercano(data);
		TwoOpt myTwoOpt = new TwoOpt(data);
		GRASP grasp = new GRASP(data, splitter, myTwoOpt);
		RutaAleatoria rutaAle= new RutaAleatoria(data);
		Soluciones soluciones = new Soluciones(data);
		SetCovering setCov;

		System.out.println("ROUND 1");
		cw.executeCW();
		grasp.excecute(cw.vectRes,dd,deep);
		soluciones.addSolutions(grasp.rutas);
		
		System.out.println("ROUND 2");
		VMC.excecute();
		grasp.excecute(VMC.vectRes,dd,deep);
		soluciones.addSolutions(grasp.rutas);
		
		System.out.println("ROUND 3");
		rutaAle.excecute();
		grasp.excecute(rutaAle.vectRes,dd,deep);
		soluciones.addSolutions(grasp.rutas);
		
//		System.out.println("Estoy en graspsplitsolver, el número de rutas factibles es: "+grasp.getRutasfactibles().size());
		
		setCov = new SetCovering(Nombre,data, grasp.getRutasfactibles(),rutasdisponibles);
		try {
			setCov.excecute(Nombre);
			rutascompletasTres=setCov.getRutascompletasTres();
		} catch (GRBException e) {
			// TODO Auto-generated catch block
			System.out.println("en el optimizador");
			e.printStackTrace();
		}
		
		
//		soluciones.writeSolutionsFile();
//		soluciones.sortSolutions();
//		soluciones.writeSolutionsFile();
//		
		long tAfter = System.currentTimeMillis();
		
		
		
	}

	public ArrayList<Route> getRutascompletasTres() {
		return rutascompletasTres;
	}

	public void setRutascompletasTres(ArrayList<Route> rutascompletasTres) {
		this.rutascompletasTres = rutascompletasTres;
	}
}
