package ima.uco;
import umontreal.iro.lecuyer.probdist.*;
import umontreal.iro.lecuyer.charts.*;
/**
 * This class does a graphic of the pdf
 * @author 	/Daniel Duque Villareal
 * 			/d.duque25@uniandes.edu.co
 * 			/Universidad de los Andes
 *			/Departamento de Ingeniería Industrial
 *			/Combined Maintenance and Routing Optimization for large scale problems
 */
public class Grafica{
	
	public static void main (String[] args) {
		ContinuousDistribution dist = new WeibullDist(1.62384097553859, 1/167.0, 0);
		ContinuousDistChart plot = new ContinuousDistChart(dist, 0, 600, 1000);
		System.out.println(dist.cdf(90));
		plot.viewDensity(600, 400);
	}
}
