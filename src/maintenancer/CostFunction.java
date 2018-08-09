package maintenancer;


import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.integration.RombergIntegrator;

import umontreal.iro.lecuyer.probdist.ContinuousDistribution;


/**
 * This class calculates the expected time of failure given a time of maintenance
 * @author 	/Daniel Duque
 * 			/d.duque25@uniandes.edu.co
 * 			/Universidad de los Andes
 *			/Departamento de Ingeniería Industrial
 *			/Combined Maintenance and Routing Optimization for large scale problems
 */
public class CostFunction implements UnivariateFunction {
	static RombergIntegrator integrator;
	static CostFunction f;
	ContinuousDistribution dist;
	double delta;
	
	/**
	 * A cost function is a continues probability distribution function. This is the constructor of this function.
	 * @param d Continues distribution function
	 * @param upper integration upper limit
	 */
	
	public CostFunction(ContinuousDistribution d, double upper) {
		dist = d;
		delta = upper;
	}
	/**
	 * M(delta) calculator, this is a method that calculates the M value.  
	 * @param nd Continues distribution function
	 * @param a lower integration limit
	 * @param b upper integration limit
	 * @return	the value of the integration
	 */
	public static double calcMdeltaLib(ContinuousDistribution nd, double a, double b) {
		integrator = new RombergIntegrator(0.0001, 0.0000001, 10, 32);
		f = new CostFunction(nd, b);
		return integrator.integrate(100000, f, a, b);
	}
	/**
	 *Esta parte no entiendo muy bien cómo funciona, qué es lo qué hace?
	 */
	public double value(double x) {
		double f_x = (x * dist.density(x)) / dist.cdf(delta);
		return f_x;
	}
	
}