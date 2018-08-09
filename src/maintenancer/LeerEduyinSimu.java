package maintenancer;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class LeerEduyinSimu {
	private BufferedReader in;
	private Route ruta = new Route();
	/**
	 * This class read the information
	 * @param route data file path
	 */
	public LeerEduyinSimu(String route){
		try{
			int id1=0; 
			int id2=0; 
			double s=0.0; 
			Tres tripleta;
			in = new BufferedReader(new FileReader(route));
			String line;
			//Aquí se agrega el nodo 0 (el depot)
			while((line=in.readLine())!=null){
				String [] list= line.split(" ");
				id1=Integer.parseInt(list[0]);
				id2=Integer.parseInt(list[1]);
				s=Double.parseDouble(list[2]);
				tripleta=new Tres(id1,id2,s);
				ruta.addTres(tripleta);
			}
			
		}catch (FileNotFoundException e) {
			e.printStackTrace();
		}catch (IOException e) {
			e.printStackTrace();
		}		
	}
	public Route getRuta() {
		return ruta;
	}
	public void setRuta(Route ruta) {
		this.ruta = ruta;
	}

}
