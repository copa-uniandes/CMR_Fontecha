package EduyinModel;

public class Solucion {
	private int id1;
	private int id2;
	private double dif;
	private double s;
	private double costz;
	
	
	public Solucion(int iduno,int iddos,double diferencia, double s, double costz){
		this.id1=iduno;
		this.id2=iddos;
		this.dif=diferencia;
		this.s=s;
		this.costz=costz;
		
	}

	public int getId1() {
		return id1;
	}

	public void setId1(int id1) {
		this.id1 = id1;
	}

	public int getId2() {
		return id2;
	}

	public void setId2(int id2) {
		this.id2 = id2;
	}

	public double getDif() {
		return dif;
	}

	public void setDif(double dif) {
		this.dif = dif;
	}

	public double getCostz() {
		return costz;
	}

	public void setCostz(double costz) {
		this.costz = costz;
	}

	public double getS() {
		return s;
	}

	public void setS(double s) {
		this.s = s;
	}
	
}
