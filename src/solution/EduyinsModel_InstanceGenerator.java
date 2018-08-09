package solution;

import java.util.Random;

import maintenancer.MGraph;
import maintenancer.MNode;
import umontreal.iro.lecuyer.probdist.ContinuousDistribution;
import umontreal.iro.lecuyer.probdist.ExponentialDist;
import umontreal.iro.lecuyer.probdist.GammaDist;
import umontreal.iro.lecuyer.probdist.NormalDist;
import umontreal.iro.lecuyer.probdist.WeibullDist;

public class EduyinsModel_InstanceGenerator {
	MGraph sitegraph=new MGraph();
	public static double velocidad=120;
	double [] x_0=new double[2];
	
	public EduyinsModel_InstanceGenerator(int instancesize, double horizon){		
		Random RandomGenerator=new Random(13);
		int x0=0;
		int y0=0;
		int limitcartesianx=1000;
		int limitcartesiany=1000;
		int lowlimitcpm=100;
		int uplimitcpm=200;
		int lowlimitccm=400;
		int uplimitccm=800;
		int lowlimitcw=100;
		int uplimitcw=200;
		int lowlimittpm=5;
		int uplimittpm=10;
		int lowlimittcm=15;
		int uplimittcm=30;
		int lowlimitscale=2;
		int uplimitscale=5;
		int npot=1; // Este representa la potencia del waiting cost
		double lowlimitdeltacost=0.1;
		double uplimitdeltacost=0.3;
		int lowlimitmean=3;
		int uplimitmean=4;
		int lowlimitdeviation=9;
		int uplimitdeviation=12;
		int xmultiplier=0;
		int ymultiplier=0;
		int x=0;
		int y=0;
		int cpm=0;
		int ccm=0;
		int cw=0;
		int tpm=0;
		int tcm=0;
		double deltacost=0;
		double distribution=0;
		double scaleparameter=0;
		double shapeparameter=0;
		double mean=0;
		double deviation=0;
		ContinuousDistribution nd; 
		double shape=0;
		double aleatorio=0;
		System.out.println("node"+"\t"+"x"+"\t"+"y"+"\t"+"cpm"+"\t"+"ccm"+"\t"+"cw"+"\t"+"tpm"+"\t"+"tcm"+"\t"+"delta"+"\t"+"distr");
		for(int i=0;i<1;i++){
			xmultiplier=1;
			ymultiplier=1;
			if(RandomGenerator.nextFloat()<0.5){
				xmultiplier=-1;
			} 
			if(RandomGenerator.nextFloat()<0.5){
				ymultiplier=-1;
			} 
			x0=RandomGenerator.nextInt(limitcartesianx)*xmultiplier;
			y0=RandomGenerator.nextInt(limitcartesiany)*ymultiplier;
			x_0[0]=x0;
			x_0[1]=y0;
			
		}

		MNode h;
		for(int i=1;i<instancesize;i++){
			xmultiplier=1;
			ymultiplier=1;
			if(RandomGenerator.nextFloat()<0.5){
				xmultiplier=-1;
			} 
			if(RandomGenerator.nextFloat()<0.5){
				ymultiplier=-1;
			} 
			x=RandomGenerator.nextInt(limitcartesianx)*xmultiplier;
			y=RandomGenerator.nextInt(limitcartesiany)*ymultiplier;
			cpm=lowlimitcpm+RandomGenerator.nextInt(uplimitcpm-lowlimitcpm);
			ccm=lowlimitccm+RandomGenerator.nextInt(uplimitccm-lowlimitccm);
			cw=lowlimitcw+RandomGenerator.nextInt(uplimitcw-lowlimitcw);
			tpm=lowlimittpm+RandomGenerator.nextInt(uplimittpm-lowlimittpm);
			tcm=lowlimittcm+RandomGenerator.nextInt(uplimittcm-lowlimittcm);
			deltacost=lowlimitdeltacost+(uplimitdeltacost-lowlimitdeltacost)*RandomGenerator.nextFloat();
//			RandomGenerator.nextFloat();
			if(RandomGenerator.nextFloat()<0.5){
				aleatorio=RandomGenerator.nextFloat();
				if(aleatorio<0.25){
					shapeparameter=2;
				}else if(aleatorio<0.5){
					shapeparameter=2.5;
				}else if(aleatorio<0.75){
					shapeparameter=3;
				}else{
					shapeparameter=3.5;
				}
				scaleparameter=lowlimitscale+RandomGenerator.nextInt(uplimitscale-lowlimitscale);
				scaleparameter=1/((Math.sqrt(Math.pow(x0-x, 2)+Math.pow(y0-y, 2))*(1/velocidad)*scaleparameter));
				nd=new WeibullDist(shapeparameter,scaleparameter, 0);
			}else{
				mean=(lowlimitmean+RandomGenerator.nextInt(uplimitmean-lowlimitmean));
				mean=(Math.sqrt(Math.pow(x0-x, 2)+Math.pow(y0-y, 2))*(1/velocidad)*mean);
				deviation=(mean/(lowlimitdeviation+RandomGenerator.nextInt(uplimitdeviation-lowlimitdeviation)))*2;
				nd=new NormalDist(mean,deviation);
			}
			h=new MNode(i,ccm,cpm, cw,tcm,tpm,deltacost,x,y,npot,nd,0,0,0,0);
			sitegraph.addNode(h);
//			System.out.println(i+"\t"+x+"\t"+y+"\t"+cpm+"\t"+ccm+"\t"+cw+"\t"+tpm+"\t"+tcm+"\t"+deltacost);
		}
	}

	public MGraph getSitegraph() {
		return sitegraph;
	}

	public void setSitegraph(MGraph sitegraph) {
		this.sitegraph = sitegraph;
	}

	public static double getVelocidad() {
		return velocidad;
	}

	public static void setVelocidad(double velocidad) {
		EduyinsModel_InstanceGenerator.velocidad = velocidad;
	}

	public double[] getX_0() {
		return x_0;
	}

	public void setX_0(double[] x_0) {
		this.x_0 = x_0;
	}

	
}
