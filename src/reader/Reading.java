package reader;
import java.io.*;

import maintenancer.MArc;
import maintenancer.MGraph;
import maintenancer.MNode;
import umontreal.iro.lecuyer.probdist.ContinuousDistribution;
import umontreal.iro.lecuyer.probdist.ExponentialDist;
import umontreal.iro.lecuyer.probdist.GammaDist;
import umontreal.iro.lecuyer.probdist.NormalDist;
import umontreal.iro.lecuyer.probdist.WeibullDist;
/**
 * This class read the information of the nodes
 * @author 	/John Edgar Fontecha García
 * 			/je.fontecha10@uniandes.edu.co
 * 			/Universidad de los Andes
 *			/Departamento de Ingeniería Industrial
 *			/Combined Maintenance and Routing Optimization for large scale problems
 */
public class Reading {
	public static double CELLDISTANCE=0.170;
	public static double VEL=5*24;// km/día//11.89
	private BufferedReader in;
	/**
	 * Sites graph
	 */
	private MGraph g = new MGraph();
	/**
	 * Depot coordinates
	 */
	private double [] pos = new double[2];
	/**
	 * This class read the information to create the sites graph
	 * @param route data file path
	 */
	public Reading(String route){
		try{
			MArc marc;
			double nposx; 
			double nposy;
			double nccm=0; 
			double ncpm=0; 
			double ncwt=0; 
			double ntcm=0; 
			double ntpm=0; 
			double nperc=0; 
			String ndist;
			double np1;
			double auxnp1;
			double np2;
			double npot;
			ContinuousDistribution nd; 
			double nwaiting=0;
			int i=0;			
			in = new BufferedReader(new FileReader(route));
			String line;
			i=0;
			while((line=in.readLine())!=null){
				String [] list= line.split(" ");
				if(i==0){
					pos[0]=Double.parseDouble(list[0]);
					pos[1]=Double.parseDouble(list[1]);
					i++;
				} else{
					nccm=Double.parseDouble(list[0]);
					ncpm=Double.parseDouble(list[1]);
					ncwt=Double.parseDouble(list[2]);
					ntcm=Double.parseDouble(list[3]);
					ntpm=Double.parseDouble(list[4]);
					nperc=Double.parseDouble(list[5]);
					nposx=Double.parseDouble(list[6]);
					nposy=Double.parseDouble(list[7]);
					ndist=list[8];
					np1=Double.parseDouble(list[9]);
					auxnp1=1/np1;
					np2=Double.parseDouble(list[10]);
					npot=Double.parseDouble(list[11]);
					nd=new WeibullDist(2,auxnp1, 0);
					if(ndist.equalsIgnoreCase("E")){
						nd=new ExponentialDist(auxnp1);
					} else if(ndist.equalsIgnoreCase("G")){
						nd=new GammaDist(np2,1/np1);
					} else if(ndist.equalsIgnoreCase("W")){
						nd=new WeibullDist(np2,1/np1, 0);
					} else if(ndist.equalsIgnoreCase("N")){
						System.out.println("mu"+np1+" gama"+np2);
						nd=new NormalDist(np1,np2);
					}
					MNode h=new MNode(i,nccm,ncpm, ncwt,ntcm,ntpm,nperc,nposx,nposy,npot,nd,nwaiting,0,0,0);
					g.addNode(h);
					i++;
				}
			}
			
			//ESTO SIRVE PARA GENERAR ARCOS EN EL SITEGRAPH
//			double ttravel=0.0;
//			for(int ii=0;ii<g.getNodes().size();ii++){
//				for(int jj=0;jj<g.getNodes().size();jj++){			
//					ttravel=(Math.sqrt(Math.pow(g.getNodes().get(ii).getPosx()-g.getNodes().get(jj).getPosx(), 2)+Math.pow(g.getNodes().get(ii).getPosy()-g.getNodes().get(jj).getPosy(), 2)))*CELLDISTANCE/VEL;
//					marc=new MArc(g.getNodes().get(ii).getId(),g.getNodes().get(jj).getId(),ttravel);
//					g.addArc(marc);
//				}
//			}

		}catch (FileNotFoundException e) {
			e.printStackTrace();
		}catch (IOException e) {
			e.printStackTrace();
		}

	}
	/**
	 * This method returns the site graph  
	 * @return g graph
	 */
	public MGraph getGraph(){
		return g;
	}
	/**
	 * This method sets the site graph  
	 * @return g graph
	 */
	public void setGraph(MGraph g){
		this.g=g;
	}
	/**
	 * This method returns the depot coordinates
	 * @return pos depot information
	 */
	public double [] getPos(){
		return pos;
	}
	/**
	 * This method sets the depot coordinates
	 * @return pos depot information
	 */
	public void setPos(double[] pos) {
		this.pos = pos;
	}
}