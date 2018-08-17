package solution;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import maintenancer.Tres;

public class Datahandler {
	private String fileName;
	private ArrayList<ArrayList<Tres>> rutas_semana;
	private ArrayList<ArrayList<Double>> horas_semana;
	
	
	public Datahandler(String nfilename) {
		fileName = nfilename;
		this.rutas_semana = new ArrayList<ArrayList<Tres>>();
		this.horas_semana = new ArrayList<ArrayList<Double>>();
	}
	
	public void readInstance() throws IOException{
		File file = new File(fileName);
		BufferedReader buffRdr = new BufferedReader(new FileReader(file));
		
		ArrayList<Tres> ruta = new ArrayList<Tres>();
		ArrayList<Double> horas = new ArrayList<Double>();
		
		String line = null;
		String[] splittedline = null;
		
		//Leo el header
		line = buffRdr.readLine();
		
		// Empiezo a leer las visitas
		line = buffRdr.readLine();
		splittedline = line.split(" ");
		Tres nodo = new Tres(Integer.parseInt(splittedline[0]), Integer.parseInt(splittedline[0]), Double.parseDouble(splittedline[1]));
		ruta.add(nodo);
		horas.add(nodo.getEventTimes());
		
		line = buffRdr.readLine();
		
		while(line != null){
			splittedline = line.split(" ");	
			nodo = new Tres(Integer.parseInt(splittedline[0]), Integer.parseInt(splittedline[0]), Double.parseDouble(splittedline[1]));
			if(nodo.getEventsSitesID() == 0){
				rutas_semana.add(ruta);
				horas_semana.add(horas);
				ruta = new ArrayList<Tres>();
				horas = new ArrayList<Double>();
			}
			ruta.add(nodo);
			horas.add(nodo.getEventTimes());	
			line = buffRdr.readLine();		
		}
		rutas_semana.add(ruta);
		horas_semana.add(horas);
		
		buffRdr.close();
	}

	public ArrayList<ArrayList<Tres>> getRutas_semana() {
		return rutas_semana;
	}

	public void setRutas_semana(ArrayList<ArrayList<Tres>> rutas_semana) {
		this.rutas_semana = rutas_semana;
	}

	public ArrayList<ArrayList<Double>> getHoras_semana() {
		return horas_semana;
	}

	public void setHoras_semana(ArrayList<ArrayList<Double>> horas_semana) {
		this.horas_semana = horas_semana;
	}
	
	
}
