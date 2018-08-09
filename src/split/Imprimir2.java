package split;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import router.RGraph;

public class Imprimir2 {
	static PrintWriter writer;
	static double CELLDISTANCE=0.170;
	static double VEL=11.89*24;
	public Imprimir2(RGraph gvrp,String outputdir,String instancename, String type,int graphnum) throws FileNotFoundException{
		String ruta = "./data/" + outputdir + "-" +instancename + "/";
		String nombreF = instancename+"_"+graphnum+"-"+type+"File"+graphnum+".csv";

		if(type=="distance") {
			try{
				File directorioFacturas = new File(ruta);
				if(!directorioFacturas.exists())
					directorioFacturas.mkdirs();
				File file = new File(ruta+nombreF);
				file.createNewFile();
				writer = new PrintWriter(new FileWriter(file));
				for(int i=0;i<gvrp.getNodes().size()-1;i++){
					for(int j=i+1;j<gvrp.getNodes().size();j++){
						writer.println(gvrp.getNodes().get(i).getId3()+","+gvrp.getNodes().get(j).getId3()+","+Math.sqrt(Math.pow(gvrp.getNodes().get(i).getPosx()-gvrp.getNodes().get(j).getPosx(), 2)+Math.pow(gvrp.getNodes().get(i).getPosy()-gvrp.getNodes().get(j).getPosy(), 2)+Math.pow((gvrp.getNodes().get(i).getOpt()-gvrp.getNodes().get(j).getOpt())*VEL, 2)));
					}
				}
				writer.close();
			}
			catch(FileNotFoundException e)
			{
				System.out.println("File Not Found");
				System.exit( 1 );
			}
			catch(IOException e)
			{
				System.out.println("something messed up");
				System.exit( 1 );
			}
		}

		if(type=="demand") {
			try{
				File directorioFacturas = new File(ruta);
				if(!directorioFacturas.exists())
					directorioFacturas.mkdirs();
				File file = new File(ruta+nombreF);
				file.createNewFile();
				writer = new PrintWriter(new FileWriter(file));
				for(int j=0;j<gvrp.getNodes().size();j++){
//					System.out.println(j+" solo para verificar "+gvrp.getNodes().get(j).getId3());
					writer.println(gvrp.getNodes().get(j).getId3()+","+gvrp.getNodes().get(j).getExpectedservicetime()+","+1+","+0);	
				}
				writer.close();
			}
			catch(FileNotFoundException e)
			{
				System.out.println("File Not Found");
				System.exit( 1 );
			}
			catch(IOException e)
			{
				System.out.println("something messed up");
				System.exit( 1 );
			}
		}

		if(type=="correlation"){
			try{
				File directorioFacturas = new File(ruta);
				if(!directorioFacturas.exists())
					directorioFacturas.mkdirs();
				File file = new File(ruta+nombreF);
				file.createNewFile();
			}catch(FileNotFoundException e)
			{
				System.out.println("File Not Found");
				System.exit( 1 );
			}
			catch(IOException e)
			{
				System.out.println("something messed up");
				System.exit( 1 );
			}
		}
		
		if(type=="capacity"){
			try{
				File directorioFacturas = new File(ruta);
				if(!directorioFacturas.exists())
					directorioFacturas.mkdirs();
				File file = new File(ruta+nombreF);
				file.createNewFile();
			}catch(FileNotFoundException e)
			{
				System.out.println("File Not Found");
				System.exit( 1 );
			}
			catch(IOException e)
			{
				System.out.println("something messed up");
				System.exit( 1 );
			}
		}
	}
}

