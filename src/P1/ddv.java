package P1;
/**
 * This class .....
 * @author 	/Daniel Duque
 * 			/d.duque25@uniandes.edu.co
 * 			/Universidad de los Andes
 *			/Departamento de Ingeniería Industrial
 *			/Combined Maintenance and Routing Optimization for large scale problems
 */
public class ddv {	
	public static void main(String[] args) {
		double[][] dd = {{1,2},{3,4}};
		double[][] bb = new double[2][2];
		//System.arraycopy(dd, 0, bb,0,  2);;
		
		bb[1][1]=100;
		
		for (int i = 0; i < bb.length; i++) {
			for (int j = 0; j < bb[0].length; j++) {
				System.out.println(bb[i][j]);
			}
		}
			
	
		System.out.println();
		for (int i = 0; i < bb.length; i++) {
			for (int j = 0; j < bb[0].length; j++) {
				System.out.println(dd[i][j]);
			}
		}
		
	}

}
