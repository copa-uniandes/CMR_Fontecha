package EduyinModel;
import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.integration.RombergIntegrator;
import umontreal.iro.lecuyer.probdist.ContinuousDistribution;
/**
 * This class solve the optimization of the maintenance model for one node
 * The Maintenance Class is used in order to calculate the optimum value of the maintenance time
 * @author 	/John Edgar Fontecha Garcia para Modelo Eduyin
 * 			/je.fontecha10@uniandes.edu.co
 * 			/Universidad de los Andes
 *			/Departamento de Ingeniería Industrial
 *			/Combined Maintenance and Routing Optimization for large scale problems
 */
public class Maintenance {
	/**
	 *  tolerance for optimization
	 */
	public static double TOL = 0.00001;
	/**
	 * Number of points to generate for cost curve graphic 
	 */
	public static int POINTS=1000; 
	/**
	 * Columns of the vector of data for cost curve graphic
	 */
	public static int COLUMNS=4; 
	/**
	 * If i select newton method as optimization method for maintenance model
	 */
	public static String NEWTON = "newton"; //Newton method
	/**
	 * If i select force method as optimization method for maintenance model
	 */
	public static String FORCE = "force"; //Force Method
	/**
	 * Optimization method selected
	 */
	public static String OPTMETHOD = FORCE; //Method selected
	/**
	 * Switch
	 */
	public static String ON="on"; //Switch for graph generation
	/**
	 * Switch
	 */
	public static String OFF="off"; //Switch for graph generation
	/**
	 * If i decide to do a cost graph or no
	 */
	public static String COST_GRAPH=ON; //To do a graph
	/**
	 * Step for graphic generation
	 */
	public static final double timeStep = 0.1; //Step used to generate the cost function curve.
	/**
	 * Step for graphic generation
	 */
	public static final double stepconvexity = 0.1; //Step used to generate the cost function curve.
	/**
	 * Slope and intercept of the piecewise lines
	 */
	private double [][] pieces; //piecewise matrix
	/**
	 * Number of maintenance of this site in the planning horizon
	 */
	private int num; //number of maintenances
	/**
	 * Optimum
	 */
	private double opt; // optimum time for the maintenance
	/**
	 * Expected cycle time
	 */
	private double timecycle; //expected value of time of cycle
	/**
	 * Expected service time
	 */
	private double expectedservicetime; //expected service time
	/**
	 * Expected cost (numerator)
	 */
	private double expectedcost; //Expected cost
	/**
	 * Lower bound of the time window
	 */
	private double l;//lower in time window
	/**
	 * Upper bound of time window
	 */
	private double u;//upper in time window
	/**
	 * Matrix saving the information of the cost curves to graph
	 */
	private double [][] costcurve = new double [POINTS][COLUMNS];// Data of the curve graph
	/**
	 * A Maintenance Model (MM) is defined by the follow parameters.
	 * @param node node with the basic information
	 * @param T planning horizon
	 * @param npieces number of pieces for linear approximation
	 * @param nouts number of pieces out of time window
	 */
	public Maintenance(MNode node, double T, int npieces, int nouts) {
		
//		System.out.println("Esta en el nodo: "+node.getId());
		if(COST_GRAPH=="on"){
			if(node.getIteration()==0 && node.getId()==6){
				double start=1;
				this.setCostcurve(datacostcurvegeneration(node,timeStep,start));
			}
		} 
		
		double x1=1;
		if(OPTMETHOD=="newton"){	
			this.setOpt(newton(node,x1,T));
		}
		else{
			this.setOpt(force(node,x1,T));
		}
		setNum((int)Math.floor(T/cycletime(node,getOpt())));
		setTimecycle(cycletime(node,getOpt()));
		setExpectedservicetime(service_time(node,getOpt()));	
		setL(lower(node,getOpt()));
		setU(upper(node,getOpt(),T));
		setPieces(datapieces(node, convexity(node, this.getOpt(), T), npieces, nouts, getL(), getU()));
		setExpectedcost(expectedcost(node,getOpt()));
//		System.out.println(node.getId()+" "+getOpt()+" "+getNum()+" "+getL()+" "+getU());

	}
	/**
	 * This method returns the node's data for curve generation
	 * @param node node 
	 * @param step step
	 * @param start start point
	 * @return costcurve cost curve ready for to be used by drawing
	 */
	public double [][] datacostcurvegeneration(MNode node, double step, double start){
		double [][] costcurve = new double [POINTS][COLUMNS];
		for(int i=1;i<=POINTS;i++){
			costcurve[i-1][0]=step*(i+start);
			costcurve[i-1][1]=costgraphpreventive(node,step*(i+start));
			costcurve[i-1][2]=costgraphcorrective(node,step*(i+start))+costgraphwaiting(node,step*(i+start));
			costcurve[i-1][3]=cost(node,step*(i+start));
//			System.out.println(costcurve[i-1][0]+" "+costcurve[i-1][1]+" "+costcurve[i-1][2]+" "+costcurve[i-1][3]);
		}
		return costcurve;
	}
	/**
	 * This method returns the node's convexity change point 
	 * @param node node
	 * @param xopt optimum
	 * @param t horizon planning
	 * @return upper upper limit in the time window
	 */
	public static double convexity(MNode node, double xopt, double t){
		double costaux=(cost(node,xopt+TOL)-2*cost(node,xopt)+cost(node,xopt-TOL))/(TOL*TOL);
		double nh=t;
		double nl=xopt;
		double costm=0;
		double convex=0;
		double nm=(nh+nl)/2;
		double nmaux=0;
		boolean follow=true;
		do{
			costm=(cost(node,nm+TOL)-2*cost(node,nm)+cost(node,nm-TOL))/(TOL*TOL);
			if(costm<=0){
				nh=nm;
				nmaux=nm;
				nm=(nh+nl)/2;
			} else if(costm>0){
				nl=nm;
				nmaux=nm;
				nm=(nh+nl)/2;
			} 

			if(Math.abs(nm-nmaux)<TOL||Math.abs(nm-nmaux)<TOL){
				follow=false;
			}
		}while(follow);

		return (0.99*nl);
	}

