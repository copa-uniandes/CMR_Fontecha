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
 * 	This class read the information of the nodes
 * @author 	/John Edgar Fontecha García
 * 			/je.fontecha10@uniandes.edu.co
 * 			/Universidad de los Andes
 *			/Departamento de Ingeniería Industrial
 *			/Proyecto de Tesis
 */
public class Reading2 {
	private BufferedReader in;
	private BufferedReader in2;
	private MGraph g = new MGraph();
	private double [] pos = new double[2];
	/**
	 * This class read the information
	 * @param route data file path
	 */
	public Reading2(String route1,String route2){
		try{
			double nccm=0; 
			double ncpm=0; 
			double ncwt=0; 
			double ntcm=0; 
			double ntpm=0; 
			double nperc=0; 
			String ndist;
			double np1=0;
			double auxnp1=1.01;
			double np2=0;
			double npot;
			ContinuousDistribution nd; 
			double nwaiting=0;
			int tail=0;
			int head=0; 
			double tt=0.0;
			in2=new BufferedReader(new FileReader(route2));
			String line2;
			int i=0;
			while((line2=in2.readLine())!=null){
				String [] list= line2.split(" ");
				tail= Integer.parseInt(list[0]);
				head= Integer.parseInt(list[1]);
				tt= Double.parseDouble(list[2]);
				MArc arc=new MArc(tail,head,tt);
				g.addArc(arc);
			}			
			
			in = new BufferedReader(new FileReader(route1));
			String line;
			nd=new WeibullDist(2,auxnp1, 0);
			//Aquí se agrega el nodo 0 (el depot)
			MNode h;
////			int id, double ccm, double cpm, double cw, double tcm, double tpm, double deltacost, double posx, double posy, ContinuousDistribution d,
////			double waiting, int iteration, double cost
//			MNode h=new MNode(0, nccm,ncpm, ncwt,ntcm,ntpm, nperc,0,0,nd, nwaiting,0,0);
//			g.addNode(h);
			i=1;
			while((line=in.readLine())!=null){
				String [] list= line.split(" ");
					ncpm=Double.parseDouble(list[0]);
					nccm=Double.parseDouble(list[1]);
					ncwt=Double.parseDouble(list[2]);
					ntpm=Double.parseDouble(list[3]);
					ntcm=Double.parseDouble(list[4]);
					ndist=list[5];
					np1=Double.parseDouble(list[6]);
					auxnp1=1/np1;
					np2=Double.parseDouble(list[7]);
					nperc=Double.parseDouble(list[8]);
					npot=Double.parseDouble(list[9]);
					nd=new WeibullDist(2,auxnp1, 0);//aquí creo que es para evitarse un try catch
					if(ndist.equalsIgnoreCase("E")){
						nd=new ExponentialDist(auxnp1);
					} else if(ndist.equalsIgnoreCase("G")){
						nd=new GammaDist(np2,1/np1);
					} else if(ndist.equalsIgnoreCase("W")){
						nd=new WeibullDist(np2,1/np1, 0);
//						System.out.println("Entro a W");
					} else if(ndist.equalsIgnoreCase("N")){
//						System.out.println("Entro a N");
						nd=new NormalDist(np1,np2);
					}
					h=new MNode(i,nccm,ncpm, ncwt,ntcm,ntpm, nperc,0,0,npot,nd, nwaiting,0,0,0);
					g.addNode(h);
					i++;
				}
			
		}catch (FileNotFoundException e) {
			e.printStackTrace();
		}catch (IOException e) {
			e.printStackTrace();
		}		
	}
	/**
	 * This method returns the graph  
	 * @return g graph
	 */
	public MGraph getGraph(){
		return g;
	}
	/**
	 * This method returns the graph
	 * @return pos depot information
	 */
	public double [] getPos(){
		return pos;
	}
	public MGraph getG() {
		return g;
	}
	public void setG(MGraph g) {
		this.g = g;
	}
	public void setPos(double[] pos) {
		this.pos = pos;
	}
}