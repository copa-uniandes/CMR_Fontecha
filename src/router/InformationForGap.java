package router;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import maintenancer.MGraph;
/**
 * This class is used to print important information for GAP
 * @author 	/John Edgar Fontecha Garcia
 * 			/je.fontecha10@uniandes.edu.co
 * 			/Universidad de los Andes
 *			/Departamento de Ingeniería Industrial
 *			/Combined Maintenance and Routing Optimization for large scale problems
 */
public class InformationForGap {
	static PrintWriter writer;
	/**
	 * This class prints the information necessary to calculate Gap
	 * @param gmm Sites Graph
	 */
	public InformationForGap(MGraph gmm){
		
		String ruta = "./data/";
		String nombreF = "ForGAP.csv";
		try{
			File directorioFacturas = new File(ruta);
			if(!directorioFacturas.exists())
				directorioFacturas.mkdirs();
			File file = new File(ruta+nombreF);
			file.createNewFile();
			writer = new PrintWriter(new FileWriter(file));
			for(int i=0;i<gmm.getNodes().size();i++){
				writer.println(gmm.getNodes().get(i).getId()+" "+gmm.getNodes().get(i).getOptcost());
			}
			writer.close();
		}
		catch(FileNotFoundException e){
			System.out.println("File Not Found");
			System.exit( 1 );
		}
		catch(IOException e){
			System.out.println("something messed up");
			System.exit( 1 );
		}
	}
}