	/**
	 * This method returns the node's upper limit (time window)
	 * @param node node
	 * @param xopt optimum
	 * @param t horizon planning
	 * @return upper upper limit in the time window
	 */
	static double upper(MNode node, double xopt, double t){
		double costaux=cost(node,xopt);
		double nh=100*xopt;
		double costm=0;
		double upper=0;
		double perc=node.getDeltacost();
		double target=costaux+perc;
		double nl=xopt;
		double nm=(nh+nl)/2;
		boolean follow=true;
		do{
			costm=cost(node,nm);
			if(Math.abs(costm-target)<TOL){
				upper=nm;	
				follow=false;
			} else if(costm-target>TOL){
				nh=nm;
				nm=(nh+nl)/2;
			} else if(target-costm>TOL){
				nl=nm;
				nm=(nh+nl)/2;
			}
		}while(follow);
		if(upper>t){
			return t;
		}
		return upper;
	}
	/**
	 * This method returns the node's lower limit (time window)
	 * @param node node
	 * @param xopt optimum
	 * @return lower lower limit in the time window
	 */
	static double lower(MNode node,  double xopt){
		double costaux=cost(node,xopt);
		double costm=0;
		double lower=0;
		double perc=node.getDeltacost();
		double target=costaux+perc;
		double nh=xopt;
		double nl=xopt/4;
		double nm=(nh+nl)/2;	
		boolean follow=true;
		do{
			costm=cost(node,nm);			
			if(Math.abs(costm-target)<TOL){
				lower=nm;	
				follow=false;
			} else if(costm-target>TOL){
				nl=nm;
				nm=(nh+nl)/2;
			} else if(target-costm>TOL){
				nh=nm;
				nm=(nh+nl)/2;
			}
		}
		while(follow);
		
		if(lower<0){
			return 0;
		}
		return lower;
	}
	/**
	 * This method returns the numerical derivative of function 
	 * @param node node that is evaluated
	 * @param point point where derivative is evaluated
	 * @return derivative numerical derivative
	 */
	static double d(MNode node, double point){
		double x2=point+TOL;
		double x3=point-TOL;
		double costx2=cost(node,x2);
		double costx3=cost(node,x3);
		double derivate=(costx2-costx3)/(2*TOL);
		return derivate;
	}
	/**
	 * This method returns the piecewise line equations 
	 * @param node node that is evaluated
	 * @param t convexity change point
	 * @param npieces number of piecewise 
	 * @param nout number of external (time window) piecewise
	 * @param nlower lower limit of time window
	 * @param nupper upper limit of time window
	 * @return pieces matrix with the paramaters m and b (y=mx+b) of each piecewise line approximation
	 */
	public double [][] datapieces(MNode node,double t,int npieces, int nout,double nlower, double nupper){
		double [][] pieces = new double [npieces][2];
		double step=(nlower-0)/(nout+1);
		for(int i=0;i<nout;i++){
			pieces[i][0]=d(node,0+(i+1)*step);
			pieces[i][1]=cost(node,0+(i+1)*step)-pieces[i][0]*(0+(i+1)*step);
		}
		step=(nupper-nlower)/(npieces-2*nout-1);
		for(int i=nout;i<npieces-nout;i++){
			pieces[i][0]=d(node,nlower+(i-nout)*step);
			pieces[i][1]=cost(node,nlower+(i-nout)*step)-pieces[i][0]*(nlower+(i-nout)*step);
		}
		step=(t-nupper)/(nout+1);
		for(int i=(npieces-nout);i<npieces;i++){
			pieces[i][0]=d(node,nupper+((i-npieces+nout+1)*step));
			pieces[i][1]=cost(node,nupper+(i-npieces+nout+1)*step)-pieces[i][0]*(nupper+(i-npieces+nout+1)*step);
		}
		return pieces;
	}
	/**
	 * This method returns the node's cycle time
	 * @param node node
	 * @param nx moment in the time
	 * @return time expected cycle time
	 */
	static double cycletime(MNode node, double nx){
		double TPM =node.getTpm();
		double TCM =node.getTcm();
		double ans1= node.getD().cdf(nx);
		double time=((nx+TPM)*(1-ans1)+(nx+TCM)*ans1);
		return time;	
	}
	/**
	 * This method returns the node's service time in a specific moment in the time
	 * @param node node
	 * @param nx moment in the time
	 * @return time expected service time 
	 */
	public static double service_time(MNode node, double nx){
		double TPM =node.getTpm();
		double TCM =node.getTcm();
		double ans1= node.getD().cdf(nx);
		double time=((TPM)*(1-ans1)+(TCM)*ans1);
		return time;	
	}
	/**
	 * This method returns the node's optimum by force
	 * @param node node 
	 * @param x1 start-iterative value
	 * @param t horizon planning
	 * @return x1 optimum
	 */
	static double force(MNode node, double x1, double t){
		double x2=0;
		double costx1=0;
		double costx2=0;
		boolean follow=true;
		double variable=0.01;
		do{
			x2=x1+variable;
			costx1=cost(node,x1);
			costx2=cost(node,x2);
			if((costx2-costx1)>0){
				follow=false;
				return newton(node,x1,t);
			} else{
				x1=x2;
			}
		}while(follow);
		if(x1>t){
			return t;
		}
		return newton(node,x1,t);
	}
	/**
	 * This method returns the node's optimum by newton
	 * @param node node 
	 * @param x1 start-iterative value
	 * @param t horizon planning
	 * @return x1 optimum
	 */
	public static double newton(MNode node, double x1, double t){
		double x2=0;
		double x3=0;
		double xaux=0;
		double costx1=0;
		double costx2=0;
		double costx3=0;
		double costaux=0;
		boolean follow=true;
		int numiteraciones=0;
		do{
			x2=x1+TOL;
			x3=x1-TOL;
			costx1=cost(node,x1);
			costx2=cost(node,x2);
			costx3=cost(node,x3);
			xaux=x1-(TOL/2)*((costx2-costx3)/(costx2-2*costx1+costx3));		
			costaux=cost(node,xaux);
			numiteraciones++;
			
			if(numiteraciones>100||x1>t){
				System.out.println("Entro");
				x1=force(node,1,t);
				return x1;
			}
			if(Math.abs(costaux-costx1)<TOL){
				follow=false;
				x1=xaux;
			}	else{
				x1=xaux;
			}	
		}while(follow);
		return x1;
	}
	/**
	 * This method calculates and returns the preventive maintenance cost for a specific moment in the time 
	 * @param node node
	 * @param nx moment in the time
	 * @return preventivecost expected preventive cost
	 */
	static double costgraphpreventive(MNode node,double nx){
		double CPM =node.getCpm();
		double TPM =node.getTpm();
		double TCM =node.getTcm();
		double ans1= node.getD().cdf(nx);
		double wait=node.getWaiting();
		double m=CostFunction.calcMdeltaLib(node.getD(),0,nx);
		double preventivecost=CPM*(1-ans1)/((nx+TPM)*(1-ans1)+(wait+m+TCM)*ans1);
		return preventivecost;	
	}
	/**
	 * This method calculates and returns the corrective maintenance cost for a specific moment in the time 
	 * @param node node
	 * @param nx moment in the time
	 * @return correctivecost expected corrective cost
	 */
	static double costgraphcorrective(MNode node,double nx){
		double CCM =node.getCcm();
		double TPM =node.getTpm();
		double TCM =node.getTcm();
		double ans1= node.getD().cdf(nx);
		double wait=node.getWaiting();
		double m=CostFunction.calcMdeltaLib(node.getD(),0,nx);
		double correctivecost=(CCM)*ans1/((nx+TPM)*(1-ans1)+(m+wait+TCM)*ans1);
		return correctivecost;	
	}
	/**
	 * This method calculates and returns the waiting for a maintenance cost for a specific moment in the time 
	 * @param node node
	 * @param nx moment in the time
	 * @return waitingcost expected waiting cost
	 */
	static double costgraphwaiting(MNode node, double nx){
		double CW = node.getCw();
		double TPM =node.getTpm();
		double TCM =node.getTcm();
		double ans1= node.getD().cdf(nx);
		double wait=node.getWaiting();
		double m=CostFunction.calcMdeltaLib(node.getD(),0,nx);
		double waitingcost=(CW*(wait))*ans1/((nx+TPM)*(1-ans1)+(wait+m+TCM)*ans1);
		return waitingcost;	
	}
	/**
	 * This method calculates and returns the total maintenance cost
	 * @param node node
	 * @param nx moment in the time
	 * @return totalcost cost in a specific time
	 */
	public static double cost(MNode node,double nx){
		double CPM =node.getCpm();
		double CCM =node.getCcm();
		double CW = node.getCw();
		double TPM =node.getTpm();
		double TCM =node.getTcm();
		double ans1= node.getD().cdf(nx);
		double wait=node.getWaiting();
		double m = CostFunction.calcMdeltaLib(node.getD(), 0, nx);
		double cost1=CPM*(1-ans1)/((nx+TPM)*(1-ans1)+(m+wait+TCM)*ans1);//m+W
		double cost2=(CCM+CW*(wait))*ans1/((nx+TPM)*(1-ans1)+(m+wait+TCM)*ans1);
		double totalcost=cost1+cost2;
		return totalcost;	
	}
	/**
	 * This method calculates and returns the total maintenance cost (This is the numerator of expected cost fuction)
	 * @param node node
	 * @param nx moment in the time
	 * @return totalcost cost in a specific time
	 */
	static double expectedcost(MNode node, double nx){
		double CPM =node.getCpm();
		double CCM =node.getCcm();
		double CW = node.getCw();
		double ans1= node.getD().cdf(nx);
		double wait=node.getWaiting();
		double m=CostFunction.calcMdeltaLib(node.getD(),0,nx);
		double cost1=CPM*(1-ans1);
		double cost2=(CCM+CW*(wait))*ans1;
		double totalcost=cost1+cost2;
		return totalcost;	
	}
	/**
	 * This method returns the node's maintenance number in the planning horizon
	 * @return num
	 */
	public int getNum() {
		return num;
	}
	/**
	 * This method returns the node's optimum
	 * @return opt
	 */
	public double getOpt() {
		return opt;
	}
	/**
	 * This method returns the node's time cycle
	 * @return timecycle
	 */
	public double getTimecycle() {
		return timecycle;
	}
	/**
	 * This method returns the node's expected service time
	 * @return expectedservicetime
	 */
	public double getExpectedservicetime() {
		return expectedservicetime;
	}
	/**
	 * This method returns the node's data for graph cost curve 
	 * @return costcurve
	 */
	public double[][] getCostcurve() {
		return costcurve;
	}
	/**
	 * This method sets the node's maintenance number in the horizon planning
	 * @param num maintenance number in the horizon planning
	 */
	public void setNum(int num) {
		this.num = num;
	}
	/**
	 * This method sets the node's optimum
	 * @param opt optimal time for maintenance
	 */
	public void setOpt(double opt) {
		this.opt = opt;
	}
	/**
	 * This method sets the node's time cycle
	 * @param timecycle time cycle
	 */
	public void setTimecycle(double timecycle) {
		this.timecycle = timecycle;
	}
	/**
	 * This method sets the node's expected service time
	 * @param expectedservicetime expected service time
	 */
	public void setExpectedservicetime(double expectedservicetime) {
		this.expectedservicetime = expectedservicetime;
	}
	/**
	 * This method sets the node's data for graph cost curve
	 * @param costcurve data for graph cost curve
	 */
	public void setCostcurve(double[][] costcurve) {
		this.costcurve = costcurve;
	}
	/**
	 * This method return de lower bound of the time window
	 * @return	l lower bound
	 */
	public double getL() {
		return l;
	}
	/**
	 * This method sets the lower bound of the time window
	 * @param l lower bound
	 */
	public void setL(double l) {
		this.l = l;
	}
	/**
	 * This method returns the upper bound of the time window
	 * @return u uuper bound
	 */
	public double getU() {
		return u;
	}
	/**
	 * This method sets the upper bound of the time window
	 * @param u upper bound
	 */

	public void setU(double u) {
		this.u = u;
	}
	/**
	 * This method returns the matrix with the parameter m and b (y=mx+b) of piecewise linear approximation
	 * @return pieces the matrix with parameters of the piecewise lienar approximation
	 */
	public double [][] getPieces() {
		return pieces;
	}
	/**
	 * This method sets the matrix with the parameter m and b
	 * @param pieces the matrix with the parametes m and b 
	 */
	public void setPieces(double [][] pieces) {
		this.pieces = pieces;
	}
	/** 
	 * This method returns the expected cost
	 * @return expectedcost Expected cost 
	 */
	public double getExpectedcost() {
		return expectedcost;
	}
	/**
	 * This method sets the expected cost
	 * @param expectedcost	Expected Cost 
	 */
	public void setExpectedcost(double expectedcost) {
		this.expectedcost = expectedcost;
	}	
}